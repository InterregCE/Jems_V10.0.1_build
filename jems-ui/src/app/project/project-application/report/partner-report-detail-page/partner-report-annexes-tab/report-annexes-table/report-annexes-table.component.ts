import {ChangeDetectionStrategy, Component} from '@angular/core';
import {AcceptedFileTypesConstants} from '@project/common/components/file-management/accepted-file-types.constants';
import {
  PageProjectReportFileDTO,
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
import PermissionsEnum = UserRoleDTO.PermissionsEnum;

@UntilDestroy()
@Component({
  selector: 'jems-report-annexes-table',
  templateUrl: './report-annexes-table.component.html',
  styleUrls: ['./report-annexes-table.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ReportAnnexesTableComponent {

  Alert = Alert;
  PermissionsEnum = PermissionsEnum;

  acceptedFilesTypes = AcceptedFileTypesConstants.acceptedFilesTypes;
  maximumAllowedFileSizeInMB: number;
  fileSizeOverLimitError$ = new Subject<boolean>();

  data$: Observable<{
    files: PageProjectReportFileDTO;
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
  ) {
    this.data$ = combineLatest([
      this.fileManagementStore.reportFileList$,
      this.fileManagementStore.reportStatus$,
      this.fileManagementStore.selectedCategory$,
      this.reportPageStore.userCanEditReport$,
    ])
      .pipe(
        map(([files, reportStatus, selectedCategory, canEdit]) => ({
          files,
          fileList: files.content.map((file: ProjectReportFileDTO) => ({
            id: file.id,
            name: file.name,
            type: file.type,
            uploaded: file.uploaded,
            author: file.author,
            sizeString: file.sizeString,
            description: file.description,
            editable: reportStatus === ProjectPartnerReportSummaryDTO.StatusEnum.Draft && canEdit,
            deletable: file.type === ProjectReportFileDTO.TypeEnum.PartnerReport
              && reportStatus === ProjectPartnerReportSummaryDTO.StatusEnum.Draft
              && canEdit,
            tooltipIfNotDeletable: 'file.table.action.delete.disabled.for.tab.tooltip',
            iconIfNotDeletable: 'delete_forever',
          })),
          reportStatus,
          selectedCategory,
          canUserEdit: canEdit,
        })),
      );
    this.fileManagementStore.getMaximumAllowedFileSize().pipe(untilDestroyed(this)).subscribe((maxAllowedSize) => this.maximumAllowedFileSizeInMB = maxAllowedSize);
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
