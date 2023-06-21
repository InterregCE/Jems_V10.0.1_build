import {Component} from '@angular/core';
import {FormService} from '@common/components/section/form/form.service';
import {
  PartnerReportExportPageStore
} from '@project/project-application/report/partner-report-detail-page/partner-report-export-tab/partner-report-export-page-store.service';
import {BehaviorSubject, combineLatest, Observable} from 'rxjs';
import {PluginInfoDTO} from '@cat/api';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {finalize, map, tap} from 'rxjs/operators';
import {DownloadService} from '@common/services/download.service';
import {
  PartnerReportDetailPageStore
} from '@project/project-application/report/partner-report-detail-page/partner-report-detail-page-store.service';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import moment from 'moment/moment';

@UntilDestroy()
@Component({
  selector: 'jems-partner-report-export-tab',
  templateUrl: './partner-report-export-tab.component.html',
  styleUrls: ['./partner-report-export-tab.component.scss'],
  providers: [FormService]
})
export class PartnerReportExportTabComponent {

  partnerId: number;
  reportId: number;
  exportForm: FormGroup;
  isExportingInProgress$ = new BehaviorSubject(false);
  data$: Observable<{
    availablePlugins: PluginInfoDTO[];
    exportLanguages: string[];
    inputLanguages: string[];
  }>;

  constructor(
      private pageStore: PartnerReportExportPageStore,
      private formBuilder: FormBuilder,
      private downloadService: DownloadService,
      private partnerReportDetailPageStore: PartnerReportDetailPageStore) {
    this.data$ = combineLatest([
      this.pageStore.availablePlugins$,
      this.pageStore.exportLanguages$,
      this.pageStore.inputLanguages$,
      this.partnerReportDetailPageStore.partnerId$,
      this.partnerReportDetailPageStore.partnerReportId$
    ]).pipe(
        tap(([availablePlugins, exportLanguages, inputLanguages, partnerId, reportId]) => {
              this.partnerId = Number(partnerId);
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
        `/api/project/report/partner/byPartnerId/${this.partnerId}/byReportId/${this.reportId}/export?${requestParameters}&localDateTime=${localDateTime}`,
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
