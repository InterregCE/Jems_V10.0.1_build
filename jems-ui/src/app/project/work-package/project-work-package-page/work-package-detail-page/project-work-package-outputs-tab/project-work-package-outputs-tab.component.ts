import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {FormService} from '@common/components/section/form/form.service';
import {ProjectWorkPackageOutputsTabConstants} from './project-work-package-outputs-tab.constants';
import {combineLatest, Observable} from 'rxjs';
import {FormArray, FormBuilder} from '@angular/forms';
import {WorkPackagePageStore} from '../work-package-page-store.service';
import {catchError, map, startWith, tap} from 'rxjs/operators';
import {InputTranslation, OutputIndicatorSummaryDTO, ProjectPeriodDTO, WorkPackageOutputDTO} from '@cat/api';
import {take} from 'rxjs/internal/operators';
import {APPLICATION_FORM} from '@project/common/application-form-model';
import {Alert} from '@common/components/forms/alert';

@UntilDestroy()
@Component({
  selector: 'jems-project-work-package-outputs-tab',
  templateUrl: './project-work-package-outputs-tab.component.html',
  styleUrls: ['./project-work-package-outputs-tab.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectWorkPackageOutputsTabComponent implements OnInit {
  constants = ProjectWorkPackageOutputsTabConstants;
  APPLICATION_FORM = APPLICATION_FORM;

  form = this.formBuilder.group({
    outputs: this.formBuilder.array([])
  });

  data$: Observable<{
    outputs: WorkPackageOutputDTO[];
    periods: ProjectPeriodDTO[];
    outputIndicators: OutputIndicatorSummaryDTO[];
    workPackageNumber: number;
  }>;
  Alert = Alert;

  constructor(public formService: FormService,
              private formBuilder: FormBuilder,
              private workPackageStore: WorkPackagePageStore) {
    this.formService.init(this.form, this.workPackageStore.isProjectEditable$);
  }

  ngOnInit(): void {
    combineLatest([
      this.workPackageStore.outputs$, this.formService.reset$.pipe(startWith(null))
    ])
      .pipe(
        map(([outputs]) => this.resetForm(outputs)),
        untilDestroyed(this)
      ).subscribe();

    this.data$ = combineLatest([
      this.workPackageStore.outputs$,
      this.workPackageStore.workPackage$,
      this.workPackageStore.outputIndicators$,
      this.workPackageStore.projectForm$,
    ]).pipe(
      map(([outputs, workPackage, indicators, projectForm$]) => ({
          outputs,
          periods: projectForm$.periods,
          outputIndicators: indicators,
          workPackageNumber: workPackage.number,
        })
      ));
  }

  updateOutputs(): void {
    this.workPackageStore.saveWorkPackageOutputs(this.outputs.value)
      .pipe(
        take(1),
        tap(() => this.formService.setSuccess('project.application.form.workpackage.outputs.save.success')),
        catchError(err => this.formService.setError(err))
      ).subscribe();
  }

  addNewOutput(): void {
    this.addOutput();
    this.formService.setDirty(true);
  }

  removeOutput(index: number): void {
    this.outputs.removeAt(index);
    this.outputs.controls.forEach(
      (output, i) => output.get(this.constants.OUTPUT_NUMBER.name)?.setValue(i)
    );
    this.formService.setDirty(true);
  }

  get outputs(): FormArray {
    return this.form.get(this.constants.OUTPUTS.name) as FormArray;
  }

  addOutputVisible(): boolean {
    return this.form.enabled && this.outputs.length < 10;
  }

  getMeasurementUnit(indicatorId: number, indicators: OutputIndicatorSummaryDTO[]): InputTranslation[] {
    return indicators.find(indicator => indicator.id === indicatorId)?.measurementUnit || [];
  }

  private resetForm(outputs: WorkPackageOutputDTO[]): void {
    this.outputs.clear();
    outputs.forEach((activity) => this.addOutput(activity));
    this.formService.resetEditable();
    this.formService.setDirty(false);
  }

  private addOutput(existing?: WorkPackageOutputDTO): void {
    this.outputs.push(this.formBuilder.group({
        outputNumber: this.formBuilder.control(existing?.outputNumber || this.outputs.length),
        programmeOutputIndicatorId: this.formBuilder.control(existing?.programmeOutputIndicatorId || null),
        title: this.formBuilder.control(existing?.title || [], this.constants.TITLE.validators),
        targetValue: this.formBuilder.control(existing?.targetValue || 1),
        periodNumber: this.formBuilder.control(existing?.periodNumber || ''),
        description: this.formBuilder.control(existing?.description || []),
      }
    ));
  }
}
