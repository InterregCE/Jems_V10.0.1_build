import {TranslatePipe, TranslateService} from '@ngx-translate/core';
import {ChangeDetectorRef, Pipe, PipeTransform} from '@angular/core';
import {LanguageStore} from '../services/language-store.service';

// This pipe will replace the 'translate' pipe from '@ngx-translate'
// It's extended the TranslatePipe to provide support for using the fallback language's translation
// For translation keys that exist in the translation file but their translation is empty
// Note: for translation keys that don't exist in the translation file the setDefaultLang of TranslateService will do the same
@Pipe({name: 'translate', pure: false})
export class CustomTranslatePipe extends TranslatePipe implements PipeTransform {

  translateService: TranslateService;

  constructor(private languageStore: LanguageStore, translate: TranslateService, _ref: ChangeDetectorRef) {
    super(translate, _ref);
    this.translateService = translate;
  }

  transform(query: string, ...args: any[]): any {
    if (!query || !query.length) {
      return query;
    }
    let translation = super.transform(query, ...args);
    if (translation === null || translation === '') {
      const currentLang = this.translateService.currentLang;
      this.translateService.use(this.languageStore.getFallbackLanguageValue());
      translation = this.translateService.instant(query, args);
      this.translateService.use(currentLang);
    }
    return translation;
  }
}
