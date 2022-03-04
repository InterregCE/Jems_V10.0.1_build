import {ChangeDetectionStrategy, Component, OnDestroy} from '@angular/core';
import {ProgrammePageSidenavService} from '../programme-page/services/programme-page-sidenav.service';
import {ProgrammeDataExportStore} from './programme-data-export-store';
import {combineLatest, Observable} from 'rxjs';
import {PluginInfoDTO, ProgrammeDataExportMetadataDTO} from '@cat/api';
import {FormBuilder, FormGroup} from '@angular/forms';
import {map, tap} from 'rxjs/operators';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';

const REFRESH_INTERVAL_IN_MILLISECOND = 5000;

@UntilDestroy()
@Component({
  selector: 'jems-programme-data-export',
  templateUrl: './programme-data-export.component.html',
  styleUrls: ['./programme-data-export.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [ProgrammeDataExportStore]

})
export class ProgrammeDataExportComponent implements OnDestroy {

  refreshInterval;
  exportForm: FormGroup;
  data$: Observable<{
    inputLanguages: string[];
    exportLanguages: string[];
    plugins: PluginInfoDTO[];
    programmeDataExportMetadata: ProgrammeDataExportMetadataDTO[];
    isExportDisabled: boolean;
  }>;

  constructor(private programmePageSidenavService: ProgrammePageSidenavService, private pageStore: ProgrammeDataExportStore, private formBuilder: FormBuilder) {

    this.refreshInterval = setInterval(() => {
      this.pageStore.refreshExportMetaData();
    }, REFRESH_INTERVAL_IN_MILLISECOND);

    this.pageStore.programmeDataExportPlugins$.pipe(
      tap(plugins => this.initForm(plugins)),
      untilDestroyed(this)
    ).subscribe();

    this.data$ = combineLatest([
      this.pageStore.inputLanguages$,
      this.pageStore.exportLanguages$,
      this.pageStore.programmeDataExportPlugins$,
      this.pageStore.programmeDataExportMetadata$,
    ]).pipe(
      map(([inputLanguages, exportLanguages, plugins, programmeDataExportMetadata]) => ({
        inputLanguages,
        exportLanguages,
        plugins,
        programmeDataExportMetadata,
        isExportDisabled: this.isExportDisabled(programmeDataExportMetadata)
      })),
    );
  }

  initForm(plugins: PluginInfoDTO[]): void {
    this.exportForm = this.formBuilder.group({
      inputLanguage: [this.pageStore.fallBackLanguage],
      exportLanguage: [this.pageStore.fallBackLanguage],
      pluginKey: [plugins.length > 0 ? plugins[0].key : undefined],
    });
  }

  downloadData(pluginKey: string): void {
    this.pageStore.download(pluginKey).pipe(untilDestroyed(this)).subscribe();
  }

  exportData(pluginKey: string, exportLanguage: string, inputLanguage: string): void {
    this.pageStore.exportData(pluginKey, exportLanguage, inputLanguage).pipe(untilDestroyed(this)).subscribe();
  }

  isExportDisabled(programmeDataExportMetadata: ProgrammeDataExportMetadataDTO[]): boolean {
    let isAnyNotTimedOutExportInProgress = false;
    programmeDataExportMetadata.forEach((val) => {
      if (!val.readyToDownload && !val.failed && !val.timedOut) {
        isAnyNotTimedOutExportInProgress = true;
      }
    });
    return isAnyNotTimedOutExportInProgress || !this.pluginKey || !this.exportLanguage || !this.inputLanguage;
  }

  getPluginName(plugins: PluginInfoDTO[],pluginKey: string) : string {
    return plugins.find(it => it.key === pluginKey)?.name || pluginKey
  }
  get inputLanguage(): string {
    return this.exportForm.get('inputLanguage')?.value || this.pageStore.fallBackLanguage;
  }

  get exportLanguage(): string {
    return this.exportForm.get('exportLanguage')?.value || this.pageStore.fallBackLanguage;
  }

  get pluginKey(): string {
    return this.exportForm.get('pluginKey')?.value;
  }

  ngOnDestroy(): void {
    clearInterval(this.refreshInterval);
  }
}
