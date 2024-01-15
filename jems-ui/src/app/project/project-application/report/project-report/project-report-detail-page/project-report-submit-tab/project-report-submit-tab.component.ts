import {ChangeDetectionStrategy, ChangeDetectorRef, Component} from '@angular/core';
import {
  ProjectApplicationFormSidenavService
} from '@project/project-application/containers/project-application-form-page/services/project-application-form-sidenav.service';
import {Alert} from '@common/components/forms/alert';
import {BehaviorSubject, combineLatest, Observable, of} from 'rxjs';
import {APIError} from '@common/models/APIError';
import {catchError, finalize, map, tap} from 'rxjs/operators';
import {Router} from '@angular/router';
import {
  ProjectStore
} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {
  PreConditionCheckResultDTO,
  ProjectPartnerReportSummaryDTO,
  ProjectReportDTO,
  ProjectReportSummaryDTO,
  UserRoleDTO
} from '@cat/api';
import {FormService} from '@common/components/section/form/form.service';
import {
  ProjectReportPageStore
} from '@project/project-application/report/project-report/project-report-page-store.service';
import {
  ProjectReportDetailPageStore
} from '@project/project-application/report/project-report/project-report-detail-page/project-report-detail-page-store.service';
import PermissionsEnum = UserRoleDTO.PermissionsEnum;
import {ReportUtil} from '@project/common/report-util';

@Component({
  selector: 'jems-project-report-submit-tab',
  templateUrl: './project-report-submit-tab.component.html',
  styleUrls: ['./project-report-submit-tab.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [FormService],
})
export class ProjectReportSubmitTabComponent {
  PermissionsEnum = PermissionsEnum;
  Alert = Alert;
  submissionPending = false;
  submissionAvailable = false;
  preConditionCheckResult: PreConditionCheckResultDTO | undefined = undefined;
  preCheckPending = false;
  StatusEnum = ProjectPartnerReportSummaryDTO.StatusEnum;
  ReportUtil = ReportUtil;
  error$ = new BehaviorSubject<APIError | null>(null);

  data$: Observable<{
    projectId: number;
    projectReport: ProjectReportDTO;
    reportStatus: ProjectReportSummaryDTO.StatusEnum;
    userCanEditReport: boolean;
  }>;

  constructor(
    public pageStore: ProjectReportPageStore,
    public projectStore: ProjectStore,
    public detailPageStore: ProjectReportDetailPageStore,
    private projectSidenavService: ProjectApplicationFormSidenavService,
    private router: Router,
    private cd: ChangeDetectorRef,
  ) {
    this.data$ = combineLatest([
      projectStore.projectId$,
      detailPageStore.projectReport$,
      detailPageStore.reportStatus$,
      pageStore.userCanEditReport$,
    ]).pipe(
      map(([projectId, projectReport, reportStatus, userCanEditReport]) => ({
        projectId,
        projectReport,
        reportStatus,
        userCanEditReport,
      }))
    );
  }

  submitReport(projectId: number, reportId: number): void {
    this.submissionPending = true;
    this.detailPageStore.submitReport(projectId, reportId)
      .pipe(
        tap(() => this.redirectToReportOverview(projectId, reportId)),
        catchError((error) => this.showErrorMessage(error.error)),
        finalize(() => this.submissionPending = false)
      ).subscribe();
  }

  runPreCheckOnReport(projectId: number, reportId: number): void {
    this.preCheckPending = true;
    this.preConditionCheckResult = undefined;
    this.detailPageStore.runPreCheck(projectId, reportId)
      .pipe(
        tap(result => this.submissionAvailable = result.submissionAllowed),
        tap(result => this.preConditionCheckResult = result),
        catchError((error) => this.showErrorMessage(error.error)),
        finalize(() => {
          this.preCheckPending = false;
          this.cd.detectChanges();
        }),
      ).subscribe();
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

  private redirectToReportOverview(projectId: number, reportId: number): void {
    this.router.navigate([`/app/project/detail/${projectId}/projectReports/${reportId}`]);
  }
}
