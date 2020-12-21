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
  label: string;
  @Input()
  maxLength = 255;

  multiLanguageFormGroup: FormGroup;

  constructor(public multiLanguageService: MultiLanguageInputService, public formBuilder: FormBuilder) {
  }

  ngOnInit(): void {
    this.initForm();
  }

  isInputVisible(currentLanguage: InputTranslation.LanguageEnum | null, language: InputTranslation.LanguageEnum): boolean {
    return currentLanguage === language;
  }

  registerOnChange(fn: any): void {
    this.inputs.valueChanges.pipe(untilDestroyed(this)).subscribe(fn);
  }

  registerOnTouched(fn: any): void {
  }

  setDisabledState(isDisabled: boolean): void {
    isDisabled ? this.multiLanguageFormGroup.disable() : this.multiLanguageFormGroup.enable();
  }

  writeValue(newValue: InputTranslation[]): void {
    if (newValue && Array.isArray(newValue)) {
      if (newValue.length !== this.multiLanguageService.languages.length) {
        const inputTranslations = this.multiLanguageService.multiLanguageFormFieldDefaultValue();
        inputTranslations.forEach(defaultItem => {
          defaultItem.translation = newValue.find(it => it.language === defaultItem.language)?.translation || '';
        });
        this.inputs.setValue(inputTranslations, {emitEvent: false});
      } else {
        this.inputs.setValue(newValue, {emitEvent: false});
      }
    } else {
      this.inputs.setValue(this.multiLanguageService.multiLanguageFormFieldDefaultValue());
    }
  }

  registerOnValidatorChange(fn: () => void): void {
  }

  validate(control: AbstractControl): ValidationErrors | null {
    this.multiLanguageFormGroup.updateValueAndValidity();
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
    this.multiLanguageService.languages.forEach(language => {
        this.inputs.push(
          this.formBuilder.group({
            translation: this.formBuilder.control('', Validators.maxLength(this.maxLength)),
            language: this.formBuilder.control(language)
          }));
      }
    );
  }

  get inputs(): FormArray {
    return this.multiLanguageFormGroup.get('inputs') as FormArray;
  }
}
