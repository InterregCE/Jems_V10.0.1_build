import {Pipe, PipeTransform} from '@angular/core';
import {LanguageStore} from '../services/language-store.service';
import {InputTranslation} from '@cat/api';
import {Observable, of} from 'rxjs';
import {map, withLatestFrom} from 'rxjs/operators';

@Pipe({name: 'translateContainerLessInputsBySystemLanguage'})
export class TranslateContainerLessInputsBySystemLanguagePipe implements PipeTransform {
  constructor(private languageStore: LanguageStore) {
  }

  transform(translations: InputTranslation[] | null): Observable<string> {
    if (translations === null || translations.length === 0) {
      return of('');
    }
    return this.languageStore.currentSystemLanguage$.pipe(
      map(currentSystemLanguage => translations.find(translation => translation.language === currentSystemLanguage)?.translation || ''),
      withLatestFrom(this.languageStore.fallbackLanguage$),
      map(([translationByCurrentSystemLanguage, fallbackLanguage]) => translationByCurrentSystemLanguage === '' ? translations.find(translation => translation.language === fallbackLanguage)?.translation || '' : translationByCurrentSystemLanguage),
      map((translationAfterFallbackCheck) => translationAfterFallbackCheck === '' ? this.getFirstTranslation(translations) : translationAfterFallbackCheck)
    );
  }

  private getFirstTranslation(inputs: InputTranslation[]): string {
    const sorted = inputs?.filter(input => !!input.translation)
      .sort((a, b) => a.language > b.language ? 1 : -1);
    if (!sorted?.length) {
      return '';
    }
    return sorted[0].translation;
  }
}
