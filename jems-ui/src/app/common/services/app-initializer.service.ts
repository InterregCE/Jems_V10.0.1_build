import {Injectable} from '@angular/core';
import {take, tap} from 'rxjs/operators';
import {Log} from '../utils/log';
import {AvailableProgrammeLanguagesDTO, ProgrammeLanguageService} from '@cat/api';
import {TranslateService} from '@ngx-translate/core';
import {LanguageService} from './language.service';

@Injectable({providedIn: 'root'})
export class AppInitializerService {
  constructor(private programmeLanguageService: ProgrammeLanguageService,
              private translate: TranslateService,
              private languageService: LanguageService
  ) {
  }

  async loadLanguages(): Promise<AvailableProgrammeLanguagesDTO> {
    return this.programmeLanguageService.getAvailableProgrammeLanguages()
      .pipe(
        take(1),
        tap((languages: AvailableProgrammeLanguagesDTO) => this.languageService.systemLanguages$.next(languages.systemLanguages)),
        tap((languages: AvailableProgrammeLanguagesDTO) => this.languageService.inputLanguages$.next(languages.inputLanguages)),
        tap((languages: AvailableProgrammeLanguagesDTO) => this.languageService.fallbackLanguage$.next(languages.fallbackLanguage)),
        tap((languages: AvailableProgrammeLanguagesDTO) => this.translate.addLangs(languages.systemLanguages)),
        tap((languages: AvailableProgrammeLanguagesDTO) => Log.info('Fetched programme languages', this, languages)),
      ).toPromise();
  }
}
