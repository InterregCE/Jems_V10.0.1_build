import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {Alert} from '@common/components/forms/alert';
import {
  PartnerControlReportGenerateControlReportAndCertificateExportStore
} from '@project/project-application/report/partner-control-report/partner-control-report-overview-and-finalize-tab/partner-control-report-generate-control-report-and-certificate/partner-control-report-generate-control-report-and-certificate-export-store';
import {FileListItem} from '@common/components/file-list/file-list-item';
import {finalize, map, switchMap, take, tap} from 'rxjs/operators';
import {FileDescriptionChange} from '@common/components/file-list/file-list-table/file-description-change';
import {combineLatest, Observable, of} from 'rxjs';
import {FormBuilder, FormGroup} from '@angular/forms';
import {
  PagePartnerReportControlFileDTO,
  PartnerReportControlFileDTO,
  PluginInfoDTO,
  ProjectPartnerControlReportFileAPIService,
  ProjectPartnerReportSummaryDTO,
} from '@cat/api';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {PartnerControlReportStore} from '@project/project-application/report/partner-control-report/partner-control-report-store.service';
import {PartnerReportPageStore} from '@project/project-application/report/partner-report-page-store.service';

@UntilDestroy()
@Component({
  selector: 'jems-partner-control-report-generate-control-report-and-certificate',
  templateUrl: './partner-control-report-generate-control-report-and-certificate.component.html',
  styleUrls: ['./partner-control-report-generate-control-report-and-certificate.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PartnerControlReportGenerateControlReportAndCertificateComponent {

  @Input()
  partnerId: number;

  @Input()
  reportId: number;

  Alert = Alert;
  exportForm: FormGroup;
  data$: Observable<{
    plugins: PluginInfoDTO[];
    files: PagePartnerReportControlFileDTO;
    fileList: FileListItem[];
    isReportFinalized: boolean;
    isUserAllowedToEditReport: boolean;
    reportStatus: ProjectPartnerReportSummaryDTO.StatusEnum;
  }>;

  isUploadDone = false;

  displayedColumns: string[] = ['name', 'location', 'uploadDate', 'user', 'size', 'description', 'action', 'attachment'];

  exportInProgress = false;

  constructor(
    public fileManagementStore: PartnerControlReportGenerateControlReportAndCertificateExportStore,
    private partnerControlReportStore: PartnerControlReportStore,
    private reportPageStore: PartnerReportPageStore,
    private projectPartnerControlReportFileApiService: ProjectPartnerControlReportFileAPIService,
    private formBuilder: FormBuilder
  ) {

    this.fileManagementStore.exportPlugins$.pipe(
      tap(plugins => this.initForm(plugins)),
      untilDestroyed(this)
    ).subscribe();

    this.data$ = combineLatest([
      this.fileManagementStore.exportPlugins$,
      this.fileManagementStore.certificateFileList$,
      this.partnerControlReportStore.controlReportFinalized$,
      this.partnerControlReportStore.canEditControlReport$,
      this.fileManagementStore.partnerControlReportStore.partnerReportDetailPageStore.reportStatus$,
    ]).pipe(
      map(([plugins, files, isReportFinalized, isUserAllowedToEditReport, reportStatus]) => ({
        plugins,
        files,
        fileList: files.content ? files.content?.map((file: PartnerReportControlFileDTO) => ({
          id: file.generatedFile.id,
          name: file.generatedFile.name,
          type: file.generatedFile.type,
          uploaded: file.generatedFile.uploaded,
          author: file.generatedFile.author,
          sizeString: file.generatedFile.sizeString,
          description: file.generatedFile.description,
          editable: isUserAllowedToEditReport,
          deletable: false,
          tooltipIfNotDeletable: '',
          iconIfNotDeletable: '',
          parentEntityId: file.id,
          attachment: file.signedFile
        } as FileListItem)) : [],
        isReportFinalized,
        isUserAllowedToEditReport,
        reportStatus
      })),
    );
  }

  initForm(plugins: PluginInfoDTO[]): void {
    this.exportForm = this.formBuilder.group({
      plugin: [],
    });
  }

  downloadFile(fileId: number): void {
    this.fileManagementStore.downloadFile(fileId)
      .pipe(take(1))
      .subscribe();
  }

  setDescriptionCallback = (data: FileDescriptionChange): Observable<any> => {
    return combineLatest([
      this.partnerControlReportStore.partnerId$.pipe(map(id => Number(id))),
      this.partnerControlReportStore.reportId$.pipe(map(id => Number(id))),
    ]).pipe(
      switchMap(([partnerId, reportId]) =>
        this.projectPartnerControlReportFileApiService.updateControlReportFileDescription(data.id, partnerId, reportId, data.description)
      ),
    );
  };

  get plugin(): PluginInfoDTO {
    return this.exportForm.get('plugin')?.value;
  }

  exportData(plugin: PluginInfoDTO): void {
    this.exportInProgress = true
    if (plugin.type === PluginInfoDTO.TypeEnum.PARTNERCONTROLREPORTEXPORT) {
      this.fileManagementStore.generateControlReportExport(this.partnerId, this.reportId, plugin.key).pipe(
          finalize(() => this.exportInProgress = false),
          untilDestroyed(this)
      ).subscribe();
    } else if (plugin.type === PluginInfoDTO.TypeEnum.PARTNERCONTROLREPORTCERTIFICATE) {
      this.fileManagementStore.generateControlReportCertificate(this.partnerId, this.reportId, plugin.key).pipe(
          finalize(() => this.exportInProgress = false),
          untilDestroyed(this)).subscribe();
    }
  }

  uploadAttachmentCallback = (target: any, fileId: number): Observable<any> => {
    if (target && fileId !== 0) {
      this.isUploadDone = false;
      return combineLatest([
        this.partnerControlReportStore.partnerId$.pipe(map(id => Number(id))),
        this.partnerControlReportStore.reportId$.pipe(map(id => Number(id))),
      ]).pipe(
        take(1),
        switchMap(([partnerId, reportId]) =>
          this.fileManagementStore.uploadAttachment(target?.files[0], partnerId, reportId, fileId)
        ),
        finalize(() => this.isUploadDone = true)
      );
    }
    return of(null);
  };

  deleteAttachmentCallback = (fileId: number, attachmentId: number): Observable<void> => {
    return this.fileManagementStore.deleteFile(fileId, attachmentId);
  };

  downloadAttachmentCallback = (fileId: number): void => {
    this.fileManagementStore.downloadAttachmentFile(fileId).pipe(take(1)).subscribe();
  };
}
