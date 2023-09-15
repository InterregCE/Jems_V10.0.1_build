import {ChangeDetectionStrategy, Component} from '@angular/core';
import {finalize, map, take, tap} from 'rxjs/operators';
import {BehaviorSubject, combineLatest, Observable} from 'rxjs';
import {JemsFileDTO, PageJemsFileDTO, PluginInfoDTO} from '@cat/api';
import {
  ProjectReportVerificationCertificateStore
} from '@project/project-application/report/project-verification-report/project-verification-report-finalize-tab/project-report-verification-certificate/project-report-verification-certificate.store';
import {FormBuilder, FormGroup} from '@angular/forms';
import {ProjectReportPageStore} from '@project/project-application/report/project-report/project-report-page-store.service';
import {FileListItem} from '@common/components/file-list/file-list-item';
import {FileDescriptionChange} from '@common/components/file-list/file-list-table/file-description-change';
import {Alert} from '@common/components/forms/alert';
import {ReportUtil} from '@project/common/report-util';
import {
  ProjectReportDetailPageStore
} from '@project/project-application/report/project-report/project-report-detail-page/project-report-detail-page-store.service';

@Component({
  selector: 'jems-project-report-verification-certificate',
  templateUrl: './project-report-verification-certificate.component.html',
  styleUrls: ['./project-report-verification-certificate.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectReportVerificationCertificateComponent {

  Alert = Alert;

  exportForm: FormGroup;
  data$: Observable<{
    plugins: PluginInfoDTO[];
    canEdit: boolean;
    certificates: PageJemsFileDTO;
    fileList: FileListItem[];
  }>;
  exportInProgress$ = new BehaviorSubject(false);

  get plugin(): PluginInfoDTO {
    return this.exportForm.get('plugin')?.value;
  }

  constructor(
    public certificateStore: ProjectReportVerificationCertificateStore,
    private formBuilder: FormBuilder,
    projectReportPageStore: ProjectReportPageStore,
    projectReportDetailPageStore: ProjectReportDetailPageStore,
  ) {
    this.data$ = combineLatest([
      certificateStore.plugins$,
      projectReportPageStore.userCanEditVerification$,
      projectReportDetailPageStore.projectReport$,
      certificateStore.certificates$
    ]).pipe(
      map(([plugins, canEdit, report, certificates]) => ({
        plugins,
        canEdit,
        certificates,
        fileList: certificates.content.map((file: JemsFileDTO) => ({
          id: file.id,
          name: file.name,
          type: file.type,
          uploaded: file.uploaded,
          author: file.author,
          sizeString: file.sizeString,
          description: file.description,
          editable: canEdit && ReportUtil.isProjectReportAfterVerificationStarted(report.status),
          deletable: false
        }) as FileListItem),
      }))
    );
    this.exportForm = this.formBuilder.group({
      plugin: [],
    });
  }

  generateVerificationCertificate(plugin: PluginInfoDTO) {
    this.exportInProgress$.next(true);
    this.certificateStore.generateVerificationCertificate(plugin.key)
      .pipe(
        take(1),
        finalize(() => this.exportInProgress$.next(false)),
      ).subscribe();
  }

  downloadFile(file: FileListItem): void {
    this.certificateStore.downloadFile(file.id)
      .pipe(take(1))
      .subscribe();
  }

  setDescriptionCallback = (data: FileDescriptionChange): Observable<any> => {
    return this.certificateStore.updateDescription(data.id, data.description);
  };

}
