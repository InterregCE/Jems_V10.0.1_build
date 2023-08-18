import {Injectable} from '@angular/core';
import {combineLatest, Observable} from 'rxjs';
import {map} from 'rxjs/operators';
import {
  ProjectReportDetailPageStore
} from '@project/project-application/report/project-report/project-report-detail-page/project-report-detail-page-store.service';
import {
  ProjectReportPageStore
} from '@project/project-application/report/project-report/project-report-page-store.service';
import {ProjectReportDTO, UserRoleDTO} from '@cat/api';
import {
  ProjectStore
} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {PermissionService} from '../../../../security/permissions/permission.service';
import PermissionsEnum = UserRoleDTO.PermissionsEnum;

@Injectable({providedIn: 'root'})
export class ProjectVerificationReportStore {

  projectReportVerificationEditable$: Observable<boolean>;
  hasMonitoringUserView$: Observable<boolean>;
  hasProjectCollaboratorView$: Observable<boolean>;

  constructor(
    public reportDetailPageStore: ProjectReportDetailPageStore,
    private projectReportPageStore: ProjectReportPageStore,
    private permissionService: PermissionService,
    private projectStore: ProjectStore,
  ) {
    this.projectReportVerificationEditable$ = this.projectReportVerificationEditable();
    this.hasMonitoringUserView$ = this.permissionService.hasPermission(PermissionsEnum.ProjectReportingVerificationProjectView);
    this.hasProjectCollaboratorView$ = this.hasProjectCollaboratorView();
  }

  projectReportVerificationEditable(): Observable<boolean> {
    return combineLatest([
      this.projectReportPageStore.userCanEditVerification$,
      this.reportDetailPageStore.projectReport$
    ])
    .pipe(
      map(([canEdit, projectReport]) => canEdit && (projectReport.status == ProjectReportDTO.StatusEnum.InVerification))
    );
  }

  hasProjectCollaboratorView(): Observable<boolean>{
    return this.projectStore.userIsProjectOwner$
      .pipe(
        map(hasView => hasView)
      );
  }

}
