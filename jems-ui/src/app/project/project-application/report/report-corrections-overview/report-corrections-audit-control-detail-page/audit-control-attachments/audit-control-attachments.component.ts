import { Component, OnInit } from '@angular/core';
import {Alert} from '@common/components/forms/alert';
import {combineLatest, Observable, Subject} from 'rxjs';
import {FileListItem} from '@common/components/file-list/file-list-item';
import {
  AuditControlAttachmentsStore
} from '@project/project-application/report/report-corrections-overview/report-corrections-audit-control-detail-page/audit-control-attachments/audit-control-attachments.store';
import {FileListComponent} from '@common/components/file-list/file-list.component';
import {map, take, tap} from 'rxjs/operators';
import {FileDescriptionChange} from '@common/components/file-list/file-list-table/file-description-change';
import {AcceptedFileTypesConstants} from '@project/common/components/file-management/accepted-file-types.constants';
import {JemsFileDTO} from '@cat/api';
import {ReportUtil} from '@project/common/report-util';
import {
  ReportCorrectionsAuditControlDetailPageStore
} from '@project/project-application/report/report-corrections-overview/report-corrections-audit-control-detail-page/report-corrections-audit-control-detail-page.store';
import {SecurityService} from '../../../../../../security/security.service';

@Component({
  selector: 'jems-audit-control-attachments',
  templateUrl: './audit-control-attachments.component.html',
  styleUrls: ['./audit-control-attachments.component.scss']
})
export class AuditControlAttachmentsComponent {

  Alert = Alert;
  acceptedFilesTypes = AcceptedFileTypesConstants.acceptedFilesTypes;
  maximumAllowedFileSizeInMB: number;
  fileSizeOverLimitError$ = new Subject<boolean>();
  isUploadInProgress = false;

  data$: Observable<{
    attachments: FileListItem[];
    canEdit: boolean;
  }>;

  constructor(
    private attachmentsStore: AuditControlAttachmentsStore,
    detailPageStore: ReportCorrectionsAuditControlDetailPageStore,
    securityService: SecurityService,
  ) {
    this.data$ = combineLatest([
      attachmentsStore.fileList$,
      detailPageStore.canEdit$,
      securityService.currentUser.pipe(map(user => user?.id ?? 0)),
    ]).pipe(
      map(([fileList, canEdit, currentUserId]) => ({
        attachments: fileList.map((file: JemsFileDTO) => ({
          id: file.id,
          name: file.name,
          type: file.type,
          uploaded: file.uploaded,
          author: file.author,
          sizeString: file.sizeString,
          description: file.description,
          editable: canEdit && file.author.id === currentUserId,
          deletable: canEdit && file.author.id === currentUserId,
          tooltipIfNotDeletable: '',
          iconIfNotDeletable: '',
        }) as FileListItem),
        canEdit,
      })),
  );
  }

  get error$() {
    return this.attachmentsStore.error$;
  }

  get newSort$() {
    return this.attachmentsStore.newSort$;
  }

  get filesChanged$() {
    return this.attachmentsStore.filesChanged$;
  }

  uploadFile(target: any): void {
    this.isUploadInProgress = true;
    FileListComponent.doFileUploadWithValidation(
      target,
      this.fileSizeOverLimitError$,
      this.attachmentsStore.error$,
      this.maximumAllowedFileSizeInMB,
      file => this.attachmentsStore.uploadFile(file),
    ).add(() => this.isUploadInProgress = false);
  }

  downloadFile(file: FileListItem): void {
    this.attachmentsStore.downloadFile(file.id)
      .pipe(take(1))
      .subscribe();
  }

  setDescriptionCallback = (data: FileDescriptionChange): Observable<any> => {
    return this.attachmentsStore.updateDescription(data.id, data.description);
  };

  deleteCallback = (file: FileListItem): Observable<void> => {
    return this.attachmentsStore.delete(file.id);
  };
}
