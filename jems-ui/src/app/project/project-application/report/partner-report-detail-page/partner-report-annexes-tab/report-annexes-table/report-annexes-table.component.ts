import {ChangeDetectionStrategy, Component} from '@angular/core';
import {AcceptedFileTypesConstants} from '@project/common/components/file-management/accepted-file-types.constants';
import {
  PageProjectReportFileDTO, ProjectPartnerReportService,
  ProjectPartnerReportSummaryDTO,
  ProjectReportFileDTO,
  UserRoleDTO,
} from '@cat/api';
import {BehaviorSubject, combineLatest, Observable, Subject} from 'rxjs';
import {CategoryInfo} from '@project/common/components/category-tree/categoryModels';
import {finalize, map, switchMap, take, tap} from 'rxjs/operators';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import { Alert } from '@common/components/forms/alert';
import { Tables } from '@common/utils/tables';
import {
  ReportFileManagementStore
} from '@project/project-application/report/partner-report-detail-page/partner-report-annexes-tab/report-file-management-store';
import PermissionsEnum = UserRoleDTO.PermissionsEnum;
import {FileListItem} from '@common/components/file-list/file-list-item';
import {FileDescriptionChange} from '@common/components/file-list/file-list-table/file-description-change';
import {
  PartnerReportDetailPageStore
} from '@project/project-application/report/partner-report-detail-page/partner-report-detail-page-store.service';
import {FileListComponent} from '@common/components/file-list/file-list.component';

@UntilDestroy()
@Component({
  selector: 'jems-report-annexes-table',
  templateUrl: './report-annexes-table.component.html',
  styleUrls: ['./report-annexes-table.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ReportAnnexesTableComponent {

  Alert = Alert;
  Tables = Tables;
  PermissionsEnum = PermissionsEnum;

  acceptedFilesTypes = AcceptedFileTypesConstants.acceptedFilesTypes;
  maximumAllowedFileSizeInMB: number;
  fileSizeOverLimitError$ = new Subject<boolean>();

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
  ) {
    this.data$ = combineLatest([
      this.fileManagementStore.reportFileList$,
      this.fileManagementStore.reportStatus$,
      this.fileManagementStore.selectedCategory$
    ])
      .pipe(
        map(([files, reportStatus, selectedCategory]) => ({
          files,
          fileList: files.content.map((file: ProjectReportFileDTO) => ({
            id: file.id,
            name: file.name,
            type: file.type,
            uploaded: file.uploaded,
            author: file.author,
            sizeString: file.sizeString,
            description: file.description,
            editable: reportStatus === ProjectPartnerReportSummaryDTO.StatusEnum.Draft,
            deletable: file.type === ProjectReportFileDTO.TypeEnum.PartnerReport && reportStatus === ProjectPartnerReportSummaryDTO.StatusEnum.Draft,
            tooltipIfNotDeletable: 'file.table.action.delete.disabled.for.tab.tooltip',
            iconIfNotDeletable: 'delete_forever',
          })),
          reportStatus,
          selectedCategory
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

  deleteFile(file: FileListItem): void {
    this.fileManagementStore.deleteFile(file.id).pipe(take(1)).subscribe();
  }

  savingDescriptionId$ = new BehaviorSubject<number | null>(null);
  updateDescription(data: FileDescriptionChange) {
    return combineLatest([
      this.partnerReportDetailPageStore.partnerId$.pipe(map(id => Number(id))),
      this.partnerReportDetailPageStore.partnerReportId$.pipe(map(id => Number(id))),
    ]).pipe(
      take(1),
      tap(() => this.savingDescriptionId$.next(data.id)),
      switchMap(([partnerId, reportId]) =>
        this.projectPartnerReportService.updateDescription(data.id, partnerId, reportId, data.description)
      ),
      tap(() => this.fileManagementStore.reportFilesChanged$.next()),
      finalize(() => this.savingDescriptionId$.next(null)),
    ).subscribe();
  }

}
