import {ChangeDetectionStrategy, Component} from '@angular/core';
import {filter, map, switchMap, take, tap} from 'rxjs/operators';
import {PageProjectFileMetadataDTO, ProjectFileMetadataDTO, ProjectStatusDTO} from '@cat/api';
import {FileManagementStore} from '@project/common/components/file-management/file-management-store';
import {Tables} from '@common/utils/tables';
import {Alert} from '@common/components/forms/alert';
import {FileCategoryInfo} from '@project/common/components/file-management/file-category';
import {MatTableDataSource} from '@angular/material/table';
import {combineLatest, Observable} from 'rxjs';
import {Forms} from '@common/utils/forms';
import {MatDialog} from '@angular/material/dialog';
import {APIError} from '@common/models/APIError';

@Component({
  selector: 'app-project-application-files-table',
  templateUrl: './project-application-files-table.component.html',
  styleUrls: ['./project-application-files-table.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFilesTableComponent {
  Alert = Alert;
  Tables = Tables;

  displayedColumns: string[] = ['name', 'uploadDate', 'user', 'description', 'actions'];
  dataSource = new MatTableDataSource<ProjectFileMetadataDTO>();

  data$: Observable<{
    files: PageProjectFileMetadataDTO,
    projectStatus: ProjectStatusDTO,
    selectedCategory: FileCategoryInfo | undefined,
  }>;

  editableDescriptionFileId: number | null;

  constructor(public fileManagementStore: FileManagementStore,
              private dialog: MatDialog) {
    this.data$ = combineLatest([
      this.fileManagementStore.fileList$,
      this.fileManagementStore.projectStatus$,
      this.fileManagementStore.selectedCategory$,
    ])
      .pipe(
        map(([files, projectStatus, selectedCategory]) => ({
          files,
          projectStatus,
          selectedCategory
        })),
        tap(data => this.dataSource.data = data.files?.content)
      );
  }

  uploadFile(target: any): void {
    if (!target) {
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

  deleteFile(file: ProjectFileMetadataDTO): void {
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

  setFileDescription(file: ProjectFileMetadataDTO): void {
    this.fileManagementStore.setFileDescription(file.id, file.description)
      .pipe(take(1))
      .subscribe();
  }

  errorMessage(error: APIError): string {
    return error?.details[0].i18nMessage?.i18nKey || error?.i18nMessage?.i18nKey || 'file.delete.message.failed';
  }
}
