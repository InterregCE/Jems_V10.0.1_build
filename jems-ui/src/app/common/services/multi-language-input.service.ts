import {Injectable} from '@angular/core';
import {ReplaySubject} from 'rxjs';
import {tap} from 'rxjs/operators';
import {InputTranslation, OutputProgrammeLanguage} from '@cat/api';
import {LanguageService} from './language.service';
import {MultiLanguageInput} from '@common/components/forms/multi-language/multi-language-input';
import {AbstractControl} from '@angular/forms';

@Injectable({
  providedIn: 'root'
})
export class MultiLanguageInputService {
  private static MAX_NUMBER_AVAILABLE_LANGUAGES = 4;
  private currentLanguage: InputTranslation.LanguageEnum;

  languages$ = new ReplaySubject<OutputProgrammeLanguage.CodeEnum[]>();
  languages: OutputProgrammeLanguage.CodeEnum[];
  currentLanguage$ = new ReplaySubject<OutputProgrammeLanguage.CodeEnum>(1);

  constructor(private languageService: LanguageService) {
    this.languageService.languages$
      .pipe(
        tap(languages => {
          let availableLanguages = languages
            .filter(value => value.input)
            .slice(0, MultiLanguageInputService.MAX_NUMBER_AVAILABLE_LANGUAGES)
            .map(value => value.code);
          // if there is no input language selected in the programme setup, use the fallback language
          if (availableLanguages.length < 1) {
            availableLanguages = languages
              .filter(value => value.fallback)
              .slice(0, MultiLanguageInputService.MAX_NUMBER_AVAILABLE_LANGUAGES)
              .map(value => value.code);
          }
          this.languages$.next(availableLanguages);
          this.languages = availableLanguages;
          this.currentLanguage$.next(availableLanguages && availableLanguages[0]);
          this.currentLanguage = availableLanguages && availableLanguages[0];
        })
      ).subscribe();

    this.currentLanguage$
      .pipe(
        tap(lang => this.currentLanguage = lang)
      )
      .subscribe();
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
    input.setValue(value, this.currentLanguage, !!valid);
  }

  getInputValue(input: MultiLanguageInput): string {
    if (!this.isMultiInput(input)) {
      return input as any;
    }
    return input.inputs.find(trans => trans.language === this.currentLanguage)?.translation || '';
  }

  getCurrentValue(inputs: InputTranslation[]): string {
    if (!inputs) {
      return '';
    }
    return inputs.find(trans => trans.language === this.currentLanguage)?.translation || '';
  }

  multiLanguageFormFieldDefaultValue(): InputTranslation[] {
    return this.languages?.map(language => {
      return {translation: '', language} as InputTranslation;
    }) || [];

  }

  private getAvailableInputs(inputs: InputTranslation[]): InputTranslation[] {
    if (!this.languages) {
      return inputs;
    }

    let allInputs: InputTranslation[] = [];
    if (inputs?.length) {
      allInputs = inputs.map(value => ({language: value.language, translation: value.translation}));
    }
    this.languages.forEach(language => {
      if (!allInputs.find(value => value.language === language)) {
        allInputs.push({language, translation: ''});
      }
    });
    return allInputs;
  }

  private isMultiInput(input: MultiLanguageInput): boolean {
    return input && input.inputs?.length > 0 && !!input.inputs[0].language;
  }
}
