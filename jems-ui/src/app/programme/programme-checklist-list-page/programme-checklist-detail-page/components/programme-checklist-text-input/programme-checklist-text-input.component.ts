import {ChangeDetectionStrategy, Component, forwardRef, NgZone, ViewChild} from '@angular/core';
import {ProgrammeChecklistComponentDTO, TextInputMetadataDTO} from '@cat/api';
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
  selector: 'jems-programme-checklist-text-input',
  templateUrl: './programme-checklist-text-input.component.html',
  styleUrls: ['./programme-checklist-text-input.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => ProgrammeChecklistTextInputComponent),
      multi: true
    },
    {
      provide: NG_VALIDATORS,
      useExisting: forwardRef(() => ProgrammeChecklistTextInputComponent),
      multi: true
    }
  ]
})
export class ProgrammeChecklistTextInputComponent implements ControlValueAccessor, Validator {

  metadata: TextInputMetadataDTO;
  form: FormGroup;

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
      explanationLabel: [this.metadata?.explanationLabel, [Validators.required, Validators.maxLength(50)]],
      explanationMaxLength: [this.metadata?.explanationMaxLength || 5000, [Validators.min(1), Validators.max(5000), Validators.maxLength(4)]]
    });

    this.updateFormValues();
  }

  updateFormValues() {
    this.form.valueChanges
      .pipe(
        tap(() => this.onChange({
          ...this.form.value,
          type: ProgrammeChecklistComponentDTO.TypeEnum.TEXTINPUT
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

