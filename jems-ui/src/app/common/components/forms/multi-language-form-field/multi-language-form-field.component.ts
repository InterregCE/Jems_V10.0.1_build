import {Component, forwardRef, Input, OnInit} from '@angular/core';
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
import {Observable, ReplaySubject} from 'rxjs';
import {MultiLanguageComponentService} from '@common/components/forms/multi-language/multi-language-component.service';
import {INPUT_STATE} from '@common/components/forms/multi-language/multi-language-input-state';

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

  disabled = false;
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
  @Input()
  hideCountersWhenInView = false;

  multiLanguageFormGroup: FormGroup;
  languages$: Observable<string[]>;
  currentLanguage$: Observable<string>;
  state$ = new ReplaySubject<{ [key: string]: INPUT_STATE }>(1);

  constructor(private componentService: MultiLanguageComponentService,
              public formBuilder: FormBuilder) {
  }

  ngOnInit(): void {
    this.languages$ = this.componentService.languages$;
    this.currentLanguage$ = this.componentService.currentLanguage$;

    this.languages$.pipe(
      tap(languages => this.resetForm(languages)),
      untilDestroyed(this)
    ).subscribe();
  }

  registerOnChange(fn: any): void {
    this.inputs?.valueChanges.pipe(
      untilDestroyed(this),
      tap(() => this.valueChanged()),
    ).subscribe(fn);
  }

  registerOnTouched(fn: any): void {
    // This is intentional
  }

  setDisabledState(isDisabled: boolean): void {
    this.disabled = isDisabled;
  }

  writeValue(newValue: InputTranslation[]): void {
    if (this.multiLanguageFormGroup) {
      if (newValue && Array.isArray(newValue)) {
        if (this.componentService.didLanguagesChange(newValue)) {
          const inputTranslations = this.componentService.multiLanguageFormFieldDefaultValue();
          inputTranslations.forEach(defaultItem => {
            defaultItem.translation = newValue.find(it => it.language === defaultItem.language)?.translation || '';
          });
          this.inputs.setValue(inputTranslations, {emitEvent: false});
        } else {
          this.inputs.setValue(newValue, {emitEvent: false});
        }
      } else {
        this.inputs.setValue(this.componentService.multiLanguageFormFieldDefaultValue(), {emitEvent: false});
      }
      this.valueChanged();
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

  private valueChanged(): void {
    this.state$.next(
      Object.fromEntries(
        this.inputs?.controls.map(group => {
          const translation = group.get('translation');
          const language = group.get('language')?.value;
          if (translation?.invalid) {
            return [language, INPUT_STATE.INVALID];
          }
          return [language, translation?.value ? INPUT_STATE.VALID : INPUT_STATE.EMPTY];
        })
      ));
  }

  private getIndexOfFormGroupForLanguage(language: string): number {
    return this.inputs.controls.findIndex(formGroup => formGroup.get('language')?.value === language);
  }

  get inputs(): FormArray {
    return this.multiLanguageFormGroup?.get('inputs') as FormArray;
  }
}
