import {ChangeDetectionStrategy, Component} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {RoutingService} from '@common/services/routing.service';
import {
  ProjectApplicationFormSidenavService
} from '@project/project-application/containers/project-application-form-page/services/project-application-form-sidenav.service';
import {
  ProjectReportDetailPageStore
} from '@project/project-application/report/project-report/project-report-detail-page/project-report-detail-page-store.service';
import {ProjectReportDTO, UserRoleCreateDTO} from '@cat/api';
import {Alert} from '@common/components/forms/alert';
import {BehaviorSubject, combineLatest, Observable, of} from 'rxjs';
import {APIError} from '@common/models/APIError';
import {
  ProjectReportPageStore
} from '@project/project-application/report/project-report/project-report-page-store.service';
import {catchError, filter, finalize, map, switchMap, take} from 'rxjs/operators';
import {PermissionService} from '../../../../../security/permissions/permission.service';
import PermissionsEnum = UserRoleCreateDTO.PermissionsEnum;
import {Forms} from '@common/utils/forms';
import {MatDialog} from '@angular/material/dialog';
import {ReportUtil} from '@project/common/report-util';

@Component({
  selector: 'jems-project-report-detail-page',
  templateUrl: './project-report-detail-page.component.html',
  styleUrls: ['./project-report-detail-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectReportDetailPageComponent {

  data$: Observable<{
    projectReport: ProjectReportDTO;
    hasReopenPermission: boolean;
  }>;

  ReportUtil = ReportUtil;
  ProjectReportDTO = ProjectReportDTO;
  TypeEnum = ProjectReportDTO.TypeEnum;
  StatusEnum = ProjectReportDTO.StatusEnum;
  error$ = new BehaviorSubject<APIError | null>(null);
  Alert = Alert;
  PermissionsEnum = PermissionsEnum;
  actionPending = false;

  constructor(private activatedRoute: ActivatedRoute,
              private pageStore: ProjectReportDetailPageStore,
              private router: RoutingService,
              private projectSidenavService: ProjectApplicationFormSidenavService,
              public projectReportPageStore: ProjectReportPageStore,
              private permissionService: PermissionService,
              private dialog: MatDialog) {

    this.data$ = combineLatest([
      this.pageStore.projectReport$,
      this.permissionService.hasPermission(PermissionsEnum.ProjectReportingProjectReOpen),
    ])
      .pipe(
        map(([projectReport, hasReopenPermission]) => ({
          projectReport,
          hasReopenPermission,
        })),
      );
  }

  activeTab(route: string): boolean {
    return this.router.url?.includes(route);
  }

  routeTo(route: string): void {
    this.router.navigate([route], {relativeTo: this.activatedRoute, queryParamsHandling: 'merge'});
  }

  public showErrorMessage(error: APIError): Observable<null> {
    this.error$.next(error);
    setTimeout(() => {
      this.error$.next(null);
    }, 4000);
    return of(null);
  }

  reopenReport(projectId: number, projectReportId: number, projectReportNumber: number) {
    Forms.confirm(
      this.dialog,
      {
        title: 'project.application.project.report.reopen',
        message: {i18nKey: 'project.application.project.report.reopen.confirm.message', i18nArguments: {number: projectReportNumber.toString()}}
      }).pipe(
      take(1),
      filter(confirmed => confirmed),
      switchMap(() => {
        this.actionPending = true;
        return this.pageStore.reopenReport(projectId, projectReportId).pipe(
          catchError((error) => this.showErrorMessage(error.error)),
          finalize(() => this.actionPending = false)
        );
      }),
    ).subscribe();
  }
}
