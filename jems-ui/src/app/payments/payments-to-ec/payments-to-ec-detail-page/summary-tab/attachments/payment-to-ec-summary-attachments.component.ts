import {ChangeDetectionStrategy, Component} from '@angular/core';
import {Alert} from '@common/components/forms/alert';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {FormService} from '@common/components/section/form/form.service';
import {AcceptedFileTypesConstants} from '@project/common/components/file-management/accepted-file-types.constants';
import {combineLatest, Observable, Subject} from 'rxjs';
import {PageFileList} from '@common/components/file-list/page-file-list';
import {ActivatedRoute} from '@angular/router';
import {
  ReportFileManagementStore
} from '@project/project-application/report/partner-report-detail-page/partner-report-annexes-tab/report-file-management-store';
import {JemsFileDTO, PaymentApplicationToEcAttachmentService, UserRoleDTO} from '@cat/api';
import {map, take} from 'rxjs/operators';
import {FileListComponent} from '@common/components/file-list/file-list.component';
import {FileListItem} from '@common/components/file-list/file-list-item';
import {FileDescriptionChange} from '@common/components/file-list/file-list-table/file-description-change';
import { Tables } from '@common/utils/tables';
import PermissionsEnum = UserRoleDTO.PermissionsEnum;
import {PaymentToEcAttachmentsStore} from './payment-to-ec-summary-attachments-store.service';

@UntilDestroy()
@Component({
  selector: 'jems-payment-to-ec-summary-attachments',
  templateUrl: './payment-to-ec-summary-attachments.component.html',
  styleUrls: ['./payment-to-ec-summary-attachments.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PaymentToEcSummaryAttachmentsComponent {
  Alert = Alert;
  Tables = Tables;
  PermissionsEnum = PermissionsEnum;

  acceptedFilesTypes = AcceptedFileTypesConstants.acceptedFilesTypes;
  maximumAllowedFileSizeInMB: number;
  fileSizeOverLimitError$ = new Subject<boolean>();
  isUploadInProgress = false;

  data$: Observable<{
    attachments: PageFileList;
    isPaymentToEcEditable: boolean;
  }>;

  constructor(
    private activatedRoute: ActivatedRoute,
    public paymentToEcAttachmentsStore: PaymentToEcAttachmentsStore,
    private fileManagementStore: ReportFileManagementStore,
    private paymentToEcAttachmentService: PaymentApplicationToEcAttachmentService
  ) {
    this.data$ = combineLatest([
      this.paymentToEcAttachmentsStore.attachments$,
      this.paymentToEcAttachmentsStore.paymentToEcEditable$,
    ]).pipe(
      map(([attachments, isPaymentToEcEditable]) => ({
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
            editable: isPaymentToEcEditable,
            deletable: isPaymentToEcEditable,
            tooltipIfNotDeletable: '',
            iconIfNotDeletable: ''
          })),
        },
        isPaymentToEcEditable,
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
      this.paymentToEcAttachmentsStore.error$,
      this.maximumAllowedFileSizeInMB,
      file => this.paymentToEcAttachmentsStore.uploadPaymentFile(file),
    ).add(() => this.isUploadInProgress = false);
  }

  downloadFile(file: FileListItem): void {
    this.paymentToEcAttachmentsStore.downloadFile(file.id)
      .pipe(take(1))
      .subscribe();
  }

  setDescriptionCallback = (data: FileDescriptionChange): Observable<any> => {
    return this.paymentToEcAttachmentService.updateAttachmentDescription(data.id, data.description);
  };

  deleteCallback = (file: FileListItem): Observable<void> => {
    return this.paymentToEcAttachmentService.deleteAttachment(file.id);
  };
}
