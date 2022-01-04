import {ChangeDetectionStrategy, Component} from '@angular/core';
import {combineLatest, Observable} from 'rxjs';
import {map, tap} from 'rxjs/operators';
import {ExportPageStore} from '@project/project-application/export/export-page-store';
import {FormBuilder, FormGroup} from '@angular/forms';
import {CategoryInfo, CategoryNode} from '@project/common/components/category-tree/categoryModels';
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
    if (selectedCategory?.type && projectId && exportLanguage && inputLanguage) {
      let url = `/api/project/${projectId}/export/${selectedCategory.type}?exportLanguage=${exportLanguage}&inputLanguage=${inputLanguage}`;
      url =  version ? url + `&version=${version}` : url;
      window.open(url, '_blank');
    }
  }

  resetForm(versions: ProjectVersionDTO[]): void {
    this.exportForm = this.formBuilder.group({
      inputLanguage: [this.exportPageStore.fallBackLanguage],
      exportLanguage: [this.exportPageStore.fallBackLanguage],
      version: [versions.find(it=>it.current)?.version || versions[0].version],
    });
  }

  onCategoryChanged(categoryInfo: CategoryInfo): void {
    this.exportPageStore.selectedCategory$.next(categoryInfo);
  }

  getVersion(versions: ProjectVersionDTO[]): string | null {
    const selectedVersion = versions.find(it => it.version === this.exportForm.get('version')?.value);
    return selectedVersion ? (selectedVersion.current ? null : selectedVersion.version) : null
  }

  get inputLanguage(): string {
    return this.exportForm.get('inputLanguage')?.value || this.exportPageStore.fallBackLanguage;
  }

  get exportLanguage(): string {
    return this.exportForm.get('exportLanguage')?.value || this.exportPageStore.fallBackLanguage;
  }
}
