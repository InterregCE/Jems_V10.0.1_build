import {Injectable} from '@angular/core';
import {combineLatest, ReplaySubject} from 'rxjs';
import {tap} from 'rxjs/operators';
import {InputTranslation} from '@cat/api';
import {LanguageService} from './language.service';
import {MultiLanguageInput} from '@common/components/forms/multi-language/multi-language-input';
import {AbstractControl} from '@angular/forms';


@Injectable({
  providedIn: 'root'
})
export class MultiLanguageInputService {
  private currentInputLanguage: string;

  inputLanguages$ = this.languageService.inputLanguages$;
  inputLanguages: string[];
  currentInputLanguage$ = new ReplaySubject<string>(1);

  systemLanguages$ = this.languageService.systemLanguages$;
  systemLanguages: string[];
  currentSystemLanguage$ = new ReplaySubject<string>(1);

  constructor(private languageService: LanguageService) {
    this.languageService.inputLanguages$
      .pipe(
        tap(inputLanguages => {
          this.inputLanguages = inputLanguages;
          this.currentInputLanguage$.next(inputLanguages && inputLanguages[0]);
          this.currentInputLanguage = inputLanguages && inputLanguages[0];
        })
      ).subscribe();

    combineLatest([this.languageService.systemLanguages$, this.languageService.systemLanguage$])
      .pipe(
        tap(([systemLanguages, currentLanguage]) => {
          this.systemLanguages = systemLanguages;
          this.currentSystemLanguage$.next(currentLanguage || systemLanguages && systemLanguages[0]);
        })
      ).subscribe();

    this.currentInputLanguage$
      .pipe(
        tap(lang => this.currentInputLanguage = lang)
      ).subscribe();
  }

  public static getFirstTranslation(inputs: InputTranslation[]): string {
    if (!inputs?.length) {
      return '';
    }
    const sorted = [...inputs]
      .sort((a, b) => a.language > b.language ? 1 : -1);
    return sorted[0].translation;
  }

  initInput(inputs: InputTranslation[], formControl?: AbstractControl): MultiLanguageInput {
    const allInputs: InputTranslation[] = this.getAvailableInputs(inputs);
    return new MultiLanguageInput(allInputs, formControl);
  }

  updateInputValue(value: string, input: MultiLanguageInput, valid?: boolean): void {
    if (!this.isMultiInput(input)) {
      return;
    }
    input.setValue(value, this.currentInputLanguage, !!valid);
  }

  getInputValue(input: MultiLanguageInput): string {
    if (!this.isMultiInput(input)) {
      return input as any;
    }
    return input.inputs.find(trans => trans.language === this.currentInputLanguage)?.translation || '';
  }

  getCurrentValue(inputs: InputTranslation[]): string {
    if (!inputs) {
      return '';
    }
    return inputs.find(trans => trans.language === this.currentInputLanguage)?.translation || '';
  }

  multiLanguageFormFieldDefaultValue(useSystemLanguages?: boolean): InputTranslation[] {
    const languages = useSystemLanguages ? this.systemLanguages : this.inputLanguages;
    return languages?.map(language => ({translation: '', language} as InputTranslation)) || [];
  }

  private getAvailableInputs(inputs: InputTranslation[]): InputTranslation[] {
    if (!this.inputLanguages) {
      return inputs;
    }

    let allInputs: InputTranslation[] = [];
    if (inputs?.length) {
      allInputs = inputs.map(value => ({language: value.language, translation: value.translation}));
    }
    this.inputLanguages.forEach(language => {
      if (!allInputs.find(value => value.language === language)) {
        allInputs.push({language, translation: ''} as InputTranslation);
      }
    });
    return allInputs;
  }

  private isMultiInput(input: MultiLanguageInput): boolean {
    return input && input.inputs?.length > 0 && !!input.inputs[0].language;
  }
}
