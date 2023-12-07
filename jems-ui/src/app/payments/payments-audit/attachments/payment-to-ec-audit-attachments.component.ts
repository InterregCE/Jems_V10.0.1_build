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
import {
  JemsFileDTO,
  PaymentApplicationToEcAttachmentService,
  PaymentAuditAttachmentService,
  UserRoleDTO
} from '@cat/api';
import {map, take} from 'rxjs/operators';
import {FileListComponent} from '@common/components/file-list/file-list.component';
import {FileListItem} from '@common/components/file-list/file-list-item';
import {FileDescriptionChange} from '@common/components/file-list/file-list-table/file-description-change';
import { Tables } from '@common/utils/tables';
import PermissionsEnum = UserRoleDTO.PermissionsEnum;
import {PaymentAuditAttachmentsStore} from './payment-to-ec-audit-attachments-store.service';

@UntilDestroy()
@Component({
  selector: 'jems-payment-to-ec-audit-attachments',
  templateUrl: './payment-to-ec-audit-attachments.component.html',
  styleUrls: ['./payment-to-ec-audit-attachments.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PaymentToEcAuditAttachmentsComponent {
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
    public paymentAuditAttachmentsStore: PaymentAuditAttachmentsStore,
    private fileManagementStore: ReportFileManagementStore,
    private paymentAuditAttachmentService: PaymentAuditAttachmentService
  ) {
    this.data$ = combineLatest([
      this.paymentAuditAttachmentsStore.attachments$,
      this.paymentAuditAttachmentsStore.paymentToEcEditable$,
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
      this.paymentAuditAttachmentsStore.error$,
      this.maximumAllowedFileSizeInMB,
      file => this.paymentAuditAttachmentsStore.uploadPaymentAuditFile(file),
    ).add(() => this.isUploadInProgress = false);
  }

  downloadFile(file: FileListItem): void {
    this.paymentAuditAttachmentsStore.downloadFile(file.id)
      .pipe(take(1))
      .subscribe();
  }

  setDescriptionCallback = (data: FileDescriptionChange): Observable<any> => {
    return this.paymentAuditAttachmentService.updateAttachmentDescription(data.id, data.description);
  };

  deleteCallback = (file: FileListItem): Observable<void> => {
    return this.paymentAuditAttachmentService.deleteAttachment(file.id);
  };
}
