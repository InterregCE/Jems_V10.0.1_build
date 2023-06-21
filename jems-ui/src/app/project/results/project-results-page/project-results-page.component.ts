import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {FormService} from '@common/components/section/form/form.service';
import {ProjectResultsPageStore} from './project-results-page-store.service';
import {ProjectResultsPageConstants} from './project-results-page.constants';
import {combineLatest, Observable} from 'rxjs';
import {FormArray, FormBuilder} from '@angular/forms';
import {catchError, map, take,tap} from 'rxjs/operators';
import {
  InputTranslation,
  OutputProgrammePriorityPolicySimpleDTO, ProjectDetailFormDTO,
  ProjectPeriodDTO,
  ProjectResultDTO, ProjectStatusDTO,
  ResultIndicatorSummaryDTO,
} from '@cat/api';
import {ActivatedRoute} from '@angular/router';
import {APPLICATION_FORM} from '@project/common/application-form-model';
import {MatSelectChange} from '@angular/material/select/select';
import {Alert} from '@common/components/forms/alert';
import {
  ProjectStore
} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {ProjectUtil} from '@project/common/project-util';

@Component({
  selector: 'jems-project-results-page',
  templateUrl: './project-results-page.component.html',
  styleUrls: ['./project-results-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [FormService, ProjectResultsPageStore]
})
export class ProjectResultsPageComponent implements OnInit {
  constants = ProjectResultsPageConstants;
  APPLICATION_FORM = APPLICATION_FORM;

  projectId = this.activatedRoute?.snapshot?.params?.projectId;
  form = this.formBuilder.group({
    results: this.formBuilder.array([])
  });

  data$: Observable<{
    results: ProjectResultDTO[];
    resultIndicators: ResultIndicatorSummaryDTO[];
    periods: ProjectPeriodDTO[];
    projectId: number;
    projectTitle: string;
    specificObjective: OutputProgrammePriorityPolicySimpleDTO;
    isAlreadyContracted: boolean;
  }>;
  Alert = Alert;

  constructor(public formService: FormService,
              private formBuilder: FormBuilder,
              private projectResultsPageStore: ProjectResultsPageStore,
              private projectStore: ProjectStore,
              private activatedRoute: ActivatedRoute) {
    this.formService.init(this.form, this.projectResultsPageStore.isProjectEditable$);
  }

  ngOnInit(): void {

    this.data$ = combineLatest([
      this.projectResultsPageStore.results$,
      this.projectResultsPageStore.resultIndicators$,
      this.projectResultsPageStore.periods$,
      this.projectResultsPageStore.projectId$,
      this.projectResultsPageStore.projectTitle$,
      this.projectResultsPageStore.projectForm$,
      this.projectStore.currentVersionOfProjectStatus$
    ])
      .pipe(
        map(([results, resultIndicators, periods, projectId, projectTitle, projectForm$, projectStatus]: [ProjectResultDTO[], ResultIndicatorSummaryDTO[], ProjectPeriodDTO[], number, string, ProjectDetailFormDTO, ProjectStatusDTO]) => (
          {results, resultIndicators, periods, projectId, projectTitle, specificObjective: projectForm$.specificObjective, isAlreadyContracted: ProjectUtil.isContractedOrAnyStatusAfterContracted(projectStatus)})
        ),
        tap(data => this.resetForm(data.results, data.resultIndicators))
      );
  }

  updateResults(): void {
    this.projectResultsPageStore.saveResults(this.results.value)
      .pipe(
        take(1),
        tap(() => this.formService.setSuccess('project.results.saved.successfully')),
        catchError(err => this.formService.setError(err))
      ).subscribe();
  }

  addNewResult(): void {
    this.addResult();
    this.formService.setDirty(true);
  }

  removeResult(index: number): void {
    this.results.removeAt(index);
    this.results.controls.forEach(
      (result, i) => result.get(this.constants.RESULT_NUMBER.name)?.setValue(i)
    );
    this.formService.setDirty(true);
  }

  deactivateResult(index: number): void {
    this.results.at(index).get('deactivated')?.setValue(true);
    this.formService.setDirty(true);
  }

  get results(): FormArray {
    return this.form.get(this.constants.RESULTS.name) as FormArray;
  }

  addResultVisible(): boolean {
    return this.form.enabled && this.results.length < 20;
  }

  getMeasurementUnit(indicatorId: number, indicators: ResultIndicatorSummaryDTO[]): InputTranslation[] {
    return indicators.find(indicator => indicator.id === indicatorId)?.measurementUnit || [];
  }

  resetForm(results: ProjectResultDTO[], resultIndicators: ResultIndicatorSummaryDTO[]): void {
    this.results.clear();
    results.forEach((result) => this.addResult(result, resultIndicators));
    this.formService.resetEditable();
  }

  updateBaseLineData(event: MatSelectChange, resultIndicators: ResultIndicatorSummaryDTO[], index: number): void {
    const baselineValue = resultIndicators.find(indicator => indicator.id === event.value)?.baseline || 0;
    this.results.controls[index]?.get(this.constants.BASELINE_MAX_VALUE.name)?.patchValue(baselineValue);
    setTimeout(() => {
      this.results.controls[index]?.get(this.constants.BASELINE.name)?.patchValue(baselineValue);
    });
  }

  private addResult(existing?: ProjectResultDTO, resultIndicators?: ResultIndicatorSummaryDTO[]): void {
    const baselineFromIndicator = resultIndicators?.find(it => it.id === existing?.programmeResultIndicatorId)?.baseline;
    const baselineMaxValue = (baselineFromIndicator || baselineFromIndicator === 0) ? baselineFromIndicator : 999_999_999.99;
    this.results.push(this.formBuilder.group(
      {
        programmeResultIndicatorId: this.formBuilder.control(existing?.programmeResultIndicatorId),
        resultNumber: this.formBuilder.control(existing?.resultNumber || 0),
        targetValue: this.formBuilder.control(existing?.targetValue || 1),
        baseline: this.formBuilder.control(existing?.baseline || 0),
        baselineMaxValue: this.formBuilder.control(baselineMaxValue),
        periodNumber: this.formBuilder.control(existing?.periodNumber || ''),
        description: this.formBuilder.control(existing?.description || []),
        deactivated: this.formBuilder.control(existing?.deactivated || false),
      })
    );
  }

}
