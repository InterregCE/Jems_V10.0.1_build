import {Component, EventEmitter, Input, Output} from '@angular/core';
import {APIError} from '@common/models/APIError';
import {ProjectReportSummaryDTO} from '@cat/api';
import {BehaviorSubject, combineLatest, Observable, of} from 'rxjs';
import {RoutingService} from '@common/services/routing.service';
import {ActivatedRoute} from '@angular/router';
import {
  ProjectReportDetailPageStore
} from '@project/project-application/report/project-report/project-report-detail-page/project-report-detail-page-store.service';
import {catchError, finalize, map, take, tap} from 'rxjs/operators';
import {MatDialog} from '@angular/material/dialog';
import {Forms} from '@common/utils/forms';
import {ProjectStore} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {ProjectReportPageStore} from '@project/project-application/report/project-report/project-report-page-store.service';
import {ReportUtil} from '../../../common/report-util';

@Component({
  selector: 'jems-start-verification-report',
  templateUrl: './start-verification-report.component.html',
  styleUrls: ['./start-verification-report.component.scss']
})
export class StartVerificationReportComponent {
  ReportUtil = ReportUtil;
  ProjectReportSummaryDTO = ProjectReportSummaryDTO;

  @Input()
  showStart = false;
  @Input()
  reportId: number;
  @Input()
  reportStatus: ProjectReportSummaryDTO.StatusEnum;
  @Output()
  onError = new EventEmitter<APIError>();

  pendingAction$ = new BehaviorSubject(false);
  data$: Observable<{
    projectId: number;
    canView: boolean;
    canEdit: boolean;
  }>;

  constructor(
    private projectStore: ProjectStore,
    private projectReportStore: ProjectReportPageStore,
    private projectReportDetailStore: ProjectReportDetailPageStore,
    private router: RoutingService,
    private activatedRoute: ActivatedRoute,
    private dialog: MatDialog,
  ) {
    this.data$ = combineLatest([
      this.projectStore.projectId$,
      this.projectReportStore.userCanViewReport$,
      this.projectReportStore.userCanViewVerification$,
      this.projectReportStore.userCanEditVerification$,
    ]).pipe(
      map(([projectId, canViewProjectReports, canViewVerification, canEditVerification]) => ({
        projectId,
        canView: canViewProjectReports || canViewVerification,
        canEdit: canEditVerification,
      }))
    );
  }

  redirectToVerification(reportId: number) {
    this.router.navigate([`../${reportId}/verificationReport/document`], {
      relativeTo: this.activatedRoute,
      queryParamsHandling: 'merge'
    });
  }

  startControlWork(projectId: number, reportId: number, canEdit: boolean) {
    if (!canEdit) {
      return;
    }

    this.pendingAction$.next(true);
    Forms.confirm(
      this.dialog,
      {
        title: 'project.application.project.verification.work.confirm.control.start.header',
        message: {
          i18nKey: 'project.application.project.verification.work.confirm.control.start.message'
        }
      }).pipe(
      take(1),
      tap((answer) => {
        if (answer) {
          this.changeStatusOfProjectReport(projectId, reportId);
        } else {
          this.pendingAction$.next(false);
        }
      })
    ).subscribe();
  }

  private changeStatusOfProjectReport(projectId: number, reportId: number) {
    if (!projectId || !reportId) {
      return;
    }

    this.projectReportDetailStore.startVerificationWork(projectId, reportId)
      .pipe(
        take(1),
        tap(() => this.redirectToVerification(reportId)),
        catchError((err) => {
          this.onError.emit(err.error);
          return of(null);
        }),
        finalize(() => this.pendingAction$.next(false))
      ).subscribe();
  }

}
