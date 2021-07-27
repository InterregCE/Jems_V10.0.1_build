import {Injectable} from '@angular/core';
import {
  InputProjectContact,
  InputProjectPartnerCreate,
  InputProjectPartnerUpdate,
  OutputProjectPartner,
  OutputProjectPartnerDetail,
  ProjectPartnerAddressDTO,
  ProjectPartnerMotivationDTO,
  ProjectPartnerService,
} from '@cat/api';
import {BehaviorSubject, combineLatest, merge, Observable, of, Subject} from 'rxjs';
import {catchError, map, shareReplay, switchMap, tap} from 'rxjs/operators';
import {Log} from '@common/utils/log';
import {ProjectApplicationFormSidenavService} from './project-application-form-sidenav.service';
import {ProjectStore} from '../../project-application-detail/services/project-store.service';
import {ProjectPartner} from '@project/model/ProjectPartner';
import {ProjectPartnerRoleEnumUtil} from '@project/model/ProjectPartnerRoleEnum';
import {RoutingService} from '@common/services/routing.service';
import {ProjectVersionStore} from '@project/common/services/project-version-store.service';

@Injectable()
export class ProjectPartnerStore {
  public static PARTNER_DETAIL_PATH = '/applicationFormPartner/';

  private partnerId: number;
  private projectId: number;
  private partnerUpdateEvent$ = new BehaviorSubject(null);

  isProjectEditable$: Observable<boolean>;
  partner$: Observable<OutputProjectPartnerDetail>;
  partners$: Observable<ProjectPartner[]>;
  dropdownPartners$: Observable<OutputProjectPartner[]>;

  private updatedPartner$ = new Subject<OutputProjectPartnerDetail>();

  constructor(private partnerService: ProjectPartnerService,
              private projectApplicationFormSidenavService: ProjectApplicationFormSidenavService,
              private projectStore: ProjectStore,
              private routingService: RoutingService,
              private projectVersionStore: ProjectVersionStore) {
    this.isProjectEditable$ = this.projectStore.projectEditable$;
    this.dropdownPartners$ = this.dropdownPartners();

    this.partners$ = combineLatest([
      this.projectStore.project$,
      this.projectVersionStore.currentRouteVersion$,
      this.partnerUpdateEvent$
    ]).pipe(
      switchMap(([project, version]) => this.partnerService.getProjectPartnersForDropdown(project.id, undefined, version)),
      map(projectPartners => projectPartners.map((projectPartner, index) =>
        new ProjectPartner(projectPartner.id, index, projectPartner.abbreviation, ProjectPartnerRoleEnumUtil.toProjectPartnerRoleEnum(projectPartner.role), projectPartner.sortNumber, projectPartner.country))),
      shareReplay(1)
    );
    this.partner$ = this.partner();
  }

  private partner(): Observable<OutputProjectPartnerDetail> {
    const initialPartner$ =  combineLatest([
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
              this.routingService.navigate([ProjectStore.PROJECT_DETAIL_PATH, this.projectId]);
              return of({} as OutputProjectPartnerDetail);
            })
          )
        : of({} as OutputProjectPartnerDetail)
      ),
      tap(partner => Log.info('Fetched the programme partner:', this, partner)),
    );

    return merge(initialPartner$, this.updatedPartner$)
      .pipe(
        shareReplay(1)
      );
  }

  savePartner(partner: InputProjectPartnerUpdate): Observable<OutputProjectPartnerDetail> {
    return this.partnerService.updateProjectPartner(partner)
      .pipe(
        tap(saved => this.updatedPartner$.next(saved)),
        tap(() => this.partnerUpdateEvent$.next(null)),
        tap(saved => Log.info('Updated partner:', this, saved))
      );
  }

  createPartner(partner: InputProjectPartnerCreate): Observable<OutputProjectPartnerDetail> {
    return this.partnerService.createProjectPartner(this.projectId, partner)
      .pipe(
        tap(created => this.updatedPartner$.next(created)),
        tap(() => this.partnerUpdateEvent$.next(null)),
        tap(created => Log.info('Created partner:', this, created)),
        tap(() => this.projectApplicationFormSidenavService.refreshPartners(this.projectId)),
      );
  }

  updatePartnerAddress(addresses: ProjectPartnerAddressDTO[]): Observable<OutputProjectPartnerDetail> {
    return this.partnerService.updateProjectPartnerAddress(this.partnerId, addresses)
      .pipe(
        tap(saved => this.updatedPartner$.next(saved)),
        tap(saved => Log.info('Updated partner addresses:', this, saved)),
      );
  }

  updatePartnerContact(contacts: InputProjectContact[]): Observable<OutputProjectPartnerDetail> {
    return this.partnerService.updateProjectPartnerContact(this.partnerId, contacts)
      .pipe(
        tap(saved => this.updatedPartner$.next(saved)),
        tap(saved => Log.info('Updated partner contact:', this, saved)),
      );
  }

  updatePartnerMotivation(motivation: ProjectPartnerMotivationDTO): Observable<OutputProjectPartnerDetail> {
    return this.partnerService.updateProjectPartnerMotivation(this.partnerId, motivation)
      .pipe(
        tap(saved => this.updatedPartner$.next(saved)),
        tap(saved => Log.info('Updated partner motivation:', this, saved)),
      );
  }

  private dropdownPartners(): Observable<OutputProjectPartner[]> {
    return this.projectVersionStore.currentRouteVersion$
      .pipe(
        switchMap(version => this.partnerService.getProjectPartnersForDropdown(this.projectId, undefined, version))
      );
  }
}
