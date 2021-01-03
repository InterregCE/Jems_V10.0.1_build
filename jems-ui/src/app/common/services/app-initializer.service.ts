import {Injectable} from '@angular/core';
import {take, tap} from 'rxjs/operators';
import {Log} from '../utils/log';
import {OutputProgrammeLanguage, ProgrammeLanguageService} from '@cat/api';
import {TranslateService} from '@ngx-translate/core';
import {LanguageService} from './language.service';

@Injectable({providedIn: 'root'})
export class AppInitializerService {
  constructor(private programmeLanguageService: ProgrammeLanguageService,
              private translate: TranslateService,
              private languageService: LanguageService
  ) {
  }

  async loadLanguages(): Promise<OutputProgrammeLanguage[]> {
    return this.programmeLanguageService.get()
      .pipe(
        take(1),
        tap(languages => {
          const availableLanguages = languages
            .filter(value => value.ui)
            .map(value => value.code);
          this.translate.addLangs(availableLanguages);
        }),
        tap(languages => this.languageService.languages$.next(languages)),
        tap(languages => Log.info('Fetched programme languages', this, languages)),
      ).toPromise();
  }
}
