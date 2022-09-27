import {ChangeDetectionStrategy, Component} from '@angular/core';
import {finalize, map, switchMap, take, tap} from 'rxjs/operators';
import {
  PageProjectFileMetadataDTO,
  ProjectFileMetadataDTO,
  ProjectStatusDTO
} from '@cat/api';
import {FileManagementStore} from '@project/common/components/file-management/file-management-store';
import {Tables} from '@common/utils/tables';
import {Alert} from '@common/components/forms/alert';
import {CategoryInfo} from '@project/common/components/category-tree/categoryModels';
import {MatTableDataSource} from '@angular/material/table';
import {BehaviorSubject, combineLatest, Observable, Subject} from 'rxjs';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {AcceptedFileTypesConstants} from '@project/common/components/file-management/accepted-file-types.constants';
import {FileListItem} from '@common/components/file-list/file-list-item';
import {FileCategoryTypeEnum} from '@project/common/components/file-management/file-category-type';
import {FileDescriptionChange} from '@common/components/file-list/file-list-table/file-description-change';
import {ProjectUtil} from '@project/common/project-util';

@UntilDestroy()
@Component({
  selector: 'jems-project-application-files-table',
  templateUrl: './project-application-files-table.component.html',
  styleUrls: ['./project-application-files-table.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFilesTableComponent {
  Alert = Alert;
  Tables = Tables;

  acceptedFilesTypes = AcceptedFileTypesConstants.acceptedFilesTypes;
  displayedColumns: string[] = ['name', 'uploadDate', 'user', 'description', 'actions'];
  dataSource = new MatTableDataSource<ProjectFileMetadataDTO>();
  maximumAllowedFileSizeInMB: number;
  fileSizeOverLimitError$ = new Subject<boolean>();

  data$: Observable<{
    files: PageProjectFileMetadataDTO;
    fileList: FileListItem[];
    selectedCategory: CategoryInfo | undefined;
  }>;

  constructor(public fileManagementStore: FileManagementStore) {
    this.data$ = combineLatest([
      this.fileManagementStore.fileList$,
      this.fileManagementStore.projectStatus$,
      this.fileManagementStore.selectedCategory$,
      this.fileManagementStore.canChangeApplicationFile$,
      this.fileManagementStore.canChangeAssessmentFile$,
      this.fileManagementStore.canChangeModificationFile$,
      this.fileManagementStore.userIsProjectOwnerOrEditCollaborator$,
    ])
      .pipe(
        map(([files, projectStatus, selectedCategory,
               canChangeApplicationFile, canChangeAssessmentFile, canChangeModificationFile, isOwner]: any) => ({
          files,
          fileList: files.content.map((file: ProjectFileMetadataDTO) => ({
            id: file.id,
            name: file.name,
            type: selectedCategory?.type,
            uploaded: file.uploadedAt,
            author: file.uploadedBy,
            sizeString: file.sizeString,
            description: file.description,
            editable: ProjectApplicationFilesTableComponent.isFileEditable(
              selectedCategory?.type,
              projectStatus,
              canChangeApplicationFile,
              canChangeAssessmentFile,
              canChangeModificationFile,
              isOwner,
            ),
            deletable: ProjectApplicationFilesTableComponent.isFileDeletable(
              selectedCategory?.type,
              projectStatus,
              canChangeApplicationFile,
              canChangeAssessmentFile,
              canChangeModificationFile,
              file.uploadedAt,
              isOwner,
            ),
            tooltipIfNotDeletable: '',
            iconIfNotDeletable: '',
          })),
          selectedCategory
        })),
        tap(data => this.dataSource.data = data.files?.content),
      );
    this.fileManagementStore.getMaximumAllowedFileSize().pipe(untilDestroyed(this)).subscribe((maxAllowedSize) => this.maximumAllowedFileSizeInMB = maxAllowedSize);
  }

  private static isFileEditable(
    type: any,
    status: ProjectStatusDTO,
    canChangeApplicationFile: boolean,
    canChangeAssessmentFile: boolean,
    canChangeModificationFile: boolean,
    isOwner: boolean,
  ): boolean {
    switch (type) {
      case FileCategoryTypeEnum.MODIFICATION:
        return canChangeModificationFile;
      case FileCategoryTypeEnum.ASSESSMENT:
        return canChangeAssessmentFile;
      case FileCategoryTypeEnum.APPLICATION:
        return canChangeApplicationFile || (isOwner && ProjectUtil.isOpenForModifications(status));
      default:
        return false;
    }
  }

  private static isFileDeletable(
    type: any,
    status: ProjectStatusDTO,
    canChangeApplicationFile: boolean,
    canChangeAssessmentFile: boolean,
    canChangeModificationFile: boolean,
    uploadedAt: Date,
    isOwner: boolean,
  ): boolean {
    switch (type) {
      case FileCategoryTypeEnum.MODIFICATION:
        return canChangeModificationFile;
      case FileCategoryTypeEnum.ASSESSMENT:
        return canChangeAssessmentFile;
      case FileCategoryTypeEnum.APPLICATION:
        return ProjectApplicationFilesTableComponent.isApplicationFileDeletable(status, uploadedAt, isOwner, canChangeApplicationFile);
      default:
        return false;
    }
  }

  private static isApplicationFileDeletable(status: ProjectStatusDTO, uploadedAt: Date, isOwner: boolean, isAllowedToChange: boolean): boolean {
    // the user can only delete files that are added after a last status change
    const lastStatusChange = status?.updated;
    const fileIsNotLocked = uploadedAt > lastStatusChange;

    const userIsAbleToDelete = isAllowedToChange
      || (isOwner && ProjectUtil.isOpenForModifications(status));
    return fileIsNotLocked && userIsAbleToDelete;
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
    return this.fileManagementStore.selectedCategory$.pipe(
      take(1),
      tap(() => this.savingDescriptionId$.next(data.id)),
      switchMap(() =>
        this.fileManagementStore.setFileDescription(data.id, data.description)
      ),
      tap(() => this.fileManagementStore.filesChanged$.next()),
      finalize(() => this.savingDescriptionId$.next(null)),
    ).subscribe();
  }
}
