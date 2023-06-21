import {Component} from '@angular/core';
import {combineLatest, Observable, Subject} from 'rxjs';
import {finalize, map, take} from 'rxjs/operators';
import {SharedFolderPageStore} from '@project/project-application/shared-folder/shared-folder-page.store';
import {JemsFileDTO, PageJemsFileDTO} from '@cat/api';
import {FileListItem} from '@common/components/file-list/file-list-item';
import {FileDescriptionChange} from '@common/components/file-list/file-list-table/file-description-change';
import {FileListComponent} from '@common/components/file-list/file-list.component';
import {AcceptedFileTypesConstants} from '@project/common/components/file-management/accepted-file-types.constants';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {Alert} from '@common/components/forms/alert';
import {SecurityService} from '../../../security/security.service';

@UntilDestroy()
@Component({
  selector: 'jems-shared-folder-page',
  templateUrl: './shared-folder-page.component.html',
  styleUrls: ['./shared-folder-page.component.scss'],
  providers: [SharedFolderPageStore]
})
export class SharedFolderPageComponent {

  data$: Observable<{
    files: PageJemsFileDTO;
    fileList: FileListItem[];
    userCanEdit: boolean;
  }>;

  isUploadInProgress = false;
  maximumAllowedFileSizeInMB: number;
  fileSizeOverLimitError$ = new Subject<boolean>();
  acceptedFilesTypes = AcceptedFileTypesConstants.acceptedFilesTypes;
  Alert = Alert;

  constructor(public pageStore: SharedFolderPageStore,
              private readonly securityService: SecurityService) {

    this.data$ = combineLatest([
      this.pageStore.fileList$,
      this.pageStore.userCanEdit$,
      this.pageStore.userCanDelete$,
      this.securityService.currentUser.pipe(map(user => user?.id || 0)),
    ]).pipe(
      map(([files, canEdit, canDelete, currentUserId]) => ({
        files,
        fileList: files.content.map((file: JemsFileDTO) => ({
          id: file.id,
          name: file.name,
          type: file.type,
          uploaded: file.uploaded,
          author: file.author,
          sizeString: file.sizeString,
          description: file.description,
          editable: canEdit && file.author.id === currentUserId,
          deletable: canDelete,
          tooltipIfNotDeletable: 'shared.folder.can.not.edit',
          iconIfNotDeletable: 'delete'
        })),
        userCanEdit: canEdit
      })),
    );

    this.pageStore.getMaximumAllowedFileSize()
      .pipe(untilDestroyed(this))
      .subscribe((maxAllowedSize) => this.maximumAllowedFileSizeInMB = maxAllowedSize);
  }

  uploadFile(target: any): void {
    this.isUploadInProgress = true;
    FileListComponent.doFileUploadWithValidation(
      target,
      this.fileSizeOverLimitError$,
      this.pageStore.error$,
      this.maximumAllowedFileSizeInMB,
      file => this.pageStore.uploadFile(file)
        .pipe(finalize(() => this.isUploadInProgress = false)),
    );
  }

  downloadFile(file: FileListItem): void {
    this.pageStore.downloadFile(file.id)
      .pipe(take(1))
      .subscribe();
  }

  setDescriptionCallback = (data: FileDescriptionChange) => this.pageStore.setDescriptionToFile(data.id, data.description);

  deleteCallback = (file: FileListItem): Observable<void> => this.pageStore.deleteFile(file.id);

}
