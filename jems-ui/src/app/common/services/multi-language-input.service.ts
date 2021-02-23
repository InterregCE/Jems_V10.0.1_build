import {Injectable} from '@angular/core';
import {combineLatest, ReplaySubject} from 'rxjs';
import {tap} from 'rxjs/operators';
import {InputTranslation} from '@cat/api';
import {LanguageService} from './language.service';


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
    const sorted = inputs?.filter(input => !!input.translation)
      .sort((a, b) => a.language > b.language ? 1 : -1);
    if (!sorted?.length) {
      return '';
    }
    return sorted[0].translation;
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
}
