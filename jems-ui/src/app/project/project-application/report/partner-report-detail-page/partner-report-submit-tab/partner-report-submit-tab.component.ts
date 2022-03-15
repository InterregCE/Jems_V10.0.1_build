import {ChangeDetectionStrategy, Component} from '@angular/core';
import {PartnerReportDetailPageStore} from '@project/project-application/report/partner-report-detail-page/partner-report-detail-page-store.service';
import {ProjectApplicationFormSidenavService} from '@project/project-application/containers/project-application-form-page/services/project-application-form-sidenav.service';
import {Alert} from '@common/components/forms/alert';
import {BehaviorSubject, combineLatest, Observable, of} from 'rxjs';
import {APIError} from '@common/models/APIError';
import {catchError, finalize, map, tap} from 'rxjs/operators';
import {Router} from '@angular/router';
import {ProjectStore} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {ProjectPartnerReportDTO, ProjectPartnerReportSummaryDTO, ProjectPartnerSummaryDTO, UserRoleDTO} from '@cat/api';
import PermissionsEnum = UserRoleDTO.PermissionsEnum;
import {PartnerReportPageStore} from '@project/project-application/report/partner-report-page-store.service';

@Component({
  selector: 'jems-partner-report-submit-tab',
  templateUrl: './partner-report-submit-tab.component.html',
  styleUrls: ['./partner-report-submit-tab.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PartnerReportSubmitTabComponent {
  PermissionsEnum = PermissionsEnum;
  Alert = Alert;
  actionPending = false;
  StatusEnum = ProjectPartnerReportSummaryDTO.StatusEnum;

  error$ = new BehaviorSubject<APIError | null>(null);

  data$: Observable<{
    projectId: number;
    partnerSummary: ProjectPartnerSummaryDTO;
    partnerReport: ProjectPartnerReportDTO;
    reportStatus: ProjectPartnerReportSummaryDTO.StatusEnum;
    userCanEditReport: boolean;
  }>;

  constructor(public pageStore: PartnerReportPageStore,
              public projectStore: ProjectStore,
              public detailPageStore: PartnerReportDetailPageStore,
              private projectSidenavService: ProjectApplicationFormSidenavService,
              private router: Router) {
    this.data$ = combineLatest([
      projectStore.projectId$,
      pageStore.partnerSummary$,
      detailPageStore.partnerReport$,
      detailPageStore.reportStatus$,
      pageStore.userCanEditReports$,
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

  submitReport(projectId: number, partnerId: number, reportId: number): void {
    this.actionPending = true;
    this.detailPageStore.submitReport(partnerId, reportId)
      .pipe(
        tap(() => this.redirectToReportOverview(projectId, partnerId, reportId)),
        catchError((error) => this.showErrorMessage(error.error)),
        finalize(() => this.actionPending = false)
      ).subscribe();
  }

  private showErrorMessage(error: APIError): Observable<null> {
    this.error$.next(error);
    setTimeout(() => {
      this.error$.next(null);
    },         4000);
    return of(null);
  }

  private redirectToReportOverview(projectId: number, partnerId: number, reportId: number): void {
    this.router.navigate([`/app/project/detail/${projectId}/reporting/${partnerId}/reports/${reportId}`]);
  }
}
