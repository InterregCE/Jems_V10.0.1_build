import {ChangeDetectionStrategy, Component} from '@angular/core';
import {BehaviorSubject, combineLatest, Observable} from 'rxjs';
import {finalize, map, tap} from 'rxjs/operators';
import {ExportPageStore} from '@project/project-application/export/export-page-store';
import {FormBuilder, FormGroup} from '@angular/forms';
import {CategoryInfo, CategoryNode} from '@project/common/components/category-tree/categoryModels';
import {ProjectCallSettingsDTO, ProjectVersionDTO} from '@cat/api';
import {DownloadService} from '@common/services/download.service';
import moment from 'moment';

@Component({
  selector: 'jems-export',
  templateUrl: './export.component.html',
  styleUrls: ['./export.component.scss'],
  providers: [ExportPageStore],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ExportComponent {

  exportForm: FormGroup;
  selectedCategory$ = this.exportPageStore.selectedCategory$;
  isExportingInProgress$ = new BehaviorSubject(false);

  data$: Observable<{
    projectTitle: string;
    projectId: number;
    inputLanguages: string[];
    exportLanguages: string[];
    versions: ProjectVersionDTO[];
    categories: CategoryNode;
    isSPFProjectCallType: boolean;
  }>;

  constructor(private exportPageStore: ExportPageStore, private formBuilder: FormBuilder, private downloadService: DownloadService) {
    this.data$ = combineLatest([
      this.exportPageStore.projectId$,
      this.exportPageStore.projectTitle$,
      this.exportPageStore.inputLanguages$,
      this.exportPageStore.exportLanguages$,
      this.exportPageStore.projectVersions$,
      this.exportPageStore.exportCategories$,
      this.exportPageStore.projectCallType$
    ]).pipe(
      map(([projectId, projectTitle, inputLanguages, exportLanguages, projectVersions, categories, projectCallType]:
             [number, string, string[], string[], ProjectVersionDTO[],CategoryNode,ProjectCallSettingsDTO.CallTypeEnum]) => ({
        projectId,
        projectTitle,
        inputLanguages,
        exportLanguages,
        versions: projectVersions,
        categories,
        isSPFProjectCallType: projectCallType === ProjectCallSettingsDTO.CallTypeEnum.SPF
      })),
      tap((data) => this.resetForm(data.versions, data.inputLanguages, data.exportLanguages))
    );
  }

  exportData(selectedCategory: CategoryInfo, exportLanguage: string, inputLanguage: string, projectId: number, version: string | null): void {
    if (selectedCategory?.type && projectId && exportLanguage && inputLanguage) {
      this.isExportingInProgress$.next(true);
      const localDateTime = moment().format('YYYY-MM-DDTHH:mm:ss');
      let url = `/api/project/${projectId}/export/${selectedCategory.type}?exportLanguage=${exportLanguage}&inputLanguage=${inputLanguage}&localDateTime=${localDateTime}`;
      url = version ? url + `&version=${version}` : url;
      this.downloadService.download(url, selectedCategory.type === 'application' ? 'application-form-export.pdf' : 'budget-export.csv').pipe(
        finalize(() => this.isExportingInProgress$.next(false)),
      ).subscribe();
    }
  }

  resetForm(versions: ProjectVersionDTO[], inputLanguages: string[], exportLanguages: string[]): void {
    this.exportForm = this.formBuilder.group({
      inputLanguage: [this.setFallBackLanguageIfInLanguageList(inputLanguages)],
      exportLanguage: [this.setFallBackLanguageIfInLanguageList(exportLanguages)],
      version: [versions.find(it => it.current)?.version || versions[0].version],
    });
  }

  onCategoryChanged(categoryInfo: CategoryInfo): void {
    this.exportPageStore.selectedCategory$.next(categoryInfo);
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

  setFallBackLanguageIfInLanguageList(languagesList: string[]): string {
    return languagesList.includes(this.exportPageStore.fallBackLanguage) ? this.exportPageStore.fallBackLanguage : '';
  }
}
