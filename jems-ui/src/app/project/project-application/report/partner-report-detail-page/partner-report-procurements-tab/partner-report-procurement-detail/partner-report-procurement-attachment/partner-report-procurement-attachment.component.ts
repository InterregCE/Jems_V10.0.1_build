import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {FormService} from '@common/components/section/form/form.service';
import {ActivatedRoute} from '@angular/router';
import {
  PartnerReportProcurementStore
} from '@project/project-application/report/partner-report-detail-page/partner-report-procurements-tab/partner-report-procurement-detail/partner-report-procurement-store.service';
import {
  PartnerReportDetailPageStore
} from '@project/project-application/report/partner-report-detail-page/partner-report-detail-page-store.service';
import {BehaviorSubject, combineLatest, Observable, Subject} from 'rxjs';
import {
  ProjectPartnerReportService,
  ProjectReportProcurementFileDTO, UserRoleDTO,
} from '@cat/api';
import {finalize, map, switchMap, take, tap} from 'rxjs/operators';
import {Alert} from '@common/components/forms/alert';
import {FileListItem} from '@common/components/file-list/file-list-item';
import {
  ReportFileManagementStore
} from '@project/project-application/report/partner-report-detail-page/partner-report-annexes-tab/report-file-management-store';
import PermissionsEnum = UserRoleDTO.PermissionsEnum;
import {AcceptedFileTypesConstants} from '@project/common/components/file-management/accepted-file-types.constants';
import {FileDescriptionChange} from '@common/components/file-list/file-list-table/file-description-change';
import {FileListComponent} from '@common/components/file-list/file-list.component';

@UntilDestroy()
@Component({
  selector: 'jems-partner-procurement-attachment',
  templateUrl: './partner-report-procurement-attachment.component.html',
  styleUrls: ['./partner-report-procurement-attachment.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PartnerReportProcurementAttachmentComponent {
  Alert = Alert;
  PermissionsEnum = PermissionsEnum;

  acceptedFilesTypes = AcceptedFileTypesConstants.acceptedFilesTypes;
  maximumAllowedFileSizeInMB: number;
  fileSizeOverLimitError$ = new Subject<boolean>();

  data$: Observable<{
    attachments: FileListItem[];
    isReportEditable: boolean;
    reportNumber: number;
  }>;

  @Input()
  private procurementId: number;

  constructor(
    private activatedRoute: ActivatedRoute,
    public procurementStore: PartnerReportProcurementStore,
    private partnerReportDetailPageStore: PartnerReportDetailPageStore,
    private fileManagementStore: ReportFileManagementStore,
    private projectPartnerReportService: ProjectPartnerReportService,
  ) {
    this.data$ = combineLatest([
      this.procurementStore.attachments$,
      this.partnerReportDetailPageStore.reportEditable$,
      this.partnerReportDetailPageStore.partnerReport$,
    ]).pipe(
      map(([attachments, isReportEditable, report]) => ({
        attachments: attachments.map((file: ProjectReportProcurementFileDTO) => ({
          id: file.id,
          name: file.name,
          type: file.type,
          uploaded: file.uploaded,
          author: file.author,
          sizeString: file.sizeString,
          description: file.description,
          editable: file.createdInThisReport && isReportEditable,
          deletable: file.createdInThisReport && isReportEditable,
          tooltipIfNotDeletable: '',
          iconIfNotDeletable: '',
        })),
        isReportEditable,
        reportNumber: report.reportNumber,
      })),
    );
    this.fileManagementStore.getMaximumAllowedFileSize().pipe(untilDestroyed(this))
      .subscribe((maxAllowedSize) => this.maximumAllowedFileSizeInMB = maxAllowedSize);
  }

  uploadFile(target: any): void {
    FileListComponent.doFileUploadWithValidation(
      target,
      this.fileSizeOverLimitError$,
      this.procurementStore.error$,
      this.maximumAllowedFileSizeInMB,
      file => this.procurementStore.uploadProcurementFile(file),
    );
  }

  downloadFile(file: FileListItem): void {
    this.fileManagementStore.downloadFile(file.id)
      .pipe(take(1))
      .subscribe();
  }

  deleteFile(file: FileListItem): void {
    this.procurementStore.deleteProcurementFile(file).pipe(take(1)).subscribe();
  }

  savingDescriptionId$ = new BehaviorSubject<number | null>(null);
  updateDescription(data: FileDescriptionChange) {
    return combineLatest([
      this.procurementStore.partnerId$.pipe(map(id => Number(id))),
      this.procurementStore.reportId$.pipe(map(id => Number(id))),
    ]).pipe(
      take(1),
      tap(() => this.savingDescriptionId$.next(data.id)),
      switchMap(([partnerId, reportId]) =>
        this.projectPartnerReportService.updateDescription(data.id, partnerId, reportId, data.description)
      ),
      tap(() => this.procurementStore.filesChanged$.next()),
      finalize(() => this.savingDescriptionId$.next(null)),
    ).subscribe();
  }

}
