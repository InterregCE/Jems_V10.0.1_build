import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {Alert} from '@common/components/forms/alert';
import {PartnerControlReportGenerateControlReportAndCertificateExportStore} from '@project/project-application/report/partner-control-report/partner-control-report-overview-and-finalize-tab/partner-control-report-generate-control-report-and-certificate/partner-control-report-generate-control-report-and-certificate-export-store';
import {FileListItem} from '@common/components/file-list/file-list-item';
import {map, switchMap, take, tap} from 'rxjs/operators';
import {FileDescriptionChange} from '@common/components/file-list/file-list-table/file-description-change';
import {combineLatest, Observable} from 'rxjs';
import {FormBuilder, FormGroup} from '@angular/forms';
import {
  PageProjectReportFileDTO,
  PluginInfoDTO,
  ProjectPartnerControlReportFileAPIService,
  ProjectReportFileDTO
} from '@cat/api';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {PartnerControlReportStore} from '@project/project-application/report/partner-control-report/partner-control-report-store.service';

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
  displayedColumns: string[] = ['name', 'location', 'creationDate', 'user', 'description', 'action'];
  exportForm: FormGroup;
  data$: Observable<{
    plugins: PluginInfoDTO[];
    files: PageProjectReportFileDTO;
    fileList: FileListItem[];
  }>;

  constructor(
    public fileManagementStore: PartnerControlReportGenerateControlReportAndCertificateExportStore,
    private partnerControlReportStore: PartnerControlReportStore,
    private projectPartnerControlReportFileApiService: ProjectPartnerControlReportFileAPIService,
    private formBuilder: FormBuilder
  ) {

    this.fileManagementStore.certificateExportPlugins$.pipe(
      tap(plugins => this.initForm(plugins)),
      untilDestroyed(this)
    ).subscribe();

    this.data$ = combineLatest([
      this.fileManagementStore.certificateExportPlugins$,
      this.fileManagementStore.certificateFileList$,
    ]).pipe(
      map(([plugins, files]) => ({
        plugins,
        files,
        fileList: files.content ? files.content?.map((file: ProjectReportFileDTO) => ({
          id: file.id,
          name: file.name,
          type: file.type,
          uploaded: file.uploaded,
          author: file.author,
          sizeString: file.sizeString,
          description: file.description,
          editable: true,
          deletable: false,
          tooltipIfNotDeletable: '',
          iconIfNotDeletable: '',
        } as FileListItem)) : [],
      })),
    );
  }

  initForm(plugins: PluginInfoDTO[]): void {
    this.exportForm = this.formBuilder.group({
      pluginKey: [plugins.length > 0 ? plugins[0].key : undefined],
    });
  }

  downloadFile(file: FileListItem): void {
    this.fileManagementStore.downloadFile(file.id)
      .pipe(take(1))
      .subscribe();
  }

  setDescriptionCallback = (data: FileDescriptionChange): Observable<any> => {
    return combineLatest([
      this.partnerControlReportStore.partnerId$.pipe(map(id => Number(id))),
      this.partnerControlReportStore.reportId$.pipe(map(id => Number(id))),
    ]).pipe(
      switchMap(([partnerId, reportId]) =>
        this.projectPartnerControlReportFileApiService.updateControlReportCertificateFileDescription(data.id, partnerId, reportId, data.description)
      ),
    );
  };

  get pluginKey(): string {
    return this.exportForm.get('pluginKey')?.value;
  }

  exportData(): void {
    this.fileManagementStore.exportData(this.partnerId, this.reportId).pipe(untilDestroyed(this)).subscribe();
  }
}
