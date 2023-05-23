import {ChangeDetectionStrategy, Component} from '@angular/core';
import {CategoryInfo, CategoryNode} from '@project/common/components/category-tree/categoryModels';
import {combineLatest, Observable, Subject} from 'rxjs';
import {FileListItem} from '@common/components/file-list/file-list-item';
import {
  PartnerControlReportFileManagementStore
} from '@project/project-application/report/partner-control-report/partner-control-report-document-tab/partner-control-report-file-management-store';
import {finalize, map, switchMap, take} from 'rxjs/operators';
import {
  ProjectPartnerReportDTO,
  ProjectPartnerReportService,
  ProjectPartnerReportSummaryDTO,
  JemsFileDTO,
} from '@cat/api';
import {FileDescriptionChange} from '@common/components/file-list/file-list-table/file-description-change';
import {AcceptedFileTypesConstants} from '@project/common/components/file-management/accepted-file-types.constants';
import {SecurityService} from '../../../../../security/security.service';
import {FileListComponent} from '@common/components/file-list/file-list.component';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {Alert} from '@common/components/forms/alert';
import {TranslateService} from '@ngx-translate/core';
import {
  PartnerControlReportStore
} from '@project/project-application/report/partner-control-report/partner-control-report-store.service';
import {FormService} from '@common/components/section/form/form.service';
import {ReportUtil} from '@project/common/report-util';

@UntilDestroy()
@Component({
  selector: 'jems-partner-control-report-document-tab',
  templateUrl: './partner-control-report-document-tab.component.html',
  styleUrls: ['./partner-control-report-document-tab.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [FormService],
})
export class PartnerControlReportDocumentTabComponent {
  Alert = Alert;

  acceptedFilesTypes = AcceptedFileTypesConstants.acceptedFilesTypes;
  maximumAllowedFileSizeInMB: number;
  fileSizeOverLimitError$ = new Subject<boolean>();
  isUploadInProgress = false;

  selectedCategory: CategoryInfo = { id: 2, type: 'docs' };

  data$: Observable<{
    attachments: FileListItem[];
    isControlReportEditable: boolean;
    categories: CategoryNode;
  }>;

  constructor(
    public controlReportFileStore: PartnerControlReportFileManagementStore,
    private projectPartnerReportService: ProjectPartnerReportService,
    private securityService: SecurityService,
    private translationService: TranslateService,
    private partnerControlReportStore: PartnerControlReportStore,
  ) {
    this.data$ = combineLatest([
      this.controlReportFileStore.fileList$,
      this.controlReportFileStore.report$,
      this.securityService.currentUser.pipe(map(user => user?.id || 0)),
      this.partnerControlReportStore.canEditControlReport$,
    ]).pipe(
      map(([fileList, report, currentUserId, canEdit]) => ({
        attachments: fileList.map((file: JemsFileDTO) => ({
          id: file.id,
          name: file.name,
          type: file.type,
          uploaded: file.uploaded,
          author: file.author,
          sizeString: file.sizeString,
          description: file.description,
          editable: PartnerControlReportDocumentTabComponent.isEditable(report) && canEdit && file.author.id === currentUserId,
          deletable: PartnerControlReportDocumentTabComponent.isDeletable(report) && canEdit && file.author.id === currentUserId,
          tooltipIfNotDeletable: '',
          iconIfNotDeletable: '',
        })),
        isControlReportEditable: PartnerControlReportDocumentTabComponent.isEditable(report) && canEdit,
        categories: {
          name: { i18nKey: this.translationService.instant(
              `project.application.partner.reports.title.number`,
              {reportNumber: `${report.reportNumber}`},
              ) },
          info: { id: 1, type: 'doc' },
          children: [{
            name: { i18nKey: 'project.application.partner.report.control.tab.document' },
            info: this.selectedCategory,
            children: [],
          }],
        },
      })),
    );
    this.controlReportFileStore.getMaximumAllowedFileSize().pipe(untilDestroyed(this))
      .subscribe((maxAllowedSize) => this.maximumAllowedFileSizeInMB = maxAllowedSize);
  }

  private static isEditable(report: ProjectPartnerReportDTO): boolean {
    return ReportUtil.isControlReportExists(report.status);
  }

  private static isDeletable(report: ProjectPartnerReportDTO): boolean {
    return ReportUtil.isControlReportOpen(report.status);
  }

  uploadFile(target: any): void {
    this.isUploadInProgress = true;
    FileListComponent.doFileUploadWithValidation(
      target,
      this.fileSizeOverLimitError$,
      this.controlReportFileStore.error$,
      this.maximumAllowedFileSizeInMB,
      file => this.controlReportFileStore.uploadFile(file).pipe(finalize(() => this.isUploadInProgress = false)),
    );
  }

  downloadFile(file: FileListItem): void {
    this.controlReportFileStore.downloadFile(file.id)
      .pipe(take(1))
      .subscribe();
  }

  setDescriptionCallback = (data: FileDescriptionChange): Observable<any> => {
    return combineLatest([
      this.controlReportFileStore.partnerId$,
      this.controlReportFileStore.reportId$,
    ]).pipe(
      switchMap(([partnerId, reportId]) =>
        this.projectPartnerReportService.updateControlReportFileDescription(data.id, partnerId, reportId, data.description)
      ),
    );
  };

  deleteCallback = (file: FileListItem): Observable<void> => {
    return combineLatest([
      this.controlReportFileStore.partnerId$,
      this.controlReportFileStore.reportId$,
    ]).pipe(
      switchMap(([partnerId, reportId]) =>
        this.projectPartnerReportService.deleteControlReportFile(file.id, partnerId, reportId)
      ),
    );
  };
}
