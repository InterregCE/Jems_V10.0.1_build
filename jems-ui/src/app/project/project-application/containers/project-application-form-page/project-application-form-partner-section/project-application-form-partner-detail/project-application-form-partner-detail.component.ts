import {ChangeDetectionStrategy, Component, OnDestroy, OnInit} from '@angular/core';
import {merge, of, Subject} from 'rxjs';
import {I18nValidationError} from '@common/validation/i18n-validation-error';
import {HttpErrorResponse} from '@angular/common/http';
import {catchError, distinctUntilChanged, mergeMap, map, takeUntil, tap} from 'rxjs/operators';
import {Log} from '../../../../../../common/utils/log';
import {ActivatedRoute, Router} from '@angular/router';
import {
  InputProjectContact,
  InputProjectPartnerContribution,
  ProjectPartnerService
} from '@cat/api';
import {BaseComponent} from '@common/components/base-component';
import {ProjectStore} from '../../../project-application-detail/services/project-store.service';
import {ProjectApplicationFormSidenavService} from '../../services/project-application-form-sidenav.service';
import {TabService} from '../../../../../../common/services/tab.service';
import {ProjectPartnerStore} from '../../services/project-partner-store.service';

@Component({
  selector: 'app-project-application-form-partner-detail',
  templateUrl: './project-application-form-partner-detail.component.html',
  styleUrls: ['./project-application-form-partner-detail.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormPartnerDetailComponent extends BaseComponent implements OnInit, OnDestroy {

  projectId = this.activatedRoute?.snapshot?.params?.projectId
  partnerId = this.activatedRoute?.snapshot?.params?.partnerId;

  activeTab$ = this.tabService.currentTab(
    ProjectApplicationFormPartnerDetailComponent.name + this.partnerId
  );

  partnerContactSaveSuccess$ = new Subject<boolean>();
  partnerContactSaveError$ = new Subject<I18nValidationError | null>();
  savePartnerContact$ = new Subject<InputProjectContact[]>();

  partnerContributionSaveSuccess$ = new Subject<boolean>();
  partnerContributionSaveError$ = new Subject<I18nValidationError | null>();
  savePartnerContribution$ = new Subject<InputProjectPartnerContribution>();

  private updatedPartnerContact$ = this.savePartnerContact$
    .pipe(
      mergeMap(partnerUpdate =>
        this.partnerService.updateProjectPartnerContact(this.partnerId, this.projectId, partnerUpdate)
      ),
      tap(() => this.partnerContactSaveError$.next(null)),
      tap(() => this.partnerContactSaveSuccess$.next(true)),
      tap(saved => Log.info('Updated partner contact:', this, saved)),
      catchError((error: HttpErrorResponse) => {
        this.partnerContactSaveError$.next(error.error);
        return of();
      })
    );

  private updatedPartnerContribution$ = this.savePartnerContribution$
    .pipe(
      mergeMap(partnerContributionUpdate =>
        this.partnerService.updateProjectPartnerContribution(this.partnerId, this.projectId, partnerContributionUpdate)
      ),
      tap(() => this.partnerContributionSaveError$.next(null)),
      tap(() => this.partnerContributionSaveSuccess$.next(true)),
      tap(saved => Log.info('Updated partner contribution:', this, saved)),
      catchError((error: HttpErrorResponse) => {
        this.partnerContributionSaveError$.next(error.error);
        return of();
      })
    );

  partner$ = merge(
    this.partnerStore.getProjectPartner(),
    this.updatedPartnerContact$,
    this.updatedPartnerContribution$,
  );

  constructor(private partnerService: ProjectPartnerService,
              private activatedRoute: ActivatedRoute,
              private projectStore: ProjectStore,
              public partnerStore: ProjectPartnerStore,
              private router: Router,
              private projectApplicationFormSidenavService: ProjectApplicationFormSidenavService,
              private tabService: TabService) {
    super();
    this.activatedRoute.params.pipe(
      takeUntil(this.destroyed$),
      map(params => params.partnerId),
      distinctUntilChanged(),
      tap(id => this.partnerStore.init(id)),
    ).subscribe();
  }

  ngOnInit(): void {
    this.projectStore.init(this.projectId);
    this.projectApplicationFormSidenavService.init(this.destroyed$, this.projectId);
  }

  ngOnDestroy() {
    super.ngOnDestroy();
    this.tabService.cleanupTab(ProjectApplicationFormPartnerDetailComponent.name + this.partnerId);
  }

  changeTab(tabIndex: number): void {
    this.tabService.changeTab(ProjectApplicationFormPartnerDetailComponent.name + this.partnerId, tabIndex);
  }

}
