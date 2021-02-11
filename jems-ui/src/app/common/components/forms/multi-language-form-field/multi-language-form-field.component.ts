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
import {InputTranslation} from '@cat/api';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {MultiLanguageFormFieldConstants} from '@common/components/forms/multi-language-form-field/multi-language-form-field.constants';
import {tap} from 'rxjs/operators';
import {Observable} from 'rxjs';

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
  useSystemLanguages = false;
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
  languages$: Observable<string[]>;
  currentLanguage$: Observable<string>;
  languages: string[];

  static didLanguagesChange(savedTranslations: InputTranslation[], currentSystemLanguages: string[]): boolean {
    if (savedTranslations.length !== currentSystemLanguages.length) {
      return true;
    }
    return !!currentSystemLanguages.filter(existingLanguage =>
      !savedTranslations.filter(newTranslation => newTranslation.language === existingLanguage).length).length;
  }

  constructor(public multiLanguageService: MultiLanguageInputService, public formBuilder: FormBuilder) {
  }

  ngOnInit(): void {
    this.languages$ = this.useSystemLanguages
      ? this.multiLanguageService.systemLanguages$ : this.multiLanguageService.inputLanguages$;
    this.currentLanguage$ = this.useSystemLanguages
      ? this.multiLanguageService.currentSystemLanguage$ : this.multiLanguageService.currentInputLanguage$;
    this.languages = this.useSystemLanguages
      ? this.multiLanguageService.systemLanguages : this.multiLanguageService.inputLanguages;

    this.languages$.pipe(
      tap(languages => this.resetForm(languages)),
      untilDestroyed(this)
    ).subscribe();
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
        if (MultiLanguageFormFieldComponent.didLanguagesChange(newValue, this.languages)) {
          const inputTranslations = this.multiLanguageService.multiLanguageFormFieldDefaultValue(this.useSystemLanguages);
          inputTranslations.forEach(defaultItem => {
            defaultItem.translation = newValue.find(it => it.language === defaultItem.language)?.translation || '';
          });
          this.inputs.setValue(inputTranslations, {emitEvent: false});
        } else {
          this.inputs.setValue(newValue, {emitEvent: false});
        }
      } else {
        this.inputs.setValue(this.multiLanguageService.multiLanguageFormFieldDefaultValue(this.useSystemLanguages));
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

  private initForm(languages: string[]): void {
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

  private resetForm(languages: string[]): void {
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

  private getIndexOfFormGroupForLanguage(language: string): number {
    return this.inputs.controls.findIndex(formGroup => formGroup.get('language')?.value === language);
  }

  get inputs(): FormArray {
    return this.multiLanguageFormGroup?.get('inputs') as FormArray;
  }
}
