import {Injectable} from '@angular/core';
import {ReplaySubject} from 'rxjs';
import {tap} from 'rxjs/operators';
import {InputTranslation, OutputProgrammeLanguage} from '@cat/api';
import {LanguageService} from './language.service';
import {MultiLanguageInput} from '@common/components/forms/multi-language/multi-language-input';

@Injectable({
  providedIn: 'root'
})
export class MultiLanguageInputService {
  private static MAX_NUMBER_AVAILABLE_LANGUAGES = 4;
  private currentLanguage: OutputProgrammeLanguage.CodeEnum;

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

  initInput(inputs: InputTranslation[], validators?: Function[]): MultiLanguageInput {
    let allInputs: InputTranslation[] = [];
    if (inputs?.length) {
      allInputs = inputs.map(value => ({language: value.language, translation: value.translation}));
    }
    this.languages.forEach(language => {
      if (!allInputs.find(value => value.language === language)) {
        allInputs.push({language, translation: ''});
      }
    });
    return new MultiLanguageInput(allInputs, validators);
  }

  updateInputValue(value: string, input: MultiLanguageInput): void {
    const translation = input?.inputs?.find(trans => trans.language === this.currentLanguage);
    if (translation) {
      translation.translation = value;
    }
  }

  getInputValue(input: MultiLanguageInput): string {
    if (!this.isMultiInput(input)) {
      return input as any;
    }
    return input.inputs.find(trans => trans.language === this.currentLanguage)?.translation || '';
  }

  inputValidForLanguage(input: MultiLanguageInput, language: InputTranslation.LanguageEnum): boolean {
    if (!this.isMultiInput(input)) {
      return true;
    }
    return input.valid(language);
  }

  inputValid(input: MultiLanguageInput): boolean {
    if (!this.isMultiInput(input)) {
      return true;
    }
    return this.languages.every(language => this.inputValidForLanguage(input, language));
  }

  private isMultiInput(input: MultiLanguageInput): boolean {
    return input && input.inputs?.length > 0 && !!input.inputs[0].language;
  }
}
