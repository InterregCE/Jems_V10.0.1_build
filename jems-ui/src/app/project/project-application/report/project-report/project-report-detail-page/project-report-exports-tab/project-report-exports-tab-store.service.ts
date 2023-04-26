import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {PluginInfoDTO, PluginService} from '@cat/api';
import {
  ProjectStore
} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {LanguageStore} from '@common/services/language-store.service';

@Injectable({providedIn: 'root'})
export class ProjectReportExportsPageStore {

  inputLanguages$ = this.languageStore.inputLanguages$;
  exportLanguages$ = this.languageStore.systemLanguages$;
  fallBackLanguage = this.languageStore.getFallbackLanguageValue();
  availablePlugins$: Observable<PluginInfoDTO[]>;

  constructor(private projectStore: ProjectStore,
              private languageStore: LanguageStore,
              private pluginService: PluginService) {
    this.availablePlugins$ = this.getExportPlugins();
  }

  private getExportPlugins(): Observable<PluginInfoDTO[]> {
    return this.pluginService.getAvailablePluginList(PluginInfoDTO.TypeEnum.REPORTPROJECTEXPORT);
  }

}
