import {Pipe, PipeTransform} from '@angular/core';
import {LanguageStore} from '../services/language-store.service';
import {InputTranslation} from '@cat/api';
import {Observable, of} from 'rxjs';
import {map, withLatestFrom} from 'rxjs/operators';

@Pipe({name: 'translateBySystemLanguage'})
export class TranslateBySystemLanguagePipe implements PipeTransform {
  constructor(private languageStore: LanguageStore) {
  }

  transform(translations: Array<InputTranslation> | null, useFallbackLanguage = true): Observable<string> {
    if (translations === null || translations.length === 0) {
      return of('');
    }

    return  this.languageStore.currentSystemLanguage$.pipe(
      map(language => translations.find(translation => translation.language === language)?.translation || ''),
      withLatestFrom(this.languageStore.fallbackLanguage$),
      map(([originalTranslation, fallbackLanguage]) => useFallbackLanguage && originalTranslation === ''
        ? translations.find(translation => translation.language === fallbackLanguage)?.translation || ''
        : originalTranslation)
    );

  }
}
