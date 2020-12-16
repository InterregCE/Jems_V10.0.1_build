import {Component, forwardRef, Input, OnInit} from '@angular/core';
import {MultiLanguageInputService} from '../../../services/multi-language-input.service';
import {
  AbstractControl,
  ControlValueAccessor,
  FormArray,
  FormBuilder,
  FormGroup,
  NG_VALIDATORS,
  NG_VALUE_ACCESSOR,
  ValidationErrors,
  Validator,
  ValidatorFn,
  Validators
} from '@angular/forms';
import {InputTranslation} from '@cat/api';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {MultiLanguageFormFieldConstants} from '@common/components/forms/multi-language-form-field/multi-language-form-field.constants';

@UntilDestroy()
@Component({
  selector: 'app-multi-language-form-field',
  templateUrl: './multi-language-form-field.component.html',
  styleUrls: ['./multi-language-form-field.component.scss'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => MultiLanguageFormFieldComponent),
      multi: true
    },
    {
      provide: NG_VALIDATORS,
      useExisting: forwardRef(() => MultiLanguageFormFieldComponent),
      multi: true
    }
  ]
})
export class MultiLanguageFormFieldComponent implements OnInit, ControlValueAccessor, Validator {

  constants = MultiLanguageFormFieldConstants;

  @Input()
  validators: ValidatorFn[] = [Validators.maxLength(255)];

  multiLanguageFormGroup: FormGroup;

  constructor(public languageService: MultiLanguageInputService, public formBuilder: FormBuilder) {
  }

  ngOnInit(): void {
    this.initForm();
  }

  isInputHidden(currentLanguage: InputTranslation.LanguageEnum | null, language: InputTranslation.LanguageEnum): boolean {
    return currentLanguage !== language;
  }

  registerOnChange(fn: any): void {
    this.inputs.valueChanges.pipe(untilDestroyed(this)).subscribe(fn);
  }

  registerOnTouched(fn: any): void {
  }

  setDisabledState(isDisabled: boolean): void {
    isDisabled ? this.multiLanguageFormGroup.disable() : this.multiLanguageFormGroup.enable();
  }

  writeValue(obj: InputTranslation[]): void {
    if (obj) {
      this.inputs.setValue(obj, {emitEvent: false});
    }
  }

  registerOnValidatorChange(fn: () => void): void {
  }

  validate(control: AbstractControl): ValidationErrors | null {
    return this.multiLanguageFormGroup.valid ? null : {
      multiLanguageError: {
        valid: false,
        message: 'multiLanguage fields are invalid'
      }
    };
  }

  private initForm(): void {
    this.multiLanguageFormGroup = this.formBuilder.group({
      inputs: this.formBuilder.array([])
    });
    this.languageService.languages.forEach(language => {
        this.inputs.push(
          this.formBuilder.group({
            translation: this.formBuilder.control('', this.validators),
            language: this.formBuilder.control(language)
          }));
      }
    );
  }

  get inputs(): FormArray {
    return this.multiLanguageFormGroup.get('inputs') as FormArray;
  }
}
