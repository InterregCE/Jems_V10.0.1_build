import {ChangeDetectionStrategy, Component, OnDestroy} from '@angular/core';
import {BaseComponent} from '@common/components/base-component';
import {ProgrammeLanguageDTO, ProgrammeLanguageService} from '@cat/api';
import {catchError, mergeMap, tap} from 'rxjs/operators';
import {Log} from '../../../../common/utils/log';
import {HttpErrorResponse} from '@angular/common/http';
import {merge, Subject} from 'rxjs';
import {I18nValidationError} from '@common/validation/i18n-validation-error';
import {ProgrammePageSidenavService} from '../../services/programme-page-sidenav.service';
import {LanguageStore} from '../../../../common/services/language-store.service';

@Component({
  selector: 'jems-programme-languages-page',
  templateUrl: './programme-languages-page.component.html',
  styleUrls: ['./programme-languages-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProgrammeLanguagesPageComponent extends BaseComponent implements OnDestroy {
  languagesSaveError$ = new Subject<I18nValidationError | null>();
  languagesSaveSuccess$ = new Subject<boolean>();
  saveLanguages$ = new Subject<ProgrammeLanguageDTO[]>();

  private initLanguages$ = this.programmeLanguageService.get()
    .pipe(
      tap(languages => Log.info('Fetched programme languages:', this, languages))
    );

  private savedLanguages$ = this.saveLanguages$
    .pipe(
      mergeMap(programmeLanguage => this.programmeLanguageService.update(programmeLanguage)),
      tap(saved => Log.info('Updated languages:', this, saved)),
      tap(() => this.languagesSaveSuccess$.next(true)),
      tap(() => this.languagesSaveError$.next(null)),
      tap((response) => this.reloadLanguages(response)),
      catchError((error: HttpErrorResponse) => {
        this.languagesSaveError$.next(error.error);
        throw error;
      })
    );
  languages$ = merge(this.initLanguages$, this.savedLanguages$);

  constructor(private programmeLanguageService: ProgrammeLanguageService,
              private programmePageSidenavService: ProgrammePageSidenavService,
              private languageStore: LanguageStore) {
    super();
  }

  reloadLanguages(response: ProgrammeLanguageDTO[]): void {
    this.languageStore.setLanguages(
      response.filter(selections => selections?.ui).map(selections => selections.code),
      response.filter(selections => selections?.input).map(selections => selections.code),
      response.find(selections => selections?.fallback)?.code.valueOf()
    );
  }
}
