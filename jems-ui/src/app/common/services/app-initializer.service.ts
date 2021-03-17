import {Injectable} from '@angular/core';
import {take, tap} from 'rxjs/operators';
import {Log} from '../utils/log';
import {AvailableProgrammeLanguagesDTO, ProgrammeLanguageService} from '@cat/api';
import {LanguageStore} from './language-store.service';

@Injectable({providedIn: 'root'})
export class AppInitializerService {
  constructor(private programmeLanguageService: ProgrammeLanguageService,
              private languageStore: LanguageStore
  ) {
  }

  async loadLanguages(): Promise<AvailableProgrammeLanguagesDTO> {
    return this.programmeLanguageService.getAvailableProgrammeLanguages()
      .pipe(
        take(1),
        tap(languageSettings => this.languageStore.setLanguages(languageSettings.systemLanguages, languageSettings.inputLanguages, languageSettings.fallbackLanguage)),
        tap((languageSettings: AvailableProgrammeLanguagesDTO) => Log.info('Fetched programme languages', this, languageSettings)),
      ).toPromise();
  }
}
