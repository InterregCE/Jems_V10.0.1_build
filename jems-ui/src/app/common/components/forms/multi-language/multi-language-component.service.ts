import {Injectable} from '@angular/core';
import {BehaviorSubject, Observable, of, Subject} from 'rxjs';
import {MultiLanguageInputService} from '../../../services/multi-language-input.service';
import {InputTranslation} from '@cat/api';

@Injectable()
export class MultiLanguageComponentService {

  languages$: Observable<string[]> = this.multiLanguageService.inputLanguages$;
  currentLanguage$: Subject<string> = this.multiLanguageService.currentInputLanguage$;
  languages: string[] = this.multiLanguageService.inputLanguages;

  didLanguagesChange(savedTranslations: InputTranslation[]): boolean {
    if (savedTranslations.length !== this.languages.length) {
      return true;
    }
    return !!this.languages.filter(existingLanguage =>
      !savedTranslations.filter(newTranslation => newTranslation.language === existingLanguage).length).length;
  }

  constructor(private multiLanguageService: MultiLanguageInputService) {
  }

  init(useSystemLanguages: boolean, staticLanguages?: string[]): void {
    if (staticLanguages?.length) {
      this.languages$ = of(staticLanguages);
      this.currentLanguage$ = new BehaviorSubject(staticLanguages[0]);
      this.languages = staticLanguages;
      return;
    }
    if (useSystemLanguages) {
      this.languages$ = this.multiLanguageService.systemLanguages$;
      this.currentLanguage$ = this.multiLanguageService.currentSystemLanguage$;
      this.languages = this.multiLanguageService.systemLanguages;
    }
  }

  multiLanguageFormFieldDefaultValue(): InputTranslation[] {
    return this.languages?.map(language => ({translation: '', language} as InputTranslation)) || [];
  }

}
