import {ChangeDetectionStrategy, Component} from '@angular/core';
import {ProgrammePageSidenavService} from '../programme-page/services/programme-page-sidenav.service';
import {ProgrammeDataExportStore} from './programme-data-export-store';
import {BehaviorSubject, combineLatest, Observable} from 'rxjs';
import {PluginInfoDTO} from '@cat/api';
import {FormBuilder, FormGroup} from '@angular/forms';
import {DownloadService} from '@common/services/download.service';
import {finalize, map, tap} from 'rxjs/operators';

@Component({
  selector: 'jems-programme-data-export',
  templateUrl: './programme-data-export.component.html',
  styleUrls: ['./programme-data-export.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [ProgrammeDataExportStore]

})
export class ProgrammeDataExportComponent {

  exportForm: FormGroup;
  isExportingInProgress$ = new BehaviorSubject(false);

  data$: Observable<{
    inputLanguages: string[];
    exportLanguages: string[];
    plugins: PluginInfoDTO[];
  }>;

  constructor(private programmePageSidenavService: ProgrammePageSidenavService, private pageStore: ProgrammeDataExportStore, private formBuilder: FormBuilder, private downloadService: DownloadService) {
    this.data$ = combineLatest([
      this.pageStore.inputLanguages$,
      this.pageStore.exportLanguages$,
      this.pageStore.programmeDataExportPlugins$,
    ]).pipe(
      map(([inputLanguages, exportLanguages, plugins]) => ({
        inputLanguages,
        exportLanguages,
        plugins
      })),
      tap((data) => this.resetForm(data.plugins))
    );
  }

  resetForm(plugins: PluginInfoDTO[]): void {
    this.exportForm = this.formBuilder.group({
      inputLanguage: [this.pageStore.fallBackLanguage],
      exportLanguage: [this.pageStore.fallBackLanguage],
      pluginKey: [plugins.length > 0 ? plugins[0].key : undefined],
    });
  }

  exportData(pluginKey: string, exportLanguage: string, inputLanguage: string): void {
    this.downloadService.download(`/api/programme/export/?pluginKey=${pluginKey}&exportLanguage=${exportLanguage}&inputLanguage=${inputLanguage}`, 'programme-data-export.xlsx').pipe(
      finalize(() => this.isExportingInProgress$.next(false)),
    ).subscribe();
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
}
