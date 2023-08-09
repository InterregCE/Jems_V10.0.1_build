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
  hasProjectOwnerOrCollaboratorView$: Observable<boolean>;

  constructor(
    public reportDetailPageStore: ProjectReportDetailPageStore,
    private projectReportPageStore: ProjectReportPageStore,
    private projectStore: ProjectStore,
    private permissionService: PermissionService
  ) {
    this.projectReportVerificationEditable$ = this.projectReportVerificationEditable();
    this.hasMonitoringUserView$ = this.permissionService.hasPermission(PermissionsEnum.ProjectReportingVerificationProjectView);
    this.hasProjectOwnerOrCollaboratorView$ = this.hasProjectOwnerOrCollaboratorView();
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

  hasProjectOwnerOrCollaboratorView(): Observable<boolean> {
    return combineLatest([
      this.projectStore.userIsProjectOwner$,
      this.projectStore.userIsPartnerCollaborator$
    ])
      .pipe(
        map(([userIsProjectOwner, userIsPartnerCollaborator]) => userIsProjectOwner || userIsPartnerCollaborator)
      );
  }

}
