import {Injectable} from '@angular/core';
import {
  ProjectContactDTO,
  ProjectPartnerAddressDTO,
  ProjectPartnerDetailDTO,
  ProjectPartnerDTO,
  ProjectPartnerMotivationDTO,
  ProjectPartnerService,
  ProjectPartnerSummaryDTO,
} from '@cat/api';
import {BehaviorSubject, combineLatest, merge, Observable, of, Subject} from 'rxjs';
import {catchError, map, shareReplay, switchMap, tap} from 'rxjs/operators';
import {Log} from '@common/utils/log';
import {ProjectStore} from '../../project-application-detail/services/project-store.service';
import {ProjectPartner} from '@project/model/ProjectPartner';
import {ProjectPartnerRoleEnum, ProjectPartnerRoleEnumUtil} from '@project/model/ProjectPartnerRoleEnum';
import {RoutingService} from '@common/services/routing.service';
import {ProjectVersionStore} from '@project/common/services/project-version-store.service';
import {ProjectPaths} from '@project/common/project-util';

@Injectable({
  providedIn: 'root'
})
export class ProjectPartnerStore {
  public static PARTNER_DETAIL_PATH = '/applicationFormPartner/';
  isProjectEditable$: Observable<boolean>;
  partner$: Observable<ProjectPartnerDetailDTO>;
  partners$: Observable<ProjectPartner[]>;
  leadPartner$: Observable<ProjectPartnerDetailDTO | null>;
  partnerSummaries$: Observable<ProjectPartnerSummaryDTO[]>;
  partnerSummariesForFiles$: Observable<ProjectPartnerSummaryDTO[]>;
  private partnerId: number;
  private projectId: number;
  private partnerUpdateEvent$ = new BehaviorSubject(null);
  private updatedPartner$ = new Subject<ProjectPartnerDetailDTO>();

  constructor(private partnerService: ProjectPartnerService,
              private projectStore: ProjectStore,
              private routingService: RoutingService,
              private projectVersionStore: ProjectVersionStore) {
    this.isProjectEditable$ = this.projectStore.projectEditable$;
    this.partnerSummaries$ = this.partnerSummaries();
    this.partnerSummariesForFiles$ = this.partnerSummariesForFiles();
    this.partners$ = combineLatest([
      this.projectStore.project$,
      this.projectVersionStore.currentRouteVersion$,
      this.partnerUpdateEvent$
    ]).pipe(
      switchMap(([project, version]) => this.partnerService.getProjectPartnersForDropdown(project.id, ['sortNumber'], version)),
      map(projectPartners => projectPartners.map((projectPartner, index) =>
        new ProjectPartner(projectPartner.id, projectPartner.abbreviation, ProjectPartnerRoleEnumUtil.toProjectPartnerRoleEnum(projectPartner.role), projectPartner.sortNumber, projectPartner.country))),
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


  private partner(): Observable<ProjectPartnerDetailDTO> {
    const initialPartner$ = combineLatest([
      this.routingService.routeParameterChanges(ProjectPartnerStore.PARTNER_DETAIL_PATH, 'partnerId'),
      this.projectStore.projectId$,
      this.projectVersionStore.currentRouteVersion$
    ]).pipe(
      tap(([partnerId, projectId]) => {
        this.partnerId = Number(partnerId);
        this.projectId = projectId;
      }),
      switchMap(([partnerId, projectId, version]) => partnerId && projectId
        ? this.partnerService.getProjectPartnerById(Number(partnerId), version)
          .pipe(
            catchError(() => {
              this.routingService.navigate([ProjectPaths.PROJECT_DETAIL_PATH, this.projectId]);
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
    return combineLatest([this.projectStore.projectId$, this.projectVersionStore.currentRouteVersion$, this.partnerUpdateEvent$])
      .pipe(
        switchMap(([projectId, version]) => this.partnerService.getProjectPartnersForDropdown(projectId, ['sortNumber'], version))
      );
  }

  private partnerSummariesForFiles(): Observable<ProjectPartnerSummaryDTO[]> {
    return combineLatest([this.projectStore.projectId$, this.partnerUpdateEvent$])
      .pipe(
        switchMap(([projectId]) => this.partnerService.getProjectPartnersForDropdown(projectId, ['sortNumber']))
      );
  }
}
