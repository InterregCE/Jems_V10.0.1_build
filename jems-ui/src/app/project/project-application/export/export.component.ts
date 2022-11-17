import {ChangeDetectionStrategy, Component} from '@angular/core';
import {BehaviorSubject, combineLatest, Observable} from 'rxjs';
import {finalize, map, tap} from 'rxjs/operators';
import {ExportPageStore} from '@project/project-application/export/export-page-store';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {ProjectCallSettingsDTO, ProjectVersionDTO} from '@cat/api';
import {DownloadService} from '@common/services/download.service';
import moment from 'moment';
import {PluginType} from '@project/project-application/export/export-plugin-type';

@Component({
  selector: 'jems-export',
  templateUrl: './export.component.html',
  styleUrls: ['./export.component.scss'],
  providers: [ExportPageStore],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ExportComponent {

  exportForm: FormGroup;
  isExportingInProgress$ = new BehaviorSubject(false);

  data$: Observable<{
    projectTitle: string;
    projectId: number;
    inputLanguages: string[];
    exportLanguages: string[];
    versions: ProjectVersionDTO[];
    availablePlugins: PluginType[];
    isSPFProjectCallType: boolean;
  }>;

  constructor(private exportPageStore: ExportPageStore, private formBuilder: FormBuilder, private downloadService: DownloadService) {
    this.data$ = combineLatest([
      this.exportPageStore.projectId$,
      this.exportPageStore.projectTitle$,
      this.exportPageStore.inputLanguages$,
      this.exportPageStore.exportLanguages$,
      this.exportPageStore.projectVersions$,
      this.exportPageStore.availablePlugins$,
      this.exportPageStore.projectCallType$
    ]).pipe(
      map(([projectId, projectTitle, inputLanguages, exportLanguages, projectVersions, availablePlugins, projectCallType]:
             [number, string, string[], string[], ProjectVersionDTO[], PluginType[], ProjectCallSettingsDTO.CallTypeEnum]) => ({
        projectId,
        projectTitle,
        inputLanguages,
        exportLanguages,
        versions: projectVersions,
        availablePlugins,
        isSPFProjectCallType: projectCallType === ProjectCallSettingsDTO.CallTypeEnum.SPF
      })),
      tap((data) => this.resetForm(data.versions, data.inputLanguages, data.exportLanguages))
    );
  }

  exportData(exportLanguage: string, inputLanguage: string, projectId: number, version: string | null): void {
    const plugin = this.exportPlugin;
    if (plugin?.type && projectId && exportLanguage && inputLanguage) {
      this.isExportingInProgress$.next(true);
      const localDateTime = moment().format('YYYY-MM-DDTHH:mm:ss');
      let url = `/api/project/${projectId}/export/${plugin.type}?exportLanguage=${exportLanguage}&inputLanguage=${inputLanguage}&localDateTime=${localDateTime}&pluginKey=${plugin.plugin.key}`;
      url = version ? url + `&version=${version}` : url;
      this.downloadService.download(url, plugin.type === 'application' ? 'application-form-export.pdf' : 'budget-export.csv').pipe(
        finalize(() => this.isExportingInProgress$.next(false)),
      ).subscribe();
    }
  }

  resetForm(versions: ProjectVersionDTO[], inputLanguages: string[], exportLanguages: string[]): void {
    this.exportForm = this.formBuilder.group({
      plugin:[null, Validators.required],
      inputLanguage: [this.setFallBackLanguageIfInLanguageList(inputLanguages)],
      exportLanguage: [this.setFallBackLanguageIfInLanguageList(exportLanguages)],
      version: [versions.find(it => it.current)?.version || versions[0].version],
    });
  }

  getVersion(versions: ProjectVersionDTO[]): string | null {
    const selectedVersion = versions.find(it => it.version === this.exportForm.get('version')?.value);
    return selectedVersion ? selectedVersion.version : null;
  }

  get inputLanguage(): string {
    return this.exportForm.get('inputLanguage')?.value;
  }

  get exportLanguage(): string {
    return this.exportForm.get('exportLanguage')?.value;
  }

  get exportPlugin(): PluginType {
    return this.exportForm.get('plugin')?.value;
  }

  setFallBackLanguageIfInLanguageList(languagesList: string[]): string {
    return languagesList.includes(this.exportPageStore.fallBackLanguage) ? this.exportPageStore.fallBackLanguage : '';
  }
}
