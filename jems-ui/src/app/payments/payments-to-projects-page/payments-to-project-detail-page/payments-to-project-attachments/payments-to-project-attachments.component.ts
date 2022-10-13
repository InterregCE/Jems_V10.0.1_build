import {ChangeDetectionStrategy, Component, Input, OnChanges, SimpleChanges} from '@angular/core';
import {FormService} from '@common/components/section/form/form.service';
import {AcceptedFileTypesConstants} from '@project/common/components/file-management/accepted-file-types.constants';
import {BehaviorSubject, combineLatest, Observable, Subject} from 'rxjs';
import {FileListItem} from '@common/components/file-list/file-list-item';
import {ActivatedRoute} from '@angular/router';
import {ReportFileManagementStore} from '@project/project-application/report/partner-report-detail-page/partner-report-annexes-tab/report-file-management-store';
import {PaymentAttachmentService, ProjectReportFileDTO, UserRoleDTO} from '@cat/api';
import {finalize, map, take, tap} from 'rxjs/operators';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {FileListComponent} from '@common/components/file-list/file-list.component';
import {FileDescriptionChange} from '@common/components/file-list/file-list-table/file-description-change';
import {Alert} from '@common/components/forms/alert';
import {PaymentAttachmentsStore} from './payments-to-project-attachments-store.service';
import {Tables} from '@common/utils/tables';
import {PageFileList} from '@common/components/file-list/page-file-list';
import PermissionsEnum = UserRoleDTO.PermissionsEnum;

@UntilDestroy()
@Component({
  selector: 'jems-payments-to-project-attachments',
  templateUrl: './payments-to-project-attachments.component.html',
  styleUrls: ['./payments-to-project-attachments.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PaymentsToProjectAttachmentsComponent implements OnChanges {
  Alert = Alert;
  Tables = Tables;
  PermissionsEnum = PermissionsEnum;

  acceptedFilesTypes = AcceptedFileTypesConstants.acceptedFilesTypes;
  maximumAllowedFileSizeInMB: number;
  fileSizeOverLimitError$ = new Subject<boolean>();

  data$: Observable<{
    attachments: PageFileList;
    isPaymentEditable: boolean;
  }>;

  @Input()
  private paymentId: number;

  constructor(
    private activatedRoute: ActivatedRoute,
    public paymentAttachmentsStore: PaymentAttachmentsStore,
    private fileManagementStore: ReportFileManagementStore,
    private paymentAttachmentService: PaymentAttachmentService
  ) {
    this.data$ = combineLatest([
      this.paymentAttachmentsStore.attachments$,
      this.paymentAttachmentsStore.paymentEditable$,
    ]).pipe(
      map(([attachments, isPaymentEditable]) => ({
        attachments: {
          ...attachments,
          content: attachments.content?.map((file: ProjectReportFileDTO) => ({
            id: file.id,
            name: file.name,
            type: file.type,
            uploaded: file.uploaded,
            author: file.author,
            sizeString: file.sizeString,
            description: file.description,
            editable: isPaymentEditable,
            deletable: isPaymentEditable,
            tooltipIfNotDeletable: '',
            iconIfNotDeletable: '',
          })),
        },
        isPaymentEditable,
      })),
    );
    this.fileManagementStore.getMaximumAllowedFileSize().pipe(untilDestroyed(this))
      .subscribe((maxAllowedSize) => this.maximumAllowedFileSizeInMB = maxAllowedSize);
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.paymentId && !changes.paymentId.previousValue) {
      this.paymentAttachmentsStore.paymentId$.next(this.paymentId);
    }
  }

  uploadFile(target: any): void {
    FileListComponent.doFileUploadWithValidation(
      target,
      this.fileSizeOverLimitError$,
      this.paymentAttachmentsStore.error$,
      this.maximumAllowedFileSizeInMB,
      file => this.paymentAttachmentsStore.uploadPaymentFile(file),
    );
  }

  downloadFile(file: FileListItem): void {
    this.paymentAttachmentsStore.downloadFile(file.id)
      .pipe(take(1))
      .subscribe();
  }

  deleteFile(file: FileListItem): void {
    this.paymentAttachmentsStore.deletePaymentFile(file).pipe(take(1)).subscribe();
  }

  savingDescriptionId$ = new BehaviorSubject<number | null>(null);

  updateDescription(data: FileDescriptionChange) {
    this.savingDescriptionId$.next(data.id);
    return this.paymentAttachmentService.updateAttachmentDescription(data.id, data.description).pipe(
      tap(() => this.paymentAttachmentsStore.filesChanged$.next()),
      finalize(() => this.savingDescriptionId$.next(null)),
    ).subscribe();
  }
}
