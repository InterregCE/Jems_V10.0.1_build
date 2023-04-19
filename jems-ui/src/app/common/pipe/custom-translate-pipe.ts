import {TranslatePipe, TranslateService} from '@ngx-translate/core';
import {ChangeDetectorRef, Pipe, PipeTransform} from '@angular/core';
import {LanguageStore} from '../services/language-store.service';
import {
    ProjectStore
} from "@project/project-application/containers/project-application-detail/services/project-store.service";
import {map} from "rxjs/operators";
import {UntilDestroy, untilDestroyed} from "@ngneat/until-destroy";

// This pipe will replace the 'translate' pipe from '@ngx-translate'
// It's extended the TranslatePipe to provide support for using the fallback language's translation
// For translation keys that exist in the translation file but their translation is empty
// Note: for translation keys that don't exist in the translation file the setDefaultLang of TranslateService will do the same
@UntilDestroy()
@Pipe({name: 'translate', pure: false})
export class CustomTranslatePipe extends TranslatePipe implements PipeTransform {

  callIdPrefix = '';
  translateService: TranslateService;

  constructor(
    private languageStore: LanguageStore,
    private readonly projectStore: ProjectStore,
    translate: TranslateService,
    _ref: ChangeDetectorRef,
  ) {
    super(translate, _ref);
    this.translateService = translate;
    this.projectStore.currentVersionOfProject$.pipe(
      untilDestroyed(this),
      map(project => `call-id-${project.callSettings.callId}.`),
    ).subscribe(callIdPrefix => this.callIdPrefix = callIdPrefix);
  }

  transform(query: string | null, ...args: any[]): any {
    if (!query || !query.length) {
      return query;
    }

    let callSpecificKey = `${this.callIdPrefix}${query}`;
    let translationIfExists = this.translateIfExists(callSpecificKey, args);
    if (translationIfExists) {
      return translationIfExists;
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

  private translationExists(query: string, result: string): boolean {
      return !!(result && result !== query && !result.startsWith(this.callIdPrefix));
  }

  private translateIfExists(query: string, args: any[]): string | null {
    let translation = super.transform(query, args[0]);
    if (this.translationExists(query, translation)) {
      return translation;
    } else {
      return null;
    }
  }

}
