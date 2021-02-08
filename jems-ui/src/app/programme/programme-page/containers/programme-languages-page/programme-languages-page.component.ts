import {ChangeDetectionStrategy, Component, OnDestroy} from '@angular/core';
import {BaseComponent} from '@common/components/base-component';
import {OutputProgrammeLanguage, ProgrammeLanguageService} from '@cat/api';
import {catchError, mergeMap, tap} from 'rxjs/operators';
import {Log} from '../../../../common/utils/log';
import {HttpErrorResponse} from '@angular/common/http';
import {merge, Subject} from 'rxjs';
import {I18nValidationError} from '@common/validation/i18n-validation-error';
import {ProgrammePageSidenavService} from '../../services/programme-page-sidenav.service';
import {Permission} from '../../../../security/permissions/permission';
import {LanguageService} from '../../../../common/services/language.service';

@Component({
  selector: 'app-programme-languages-page',
  templateUrl: './programme-languages-page.component.html',
  styleUrls: ['./programme-languages-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProgrammeLanguagesPageComponent extends BaseComponent implements OnDestroy {
  Permission = Permission;

  languagesSaveError$ = new Subject<I18nValidationError | null>();
  languagesSaveSuccess$ = new Subject<boolean>();
  saveLanguages$ = new Subject<OutputProgrammeLanguage[]>();

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
              private languageService: LanguageService) {
    super();
  }

  reloadLanguages(response: OutputProgrammeLanguage[]): void {
    this.languageService.updateLanguages(response);
  }
}
