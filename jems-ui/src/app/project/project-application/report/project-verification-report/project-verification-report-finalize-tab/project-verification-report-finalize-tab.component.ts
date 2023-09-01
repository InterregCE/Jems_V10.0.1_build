import {ChangeDetectionStrategy, Component} from '@angular/core';
import {FormService} from '@common/components/section/form/form.service';
import {BehaviorSubject, combineLatest, Observable, of} from 'rxjs';
import {
  ProjectDetailDTO,
  ProjectReportDTO,
  ProjectReportVerificationConclusionDTO, ProjectReportVerificationNotificationDTO,
  ProjectReportVerificationService
} from '@cat/api';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {catchError, finalize, map, take, tap} from 'rxjs/operators';
import {Alert} from '@common/components/forms/alert';
import {APIError} from '@common/models/APIError';
import {
  ProjectReportDetailPageStore
} from '@project/project-application/report/project-report/project-report-detail-page/project-report-detail-page-store.service';
import {
  ProjectReportPageStore
} from '@project/project-application/report/project-report/project-report-page-store.service';
import {
  ProjectVerificationReportFinalizeStore
} from '@project/project-application/report/project-verification-report/project-verification-report-finalize-tab/project-verification-report-finalize-store.service';
import {
  ProjectStore
} from '@project/project-application/containers/project-application-detail/services/project-store.service';

@Component({
  selector: 'jems-project-verification-report-finalize-tab',
  templateUrl: './project-verification-report-finalize-tab.component.html',
  styleUrls: ['./project-verification-report-finalize-tab.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectVerificationReportFinalizeTabComponent {
  Alert = Alert;
  error$ = new BehaviorSubject<APIError | null>(null);
  finalizationLoading = false;
  sendNotificationLoading = false;

  data$: Observable<{
    conclusions: ProjectReportVerificationConclusionDTO;
    reportId: number;
    projectId: number;
    finalizationAllowed: boolean;
    reportFinalised: boolean;
    userCanEdit: boolean;
    userCanView: boolean;
    userCanSendNotification: boolean;
    verificationNotification: ProjectReportVerificationNotificationDTO;
    projectDetail: ProjectDetailDTO;
  }>;
  overviewForm: FormGroup = this.formBuilder.group({
    startDate: [''],
    conclusionJS: ['', Validators.maxLength(5000)],
    conclusionMA: ['', Validators.maxLength(5000)],
    verificationFollowUp: ['', Validators.maxLength(5000)],
  });

  constructor(
    private formBuilder: FormBuilder,
    private formService: FormService,
    private reportDetailPageStore: ProjectReportDetailPageStore,
    private router: Router,
    private route: ActivatedRoute,
    private reportPageStore: ProjectReportPageStore,
    private projectReportVerificationService: ProjectReportVerificationService,
    private projectVerificationReportFinalizeStore: ProjectVerificationReportFinalizeStore,
    private projectStore: ProjectStore
  ) {
    this.data$ = combineLatest([
      this.projectVerificationReportFinalizeStore.conclusions$,
      this.reportDetailPageStore.projectReport$,
      this.reportPageStore.userCanEditVerification$,
      this.reportPageStore.userCanViewVerification$,
      this.reportPageStore.userCanFinalizeVerification$,
      this.reportPageStore.userHasEditVerificationPrivilege$,
      this.reportDetailPageStore.projectReportVerificationNotification$,
      this.projectStore.project$
    ]).pipe(
      map(([conclusions, report, userCanEdit, userCanView, canFinalize, userHasEditVerificationPrivilege, verificationNotification, projectDetail]: any) => ({
        conclusions,
        reportId: report.id,
        projectId: report.projectId,
        finalizationAllowed: canFinalize,
        reportFinalised: report.status === ProjectReportDTO.StatusEnum.Finalized,
        userCanEdit,
        userCanView,
        userCanSendNotification: userHasEditVerificationPrivilege,
        verificationNotification,
        projectDetail
      })),
      tap(() => this.initForm()),
      tap(data => this.resetForm(data.conclusions)),
      tap(data => this.disableForms(data.userCanEdit && !data.reportFinalised))
    );
  }

  resetForm(conclusions: ProjectReportVerificationConclusionDTO): void {
    this.overviewForm.reset();
    this.overviewForm.controls.startDate.setValue(conclusions.startDate);
    this.overviewForm.controls.conclusionJS.setValue(conclusions.conclusionJS);
    this.overviewForm.controls.conclusionMA.setValue(conclusions.conclusionMA);
    this.overviewForm.controls.verificationFollowUp.setValue(conclusions.verificationFollowUp);
  }

  saveForm(projectId: number, reportId: number): void {
    const toSave = {
      startDate: this.overviewForm.controls.startDate.value,
      conclusionJS: this.overviewForm.controls.conclusionJS.value,
      conclusionMA: this.overviewForm.controls.conclusionMA.value,
      verificationFollowUp: this.overviewForm.controls.verificationFollowUp.value
    } as ProjectReportVerificationConclusionDTO;

    this.projectReportVerificationService.updateReportVerificationConclusion(projectId, reportId, toSave)
      .pipe(
        take(1),
        tap(() => this.formService.setSuccess('project.application.project.verification.work.tab.finalise.save.success')),
        catchError(error => this.formService.setError(error)),
      )
      .subscribe();
  }

  finalizeReport(projectId: number, reportId: number): void {
    this.finalizationLoading = true;
    this.reportDetailPageStore.finalizeReport(projectId, reportId)
      .pipe(
        tap(() => this.redirectToReportList()),
        catchError((error) => this.showErrorMessage(error.error)),
        finalize(() => this.finalizationLoading = false)
      ).subscribe();
  }

  private initForm(): void {
    this.formService.init(this.overviewForm);
  }

  private disableForms(userCanEdit: boolean): void {
    if (!userCanEdit) {
      this.overviewForm.disable();
    }
  }

  private showErrorMessage(error: APIError): Observable<null> {
    this.error$.next(error);
    setTimeout(() => {
      if (this.error$.value?.id === error.id) {
        this.error$.next(null);
      }
    }, 10000);

    return of(null);
  }

  private redirectToReportList(): void {
    this.router.navigate(['../../..'], {relativeTo: this.route});
  }

  sendNotificationDoneByJs(projectId: number, reportId: number) {
    this.sendNotificationLoading = true;
    this.reportDetailPageStore.sendNotification(projectId, reportId)
      .pipe(
        catchError((error) => this.showErrorMessage(error.error)),
        finalize(() => this.sendNotificationLoading = false)
      ).subscribe();

  }
}
