import {Component} from '@angular/core';
import {BehaviorSubject, combineLatest, Observable, of} from 'rxjs';
import {APIError} from '@common/models/APIError';
import {ActivatedRoute} from '@angular/router';
import {RoutingService} from '@common/services/routing.service';
import {catchError, filter, finalize, map, switchMap, take, tap} from 'rxjs/operators';
import {ProjectReportDTO} from '@cat/api';
import {
  ProjectReportDetailPageStore
} from '@project/project-application/report/project-report/project-report-detail-page/project-report-detail-page-store.service';
import {Alert} from '@common/components/forms/alert';
import {
  ProjectReportPageStore
} from '@project/project-application/report/project-report/project-report-page-store.service';
import {
  ProjectVerificationReportStore
} from '@project/project-application/report/project-verification-report/project-verification-report-store.service';
import {Forms} from '@common/utils/forms';
import {MatDialog} from '@angular/material/dialog';

@Component({
  selector: 'jems-project-verification-report',
  templateUrl: './project-verification-report.component.html',
  styleUrls: ['./project-verification-report.component.scss']
})
export class ProjectVerificationReportComponent {

  Alert = Alert;
  data$: Observable<{
    projectReport: ProjectReportDTO;
    isVisibleForMonitoringUser: boolean;
    isVisibleForApplicantUser: boolean;
    hasReopenPermission: boolean;
  }>;
  error$ = new BehaviorSubject<APIError | null>(null);
  StatusEnum = ProjectReportDTO.StatusEnum;
  actionPending = false;

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: RoutingService,
    private projectReportDetailStore: ProjectReportDetailPageStore,
    private reportPageStore: ProjectReportPageStore,
    private projectVerificationReportStore: ProjectVerificationReportStore,
    private dialog: MatDialog,
  ) {
    this.data$ = combineLatest([
      this.projectReportDetailStore.projectReport$,
      this.projectVerificationReportStore.hasMonitoringUserView$,
      this.projectVerificationReportStore.hasProjectCollaboratorView$,
      this.projectVerificationReportStore.hasReopenPermission$
    ]).pipe(
      map(([projectReport, hasMonitoringUserView, hasProjectCollaboratorView, hasReopenPermission]) => ({
        projectReport,
        isVisibleForMonitoringUser: hasMonitoringUserView,
        isVisibleForApplicantUser: hasProjectCollaboratorView,
        hasReopenPermission
      }))
    );
  }

  activeTab(route: string): boolean {
    return this.router.url?.includes(route);
  }

  routeTo(route: string): void {
    this.router.navigate([route], {relativeTo: this.activatedRoute, queryParamsHandling: 'merge'});
  }

  public redirectToReport(reportId: number): void {
    this.router.navigate([`../../${reportId}/identification`], {
      relativeTo: this.activatedRoute,
      queryParamsHandling: 'merge'
    });
  }

  isFinance(type: ProjectReportDTO.TypeEnum) {
    return [ProjectReportDTO.TypeEnum.Finance, ProjectReportDTO.TypeEnum.Both].includes(type);
  }

  private showErrorMessage(error: APIError): Observable<null> {
    this.error$.next(error);
    setTimeout(() => {
      this.error$.next(null);
    }, 4000);
    return of(null);
  }

  private redirectToReportList(): void {
    this.router.navigate(['../..'], {relativeTo: this.activatedRoute});
  }

  reopenVerificationReport(projectId: number, projectReportId: number){
    Forms.confirm(
      this.dialog,
      {
        title: 'project.application.project.report.verification.reopen',
        message: {i18nKey: 'project.application.project.report.verification.reopen.confirm.message'}
      }).pipe(
      take(1),
      filter(confirmed => confirmed),
      switchMap(() => {
        this.actionPending = true;
        return this.projectVerificationReportStore.reopenVerificationReport(projectId, projectReportId).pipe(
          tap(() => this.redirectToReportList()),
          catchError((error) => this.showErrorMessage(error.error)),
          finalize(() => this.actionPending = false)
        );
      }),
    ).subscribe();
  }
}
