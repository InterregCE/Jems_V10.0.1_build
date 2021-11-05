import {Pipe, PipeTransform} from '@angular/core';
import {InputTranslation} from '@cat/api';
import {Observable, of} from 'rxjs';
import {map} from 'rxjs/operators';
import {MultiLanguageGlobalService} from '@common/components/forms/multi-language-container/multi-language-global.service';

@Pipe({name: 'translateByInputLanguage'})
export class TranslateByInputLanguagePipe implements PipeTransform {
  constructor(private multiLanguageGlobalService: MultiLanguageGlobalService) {
  }

  transform(translations: InputTranslation[] | null): Observable<string> {
    if (translations === null || translations.length === 0) {
      return of('');
    }

    return this.multiLanguageGlobalService.activeInputLanguage$.pipe(
      map(language => translations.find(translation => translation.language === language)?.translation || ''),
    );
  }
}
