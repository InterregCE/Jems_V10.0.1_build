import {ChangeDetectionStrategy, Component} from '@angular/core';
import {AcceptedFileTypesConstants} from '@project/common/components/file-management/accepted-file-types.constants';
import {
  JemsFileDTO,
  PageJemsFileDTO,
  ProjectPartnerReportService,
  ProjectPartnerReportSummaryDTO,
  UserRoleDTO,
} from '@cat/api';
import {combineLatest, Observable, Subject} from 'rxjs';
import {CategoryInfo} from '@project/common/components/category-tree/categoryModels';
import {finalize, map, switchMap, take} from 'rxjs/operators';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {Alert} from '@common/components/forms/alert';
import {
  ReportFileManagementStore
} from '@project/project-application/report/partner-report-detail-page/partner-report-annexes-tab/report-file-management-store';
import {FileListItem} from '@common/components/file-list/file-list-item';
import {FileDescriptionChange} from '@common/components/file-list/file-list-table/file-description-change';
import {
  PartnerReportDetailPageStore
} from '@project/project-application/report/partner-report-detail-page/partner-report-detail-page-store.service';
import {FileListComponent} from '@common/components/file-list/file-list.component';
import {PartnerReportPageStore} from '@project/project-application/report/partner-report-page-store.service';
import {PrivilegesPageStore} from '@project/project-application/privileges-page/privileges-page-store.service';
import {PermissionService} from '../../../../../../security/permissions/permission.service';
import PermissionsEnum = UserRoleDTO.PermissionsEnum;

@UntilDestroy()
@Component({
  selector: 'jems-report-annexes-table',
  templateUrl: './report-annexes-table.component.html',
  styleUrls: ['./report-annexes-table.component.scss'],
  providers: [PrivilegesPageStore],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ReportAnnexesTableComponent {

  private static SENSITIVE_FILE_NAME_MASK = '*********.***';

  Alert = Alert;
  PermissionsEnum = PermissionsEnum;

  acceptedFilesTypes = AcceptedFileTypesConstants.acceptedFilesTypes;
  maximumAllowedFileSizeInMB: number;
  fileSizeOverLimitError$ = new Subject<boolean>();
  isUploadInProgress = false;
  data$: Observable<{
    files: PageJemsFileDTO;
    fileList: FileListItem[];
    reportStatus: ProjectPartnerReportSummaryDTO.StatusEnum;
    selectedCategory: CategoryInfo | undefined;
    canUserEdit: boolean;
  }>;

  constructor(
    public fileManagementStore: ReportFileManagementStore,
    private projectPartnerReportService: ProjectPartnerReportService,
    private partnerReportDetailPageStore: PartnerReportDetailPageStore,
    private reportPageStore: PartnerReportPageStore,
    private privilegesPageStore: PrivilegesPageStore,
    public permissionService: PermissionService

  ) {
    this.data$ = combineLatest([
      this.fileManagementStore.reportFileList$,
      this.fileManagementStore.reportStatus$,
      this.fileManagementStore.selectedCategory$,
      this.reportPageStore.userCanEditReport$,
      this.privilegesPageStore.isCurrentUserGDPRCompliant$,
      this.permissionService.hasPermission(UserRoleDTO.PermissionsEnum.ProjectReportingEdit),
      this.permissionService.hasPermission(UserRoleDTO.PermissionsEnum.ProjectReportingView),
    ])
      .pipe(
        map(([files, reportStatus, selectedCategory, canEdit, userIsGdprCompliant, userIsMonitorEdit, userIsMonitorView]: any) =>  {
              return ({
                files,
                fileList: files.content.map((file: JemsFileDTO) => ({
                  id: file.id,
                  name: file.name,
                  type: file.type,
                  uploaded: file.uploaded,
                  author: file.author,
                  sizeString: file.sizeString,
                  description: file.description,
                  editable: this.isFileEditable(file.name, reportStatus, userIsGdprCompliant, userIsMonitorEdit, canEdit),
                  deletable: file.type === JemsFileDTO.TypeEnum.PartnerReport
                      && reportStatus === ProjectPartnerReportSummaryDTO.StatusEnum.Draft
                      && canEdit,
                  downloadable: this.isFileSensitive(file.name) ? (userIsGdprCompliant || userIsMonitorView): true,
                  tooltipIfNotDeletable: canEdit ? 'file.table.action.delete.disabled.for.tab.tooltip' : '',
                  iconIfNotDeletable: canEdit ? 'delete_forever' : ''
                })),
                reportStatus,
                selectedCategory,
                canUserEdit: canEdit,
              });
            }
        ),
      );
    this.fileManagementStore.getMaximumAllowedFileSize().pipe(untilDestroyed(this)).subscribe((maxAllowedSize) => this.maximumAllowedFileSizeInMB = maxAllowedSize);
  }

  uploadFile(target: any): void {
    this.isUploadInProgress = true;
    FileListComponent.doFileUploadWithValidation(
      target,
      this.fileSizeOverLimitError$,
      this.fileManagementStore.error$,
      this.maximumAllowedFileSizeInMB,
      file => this.fileManagementStore.uploadFile(file).pipe(
        finalize(() => this.isUploadInProgress = false)
      ),
    );
  }

  downloadFile(file: FileListItem): void {
    this.fileManagementStore.downloadFile(file.id)
      .pipe(take(1))
      .subscribe();
  }

  setDescriptionCallback = (data: FileDescriptionChange): Observable<any> => {
    return combineLatest([
      this.partnerReportDetailPageStore.partnerId$.pipe(map(id => Number(id))),
      this.partnerReportDetailPageStore.partnerReportId$.pipe(map(id => Number(id))),
    ]).pipe(
      switchMap(([partnerId, reportId]) =>
        this.projectPartnerReportService.updateReportFileDescription(data.id, partnerId, reportId, data.description)
      ),
    );
  };

  deleteCallback = (file: FileListItem): Observable<void> => {
    return combineLatest([
      this.partnerReportDetailPageStore.partnerId$.pipe(map(id => Number(id))),
      this.partnerReportDetailPageStore.partnerReportId$.pipe(map(id => Number(id))),
    ]).pipe(
      switchMap(([partnerId, reportId]) =>
        this.projectPartnerReportService.deleteReportFile(file.id, partnerId, reportId)
      ),
    );
  };

  isFileEditable(
      fileName: string,
      reportStatus: ProjectPartnerReportSummaryDTO.StatusEnum,
      userIsGdprCompliant: boolean,
      userIsMonitorEdit: boolean,
      canEdit: boolean) {

    return reportStatus === ProjectPartnerReportSummaryDTO.StatusEnum.Draft &&
        (this.isFileSensitive(fileName) ? (userIsGdprCompliant || userIsMonitorEdit) : canEdit);
  }

  isFileSensitive(fileName: string) {
    return fileName === ReportAnnexesTableComponent.SENSITIVE_FILE_NAME_MASK;
  }
}
