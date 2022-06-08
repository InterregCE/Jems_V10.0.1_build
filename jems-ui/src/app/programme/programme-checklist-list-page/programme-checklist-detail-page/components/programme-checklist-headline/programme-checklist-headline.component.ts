import {ChangeDetectionStrategy, Component, forwardRef} from '@angular/core';
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
import {HeadlineMetadataDTO, ProgrammeChecklistComponentDTO} from '@cat/api';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {tap} from 'rxjs/operators';

@UntilDestroy()
@Component({
  selector: 'jems-programme-checklist-headline',
  templateUrl: './programme-checklist-headline.component.html',
  styleUrls: ['./programme-checklist-headline.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => ProgrammeChecklistHeadlineComponent),
      multi: true
    },
    {
      provide: NG_VALIDATORS,
      useExisting: forwardRef(() => ProgrammeChecklistHeadlineComponent),
      multi: true
    }
  ]
})
export class ProgrammeChecklistHeadlineComponent implements ControlValueAccessor, Validator {
  metadata: HeadlineMetadataDTO;
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
      value: [this.metadata?.value, [Validators.required, Validators.maxLength(200)]]
    });

    this.form.valueChanges
      .pipe(
        tap(() => this.onChange({
          ...this.form.value,
          type: ProgrammeChecklistComponentDTO.TypeEnum.HEADLINE
        })),
        untilDestroyed(this)
      )
      .subscribe();
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
