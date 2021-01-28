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
  Validators
} from '@angular/forms';
import {InputTranslation, OutputProgrammeLanguage} from '@cat/api';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {MultiLanguageFormFieldConstants} from '@common/components/forms/multi-language-form-field/multi-language-form-field.constants';
import {tap} from 'rxjs/operators';

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
  type: 'input' | 'textarea' = 'input';
  @Input()
  label: string;
  @Input()
  maxLength = 255;
  @Input()
  minRows ? = 3;
  @Input()
  maxRows ? = 50;
  @Input()
  contextInfoText?: string;

  multiLanguageFormGroup: FormGroup;

  constructor(public multiLanguageService: MultiLanguageInputService, public formBuilder: FormBuilder) {
  }

  ngOnInit(): void {
    this.multiLanguageService.languages$.pipe(
      tap(languages => this.resetForm(languages)),
      untilDestroyed(this)
    ).subscribe();
  }

  isInputVisible(currentLanguage: InputTranslation.LanguageEnum | null, language: InputTranslation.LanguageEnum): boolean {
    return currentLanguage === language;
  }

  registerOnChange(fn: any): void {
    this.inputs?.valueChanges.pipe(untilDestroyed(this)).subscribe(fn);
  }

  registerOnTouched(fn: any): void {
    // This is intentional
  }

  setDisabledState(isDisabled: boolean): void {
    if (isDisabled) {
      this.multiLanguageFormGroup?.disable({emitEvent: false});
    } else {
      this.multiLanguageFormGroup?.enable({emitEvent: false});
    }
  }

  writeValue(newValue: InputTranslation[]): void {
    if (this.multiLanguageFormGroup) {
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
  }

  registerOnValidatorChange(fn: () => void): void {
    // This is intentional
  }

  validate(control: AbstractControl): ValidationErrors | null {
    this.multiLanguageFormGroup?.updateValueAndValidity();
    return this.multiLanguageFormGroup?.valid ? null : {
      multiLanguageError: {
        valid: false,
        message: 'multiLanguage fields are invalid'
      }
    };
  }

  private initForm(languages: OutputProgrammeLanguage.CodeEnum[]): void {
    this.multiLanguageFormGroup = this.formBuilder.group({
      inputs: this.formBuilder.array([])
    });
    languages.forEach(language => {
        this.inputs.push(
          this.formBuilder.group({
            translation: ['', Validators.maxLength(this.maxLength)],
            language: [language]
          }));
      }
    );
  }

  private resetForm(languages: OutputProgrammeLanguage.CodeEnum[]): void {
    if (!this.multiLanguageFormGroup) {
      this.initForm(languages);
    } else {
      const finalFromGroups: FormGroup[] = [];
      languages.forEach(language => {
          const formGroupIndexOfIndex = this.getIndexOfFormGroupForLanguage(language);
          if (formGroupIndexOfIndex >= 0) {
            finalFromGroups.push(this.inputs.controls[formGroupIndexOfIndex] as FormGroup);
          } else {
            finalFromGroups.push(
              this.formBuilder.group({
                translation: ['', Validators.maxLength(this.maxLength)],
                language: language.valueOf
              }));
          }
        }
      );
      this.inputs.clear();
      finalFromGroups.forEach(formGroup => {
        this.inputs.push(formGroup);
      });
    }
  }

  private getIndexOfFormGroupForLanguage(language: OutputProgrammeLanguage.CodeEnum): number {
    return this.inputs.controls.findIndex(formGroup => formGroup.get('language')?.value === language);
  }

  get inputs(): FormArray {
    return this.multiLanguageFormGroup?.get('inputs') as FormArray;
  }
}
