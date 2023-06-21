import {ChangeDetectionStrategy, ChangeDetectorRef, Component} from '@angular/core';
import {PartnerReportDetailPageStore} from '@project/project-application/report/partner-report-detail-page/partner-report-detail-page-store.service';
import {ProjectApplicationFormSidenavService} from '@project/project-application/containers/project-application-form-page/services/project-application-form-sidenav.service';
import {Alert} from '@common/components/forms/alert';
import {BehaviorSubject, combineLatest, Observable, of} from 'rxjs';
import {APIError} from '@common/models/APIError';
import {catchError, finalize, map, tap} from 'rxjs/operators';
import {Router} from '@angular/router';
import {ProjectStore} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {
  PreConditionCheckResultDTO,
  ProjectPartnerReportDTO,
  ProjectPartnerReportSummaryDTO,
  ProjectPartnerSummaryDTO,
  UserRoleDTO
} from '@cat/api';
import PermissionsEnum = UserRoleDTO.PermissionsEnum;
import {PartnerReportPageStore} from '@project/project-application/report/partner-report-page-store.service';
import {FormService} from '@common/components/section/form/form.service';
import {ReportUtil} from '@project/common/report-util';

@Component({
  selector: 'jems-partner-report-submit-tab',
  templateUrl: './partner-report-submit-tab.component.html',
  styleUrls: ['./partner-report-submit-tab.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [FormService],
})
export class PartnerReportSubmitTabComponent {
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
    partnerSummary: ProjectPartnerSummaryDTO;
    partnerReport: ProjectPartnerReportDTO;
    reportStatus: ProjectPartnerReportSummaryDTO.StatusEnum;
    userCanEditReport: boolean;
  }>;

  constructor(
    public pageStore: PartnerReportPageStore,
    public projectStore: ProjectStore,
    public detailPageStore: PartnerReportDetailPageStore,
    private projectSidenavService: ProjectApplicationFormSidenavService,
    private router: Router,
    private cd: ChangeDetectorRef,
  ) {
    this.data$ = combineLatest([
      projectStore.projectId$,
      pageStore.partnerSummary$,
      detailPageStore.partnerReport$,
      detailPageStore.reportStatus$,
      pageStore.userCanEditReport$,
    ]).pipe(
      map(([projectId, partnerSummary, partnerReport, reportStatus, userCanEditReport]) => ({
        projectId,
        partnerSummary,
        partnerReport,
        reportStatus,
        userCanEditReport,
      }))
    );
  }

  runPreCheckOnReport(partnerId: number, reportId: number): void {
    this.preCheckPending = true;
    this.preConditionCheckResult = undefined;
    this.detailPageStore.runPreCheck(partnerId, reportId)
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

  submitReport(projectId: number, partnerId: number, reportId: number): void {
    this.submissionPending = true;
    this.detailPageStore.submitReport(partnerId, reportId)
      .pipe(
        tap(() => this.redirectToReportOverview(projectId, partnerId, reportId)),
        catchError((error) => this.showErrorMessage(error.error)),
        finalize(() => this.submissionPending = false)
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

  private redirectToReportOverview(projectId: number, partnerId: number, reportId: number): void {
    this.router.navigate([`/app/project/detail/${projectId}/reporting/${partnerId}/reports/${reportId}`]);
  }
}
