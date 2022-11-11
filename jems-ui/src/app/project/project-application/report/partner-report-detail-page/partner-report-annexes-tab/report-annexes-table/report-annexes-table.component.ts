import {ChangeDetectionStrategy, Component} from '@angular/core';
import {AcceptedFileTypesConstants} from '@project/common/components/file-management/accepted-file-types.constants';
import {
  AuthenticationService,
  OutputCurrentUser,
  PageProjectReportFileDTO,
  PartnerUserCollaboratorDTO,
  ProjectPartnerReportService,
  ProjectPartnerReportSummaryDTO,
  ProjectReportFileDTO,
  UserRoleDTO,
} from '@cat/api';
import {combineLatest, Observable, Subject} from 'rxjs';
import {CategoryInfo} from '@project/common/components/category-tree/categoryModels';
import {map, switchMap, take} from 'rxjs/operators';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {Alert} from '@common/components/forms/alert';
import {Tables} from '@common/utils/tables';
import {
  ReportFileManagementStore
} from '@project/project-application/report/partner-report-detail-page/partner-report-annexes-tab/report-file-management-store';
import {FileListItem} from '@common/components/file-list/file-list-item';
import {FileDescriptionChange} from '@common/components/file-list/file-list-table/file-description-change';
import {
  PartnerReportDetailPageStore
} from '@project/project-application/report/partner-report-detail-page/partner-report-detail-page-store.service';
import {FileListComponent} from '@common/components/file-list/file-list.component';
import {PermissionService} from '../../../../../../security/permissions/permission.service';
import {PrivilegesPageStore} from '@project/project-application/privileges-page/privileges-page-store.service';
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

  Alert = Alert;
  Tables = Tables;
  PermissionsEnum = PermissionsEnum;

  acceptedFilesTypes = AcceptedFileTypesConstants.acceptedFilesTypes;
  maximumAllowedFileSizeInMB: number;
  fileSizeOverLimitError$ = new Subject<boolean>();
  isPartnerCollaborator: boolean;
  hasPermission: boolean;

  data$: Observable<{
    files: PageProjectReportFileDTO;
    fileList: FileListItem[];
    reportStatus: ProjectPartnerReportSummaryDTO.StatusEnum;
    selectedCategory: CategoryInfo | undefined;
  }>;

  constructor(
    public fileManagementStore: ReportFileManagementStore,
    private projectPartnerReportService: ProjectPartnerReportService,
    private partnerReportDetailPageStore: PartnerReportDetailPageStore,
    private permissionService: PermissionService,
    private pageStore: PrivilegesPageStore,
    private authenticationService: AuthenticationService
  ) {
    this.permissionService.hasPermission(PermissionsEnum.ProjectReportingEdit).pipe().subscribe(hasPermission => this.hasPermission = hasPermission);
    this.data$ = combineLatest([
      this.fileManagementStore.reportFileList$,
      this.fileManagementStore.reportStatus$,
      this.fileManagementStore.selectedCategory$,
      this.pageStore.partnerCollaborators$,
      this.authenticationService.getCurrentUser()
    ])
      .pipe(
        map(([files, reportStatus, selectedCategory, partnerCollaborators, currentUser]) => ({
          files,
          fileList: files.content.map((file: ProjectReportFileDTO) => ({
            id: file.id,
            name: file.name,
            type: file.type,
            uploaded: file.uploaded,
            author: file.author,
            sizeString: file.sizeString,
            description: file.description,
            editable: reportStatus === ProjectPartnerReportSummaryDTO.StatusEnum.Draft && (this.hasPermission || this.isPartnerCollaboratorWithEdit(partnerCollaborators.values(), currentUser)),
            deletable: file.type === ProjectReportFileDTO.TypeEnum.PartnerReport && reportStatus === ProjectPartnerReportSummaryDTO.StatusEnum.Draft && (this.hasPermission || this.isPartnerCollaboratorWithEdit(partnerCollaborators.values(), currentUser)),
            tooltipIfNotDeletable: 'file.table.action.delete.disabled.for.tab.tooltip',
            iconIfNotDeletable: 'delete_forever',
          })),
          reportStatus,
          selectedCategory
        })),
      );
    this.fileManagementStore.getMaximumAllowedFileSize().pipe(untilDestroyed(this)).subscribe((maxAllowedSize) => this.maximumAllowedFileSizeInMB = maxAllowedSize);
  }

  private isPartnerCollaboratorWithEdit(partnerCollaborators:  IterableIterator<PartnerUserCollaboratorDTO[]>, currentUser: OutputCurrentUser): boolean {
    let hasPermission = false;
    for (const partnerCollaboratorList of partnerCollaborators) {
      Array.from(partnerCollaboratorList.values()).forEach(
          partnerCollaborator => hasPermission = partnerCollaborator.level === 'EDIT'
          && partnerCollaborator.userId === currentUser.id);
          if (hasPermission) {
            this.isPartnerCollaborator = true;
            return true;
          }
    }

    this.isPartnerCollaborator = false;
    return false;
  }

  uploadFile(target: any): void {
    FileListComponent.doFileUploadWithValidation(
      target,
      this.fileSizeOverLimitError$,
      this.fileManagementStore.error$,
      this.maximumAllowedFileSizeInMB,
      file => this.fileManagementStore.uploadFile(file),
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
}
