import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output
} from '@angular/core';
import {ViewEditForm} from '@common/components/forms/view-edit-form';
import {Permission} from '../../../../security/permissions/permission';
import {
  InputIndicatorOutputCreate,
  InputIndicatorOutputUpdate,
  OutputIndicatorOutput,
  OutputProgrammePriority
} from '@cat/api';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {MatDialog} from '@angular/material/dialog';
import {Observable} from 'rxjs';
import {filter, map, startWith, take, takeUntil} from 'rxjs/operators';
import {FormState} from '@common/components/forms/form-state';
import {Forms} from '../../../../common/utils/forms';
import {ProgrammeOutputIndicatorConstants} from './constants/programme-output-indicator-constants';

@Component({
  selector: 'app-programme-output-indicator-detail',
  templateUrl: './programme-output-indicator-detail.component.html',
  styleUrls: ['./programme-output-indicator-detail.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProgrammeOutputIndicatorDetailComponent extends ViewEditForm implements OnInit {

  Permission = Permission;
  programmeOutputIndicatorConstants = ProgrammeOutputIndicatorConstants;

  @Input()
  outputIndicator: OutputIndicatorOutput;
  @Input()
  priorities: Array<OutputProgrammePriority>;
  @Input()
  isCreate: boolean;
  @Output()
  createOutputIndicator: EventEmitter<InputIndicatorOutputCreate> = new EventEmitter<InputIndicatorOutputCreate>();
  @Output()
  updateOutputIndicator: EventEmitter<InputIndicatorOutputUpdate> = new EventEmitter<InputIndicatorOutputUpdate>();
  @Output()
  cancelCreate: EventEmitter<void> = new EventEmitter<void>();

  indicatorCodes = this.programmeOutputIndicatorConstants.indicatorCodes;

  indicatorNames = this.programmeOutputIndicatorConstants.indicatorNames;

  measurementUnits =  this.programmeOutputIndicatorConstants.measurementUnits;

  filteredIndicatorNames: Observable<string[]>;
  filteredMeasurementUnits: Observable<string[]>;

  outputIndicatorForm = this.formBuilder.group({
    identifier: ['', Validators.compose([Validators.required, Validators.maxLength(5)])],
    indicatorCode: ['', Validators.maxLength(6)],
    indicatorName: ['', Validators.compose([Validators.required, Validators.maxLength(250)])],
    specificObjective: ['', Validators.required],
    measurementUnit: ['', Validators.maxLength(255)],
    milestone: [''],
    finalTarget: ['']
  });

  identifierErrors = {
    required: 'indicator.identifier.should.not.be.empty',
    maxlength: 'indicator.identifier.size.too.long',
  };

  indicatorCodeErrors = {
    maxlength: 'indicator.code.size.too.long',
  };

  indicatorNameErrors = {
    required: 'indicator.name.should.not.be.empty',
    maxlength: 'indicator.name.size.too.long',
  };

  measurementUnitErrors = {
    maxlength: 'indicator.measurementUnit.size.too.long',
  };

  specificObjectiveErrors = {
    required: 'indicator.specific.objective.not.be.empty',
  }

  constructor(private formBuilder: FormBuilder,
              private dialog: MatDialog,
              protected changeDetectorRef: ChangeDetectorRef) {
    super(changeDetectorRef);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.indicatorNames.sort();
    this.filteredIndicatorNames = this.outputIndicatorForm.controls.indicatorName.valueChanges
      .pipe(
        startWith(''),
        map(value => this._filter(value, this.indicatorNames))
      );
    this.filteredMeasurementUnits = this.outputIndicatorForm.controls.measurementUnit.valueChanges
      .pipe(
        startWith(''),
        map(value => this._filter(value, this.measurementUnits))
      );
    if (this.isCreate) {
      this.changeFormState$.next(FormState.EDIT);
    } else {
      this.outputIndicatorForm.controls.identifier.setValue(this.outputIndicator.identifier);
      this.outputIndicatorForm.controls.indicatorCode.setValue(this.outputIndicator.code);
      this.outputIndicatorForm.controls.indicatorName.setValue(this.outputIndicator.name);
      this.outputIndicatorForm.controls.specificObjective.setValue(this.outputIndicator.programmePriorityPolicySpecificObjective);
      this.outputIndicatorForm.controls.measurementUnit.setValue(this.outputIndicator.measurementUnit);
      this.outputIndicatorForm.controls.milestone.setValue(this.outputIndicator.milestone);
      this.outputIndicatorForm.controls.finalTarget.setValue(this.outputIndicator.finalTarget);
      this.changeFormState$.next(FormState.VIEW);
    }
  }

  getForm(): FormGroup | null {
    return this.outputIndicatorForm;
  }

  onSubmit(): void {

    Forms.confirmDialog(
      this.dialog,
      this.isCreate ? 'output.indicator.final.dialog.title.save' : 'output.indicator.final.dialog.title.update',
      this.isCreate ? 'output.indicator.final.dialog.message.save' : 'output.indicator.final.dialog.message.update'
    ).pipe(
      take(1),
      takeUntil(this.destroyed$),
      filter(yes => !!yes)
    ).subscribe(() => {
      if (this.isCreate) {
        this.createOutputIndicator.emit({
          identifier: this.outputIndicatorForm?.controls?.identifier?.value,
          code: this.outputIndicatorForm?.controls?.indicatorCode?.value,
          name: this.outputIndicatorForm?.controls?.indicatorName?.value,
          programmeObjectivePolicy: this.outputIndicatorForm?.controls?.specificObjective?.value,
          measurementUnit: this.outputIndicatorForm?.controls?.measurementUnit?.value,
          milestone: Number(this.outputIndicatorForm?.controls?.milestone?.value),
          finalTarget: Number(this.outputIndicatorForm?.controls?.finalTarget?.value),
        })
      } else {
        this.updateOutputIndicator.emit({
          id: this.outputIndicator?.id,
          identifier: this.outputIndicatorForm?.controls?.identifier?.value,
          code: this.outputIndicatorForm?.controls?.indicatorCode?.value,
          name: this.outputIndicatorForm?.controls?.indicatorName?.value,
          programmeObjectivePolicy: this.outputIndicatorForm?.controls?.specificObjective?.value,
          measurementUnit: this.outputIndicatorForm?.controls?.measurementUnit?.value,
          milestone: Number(this.outputIndicatorForm?.controls?.milestone?.value),
          finalTarget: Number(this.outputIndicatorForm?.controls?.finalTarget?.value),
        })
      }
    });
  }

  onCancel(): void {
    if (this.isCreate) {
      this.cancelCreate.emit();
    } else {
      this.changeFormState$.next(FormState.VIEW);
    }
  }

  private _filter(value: string, source: string[]): string[] {
    const filterValue = value.toLowerCase();
    return source.filter(option => option.toLowerCase().includes(filterValue));
  }
}
