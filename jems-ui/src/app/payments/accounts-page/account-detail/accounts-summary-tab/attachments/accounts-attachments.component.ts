import {ChangeDetectionStrategy, Component} from '@angular/core';
import {Alert} from '@common/components/forms/alert';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {AcceptedFileTypesConstants} from '@project/common/components/file-management/accepted-file-types.constants';
import {combineLatest, Observable, Subject} from 'rxjs';
import {PageFileList} from '@common/components/file-list/page-file-list';
import {ActivatedRoute} from '@angular/router';
import {
  ReportFileManagementStore
} from '@project/project-application/report/partner-report-detail-page/partner-report-annexes-tab/report-file-management-store';
import {JemsFileDTO, UserRoleDTO} from '@cat/api';
import {map, take} from 'rxjs/operators';
import {FileListComponent} from '@common/components/file-list/file-list.component';
import {FileListItem} from '@common/components/file-list/file-list-item';
import {FileDescriptionChange} from '@common/components/file-list/file-list-table/file-description-change';
import {Tables} from '@common/utils/tables';
import {AccountsAttachmentsStore} from './accounts-attachments-store.service';
import PermissionsEnum = UserRoleDTO.PermissionsEnum;

@UntilDestroy()
@Component({
  selector: 'jems-payment-account-attachments',
  templateUrl: './accounts-attachments.component.html',
  styleUrls: ['./accounts-attachments.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AccountsAttachmentsComponent {
  Alert = Alert;
  Tables = Tables;
  PermissionsEnum = PermissionsEnum;

  acceptedFilesTypes = AcceptedFileTypesConstants.acceptedFilesTypes;
  maximumAllowedFileSizeInMB: number;
  fileSizeOverLimitError$ = new Subject<boolean>();
  isUploadInProgress = false;

  data$: Observable<{
    attachments: PageFileList;
    isPaymentAccountEditable: boolean;
  }>;

  constructor(
    private activatedRoute: ActivatedRoute,
    public paymentAccountAttachmentsStore: AccountsAttachmentsStore,
    private fileManagementStore: ReportFileManagementStore,
  ) {
    this.data$ = combineLatest([
      this.paymentAccountAttachmentsStore.attachments$,
      this.paymentAccountAttachmentsStore.paymentAccountEditable$,
    ]).pipe(
      map(([attachments, isPaymentAccountEditable]) => ({
        attachments: {
          ...attachments,
          content: attachments.content?.map((file: JemsFileDTO) => ({
            id: file.id,
            name: file.name,
            type: file.type,
            uploaded: file.uploaded,
            author: file.author,
            sizeString: file.sizeString,
            description: file.description,
            editable: isPaymentAccountEditable,
            deletable: isPaymentAccountEditable,
            tooltipIfNotDeletable: '',
            iconIfNotDeletable: ''
          })),
        },
        isPaymentAccountEditable,
      })),
    );
    this.fileManagementStore.getMaximumAllowedFileSize().pipe(untilDestroyed(this))
      .subscribe((maxAllowedSize) => this.maximumAllowedFileSizeInMB = maxAllowedSize);
  }

  uploadFile(target: any): void {
    this.isUploadInProgress = true;
    FileListComponent.doFileUploadWithValidation(
      target,
      this.fileSizeOverLimitError$,
      this.paymentAccountAttachmentsStore.error$,
      this.maximumAllowedFileSizeInMB,
      file => this.paymentAccountAttachmentsStore.uploadPaymentFile(file),
    ).add(() => this.isUploadInProgress = false);
  }

  downloadFile(file: FileListItem): void {
    this.paymentAccountAttachmentsStore.downloadFile(file.id)
      .pipe(take(1))
      .subscribe();
  }

  setDescriptionCallback = (data: FileDescriptionChange): Observable<any> => {
    return this.paymentAccountAttachmentsStore.setDescription(data);
  };

  deleteCallback = (file: FileListItem): Observable<void> => {
    return this.paymentAccountAttachmentsStore.deletePaymentFile(file);
  };
}
