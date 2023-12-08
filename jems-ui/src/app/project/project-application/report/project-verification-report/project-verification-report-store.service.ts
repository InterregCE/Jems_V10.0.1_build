import {Injectable} from '@angular/core';
import {combineLatest, Observable, Subject} from 'rxjs';
import {map, tap} from 'rxjs/operators';
import {
  ProjectReportDetailPageStore
} from '@project/project-application/report/project-report/project-report-detail-page/project-report-detail-page-store.service';
import {
  ProjectReportPageStore
} from '@project/project-application/report/project-report/project-report-page-store.service';
import {ProjectReportDTO, ProjectReportService, ProjectReportSummaryDTO, UserRoleDTO} from '@cat/api';
import {
  ProjectStore
} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {PermissionService} from '../../../../security/permissions/permission.service';
import PermissionsEnum = UserRoleDTO.PermissionsEnum;
import {Log} from '@common/utils/log';

@Injectable({providedIn: 'root'})
export class ProjectVerificationReportStore {

  projectReportVerificationEditable$: Observable<boolean>;
  hasMonitoringUserView$: Observable<boolean>;
  hasProjectCollaboratorView$: Observable<boolean>;
  hasReopenPermission$: Observable<boolean>;
  updatedReportStatus$ = new Subject<ProjectReportSummaryDTO.StatusEnum>();

  constructor(
    public reportDetailPageStore: ProjectReportDetailPageStore,
    private projectReportPageStore: ProjectReportPageStore,
    private permissionService: PermissionService,
    private projectReportService: ProjectReportService,
    private projectStore: ProjectStore,
  ) {
    this.projectReportVerificationEditable$ = this.projectReportVerificationEditable();
    this.hasMonitoringUserView$ = this.permissionService.hasPermission(PermissionsEnum.ProjectReportingVerificationProjectView);
    this.hasProjectCollaboratorView$ = this.hasProjectCollaboratorView();
    this.hasReopenPermission$ = this.permissionService.hasPermission(PermissionsEnum.ProjectReportingVerificationReOpen);
  }

  projectReportVerificationEditable(): Observable<boolean> {
    return combineLatest([
      this.projectReportPageStore.userCanEditVerification$,
      this.reportDetailPageStore.projectReport$
    ])
    .pipe(
      map(([canEdit, projectReport]) => canEdit && (projectReport.status == ProjectReportDTO.StatusEnum.InVerification || projectReport.status == ProjectReportDTO.StatusEnum.ReOpenFinalized))
    );
  }

  hasProjectCollaboratorView(): Observable<boolean>{
    return this.projectStore.userIsProjectOwner$
      .pipe(
        map(hasView => hasView)
      );
  }

  reopenVerificationReport(projectId: number, projectReportId: number): Observable<ProjectReportDTO.StatusEnum> {
    return this.projectReportService.reopenVerificationOnProjectReport(projectId, projectReportId)
      .pipe(
        map(status => status as ProjectReportDTO.StatusEnum),
        tap(status => this.updatedReportStatus$.next(status)),
        tap(status => Log.info('Changed status for report', projectReportId, status))
      );
  }

}
