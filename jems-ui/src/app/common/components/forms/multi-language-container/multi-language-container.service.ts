import {Injectable} from '@angular/core';
import {Observable, of} from 'rxjs';
import {InputTranslation} from '@cat/api';
import {LanguageStore} from '@common/services/language-store.service';
import {MultiLanguageGlobalService} from './multi-language-global.service';

export enum MultiLanguageContainerTypeEnum {
  INPUT_LANGUAGES = 'input',
  SYSTEM_LANGUAGES = 'system',
  STATIC_LANGUAGES = 'static'
}

@Injectable()
export class MultiLanguageContainerService {

  languages$: Observable<string[]>;
  activeLanguage$: Observable<string>;

  private type: MultiLanguageContainerTypeEnum;
  private staticLanguages: string[] = [];

  constructor(private languageStore: LanguageStore, private multiLanguageGlobalService: MultiLanguageGlobalService) {
  }

  init(useSystemLanguages: boolean, staticLanguages?: string[]): void {

    if (staticLanguages?.length) {
      this.type = MultiLanguageContainerTypeEnum.STATIC_LANGUAGES;
      this.staticLanguages = staticLanguages;
      this.languages$ = of(staticLanguages);
      this.activeLanguage$ = of(staticLanguages && staticLanguages[0]);
    } else if (useSystemLanguages) {
      this.type = MultiLanguageContainerTypeEnum.SYSTEM_LANGUAGES;
      this.languages$ = this.languageStore.systemLanguages$;
      this.activeLanguage$ = this.multiLanguageGlobalService.activeSystemLanguage$;
    } else {
      this.type = MultiLanguageContainerTypeEnum.INPUT_LANGUAGES;
      this.languages$ = this.languageStore.inputLanguages$;
      this.activeLanguage$ = this.multiLanguageGlobalService.activeInputLanguage$;
    }
  }

  multiLanguageFormFieldDefaultValue(includeFallbackIfNotExist?: boolean): InputTranslation[] {
    const neededLanguages = [...this.getCurrentLanguagesValue()] || [];
    if (includeFallbackIfNotExist && neededLanguages.indexOf(this.languageStore.getFallbackLanguageValue()) < 0) {
      neededLanguages.push(this.languageStore.getFallbackLanguageValue());
    }
    return neededLanguages.map(language => ({translation: '', language} as InputTranslation));
  }

  changeLanguage(language: string, useSystemLanguages: boolean): void {
    if (useSystemLanguages) {
      this.multiLanguageGlobalService.setActiveSystemLanguage(language);
    } else {
      this.multiLanguageGlobalService.setActiveInputLanguage(language);
    }
  }

  didLanguagesChange(savedTranslations: InputTranslation[]): boolean {
    const currentLanguagesValue = this.getCurrentLanguagesValue();
    if (savedTranslations.length !== currentLanguagesValue.length) {
      return true;
    }
    return !!currentLanguagesValue.filter(existingLanguage =>
      !savedTranslations.filter(newTranslation => newTranslation.language === existingLanguage).length).length;
  }

  private getCurrentLanguagesValue(): string[] {
    switch (this.type) {
      case MultiLanguageContainerTypeEnum.SYSTEM_LANGUAGES:
        return this.languageStore.getSystemLanguagesValue();
      case MultiLanguageContainerTypeEnum.STATIC_LANGUAGES:
        return this.staticLanguages;
      default:
        return this.languageStore.getInputLanguagesValue();
    }
  }

}
