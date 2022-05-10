import {ChangeDetectionStrategy, Component} from '@angular/core';
import {AcceptedFileTypesConstants} from '@project/common/components/file-management/accepted-file-types.constants';
import {MatTableDataSource} from '@angular/material/table';
import {
  PageProjectReportFileDTO,
  ProjectPartnerReportSummaryDTO,
  ProjectReportFileDTO, ProjectReportFileMetadataDTO
} from '@cat/api';
import {combineLatest, Observable, Subject} from 'rxjs';
import {CategoryInfo} from '@project/common/components/category-tree/categoryModels';
import {MatDialog} from '@angular/material/dialog';
import {filter, map, switchMap, take, tap} from 'rxjs/operators';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {Forms} from '@common/utils/forms';
import { Alert } from '@common/components/forms/alert';
import { Tables } from '@common/utils/tables';
import {
  ReportFileManagementStore
} from '@project/project-application/report/partner-report-detail-page/partner-report-annexes-tab/report-file-management-store';

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

  acceptedFilesTypes = AcceptedFileTypesConstants.acceptedFilesTypes;
  displayedColumns: string[] = ['name', 'location', 'uploadDate', 'user', 'size', 'actions'];
  dataSource = new MatTableDataSource<ProjectReportFileDTO>();
  maximumAllowedFileSizeInMB: number;
  fileSizeOverLimitError$ = new Subject<boolean>();

  data$: Observable<{
    files: PageProjectReportFileDTO;
    reportStatus: ProjectPartnerReportSummaryDTO.StatusEnum;
    selectedCategory: CategoryInfo | undefined;
  }>;

  constructor(public fileManagementStore: ReportFileManagementStore,
              private dialog: MatDialog) {
    this.data$ = combineLatest([
      this.fileManagementStore.reportFileList$,
      this.fileManagementStore.reportStatus$,
      this.fileManagementStore.selectedCategory$
    ])
      .pipe(
        map(([files, reportStatus, selectedCategory]) => ({
          files,
          reportStatus,
          selectedCategory
        })),
        tap(data => this.dataSource.data = data.files?.content)
      );
    this.fileManagementStore.getMaximumAllowedFileSize().pipe(untilDestroyed(this)).subscribe((maxAllowedSize) => this.maximumAllowedFileSizeInMB = maxAllowedSize);
  }

  uploadFile(target: any): void {
    if (!target) {
      return;
    }

    this.fileSizeOverLimitError$.next(false);
    this.fileManagementStore.error$.next(null);

    if (target?.files[0].size > this.maximumAllowedFileSizeInMB * 1024 * 1024) {
      setTimeout(() => this.fileSizeOverLimitError$.next(true), 10);
      return;
    }

    this.fileManagementStore.uploadFile(target?.files[0])
      .pipe(take(1))
      .subscribe();
  }

  downloadFile(fileId: number): void {
    this.fileManagementStore.downloadFile(fileId)
      .pipe(take(1))
      .subscribe();
  }

  deleteFile(file: ProjectReportFileMetadataDTO): void {
    Forms.confirm(
      this.dialog, {
        title: file.name,
        message: {i18nKey: 'file.dialog.message', i18nArguments: {name: file.name}}
      })
      .pipe(
        take(1),
        filter(answer => !!answer),
        switchMap(() => this.fileManagementStore.deleteFile(file.id)),
      ).subscribe();
  }
}
