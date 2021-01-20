import {Injectable} from '@angular/core';
import {
  InputProjectContact,
  InputProjectPartnerCreate,
  InputProjectPartnerUpdate,
  OutputProjectPartnerDetail,
  ProjectPartnerAddressDTO,
  ProjectPartnerMotivationDTO,
  ProjectPartnerService,
} from '@cat/api';
import {BehaviorSubject, combineLatest, Observable, ReplaySubject, Subject} from 'rxjs';
import {map, shareReplay, switchMap, tap} from 'rxjs/operators';
import {Log} from '../../../../../common/utils/log';
import {ProjectApplicationFormSidenavService} from './project-application-form-sidenav.service';
import {ProjectStore} from '../../project-application-detail/services/project-store.service';
import {ProjectPartner} from '../../../../model/ProjectPartner';
import {ProjectPartnerRoleEnumUtil} from '../../../../model/ProjectPartnerRoleEnum';

@Injectable()
export class ProjectPartnerStore {

  private partnerId: number;
  private projectId: number;
  private partnerUpdateEvent$ = new BehaviorSubject(null);

  isProjectEditable$: Observable<boolean>;
  partner$ = new ReplaySubject<OutputProjectPartnerDetail | any>(1);
  partners$: Observable<ProjectPartner[]>;

  constructor(private partnerService: ProjectPartnerService,
              private projectApplicationFormSidenavService: ProjectApplicationFormSidenavService,
              private projectStore: ProjectStore) {
    this.isProjectEditable$ = this.projectStore.projectEditable$;

    this.partners$ = combineLatest([this.projectStore.getProject(), this.partnerUpdateEvent$]).pipe(
      switchMap(([project]) => this.partnerService.getProjectPartnersForDropdown(project.id, ['sortNumber,asc'])),
      map(projectPartners => projectPartners.map(projectPartner => new ProjectPartner(projectPartner.id, projectPartner.abbreviation, ProjectPartnerRoleEnumUtil.toProjectPartnerRoleEnum(projectPartner.role), projectPartner.sortNumber, projectPartner.country))),
      shareReplay(1)
    );
  }

  init(partnerId: number | string | null, projectId: number): void {
    if (partnerId === this.partnerId) {
      return;
    }
    this.partnerId = Number(partnerId);
    this.projectId = projectId;
    if (!this.partnerId || !this.projectId) {
      this.partner$.next({});
      return;
    }
    this.partnerService.getProjectPartnerById(this.partnerId, this.projectId)
      .pipe(
        tap(projectPartner => Log.info('Fetched project partner:', this, projectPartner)),
        tap(projectPartner => this.partner$.next(projectPartner)),
      ).subscribe();
  }

  savePartner(partner: InputProjectPartnerUpdate): Observable<OutputProjectPartnerDetail> {
    return this.partnerService.updateProjectPartner(this.projectId, partner)
      .pipe(
        tap(saved => this.partner$.next(saved)),
        tap(() => this.partnerUpdateEvent$.next(null)),
        tap(saved => Log.info('Updated partner:', this, saved))
      );
  }

  createPartner(partner: InputProjectPartnerCreate): Observable<OutputProjectPartnerDetail> {
    return this.partnerService.createProjectPartner(this.projectId, partner)
      .pipe(
        tap(created => this.partner$.next(created)),
        tap(() => this.partnerUpdateEvent$.next(null)),
        tap(created => Log.info('Created partner:', this, created)),
        tap(() => this.projectApplicationFormSidenavService.refreshPartners(this.projectId)),
      );
  }

  updatePartnerAddress(addresses: ProjectPartnerAddressDTO[]): Observable<OutputProjectPartnerDetail> {
    return this.partnerService.updateProjectPartnerAddress(this.partnerId, this.projectId, addresses)
      .pipe(
        tap(saved => this.partner$.next(saved)),
        tap(saved => Log.info('Updated partner addresses:', this, saved)),
      );
  }

  updatePartnerContact(contacts: InputProjectContact[]): Observable<OutputProjectPartnerDetail> {
    return this.partnerService.updateProjectPartnerContact(this.partnerId, this.projectId, contacts)
      .pipe(
        tap(saved => this.partner$.next(saved)),
        tap(saved => Log.info('Updated partner contact:', this, saved)),
      );
  }

  updatePartnerMotivation(motivation: ProjectPartnerMotivationDTO): Observable<OutputProjectPartnerDetail> {
    return this.partnerService.updateProjectPartnerMotivation(this.partnerId, this.projectId, motivation)
      .pipe(
        tap(saved => this.partner$.next(saved)),
        tap(saved => Log.info('Updated partner motivation:', this, saved)),
      );
  }
}
