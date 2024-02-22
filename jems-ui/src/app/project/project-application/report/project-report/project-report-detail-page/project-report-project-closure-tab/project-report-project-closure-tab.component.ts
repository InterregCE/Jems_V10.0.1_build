import {Component} from '@angular/core';
import {FormService} from '@common/components/section/form/form.service';
import {combineLatest, Observable, of} from 'rxjs';
import {
  InputTranslation,
  ProjectReportProjectClosureDTO,
  ProjectReportProjectClosurePrizeDTO, UpdateProjectReportProjectResultDTO,
} from '@cat/api';
import {
  ProjectStore
} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {
  ProjectReportPageStore
} from '@project/project-application/report/project-report/project-report-page-store.service';
import {
  ProjectReportDetailPageStore
} from '@project/project-application/report/project-report/project-report-detail-page/project-report-detail-page-store.service';
import {FormArray, FormBuilder, Validators} from '@angular/forms';
import {TranslateService} from '@ngx-translate/core';
import {
  ProjectReportProjectClosureTabStore
} from '@project/project-application/report/project-report/project-report-detail-page/project-report-project-closure-tab/project-report-project-closure-tab.store';
import {catchError, map, take, tap} from 'rxjs/operators';
import {LanguageStore} from '@common/services/language-store.service';

@Component({
  selector: 'jems-project-report-project-closure-tab',
  templateUrl: './project-report-project-closure-tab.component.html',
  styleUrls: ['./project-report-project-closure-tab.component.scss'],
  providers: [FormService],
})
export class ProjectReportProjectClosureTabComponent {

  data$: Observable<{
    projectId: number;
    projectReportId: number;
    reportEditable: boolean;
    projectClosure: ProjectReportProjectClosureDTO;
  }>;

  form = this.formBuilder.group({
    story: [[], Validators.maxLength(5000)],
    storyEn: [[], Validators.maxLength(5000)],
    prizes: this.formBuilder.array([]),
  });

  LANGUAGE = InputTranslation.LanguageEnum;

  constructor(
    private projectStore: ProjectStore,
    private projectReportPageStore: ProjectReportPageStore,
    private projectReportDetailPageStore: ProjectReportDetailPageStore,
    private projectReportProjectClosureTabStore: ProjectReportProjectClosureTabStore,
    private formBuilder: FormBuilder,
    private formService: FormService,
    protected translationService: TranslateService,
    public languageStore: LanguageStore
  ) {
    this.data$ = combineLatest([
      this.projectReportDetailPageStore.projectReport$,
      this.projectReportDetailPageStore.reportEditable$,
      this.projectReportProjectClosureTabStore.projectClosure$
    ]).pipe(
      map(([projectReport, reportEditable, projectClosure]) => ({
        projectId: projectReport.projectId,
        projectReportId: projectReport.id,
        reportEditable,
        projectClosure,
      })),
      tap(data => this.formService.init(this.form, of(data.reportEditable))),
      tap(data => this.resetForm(data.projectClosure)),
    );
  }

  get prizes(): FormArray {
    return this.form.get('prizes') as FormArray;
  }

  saveForm() {
    this.projectReportProjectClosureTabStore.updateProjectClosure(this.convertFormToDTO())
      .pipe(
        take(1),
        tap(() => this.formService.setSuccess('project.application.form.save.success')),
        catchError(error => this.formService.setError(error)),
      )
      .subscribe();
  }

  resetForm(projectClosure: ProjectReportProjectClosureDTO) {
    this.form.get('story')?.setValue(projectClosure.story || []);
    if (!this.languageStore.isInputLanguageExist(this.LANGUAGE.EN)) {
      this.form.controls.introEn.setValue(projectClosure.story || []);
    }
    this.prizes.clear();
    projectClosure.prizes.forEach((prizeDTO: ProjectReportProjectClosurePrizeDTO) => {
      this.addPrizeToForm(prizeDTO);
    });
  }

  private convertFormToDTO(): ProjectReportProjectClosureDTO {
    const prizeDTOs: ProjectReportProjectClosurePrizeDTO[] = [];
    this.prizes.getRawValue().forEach((value, index) => {
      prizeDTOs.push({
        orderNum: index + 1,
        prize: value.prize,
        id: value.id
      } as ProjectReportProjectClosurePrizeDTO);
    });

    const projectClosureDTO = {
      prizes: prizeDTOs,
      story: this.form.controls.story.value || [],
    } as ProjectReportProjectClosureDTO;

    const english = this.form.controls.storyEn?.value?.find((translation: any) => translation.language === 'EN');
    if (english) {
      projectClosureDTO.story = projectClosureDTO.story.filter(translation => translation.language !== 'EN').concat(english);
    }
    return projectClosureDTO;
  }

  addNewPrize(): void {
    this.addPrizeToForm();
    this.formService.setDirty(true);
  }

  deletePrize(elementIndex: number): void {
    this.prizes.removeAt(elementIndex);
    this.formService.setDirty(true);
  }

  private addPrizeToForm(prize?: ProjectReportProjectClosurePrizeDTO): void {
    this.prizes.push(this.formBuilder.group({
      prize: this.formBuilder.control(prize?.prize || [], [Validators.maxLength(500)]),
      id: this.formBuilder.control(prize?.id || null)
    }));
  }
}
