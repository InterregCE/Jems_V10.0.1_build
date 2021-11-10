import {ChangeDetectionStrategy, Component} from '@angular/core';
import {combineLatest, Observable} from 'rxjs';
import {map, tap} from 'rxjs/operators';
import {ExportPageStore} from '@project/project-application/export/export-page-store.service';
import {FormBuilder, FormGroup} from '@angular/forms';
import {CategoryInfo, CategoryNode} from '@project/common/components/category-tree/categoryModels';
import {ExportCategoryTypeEnum} from '@project/project-application/export/export-category-type';
import {ProjectVersionDTO} from '@cat/api';

@Component({
  selector: 'app-export',
  templateUrl: './export.component.html',
  styleUrls: ['./export.component.scss'],
  providers: [ExportPageStore],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ExportComponent {

  exportForm: FormGroup;
  selectedCategory$ = this.exportPageStore.selectedCategory$;

  data$: Observable<{
    projectTitle: string;
    projectId: number;
    inputLanguages: string[];
    exportLanguages: string[];
    versions: ProjectVersionDTO[];
    categories: CategoryNode;
  }>;

  constructor(private exportPageStore: ExportPageStore, private formBuilder: FormBuilder) {
    this.data$ = combineLatest([
      this.exportPageStore.projectId$,
      this.exportPageStore.projectTitle$,
      this.exportPageStore.inputLanguages$,
      this.exportPageStore.exportLanguages$,
      this.exportPageStore.projectVersions$,
      this.exportPageStore.exportCategories$,
    ]).pipe(
      map(([projectId, projectTitle, inputLanguages, exportLanguages, projectVersions, categories]) => ({
        projectId,
        projectTitle,
        inputLanguages,
        exportLanguages,
        versions: projectVersions,
        categories
      })),
      tap((data) => this.resetForm(data.versions))
    );
  }

  exportData(selectedCategory: CategoryInfo, exportLanguage: string, inputLanguage: string, projectId: number, version: string | null): void {
    if (selectedCategory && projectId && exportLanguage && inputLanguage) {
      let url = null;
      switch (selectedCategory.type) {
        case ExportCategoryTypeEnum.BUDGET:
          url = `/api/project/${projectId}/budget/export?exportLanguage=${exportLanguage}&inputLanguage=${inputLanguage}`;
          break;
      }
      url = (url && version) ? url + `&version=${version}` : url;

      if (url) {
        window.open(url, '_blank');
      }
    }
  }

  resetForm(versions: ProjectVersionDTO[]): void {
    this.exportForm = this.formBuilder.group({
      inputLanguage: [this.exportPageStore.fallBackLanguage],
      exportLanguage: [this.exportPageStore.fallBackLanguage],
      version: [versions[0].version],
    });
  }

  onCategoryChanged(categoryInfo: CategoryInfo): void {
    this.exportPageStore.selectedCategory$.next(categoryInfo);
  }

  getVersion(versions: ProjectVersionDTO[]): string | null {
    const selectedVersion = this.exportForm.get('version')?.value;
    return versions.find(it => it.version === selectedVersion)?.createdAt ? selectedVersion : null;
  }

  get inputLanguage(): string {
    return this.exportForm.get('inputLanguage')?.value || this.exportPageStore.fallBackLanguage;
  }

  get exportLanguage(): string {
    return this.exportForm.get('exportLanguage')?.value || this.exportPageStore.fallBackLanguage;
  }
}
