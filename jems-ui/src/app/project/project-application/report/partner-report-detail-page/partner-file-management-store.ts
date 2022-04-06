import {Injectable} from '@angular/core';
import {Observable, of, Subject} from 'rxjs';
import {
  ProjectPartnerReportService,
  ProjectPartnerReportWorkPlanService,
  ProjectStatusDTO, SettingsService,
  UserRoleDTO
} from '@cat/api';
import {
  ProjectStore
} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {switchMap, take} from 'rxjs/operators';
import {MatSort} from '@angular/material/sort';
import {APIError} from '@common/models/APIError';
import {PermissionService} from '../../../../security/permissions/permission.service';
import {
  ProjectPartnerStore
} from '@project/project-application/containers/project-application-form-page/services/project-partner-store.service';
import {FormVisibilityStatusService} from '@project/common/services/form-visibility-status.service';
import PermissionsEnum = UserRoleDTO.PermissionsEnum;
import {DownloadService} from '@common/services/download.service';
import {
  PartnerReportDetailPageStore
} from '@project/project-application/report/partner-report-detail-page/partner-report-detail-page-store.service';

@Injectable({
  providedIn: 'root'
})
export class PartnerFileManagementStore {

  projectStatus$: Observable<ProjectStatusDTO>;
  userIsProjectOwnerOrEditCollaborator$: Observable<boolean>;
  canChangeAssessmentFile$: Observable<boolean>;
  canChangeApplicationFile$: Observable<boolean>;
  canReadAssessmentFile$: Observable<boolean>;

  deleteSuccess$ = new Subject<boolean>();
  error$ = new Subject<APIError | null>();

  newPageSize$ = new Subject<number>();
  newPageIndex$ = new Subject<number>();
  newSort$ = new Subject<Partial<MatSort>>();

  constructor(private projectPartnerReportWorkPlanService: ProjectPartnerReportWorkPlanService,
              private settingsService: SettingsService,
              private projectStore: ProjectStore,
              private projectPartnerStore: ProjectPartnerStore,
              private permissionService: PermissionService,
              private visibilityStatusService: FormVisibilityStatusService,
              private downloadService: DownloadService,
              private projectPartnerReportService: ProjectPartnerReportService,
              private partnerReportDetailPageStore: PartnerReportDetailPageStore
  ) {
    this.projectStatus$ = this.projectStore.projectStatus$;
    this.userIsProjectOwnerOrEditCollaborator$ = this.projectStore.userIsProjectOwnerOrEditCollaborator$;
    this.canChangeAssessmentFile$ = this.permissionService.hasPermission(PermissionsEnum.ProjectFileAssessmentUpdate);
    this.canChangeApplicationFile$ = this.permissionService.hasPermission(PermissionsEnum.ProjectFileApplicationUpdate);
    this.canReadAssessmentFile$ = this.permissionService.hasPermission(PermissionsEnum.ProjectFileAssessmentRetrieve);
  }

  deleteFile(fileId: number): Observable<any> {
    return this.partnerReportDetailPageStore.partnerId$
      .pipe(
        take(1),
        switchMap(partnerId => this.projectPartnerReportService.deleteAttachment(fileId, partnerId as number))
      );
  }

  downloadFile(fileId: number): Observable<any> {
    return this.partnerReportDetailPageStore.partnerId$
      .pipe(
        take(1),
        switchMap(partnerId => {
          this.downloadService.download(`/api/project/report/partner/byPartnerId/${partnerId}/${fileId}`, 'partner-report');
          return of(null);
        })
      );
  }
}
