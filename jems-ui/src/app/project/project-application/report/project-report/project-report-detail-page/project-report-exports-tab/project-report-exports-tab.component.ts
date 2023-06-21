import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {Component} from '@angular/core';
import {FormService} from '@common/components/section/form/form.service';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {BehaviorSubject, combineLatest, Observable} from 'rxjs';
import {PluginInfoDTO} from '@cat/api';
import {
  PartnerReportExportPageStore
} from '@project/project-application/report/partner-report-detail-page/partner-report-export-tab/partner-report-export-page-store.service';
import {DownloadService} from '@common/services/download.service';
import {finalize, map, tap} from 'rxjs/operators';
import {
  ProjectReportDetailPageStore
} from '@project/project-application/report/project-report/project-report-detail-page/project-report-detail-page-store.service';
import moment from 'moment/moment';
import {
  ProjectStore
} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {
  ProjectReportExportsPageStore
} from '@project/project-application/report/project-report/project-report-detail-page/project-report-exports-tab/project-report-exports-tab-store.service';

@UntilDestroy()
@Component({
  selector: 'jems-project-report-exports-tab',
  templateUrl: './project-report-exports-tab.component.html',
  styleUrls: ['./project-report-exports-tab.component.scss'],
  providers: [FormService]
})
export class ProjectReportExportsTabComponent {

  projectId: number;
  reportId: number;
  exportForm: FormGroup;
  isExportingInProgress$ = new BehaviorSubject(false);
  data$: Observable<{
    availablePlugins: PluginInfoDTO[];
    exportLanguages: string[];
    inputLanguages: string[];
  }>;

  constructor(private pageStore: ProjectReportExportsPageStore,
              private formBuilder: FormBuilder,
              private downloadService: DownloadService,
              private projectReportDetailPageStore: ProjectReportDetailPageStore,
              private projectStore: ProjectStore) {
    this.data$ = combineLatest([
      this.pageStore.availablePlugins$,
      this.pageStore.exportLanguages$,
      this.pageStore.inputLanguages$,
      this.projectStore.projectId$,
      this.projectReportDetailPageStore.projectReportId$
    ]).pipe(
      tap(([availablePlugins, exportLanguages, inputLanguages, projectId, reportId]) => {
          this.projectId = projectId;
          this.reportId = reportId;
        }
      ),
      map(([availablePlugins, exportLanguages, inputLanguages]) => ({
          availablePlugins,
          exportLanguages,
          inputLanguages
        })
      ),
      tap(() => this.resetForm())
    );
  }

  resetForm(): void {
    this.exportForm = this.formBuilder.group({
      plugin: ['', Validators.required],
      inputLanguage: [this.pageStore.fallBackLanguage],
      exportLanguage: [this.pageStore.fallBackLanguage]
    });
  }

  exportData(): void {
    const requestParameters = `pluginKey=${this.exportPlugin.key}&exportLanguage=${this.exportLanguage}&inputLanguage=${this.inputLanguage}`;
    const localDateTime = moment().format('YYYY-MM-DDTHH:mm:ss');
    this.isExportingInProgress$.next(true);
    this.downloadService.download(
      `/api/project/report/byProjectId/${this.projectId}/byReportId/${this.reportId}/export?${requestParameters}&localDateTime=${localDateTime}`,
      'report_partner_export'
    ).pipe(
      finalize(() => this.isExportingInProgress$.next(false)),
      untilDestroyed(this)
    ).subscribe();
  }

  get inputLanguage(): string {
    return this.exportForm.get('inputLanguage')?.value;
  }

  get exportLanguage(): string {
    return this.exportForm.get('exportLanguage')?.value;
  }

  get exportPlugin(): PluginInfoDTO {
    return this.exportForm.get('plugin')?.value;
  }

}
