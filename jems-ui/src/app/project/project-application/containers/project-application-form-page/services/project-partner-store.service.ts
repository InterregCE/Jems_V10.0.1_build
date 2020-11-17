import {Injectable} from '@angular/core';
import {
  InputProjectPartnerCreate,
  InputProjectPartnerUpdate,
  OutputProjectPartnerDetail,
  ProjectPartnerService,
  InputProjectPartnerAddress,
  InputProjectContact,
  InputProjectPartnerContribution
} from '@cat/api';
import {Observable, ReplaySubject, Subject} from 'rxjs';
import {
  tap
} from 'rxjs/operators';
import {Log} from '../../../../../common/utils/log';
import {ProjectApplicationFormSidenavService} from './project-application-form-sidenav.service';

@Injectable()
export class ProjectPartnerStore {

  private partnerId: number;
  private projectId: number;

  totalAmountChanged$ = new Subject<boolean>();
  partner$ = new ReplaySubject<OutputProjectPartnerDetail | any>(1);

  constructor(private partnerService: ProjectPartnerService,
              private projectApplicationFormSidenavService: ProjectApplicationFormSidenavService) {
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
        tap(saved => Log.info('Updated partner:', this, saved))
      );
  }

  createPartner(partner: InputProjectPartnerCreate): Observable<OutputProjectPartnerDetail> {
    return this.partnerService.createProjectPartner(this.projectId, partner)
      .pipe(
        tap(created => this.partner$.next(created)),
        tap(created => Log.info('Created partner:', this, created)),
        tap(() => this.projectApplicationFormSidenavService.refreshPartners(this.projectId)),
      );
  }

  updatePartnerAddress(addresses: InputProjectPartnerAddress[]): Observable<OutputProjectPartnerDetail> {
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

  updatePartnerContribution(contribution: InputProjectPartnerContribution): Observable<OutputProjectPartnerDetail> {
    return this.partnerService.updateProjectPartnerContribution(this.partnerId, this.projectId, contribution)
      .pipe(
        tap(saved => this.partner$.next(saved)),
        tap(saved => Log.info('Updated partner contribution:', this, saved)),
      );
  }
}
