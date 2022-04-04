import {ChangeDetectionStrategy, Component, forwardRef} from '@angular/core';
import {OptionsToggleMetadataDTO, ProgrammeChecklistComponentDTO} from '@cat/api';
import {
  AbstractControl,
  ControlValueAccessor,
  FormBuilder,
  FormGroup,
  NG_VALIDATORS,
  NG_VALUE_ACCESSOR, ValidationErrors,
  Validator,
  Validators
} from '@angular/forms';
import {tap} from 'rxjs/operators';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';

@UntilDestroy()
@Component({
  selector: 'jems-programme-checklist-options-toggle',
  templateUrl: './programme-checklist-options-toggle.component.html',
  styleUrls: ['./programme-checklist-options-toggle.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => ProgrammeChecklistOptionsToggleComponent),
      multi: true
    },
    {
      provide: NG_VALIDATORS,
      useExisting: forwardRef(() => ProgrammeChecklistOptionsToggleComponent),
      multi: true
    }
  ]
})
export class ProgrammeChecklistOptionsToggleComponent implements ControlValueAccessor, Validator {

  metadata: OptionsToggleMetadataDTO;
  form: FormGroup;

  constructor(private formBuilder: FormBuilder) { }

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
      firstOption: [this.metadata?.firstOption, [Validators.required, Validators.maxLength(100)]],
      secondOption: [this.metadata?.secondOption, [Validators.required, Validators.maxLength(100)]],
      thirdOption: [this.metadata?.thirdOption, Validators.maxLength(100)],
    });

    this.form.valueChanges
      .pipe(
        tap(() => this.onChange({
          ...this.form.value,
          type: ProgrammeChecklistComponentDTO.TypeEnum.OPTIONSTOGGLE
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

