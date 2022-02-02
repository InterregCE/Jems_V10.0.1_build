import {Injectable} from '@angular/core';
import {LanguageStore} from '@common/services/language-store.service';
import {Observable} from 'rxjs';
import {PluginStore} from '@common/services/plugin-store.service';
import {PluginInfoDTO} from '@cat/api';
import TypeEnum = PluginInfoDTO.TypeEnum;

@Injectable()
export class ProgrammeDataExportStore {

  inputLanguages$ = this.languageStore.inputLanguages$;
  exportLanguages$ = this.languageStore.systemLanguages$;
  fallBackLanguage = this.languageStore.getFallbackLanguageValue();

  programmeDataExportPlugins$: Observable<PluginInfoDTO[]>;

  constructor(private languageStore: LanguageStore, private pluginStore: PluginStore) {
    this.programmeDataExportPlugins$ = this.pluginStore.getPluginListByType(TypeEnum.PROGRAMMEDATAEXPORT);
  }

}
