import {ChangeDetectionStrategy, Component, OnDestroy, OnInit} from '@angular/core';
import {combineLatest, merge, of, Subject} from 'rxjs';
import {I18nValidationError} from '@common/validation/i18n-validation-error';
import {HttpErrorResponse} from '@angular/common/http';
import {catchError, flatMap, map, switchMap, tap} from 'rxjs/operators';
import {Log} from '../../../../../../common/utils/log';
import {ActivatedRoute, Router} from '@angular/router';
import {
  InputProjectPartnerAddress,
  InputProjectPartnerContact,
  InputProjectPartnerContribution,
  InputProjectPartnerCreate,
  InputProjectPartnerUpdate,
  OutputProjectStatus,
  ProjectPartnerService
} from '@cat/api';
import {BaseComponent} from '@common/components/base-component';
import {ProjectStore} from '../../../project-application-detail/services/project-store.service';
import {ProjectApplicationFormSidenavService} from '../../services/project-application-form-sidenav.service';
import {TabService} from '../../../../../../common/services/tab.service';

@Component({
  selector: 'app-project-application-form-partner-detail',
  templateUrl: './project-application-form-partner-detail.component.html',
  styleUrls: ['./project-application-form-partner-detail.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormPartnerDetailComponent extends BaseComponent implements OnInit, OnDestroy {

  projectId = this.activatedRoute?.snapshot?.params?.projectId
  partnerId = this.activatedRoute?.snapshot?.params?.partnerId;
  partnerSaveSuccess$ = new Subject<boolean>();
  partnerSaveError$ = new Subject<I18nValidationError | null>();
  savePartner$ = new Subject<InputProjectPartnerUpdate>();
  createPartner$ = new Subject<InputProjectPartnerCreate>();

  activeTab$ = this.tabService.currentTab(
    ProjectApplicationFormPartnerDetailComponent.name + this.partnerId
  );


  partnerContactSaveSuccess$ = new Subject<boolean>();
  partnerContactSaveError$ = new Subject<I18nValidationError | null>();
  savePartnerContact$ = new Subject<InputProjectPartnerContact[]>();

  partnerContributionSaveSuccess$ = new Subject<boolean>();
  partnerContributionSaveError$ = new Subject<I18nValidationError | null>();
  savePartnerContribution$ = new Subject<InputProjectPartnerContribution>();

  partnerOrganizationDetailsSaveSuccess$ = new Subject<boolean>();
  partnerOrganizationDetailsSaveError$ = new Subject<I18nValidationError | null>();
  savePartnerOrganizationDetails$ = new Subject<InputProjectPartnerAddress[]>();

  private partnerById$ = this.partnerId
    ? this.partnerService.getProjectPartnerById(this.partnerId, this.projectId)
      .pipe(
        tap(partner => Log.info('Fetched partner:', this, partner))
      )
    : of({});

  private savedPartner$ = this.savePartner$
    .pipe(
      switchMap(partnerUpdate =>
        this.partnerService.updateProjectPartner(this.projectId, partnerUpdate)
          .pipe(
            catchError((error: HttpErrorResponse) => {
              this.partnerSaveError$.next(error.error);
              return of();
            })
          )
      ),
      tap(() => this.partnerSaveError$.next(null)),
      tap(() => this.partnerSaveSuccess$.next(true)),
      tap(saved => Log.info('Updated partner:', this, saved))
    );

  private updatedPartnerContact$ = this.savePartnerContact$
    .pipe(
      flatMap(partnerUpdate =>
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
      flatMap(partnerContributionUpdate =>
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

  private updatedPartnerOrganizationDetails$ = this.savePartnerOrganizationDetails$
    .pipe(
      flatMap(partnerOrganizationDetailsUpdate =>
        this.partnerService.updateProjectPartnerAddress(this.partnerId, this.projectId, partnerOrganizationDetailsUpdate)
      ),
      tap(() => this.partnerOrganizationDetailsSaveError$.next(null)),
      tap(() => this.partnerOrganizationDetailsSaveSuccess$.next(true)),
      tap(saved => Log.info('Updated partner organization details:', this, saved)),
      catchError((error: HttpErrorResponse) => {
        this.partnerOrganizationDetailsSaveError$.next(error.error);
        return of();
      })
    )

  private createdPartner$ = this.createPartner$
    .pipe(
      switchMap(partnerCreate =>
        this.partnerService.createProjectPartner(this.projectId, partnerCreate)
          .pipe(
            tap(saved => this.partnerId = saved.id),
            catchError((error: HttpErrorResponse) => {
              this.partnerSaveError$.next(error.error);
              return of();
            })
          )
      ),
      tap(() => this.partnerSaveError$.next(null)),
      tap(() => this.partnerSaveSuccess$.next(true)),
      tap(saved => Log.info('Created partner:', this, saved)),
      tap(() => this.projectApplicationFormSidenavService.refreshPartners()),
    );

  public partner$ = merge(
    this.partnerById$,
    this.savedPartner$,
    this.createdPartner$,
    this.updatedPartnerContact$,
    this.updatedPartnerContribution$,
    this.updatedPartnerOrganizationDetails$
  );

  details$ = combineLatest([
    this.partner$,
    this.projectStore.getProject()
  ])
    .pipe(
      tap(([partner, project]) => this.projectApplicationFormSidenavService.setAcronym(project.acronym)),
      map(
        ([partner, project]) => ({
          partner,
          editable: project.projectStatus.status === OutputProjectStatus.StatusEnum.DRAFT
            || project.projectStatus.status === OutputProjectStatus.StatusEnum.RETURNEDTOAPPLICANT
        })
      )
    );

  constructor(private partnerService: ProjectPartnerService,
              private activatedRoute: ActivatedRoute,
              private projectStore: ProjectStore,
              private router: Router,
              private projectApplicationFormSidenavService: ProjectApplicationFormSidenavService,
              private tabService: TabService) {
    super();
  }

  ngOnInit(): void {
    this.projectStore.init(this.projectId);
    this.projectApplicationFormSidenavService.init(this.destroyed$, this.projectId);
  }

  ngOnDestroy() {
    super.ngOnDestroy();
    this.tabService.cleanupTab(ProjectApplicationFormPartnerDetailComponent.name + this.partnerId);
  }

  redirectToPartnerOverview(): void {
    this.router.navigate(['app', 'project', 'detail', this.projectId, 'applicationForm']);
  }

  changeTab(tabIndex: number): void {
    this.tabService.changeTab(ProjectApplicationFormPartnerDetailComponent.name + this.partnerId, tabIndex);
  }

}
