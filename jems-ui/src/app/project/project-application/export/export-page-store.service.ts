import {Injectable} from '@angular/core';
import {ProjectStore} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {LanguageStore} from '@common/services/language-store.service';
import {BehaviorSubject, Observable} from 'rxjs';
import {CategoryInfo, CategoryNode} from '@project/common/components/category-tree/categoryModels';
import {map} from 'rxjs/operators';
import {ExportCategoryTypeEnum} from '@project/project-application/export/export-category-type';
import {ProjectPageTemplateStore} from '@project/project-page-template/project-page-template-store.service';

@Injectable()
export class ExportPageStore {

  inputLanguages$ = this.languageStore.inputLanguages$;
  exportLanguages$ = this.languageStore.systemLanguages$;
  projectVersions$ = this.projectVersionStore.versions$;
  projectId$ = this.projectStore.projectId$;
  projectTitle$ = this.projectStore.projectTitle$;
  fallBackLanguage = this.languageStore.getFallbackLanguageValue();

  exportCategories$: Observable<CategoryNode>;
  selectedCategory$ = new BehaviorSubject<CategoryInfo>({type: ExportCategoryTypeEnum.APPLICATION});

  constructor(private projectStore: ProjectStore,
              private languageStore: LanguageStore,
              private projectVersionStore: ProjectPageTemplateStore) {
    this.exportCategories$ = this.projectStore.projectTitle$.pipe(
      map(projectTitle => ExportPageStore.getExportCategories(projectTitle))
    );
  }

  private static getExportCategories(projectTitle: string): CategoryNode {
    return {
      name: {i18nKey: projectTitle},
      info: {type: ExportCategoryTypeEnum.ALL},
      disabled: true,
      children: [
        {
          name: {i18nKey: 'export.tree.type.application.form'},
          info: {type: ExportCategoryTypeEnum.APPLICATION},
          children: [
            {
              name: {i18nKey: 'export.tree.type.partner.budgets'},
              info: {type: ExportCategoryTypeEnum.BUDGET},
            }
          ]
        }
      ]
    };
  }
}
