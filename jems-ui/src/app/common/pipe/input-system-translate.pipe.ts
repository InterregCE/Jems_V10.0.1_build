import {Pipe, PipeTransform} from '@angular/core';
import {LanguageService} from '../services/language.service';
import {InputTranslation} from '@cat/api';
import {Observable, of} from 'rxjs';
import {map} from 'rxjs/operators';

@Pipe({name: 'translateSystem'})
export class InputSystemTranslatePipe implements PipeTransform {
  constructor(private languageService: LanguageService) {
  }

  transform(translations: Array<InputTranslation>): Observable<string> {
    if (translations === null || translations.length === 0) {
      return of('');
    }

    return this.languageService.systemLanguage$.pipe(
      map(language => translations.find(translation => translation.language === language)?.translation || '')
    );
  }
}
