import {Component} from '@angular/core';
import {FormService} from '@common/components/section/form/form.service';
import {combineLatest, Observable, of} from 'rxjs';
import {FormArray, FormBuilder, FormGroup, Validators} from '@angular/forms';
import {
  ProjectStore
} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {
  ProjectReportPageStore
} from '@project/project-application/report/project-report/project-report-page-store.service';
import {
  ProjectReportDetailPageStore
} from '@project/project-application/report/project-report/project-report-detail-page/project-report-detail-page-store.service';
import {catchError, finalize, map, take, tap} from 'rxjs/operators';
import {
  ProjectReportResultsAndPrinciplesTabStore
} from '@project/project-application/report/project-report/project-report-detail-page/project-report-results-and-principles-tab/project-report-results-and-principles-tab.store';
import {
  JemsFileMetadataDTO,
  ProjectReportProjectResultDTO,
  ProjectReportResultPrincipleDTO,
  UpdateProjectReportProjectResultDTO,
  UpdateProjectReportResultPrincipleDTO
} from '@cat/api';
import {TranslateService} from '@ngx-translate/core';
import {APPLICATION_FORM} from '@project/common/application-form-model';
import {SelectionModel} from '@angular/cdk/collections';
import {NumberService} from '@common/services/number.service';
import {ReportUtil} from '@project/common/report-util';
import {ProjectUtil} from '@project/common/project-util';

@Component({
  selector: 'jems-project-report-results-and-principles-tab',
  templateUrl: './project-report-results-and-principles-tab.component.html',
  styleUrls: ['./project-report-results-and-principles-tab.component.scss'],
  providers: [FormService],
})
export class ProjectReportResultsAndPrinciplesTabComponent {
  APPLICATION_FORM = APPLICATION_FORM;
  MIN_VALUE = -999_999_999.99;
  MAX_VALUE = 999_999_999.99;
  form = this.formBuilder.group({
    results: this.formBuilder.array([]),
    principles: this.formBuilder.group({
      principlesSustainable: ['', Validators.maxLength(2000)],
      principlesOpportunities: ['', Validators.maxLength(2000)],
      principlesEquality: ['', Validators.maxLength(2000)]
    }),
  });

  ReportUtil = ReportUtil;
  ProjectUtil = ProjectUtil;
  isUploadDone = false;
  selection = new SelectionModel<string>(true, []);
  selectedContributionPrincipleDevelopment = '';
  selectedContributionPrincipleOpportunities = '';
  selectedContributionPrincipleEquality = '';
  initialCumulativeValues: number[] = [];

  data$: Observable<{
    projectId: number;
    projectReportId: number;
    reportEditable: boolean;
    reopenedLimited: boolean;
    resultsAndPrinciples: ProjectReportResultPrincipleDTO;
  }>;

  toggleStatesOfResults: boolean[] = [];

  constructor(
    private projectStore: ProjectStore,
    private projectReportPageStore: ProjectReportPageStore,
    private projectReportDetailPageStore: ProjectReportDetailPageStore,
    private resultsAndPrinciplesTabStore: ProjectReportResultsAndPrinciplesTabStore,
    private formBuilder: FormBuilder,
    private formService: FormService,
    protected translationService: TranslateService,
  ) {
    this.data$ = combineLatest([
      this.projectReportDetailPageStore.projectReport$,
      this.projectReportDetailPageStore.reportEditable$,
      this.resultsAndPrinciplesTabStore.resultsAndPrinciples$
    ]).pipe(
      map(([projectReport, reportEditable, resultsAndPrinciples]) => ({
        projectId: projectReport.projectId,
        projectReportId: projectReport.id,
        reportEditable,
        resultsAndPrinciples,
        reopenedLimited: ReportUtil.isProjectReportLimitedReopened(projectReport.status)
      })),
      tap(data => this.formService.init(this.form, of(data.reportEditable))),
      tap(data => this.resetForm(data.resultsAndPrinciples, data.reportEditable, data.reopenedLimited)),
    );
  }

  saveForm() {
    this.resultsAndPrinciplesTabStore.updateResultsAndPrinciples(this.convertFormToDTO())
      .pipe(
        take(1),
        tap(() => this.formService.setSuccess('project.application.form.save.success')),
        catchError(error => this.formService.setError(error)),
      )
      .subscribe();
  }

  get results(): FormArray {
    return this.form.get('results') as FormArray;
  }

  get principles(): FormGroup {
    return this.form.get('principles') as FormGroup;
  }

  onUpload(target: any, resultNumber: number, index: number) {
    if (!target) {
      return;
    }
    this.isUploadDone = false;
    this.resultsAndPrinciplesTabStore.uploadFile(target?.files[0], resultNumber)
      .pipe(
        take(1),
        catchError(err => this.formService.setError(err)),
        finalize(() => this.isUploadDone = true)
      )
      .subscribe(value => {
        this.results.at(index).patchValue({attachment: value});
      });
  }

  onDownload(resultNumber: number) {
    this.resultsAndPrinciplesTabStore.downloadFile(resultNumber)
      .pipe(take(1))
      .subscribe();
  }

  onDelete(resultNumber: number, index: number) {
    this.resultsAndPrinciplesTabStore.deleteFile(resultNumber)
      .pipe(take(1))
      .subscribe(() => this.results.at(index).patchValue({attachment: null}));
  }

  resetForm(resultsAndPrinciples: ProjectReportResultPrincipleDTO, reportEditable: boolean, reopenedLimited: boolean) {
    const attachments: [JemsFileMetadataDTO] = this.results.value.map((result: any) => result.attachment);
    this.initialCumulativeValues = resultsAndPrinciples.projectResults.map((result: ProjectReportProjectResultDTO) => result.cumulativeValue);
    this.results.clear();
    resultsAndPrinciples.projectResults.forEach((resultDTO: ProjectReportProjectResultDTO, index) => {
      const result = this.formBuilder.group({
        resultNumber: this.formBuilder.control(resultDTO.resultNumber),
        deactivated: this.formBuilder.control(resultDTO.deactivated),
        indicatorId: this.formBuilder.control(resultDTO.programmeResultIndicatorId),
        indicator: this.formBuilder.control(resultDTO.programmeResultIndicatorIdentifier),
        indicatorName: this.formBuilder.control(resultDTO.programmeResultIndicatorName),
        baseline: this.formBuilder.control({value: resultDTO.baseline, disabled: true}),
        periodDetail: this.formBuilder.control(resultDTO.periodDetail),
        targetValue: this.formBuilder.control({value: resultDTO.targetValue, disabled: true}),
        achievedInReportingPeriod: this.formBuilder.control({value: resultDTO.achievedInReportingPeriod, disabled: !reportEditable || reopenedLimited}),
        cumulativeValue: this.formBuilder.control({value: resultDTO.cumulativeValue + resultDTO.achievedInReportingPeriod, disabled: true}),
        description: this.formBuilder.control(resultDTO.description ?? [], [Validators.maxLength(2000)]),
        measurementUnit: this.formBuilder.control({value: resultDTO.measurementUnit, disabled: true}),
        attachment: this.formBuilder.control(resultDTO.attachment ?? attachments[index]),
      });
      this.results.push(result);
    });
    this.principles.controls.principlesSustainable.setValue(resultsAndPrinciples?.sustainableDevelopmentDescription);
    this.principles.controls.principlesOpportunities.setValue(resultsAndPrinciples?.equalOpportunitiesDescription);
    this.principles.controls.principlesEquality.setValue(resultsAndPrinciples?.sexualEqualityDescription);
    this.selectedContributionPrincipleDevelopment = resultsAndPrinciples?.horizontalPrinciples.sustainableDevelopmentCriteriaEffect;
    this.selectedContributionPrincipleOpportunities = resultsAndPrinciples?.horizontalPrinciples.equalOpportunitiesEffect;
    this.selectedContributionPrincipleEquality = resultsAndPrinciples?.horizontalPrinciples.sexualEqualityEffect;
  }

  private convertFormToDTO(): UpdateProjectReportResultPrincipleDTO {
    const projectResultDTOs = [];
    for (const item of this.results.getRawValue()) {
      projectResultDTOs.push({
        resultNumber: item.resultNumber,
        achievedInReportingPeriod: item.achievedInReportingPeriod,
        description: item.description,
      } as UpdateProjectReportProjectResultDTO);
    }

    return {
      projectResults: projectResultDTOs,
      sustainableDevelopmentDescription: this.principles.controls.principlesSustainable.value || [],
      equalOpportunitiesDescription: this.principles.controls.principlesOpportunities.value || [],
      sexualEqualityDescription: this.principles.controls.principlesEquality.value || [],
    } as UpdateProjectReportResultPrincipleDTO;
  }

  totalChanged(resultIndex: number) {
    const getFormControl = (formControl: string) => this.results.get(String(resultIndex))?.get(formControl);
    const achievedInReportingPeriod = getFormControl('achievedInReportingPeriod')?.value ?? 0;
    const cumulativeValue = getFormControl('cumulativeValue');

    cumulativeValue?.patchValue(NumberService.sum([
      achievedInReportingPeriod,
      this.initialCumulativeValues[resultIndex] ?? 0]
    ));
  }

  toggleResultsRowAtIndex(index: number): void {
    this.toggleStatesOfResults[index] = !this.toggleStatesOfResults[index];
  }

  getResultsRowToggleStateAtIndex(index: number): boolean {
    return this.toggleStatesOfResults[index];
  }

}
