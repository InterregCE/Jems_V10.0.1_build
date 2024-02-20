import {ChangeDetectionStrategy, Component} from '@angular/core';
import {finalize, map, switchMap, take, tap} from 'rxjs/operators';
import {
  PageProjectFileMetadataDTO,
  ProjectFileMetadataDTO,
  ProjectFileService,
  ProjectStatusDTO
} from '@cat/api';
import {FileManagementStore} from '@project/common/components/file-management/file-management-store';
import {Tables} from '@common/utils/tables';
import {Alert} from '@common/components/forms/alert';
import {CategoryInfo} from '@project/common/components/category-tree/categoryModels';
import {MatTableDataSource} from '@angular/material/table';
import {combineLatest, Observable, Subject} from 'rxjs';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {AcceptedFileTypesConstants} from '@project/common/components/file-management/accepted-file-types.constants';
import {FileListItem} from '@common/components/file-list/file-list-item';
import {FileCategoryTypeEnum} from '@project/common/components/file-management/file-category-type';
import {FileDescriptionChange} from '@common/components/file-list/file-list-table/file-description-change';
import {
  ProjectStore
} from '@project/project-application/containers/project-application-detail/services/project-store.service';

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
  isUploadInProgress = false;

  data$: Observable<{
    files: PageProjectFileMetadataDTO;
    fileList: FileListItem[];
    selectedCategory: CategoryInfo | undefined;
  }>;

  constructor(
    public fileManagementStore: FileManagementStore,
    private projectFileService: ProjectFileService,
    private projectStore: ProjectStore
  ) {
    this.data$ = combineLatest([
      this.fileManagementStore.fileList$,
      this.fileManagementStore.selectedCategory$,
      this.fileManagementStore.canChangeApplicationFile$,
      this.fileManagementStore.canChangeAssessmentFile$,
      this.fileManagementStore.canChangeModificationFile$,
      this.fileManagementStore.userIsProjectOwnerOrEditCollaborator$,
      this.fileManagementStore.currentProjectStatus$
    ])
      .pipe(
        map(([files, selectedCategory, canChangeApplicationFile, canChangeAssessmentFile, canChangeModificationFile, isOwner, currentProjectStatus]: any) => ({
          files,
          fileList: files.content.map((file: ProjectFileMetadataDTO) => ({
            id: file.id,
            name: file.name,
            type: file.category || selectedCategory?.type,
            uploaded: file.uploadedAt,
            author: file.uploadedBy,
            sizeString: file.sizeString,
            description: file.description,
            editable: ProjectApplicationFilesTableComponent.isFileEditable(
              selectedCategory?.type,
              currentProjectStatus,
              canChangeApplicationFile,
              canChangeAssessmentFile,
              canChangeModificationFile,
              file.uploadedAt,
              isOwner,
              this.fileManagementStore.isInModifiableStatus(currentProjectStatus.status)
            ),
            deletable: ProjectApplicationFilesTableComponent.isFileDeletable(
              selectedCategory?.type,
              currentProjectStatus,
              canChangeApplicationFile,
              canChangeAssessmentFile,
              canChangeModificationFile,
              file.uploadedAt,
              isOwner,
              this.fileManagementStore.isInModifiableStatus(currentProjectStatus.status)
            ),
            tooltipIfNotDeletable: '',
            iconIfNotDeletable: '',
          })),
          selectedCategory
        })),
        tap(data => this.dataSource.data = data.files?.content),
      );

    this.fileManagementStore.maxFileSize$.pipe(untilDestroyed(this)).subscribe((maxAllowedSize) => this.maximumAllowedFileSizeInMB = maxAllowedSize);
  }

  private static isFileEditable(
    type: any,
    status: ProjectStatusDTO,
    canChangeApplicationFile: boolean,
    canChangeAssessmentFile: boolean,
    canChangeModificationFile: boolean,
    uploadedAt: Date,
    isOwner: boolean,
    isInModifiableStatus: boolean
  ): boolean {
    const fileIsNotLocked = uploadedAt > status.updated;

    switch (type) {
      case FileCategoryTypeEnum.MODIFICATION:
        return canChangeModificationFile;
      case FileCategoryTypeEnum.ASSESSMENT:
        return canChangeAssessmentFile;
      case FileCategoryTypeEnum.APPLICATION:
        return canChangeApplicationFile && isInModifiableStatus;
      case FileCategoryTypeEnum.PARTNER:
      case FileCategoryTypeEnum.INVESTMENT:
        return (canChangeApplicationFile || isOwner) && fileIsNotLocked && isInModifiableStatus;
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
    isInModifiableStatus: boolean,
  ): boolean {
    // the user can only delete files that are added after a last status change
    const fileIsNotLocked = uploadedAt > status?.updated;

    switch (type) {
      case FileCategoryTypeEnum.MODIFICATION:
        return canChangeModificationFile;
      case FileCategoryTypeEnum.ASSESSMENT:
        return canChangeAssessmentFile && fileIsNotLocked;
      case FileCategoryTypeEnum.APPLICATION:
        return canChangeApplicationFile && fileIsNotLocked && isInModifiableStatus;
      case FileCategoryTypeEnum.PARTNER:
      case FileCategoryTypeEnum.INVESTMENT:
        return (canChangeApplicationFile || isOwner) && fileIsNotLocked && isInModifiableStatus;
      default:
        return false;
    }
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

    this.isUploadInProgress = true;
    this.fileManagementStore.uploadFile(target?.files[0])
      .pipe(
        take(1),
        finalize(() => this.isUploadInProgress = false)
      )
      .subscribe();
  }

  downloadFile(file: FileListItem): void {
    this.fileManagementStore.downloadFile(file.id)
      .pipe(take(1))
      .subscribe();
  }

  setDescriptionCallback = (data: FileDescriptionChange): Observable<any> => {
    return this.projectStore.projectId$.pipe(
      switchMap(projectId => this.projectFileService.setProjectFileDescription(data.id, projectId, data.description)),
    );
  };

  deleteCallback = (file: FileListItem): Observable<void> => {
    return this.projectStore.projectId$.pipe(
      switchMap((projectId) => this.projectFileService.deleteProjectFile(file.id, projectId)),
    );
  };
}
