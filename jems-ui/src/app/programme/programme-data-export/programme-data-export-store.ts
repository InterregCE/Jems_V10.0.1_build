import {Injectable} from '@angular/core';
import {LanguageStore} from '@common/services/language-store.service';
import {BehaviorSubject, combineLatest, Observable} from 'rxjs';
import {PluginStore} from '@common/services/plugin-store.service';
import {PluginInfoDTO, ProgrammeDataExportMetadataDTO, ProgrammeDataExportService} from '@cat/api';
import {switchMap, tap} from 'rxjs/operators';
import {DownloadService} from '@common/services/download.service';
import TypeEnum = PluginInfoDTO.TypeEnum;

@Injectable()
export class ProgrammeDataExportStore {

  inputLanguages$ = this.languageStore.inputLanguages$;
  exportLanguages$ = this.languageStore.systemLanguages$;
  fallBackLanguage = this.languageStore.getFallbackLanguageValue();

  programmeDataExportPlugins$: Observable<PluginInfoDTO[]>;
  programmeDataExportMetadata$: Observable<ProgrammeDataExportMetadataDTO[]>;

  private exportTriggeredEvent$ = new BehaviorSubject<void>(undefined);
  private refreshExportMetadata$ = new BehaviorSubject<void>(undefined);

  constructor(private languageStore: LanguageStore, private pluginStore: PluginStore, private exportService: ProgrammeDataExportService, private downloadService: DownloadService) {
    this.programmeDataExportPlugins$ = this.pluginStore.getPluginListByType(TypeEnum.PROGRAMMEDATAEXPORT);
    this.programmeDataExportMetadata$ = combineLatest([this.exportTriggeredEvent$, this.refreshExportMetadata$]).pipe(switchMap(() => this.exportService.list()));
  }

  exportData(pluginKey: string, exportLanguage: string, inputLanguage: string): Observable<any> {
    return this.exportService._export(exportLanguage, inputLanguage, pluginKey).pipe(
      tap(() => this.exportTriggeredEvent$.next())
    );
  }

  download(pluginKey: string): Observable<any> {
    return this.downloadService.download(`/api/programme/export/download?pluginKey=${pluginKey}`, 'programme-data.xlsx');
  }

  refreshExportMetaData() {
    this.refreshExportMetadata$.next();
  }
}
