import {Injectable} from '@angular/core';
import {
  ProjectCallSettingsDTO,
  ProjectContactDTO,
  ProjectPartnerAddressDTO,
  ProjectPartnerDetailDTO,
  ProjectPartnerDTO,
  ProjectPartnerMotivationDTO,
  ProjectPartnerService,
  ProjectReportService,
  ProjectPartnerSummaryDTO, ProjectStatusDTO, ProjectVersionDTO, ProjectContractingPartnersService
} from '@cat/api';
import {BehaviorSubject, combineLatest, merge, Observable, of, ReplaySubject, Subject} from 'rxjs';
import {catchError, filter, map, shareReplay, switchMap, tap} from 'rxjs/operators';
import {Log} from '@common/utils/log';
import {ProjectStore} from '../../project-application-detail/services/project-store.service';
import {ProjectPartner} from '@project/model/ProjectPartner';
import {ProjectPartnerRoleEnum, ProjectPartnerRoleEnumUtil} from '@project/model/ProjectPartnerRoleEnum';
import {RoutingService} from '@common/services/routing.service';
import {ProjectVersionStore} from '@project/common/services/project-version-store.service';
import {ProjectPaths} from '@project/common/project-util';
import StatusEnum = ProjectStatusDTO.StatusEnum;
import {Tools} from '@common/utils/tools';
import CallTypeEnum = ProjectCallSettingsDTO.CallTypeEnum;

@Injectable({
  providedIn: 'root'
})
export class ProjectPartnerStore {
  public static PARTNER_DETAIL_PATH = '/applicationFormPartner/';

  isProjectEditable$: Observable<boolean>;
  projectCallType$: Observable<CallTypeEnum>;
  isProjectCallTypeSpf$: Observable<boolean>;
  partner$: Observable<ProjectPartnerDetailDTO>;
  partners$: Observable<ProjectPartner[]>;
  leadPartner$: Observable<ProjectPartnerDetailDTO | null>;
  partnerSummaries$: Observable<ProjectPartnerSummaryDTO[]>;
  partnerSummariesOfLastApprovedProjectVersion$: Observable<ProjectPartnerSummaryDTO[]>;
  partnerSummariesForContracting$: Observable<ProjectPartnerSummaryDTO[]>;
  partnerReportSummaries$: Observable<ProjectPartnerSummaryDTO[]>;
  latestPartnerSummaries$: Observable<ProjectPartnerSummaryDTO[]>;
  partnerSummariesOfLastApprovedVersion$: Observable<ProjectPartnerSummaryDTO[]>;
  private partnerId: number;
  private projectId: number;
  private lastContractedVersion$ = new ReplaySubject<string>(1);
  private partnerUpdateEvent$ = new BehaviorSubject(null);
  private updatedPartner$ = new Subject<ProjectPartnerDetailDTO>();

  constructor(private partnerService: ProjectPartnerService,
              private projectStore: ProjectStore,
              private routingService: RoutingService,
              private projectVersionStore: ProjectVersionStore,
              private projectReportService: ProjectReportService,
              private projectContractingPartnersService: ProjectContractingPartnersService) {
    this.isProjectEditable$ = this.projectStore.projectEditable$;
    this.projectCallType$ = this.projectStore.projectCallType$;
    this.isProjectCallTypeSpf$ = this.projectCallType$.pipe(map(type => type === CallTypeEnum.SPF));
    this.partnerSummaries$ = this.partnerSummaries();
    this.partnerSummariesForContracting$ = this.partnerSummariesForContracting();
    this.partnerSummariesOfLastApprovedProjectVersion$ = this.partnerSummariesOfLastApprovedVersion();
    this.latestPartnerSummaries$ = this.partnerSummariesFromVersion();
    this.partnerReportSummaries$ = this.partnerReportSummaries();
    this.partnerSummariesOfLastApprovedVersion$ = this.getPartnerSummariesOfLastApprovedVersion();
    this.partners$ = combineLatest([
      this.projectStore.project$,
      this.projectVersionStore.selectedVersionParam$,
      this.partnerUpdateEvent$
    ]).pipe(
      switchMap(([project, version]) =>
        this.partnerService.getProjectPartnersForDropdown(project.id, ['sortNumber'], version)
            .pipe(map(projectPartners => {
              return {
                projectPartners,
                projectCallType: project.callSettings.callType
              };
            }))
      ),
      map(data => data.projectPartners.map(projectPartner =>
        new ProjectPartner(
          projectPartner.id,
          projectPartner.active,
          projectPartner.abbreviation,
          ProjectPartnerRoleEnumUtil.toProjectPartnerRoleEnum(projectPartner.role),
          projectPartner.sortNumber,
          projectPartner.country,
          ProjectPartnerStore.getPartnerNumber(data.projectCallType, projectPartner.role, projectPartner.sortNumber)
        ))),
      shareReplay(1)
    );
    this.partner$ = this.partner();

    this.leadPartner$ = this.partners$.pipe(
      switchMap(partners => {
        const leadPartnerId = partners.find(partner => partner.role === ProjectPartnerRoleEnum.LEAD_PARTNER)?.id;
        return leadPartnerId ? this.partnerService.getProjectPartnerById(leadPartnerId) : of(null);
      }),
    );
  }

  savePartner(partner: ProjectPartnerDTO): Observable<ProjectPartnerDetailDTO> {
    return this.partnerService.updateProjectPartner(partner)
      .pipe(
        tap(saved => this.updatedPartner$.next(saved)),
        tap(() => this.partnerUpdateEvent$.next(null)),
        tap(saved => Log.info('Updated partner:', this, saved))
      );
  }

  createPartner(partner: ProjectPartnerDTO): Observable<ProjectPartnerDetailDTO> {
    return this.partnerService.createProjectPartner(this.projectId, partner)
      .pipe(
        tap(created => this.updatedPartner$.next(created)),
        tap(() => this.partnerUpdateEvent$.next(null)),
        tap(created => Log.info('Created partner:', this, created))
      );
  }

  updatePartnerAddress(addresses: ProjectPartnerAddressDTO[]): Observable<ProjectPartnerDetailDTO> {
    return this.partnerService.updateProjectPartnerAddress(this.partnerId, addresses)
      .pipe(
        tap(saved => this.updatedPartner$.next(saved)),
        tap(saved => Log.info('Updated partner addresses:', this, saved))
      );
  }

  updatePartnerContact(contacts: ProjectContactDTO[]): Observable<ProjectPartnerDetailDTO> {
    return this.partnerService.updateProjectPartnerContact(this.partnerId, contacts)
      .pipe(
        tap(saved => this.updatedPartner$.next(saved)),
        tap(saved => Log.info('Updated partner contact:', this, saved)),
      );
  }

  updatePartnerMotivation(motivation: ProjectPartnerMotivationDTO): Observable<ProjectPartnerDetailDTO> {
    return this.partnerService.updateProjectPartnerMotivation(this.partnerId, motivation)
      .pipe(
        tap(saved => this.updatedPartner$.next(saved)),
        tap(saved => Log.info('Updated partner motivation:', this, saved)),
      );
  }

  deletePartner(partnerId: number): Observable<void> {
    return this.partnerService.deleteProjectPartner(partnerId)
      .pipe(
        tap(() => this.partnerUpdateEvent$.next(null)),
        tap(() => Log.info('Partner removed:', this, partnerId))
      );
  }

  deactivatePartner(partnerId: number): Observable<void> {
    return this.partnerService.deactivate(partnerId)
      .pipe(
        tap(() => this.partnerUpdateEvent$.next(null)),
        tap(() => Log.info('Partner deactivated:', this, partnerId))
      );
  }

  partnerSummariesFromVersion(version?: string): Observable<ProjectPartnerSummaryDTO[]> {
    return this.projectStore.projectId$
      .pipe(
        switchMap(projectId => this.partnerService.getProjectPartnersForDropdown(projectId, ['sortNumber'], version))
      );
  }

  lastContractedVersionASObservable(): Observable<string> {
    return this.lastContractedVersion$.asObservable();
  }

  private partner(): Observable<ProjectPartnerDetailDTO> {
    const initialPartner$ = combineLatest([
      this.routingService.routeParameterChanges(ProjectPartnerStore.PARTNER_DETAIL_PATH, 'partnerId'),
      this.projectStore.projectId$,
      this.projectVersionStore.selectedVersionParam$
    ]).pipe(
      tap(([partnerId, projectId]) => {
        this.partnerId = Number(partnerId);
        this.projectId = projectId;
      }),
      switchMap(([partnerId, projectId, version]) => partnerId && projectId
        ? this.partnerService.getProjectPartnerById(Number(partnerId), version)
          .pipe(
            catchError(() => {
              this.routingService.navigate([ProjectPaths.PROJECT_DETAIL_PATH, this.projectId, 'applicationFormPartner']);
              return of({} as ProjectPartnerDetailDTO);
            })
          )
        : of({} as ProjectPartnerDetailDTO)
      ),
      tap(partner => Log.info('Fetched the programme partner:', this, partner)),
    );

    return merge(initialPartner$, this.updatedPartner$)
      .pipe(
        shareReplay(1)
      );
  }

  private partnerSummaries(): Observable<ProjectPartnerSummaryDTO[]> {
    return combineLatest([this.projectStore.projectId$, this.projectVersionStore.selectedVersionParam$, this.partnerUpdateEvent$])
      .pipe(
        switchMap(([projectId, version]) => this.partnerService.getProjectPartnersForDropdown(projectId, ['sortNumber'], version))
      );
  }

  private partnerSummariesForContracting(): Observable<ProjectPartnerSummaryDTO[]> {
    return combineLatest([
      this.projectStore.projectId$,
      this.projectVersionStore.lastApprovedOrContractedVersion$,
      this.partnerUpdateEvent$
    ])
      .pipe(
        switchMap(([projectId, version]) => this.projectContractingPartnersService.getProjectPartnersForContracting(projectId, ['sortNumber'], version?.version))
      );
  }

  private partnerSummariesOfLastApprovedVersion(): Observable<ProjectPartnerSummaryDTO[]> {
    return combineLatest([
      this.projectStore.projectId$,
      this.projectVersionStore.lastApprovedOrContractedVersion$,
      this.partnerUpdateEvent$
    ])
      .pipe(
        switchMap(([projectId, version]) => this.partnerService.getProjectPartnersForDropdown(projectId, ['sortNumber'], version?.version))
      );
  }

  private partnerReportSummaries(): Observable<ProjectPartnerSummaryDTO[]> {
    return combineLatest([this.projectStore.projectId$, this.projectVersionStore.versions$])
      .pipe(
        filter(([projectId, version]) => !!projectId),
        switchMap(([projectId, versions]) => {
            const contractedVersion = this.getLastContractedVersion(versions);
            this.lastContractedVersion$.next(contractedVersion);
            return contractedVersion ? this.projectReportService.getProjectPartnersForReporting(projectId, ['sortNumber'], contractedVersion)
              : of([]);
          }
        ),
        shareReplay(1),
        catchError(() => {
          return of([]);
        })
      );
  }

  private getLastContractedVersion(versions: ProjectVersionDTO[]): string {
    return Tools.first(versions.filter(version => version.status === StatusEnum.CONTRACTED)
      .sort((a, b) => a.createdAt > b.createdAt ? -1 : 1))?.version;
  }

  private getPartnerSummariesOfLastApprovedVersion(): Observable<ProjectPartnerSummaryDTO[]> {
    return this.projectVersionStore.lastApprovedOrContractedVersion$
      .pipe(
        map(lastApprovedVersion => lastApprovedVersion?.version),
        filter(version => !!version),
        switchMap(version => this.partnerSummariesFromVersion(version)),
      );
  }


  static getPartnerTranslationKey(role: ProjectPartnerDTO.RoleEnum, callType: CallTypeEnum) {
    const prefix = (callType === CallTypeEnum.STANDARD) ? '' : callType.toLocaleLowerCase() + '.';
    return `${prefix}common.label.project.partner.role.shortcut.${role}`;
  }

  static getPartnerNumber(callType: CallTypeEnum, role: ProjectPartnerSummaryDTO.RoleEnum, sortNumber: number): string {
    if (callType === undefined || callType === CallTypeEnum.STANDARD) {
      return role === ProjectPartnerRoleEnum.LEAD_PARTNER ? 'LP1' : 'PP'.concat(sortNumber.toString());
    }
    return 'PP1 SPF';
  }
}
