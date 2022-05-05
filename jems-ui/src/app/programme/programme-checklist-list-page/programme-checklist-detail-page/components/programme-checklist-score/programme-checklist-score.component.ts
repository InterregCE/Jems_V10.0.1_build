import {ChangeDetectionStrategy, Component, forwardRef, NgZone, ViewChild} from '@angular/core';
import {ProgrammeChecklistComponentDTO, ScoreMetadataDTO, TextInputMetadataDTO} from '@cat/api';
import {
  AbstractControl,
  ControlValueAccessor,
  FormBuilder,
  FormGroup,
  NG_VALIDATORS,
  NG_VALUE_ACCESSOR,
  ValidationErrors,
  Validator,
  Validators
} from '@angular/forms';
import {take, tap} from 'rxjs/operators';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {CdkTextareaAutosize} from '@angular/cdk/text-field';
import {FormService} from '@common/components/section/form/form.service';

@UntilDestroy()
@Component({
  selector: 'jems-programme-checklist-score',
  templateUrl: './programme-checklist-score.component.html',
  styleUrls: ['./programme-checklist-score.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => ProgrammeChecklistScoreComponent),
      multi: true
    },
    {
      provide: NG_VALIDATORS,
      useExisting: forwardRef(() => ProgrammeChecklistScoreComponent),
      multi: true
    }
  ]
})
export class ProgrammeChecklistScoreComponent implements ControlValueAccessor, Validator {

  metadata: ScoreMetadataDTO;
  form: FormGroup;
  FORM_ERRORS = {
    weight: {
      max: 'programme.checklists.instance.slider.max.error'
    }
  };
  FORM_ERRORS_ARGS = {
    weight: {
      max: {maxValue: 100}
    }
  };

  @ViewChild('autosize', {static: false}) autosize: CdkTextareaAutosize;

  constructor(private formBuilder: FormBuilder,
              private zone: NgZone,
              private formService: FormService) {
    this.zone.onStable.pipe(
      take(1),
      tap(() => this.autosize.resizeToFitContent(true))
    ).subscribe();
  }

  onChange = (value: any) => {
    // Intentionally left blank
  };

  registerOnChange(fn: any): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: any): void {
    // Intentionally left blank
  }

  writeValue(obj: any[]): void {
    this.metadata = obj as any;
    this.form = this.formBuilder.group({
      question: [this.metadata?.question, [Validators.required, Validators.maxLength(1000)]],
      weight: [this.metadata?.weight || 1, [Validators.required, Validators.min(0), Validators.max(100)]],
    });

    this.updateFormValues();
  }

  updateFormValues() {
    this.form.valueChanges
      .pipe(
        tap(() => this.onChange({
          ...this.form.value,
          type: ProgrammeChecklistComponentDTO.TypeEnum.SCORE
        })),
        untilDestroyed(this)
      ).subscribe();
  }

  validate(control: AbstractControl): ValidationErrors | null {
    return this.form.valid ? null : {invalid: true};
  }

  setDisabledState?(isDisabled: boolean): void {
    if (isDisabled) {
      this.form.disable({emitEvent: false});
    }
  }
}

