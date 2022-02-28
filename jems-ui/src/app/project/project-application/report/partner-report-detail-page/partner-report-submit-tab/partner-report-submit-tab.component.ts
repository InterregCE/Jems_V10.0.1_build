import {ChangeDetectionStrategy, Component} from '@angular/core';
import {
  PartnerReportDetailPageStore
} from '@project/project-application/report/partner-report-detail-page/partner-report-detail-page-store.service';
import {
  ProjectApplicationFormSidenavService
} from '@project/project-application/containers/project-application-form-page/services/project-application-form-sidenav.service';
import { Alert } from '@common/components/forms/alert';
import {BehaviorSubject, combineLatest, Observable, of} from 'rxjs';
import {APIError} from '@common/models/APIError';
import {catchError, finalize, map, tap} from 'rxjs/operators';
import {Router} from '@angular/router';
import {
  ProjectStore
} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {ProjectPartnerReportDTO, ProjectPartnerSummaryDTO} from '@cat/api';

@Component({
  selector: 'jems-partner-report-submit-tab',
  templateUrl: './partner-report-submit-tab.component.html',
  styleUrls: ['./partner-report-submit-tab.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PartnerReportSubmitTabComponent {
  Alert = Alert;
  actionPending = false;

  error$ = new BehaviorSubject<APIError | null>(null);

  data$: Observable<{
    projectId: number,
    partnerSummary: ProjectPartnerSummaryDTO,
    partnerReport: ProjectPartnerReportDTO
    }>;

  constructor(public pageStore: PartnerReportDetailPageStore,
              public projectStore: ProjectStore,
              public partnerReportDetailPageStore: PartnerReportDetailPageStore,
              private projectSidenavService: ProjectApplicationFormSidenavService,
              private router: Router) {
    this.data$ = combineLatest([
      projectStore.projectId$,
      pageStore.partnerSummary$,
      pageStore.partnerReport$
    ]).pipe(
      map(([projectId, partnerSummary, partnerReport]) => ({
        projectId,
        partnerSummary,
        partnerReport
      }))
    )
  }

  submitReport(projectId: number, partnerId: number, reportId: number): void {
    this.actionPending = true;
    this.partnerReportDetailPageStore.submitReport(partnerId, reportId)
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
