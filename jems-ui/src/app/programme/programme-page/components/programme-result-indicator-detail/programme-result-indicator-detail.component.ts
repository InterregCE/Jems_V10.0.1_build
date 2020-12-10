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
import {Observable} from 'rxjs';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {MatDialog} from '@angular/material/dialog';
import {filter, map, startWith, take, takeUntil} from 'rxjs/operators';
import {FormState} from '@common/components/forms/form-state';
import {Forms} from '../../../../common/utils/forms';
import {
  InputIndicatorResultCreate,
  InputIndicatorResultUpdate,
  OutputIndicatorResult,
  OutputProgrammePriority
} from '@cat/api';
import {Permission} from '../../../../security/permissions/permission';
import {ProgrammeResultIndicatorConstants} from './constants/programme-result-indicator-constants';
import {NumberService} from '../../../../common/services/number.service';

@Component({
  selector: 'app-programme-result-indicator-detail',
  templateUrl: './programme-result-indicator-detail.component.html',
  styleUrls: ['./programme-result-indicator-detail.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProgrammeResultIndicatorDetailComponent extends ViewEditForm implements OnInit {

  Permission = Permission;
  programmeResultIndicatorConstants = ProgrammeResultIndicatorConstants;
  @Input()
  resultIndicator: OutputIndicatorResult;
  @Input()
  priorities: Array<OutputProgrammePriority>;
  @Input()
  isCreate: boolean;
  @Output()
  createResultIndicator: EventEmitter<InputIndicatorResultCreate> = new EventEmitter<InputIndicatorResultCreate>();
  @Output()
  updateResultIndicator: EventEmitter<InputIndicatorResultUpdate> = new EventEmitter<InputIndicatorResultUpdate>();
  @Output()
  cancelCreate: EventEmitter<void> = new EventEmitter<void>();

  indicatorCodes = this.programmeResultIndicatorConstants.indicatorCodes;

  indicatorNames = this.programmeResultIndicatorConstants.indicatorNames;

  measurementUnits = this.programmeResultIndicatorConstants.measurementUnits;

  filteredIndicatorNames: Observable<string[]>;
  filteredMeasurementUnits: Observable<string[]>;

  resultIndicatorForm = this.formBuilder.group({
    identifier: ['', Validators.compose([Validators.required, Validators.maxLength(5)])],
    indicatorCode: ['', Validators.maxLength(6)],
    indicatorName: ['', Validators.compose([Validators.required, Validators.maxLength(250)])],
    specificObjective: ['', Validators.required],
    measurementUnit: ['', Validators.maxLength(255)],
    baseline: [null],
    referenceYear: ['', Validators.maxLength(10)],
    finalTarget: [null],
    sourceOfData: ['', Validators.maxLength(1000)],
    comments: ['', Validators.maxLength(1000)]
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
  };

  referenceYearErrors = {
    maxlength: 'indicator.referenceYear.size.too.long',
  };

  sourceOfDataErrors = {
    maxlength: 'indicator.sourceOfData.size.too.long',
  };

  commentsErrors = {
    maxlength: 'indicator.comment.size.too.long',
  };

  constructor(private formBuilder: FormBuilder,
              private dialog: MatDialog,
              protected changeDetectorRef: ChangeDetectorRef,
              public numberService: NumberService) {
    super(changeDetectorRef);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.indicatorNames.sort();
    this.filteredIndicatorNames = this.resultIndicatorForm.controls.indicatorName.valueChanges
      .pipe(
        startWith(''),
        map(value => this._filter(value, this.indicatorNames))
      );
    this.filteredMeasurementUnits = this.resultIndicatorForm.controls.measurementUnit.valueChanges
      .pipe(
        startWith(''),
        map(value => this._filter(value, this.measurementUnits))
      );
    if (!this.isCreate) {
      this.resultIndicatorForm.controls.identifier.setValue(this.resultIndicator.identifier);
      this.resultIndicatorForm.controls.indicatorCode.setValue(this.resultIndicator.code);
      this.resultIndicatorForm.controls.indicatorName.setValue(this.resultIndicator.name);
      this.resultIndicatorForm.controls.specificObjective.setValue(this.resultIndicator.programmePriorityPolicySpecificObjective);
      this.resultIndicatorForm.controls.measurementUnit.setValue(this.resultIndicator.measurementUnit);
      this.resultIndicatorForm.controls.baseline.setValue(this.resultIndicator.baseline);
      this.resultIndicatorForm.controls.referenceYear.setValue(this.resultIndicator.referenceYear);
      this.resultIndicatorForm.controls.finalTarget.setValue(this.resultIndicator.finalTarget);
      this.resultIndicatorForm.controls.sourceOfData.setValue(this.resultIndicator.sourceOfData);
      this.resultIndicatorForm.controls.comments.setValue(this.resultIndicator.comment);
      this.changeFormState$.next(FormState.VIEW);
    } else {
      this.changeFormState$.next(FormState.EDIT);
    }
  }

  getForm(): FormGroup | null {
    return this.resultIndicatorForm;
  }

  onSubmit(): void {

    Forms.confirmDialog(
      this.dialog,
      this.isCreate ? 'result.indicator.final.dialog.title.save' : 'result.indicator.final.dialog.title.update',
      this.isCreate ? 'result.indicator.final.dialog.message.save' : 'result.indicator.final.dialog.message.update'
    ).pipe(
      take(1),
      takeUntil(this.destroyed$),
      filter(yes => !!yes)
    ).subscribe(() => {
      if (this.isCreate) {
        this.createResultIndicator.emit({
          identifier: this.resultIndicatorForm?.controls?.identifier?.value,
          code: this.resultIndicatorForm?.controls?.indicatorCode?.value,
          name: this.resultIndicatorForm?.controls?.indicatorName?.value,
          programmeObjectivePolicy: this.resultIndicatorForm?.controls?.specificObjective?.value,
          measurementUnit: this.resultIndicatorForm?.controls?.measurementUnit?.value,
          baseline: this.resultIndicatorForm?.controls?.baseline?.value,
          referenceYear: this.resultIndicatorForm?.controls?.referenceYear?.value,
          finalTarget: this.resultIndicatorForm?.controls?.finalTarget?.value,
          sourceOfData: this.resultIndicatorForm?.controls?.sourceOfData?.value,
          comment: this.resultIndicatorForm?.controls?.comments?.value,
        });
      } else {
        this.updateResultIndicator.emit({
          id: this.resultIndicator?.id,
          identifier: this.resultIndicatorForm?.controls?.identifier?.value,
          code: this.resultIndicatorForm?.controls?.indicatorCode?.value,
          name: this.resultIndicatorForm?.controls?.indicatorName?.value,
          programmeObjectivePolicy: this.resultIndicatorForm?.controls?.specificObjective?.value,
          measurementUnit: this.resultIndicatorForm?.controls?.measurementUnit?.value,
          baseline: this.resultIndicatorForm?.controls?.baseline?.value,
          referenceYear: this.resultIndicatorForm?.controls?.referenceYear?.value,
          finalTarget: this.resultIndicatorForm?.controls?.finalTarget?.value,
          sourceOfData: this.resultIndicatorForm?.controls?.sourceOfData?.value,
          comment: this.resultIndicatorForm?.controls?.comments?.value,
        });
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
