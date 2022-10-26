import {Injectable} from '@angular/core';
import {ProjectStore} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {LanguageStore} from '@common/services/language-store.service';
import {BehaviorSubject, combineLatest, Observable} from 'rxjs';
import {CategoryInfo, CategoryNode} from '@project/common/components/category-tree/categoryModels';
import {map} from 'rxjs/operators';
import {ExportCategoryTypeEnum} from '@project/project-application/export/export-category-type';
import {ProjectPageTemplateStore} from '@project/project-page-template/project-page-template-store.service';
import {PluginInfoDTO, PluginService} from '@cat/api';
import {PluginType} from '@project/project-application/export/export-plugin-type';

@Injectable()
export class ExportPageStore {

  inputLanguages$ = this.languageStore.inputLanguages$;
  exportLanguages$ = this.languageStore.systemLanguages$;
  projectVersions$ = this.projectVersionStore.versions$;
  projectId$ = this.projectStore.projectId$;
  projectTitle$ = this.projectStore.projectTitle$;
  fallBackLanguage = this.languageStore.getFallbackLanguageValue();

  availablePlugins$: Observable<PluginType[]>;
  selectedCategory$ = new BehaviorSubject<CategoryInfo>({type: ExportCategoryTypeEnum.APPLICATION});
  projectCallType$ = this.projectStore.projectCallType$;

  constructor(private projectStore: ProjectStore,
              private languageStore: LanguageStore,
              private projectVersionStore: ProjectPageTemplateStore,
              private pluginService: PluginService) {
    this.availablePlugins$ = this.getExportPlugins();
  }

  private getExportPlugins(): Observable<PluginType[]> {
    return combineLatest([
      this.pluginService.getAvailablePluginList(PluginInfoDTO.TypeEnum.APPLICATIONFORMEXPORT),
      this.pluginService.getAvailablePluginList(PluginInfoDTO.TypeEnum.BUDGETEXPORT)
    ]).pipe(
      map(([appPlugins, budgetPlugins]) => [
        ...appPlugins.map(plugin => ({type: ExportCategoryTypeEnum.APPLICATION, plugin} as PluginType)),
        ...budgetPlugins.map(plugin => ({type: ExportCategoryTypeEnum.BUDGET, plugin} as PluginType))
      ])
    );
  }
}
