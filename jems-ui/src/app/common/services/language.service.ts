import {Injectable} from '@angular/core';
import {
  InputUserProfile,
  OutputProgrammeLanguage,
  OutputUserProfile,
  ProgrammeLanguageService,
  UserProfileService
} from '@cat/api'
import {filter, map, shareReplay, take, tap} from 'rxjs/operators';
import {TranslateService} from '@ngx-translate/core';
import {SecurityService} from '../../security/security.service';
import {Log} from '../utils/log';
import {BehaviorSubject, merge, ReplaySubject, Subject} from 'rxjs';

@Injectable({providedIn: 'root'})
export class LanguageService {
  private profileChanged$ = new Subject<OutputUserProfile>();
  private systemLanguageSubject = new BehaviorSubject<string>(this.default())
  public systemLanguage$ = this.systemLanguageSubject.asObservable();
  public languagesChanged$: ReplaySubject<OutputProgrammeLanguage[]> = new ReplaySubject(1);
  public currentLanguage$ = new Subject<OutputProgrammeLanguage.CodeEnum>();
  private MAX_NUMBER_AVAILABLE_LANGUAGES = 4;

  languagesInitialized$ = this.programmeLanguageService.get()
    .pipe(
      take(1),
      tap(programmeLanguages => Log.info('Fetched programmeLanguages', this, programmeLanguages)),
      shareReplay(1)
    );

  languageList$ = merge(this.languagesInitialized$, this.languagesChanged$)
    .pipe(
      map(programmeLanguages => {
        return programmeLanguages
          .filter(selections => selections?.ui)
          .map(selections => selections.code)
      }),
      map(value => ({isEnglishAvailable: this.isDefaultLanguageIncluded(value), languages: value})));

  public inputLanguageList$ = merge(this.languagesInitialized$, this.languagesChanged$).pipe(
    map(languages => {
          let availableLanguages = languages
            .filter(value => value.input)
            .slice(0, this.MAX_NUMBER_AVAILABLE_LANGUAGES)
            .map(value => value.code);
          // if there is no input language selected in the programme setup, use the fallback language
          if (availableLanguages.length < 1) {
            availableLanguages = languages
              .filter(value => value.fallback)
              .slice(0, this.MAX_NUMBER_AVAILABLE_LANGUAGES)
              .map(value => value.code);
          }
          return availableLanguages;
    })
  );

  isDefaultLanguageIncluded(langs: string[]): boolean {
    return !!langs.find((language: string) => language === this.default());
  }

  constructor(private userProfileService: UserProfileService,
              private translate: TranslateService,
              private securityService: SecurityService,
              private programmeLanguageService: ProgrammeLanguageService) {

    this.programmeLanguageService.get()
      .pipe(
        take(1),
      )
      .subscribe( response => {
        const availableLanguages = response
          .filter(value => value.ui)
          .map(value => value.code);
        translate.addLangs(availableLanguages);
      });

    this.securityService.currentUser.subscribe(response => {
      if (!response) {
        translate.use(this.default());
        return;
      }
      this.userProfileService.getUserProfile()
        .pipe(
          take(1),
          tap( profile => this.profileChanged$.next(profile))
          )
        .subscribe()
    });

    this.profileChanged$
      .pipe(
        tap((user: InputUserProfile) => {
          if (user == null ){
            translate.use(this.default());
            return;
          }
          translate.use(this.getDefaultIfNotAvailable(user.language));
        }),
        tap(user => this.systemLanguageSubject.next(this.getDefaultIfNotAvailable(user.language))),
      )
      .subscribe()
  }

  changeLanguage(newLanguage: string): void {
    this.translate.use(newLanguage);
    this.securityService.currentUser
      .pipe(
        filter(user => !!user),
        take(1)
      )
      .subscribe(response => {
        const inputProfile = {language: newLanguage} as InputUserProfile;
        this.userProfileService.updateUserProfile(inputProfile)
          .pipe(
            take(1),
            tap(profile => Log.info('Updated user profile', this, profile)),
            tap(profile => this.profileChanged$.next(profile))
          )
          .subscribe();
      })
  }

  default(): string {
    return 'EN';
  }

  getDefaultIfNotAvailable(newLanguage: string): string {
    const currentLang = this.translate.getLangs().find(lang => lang === newLanguage);
    return currentLang ? currentLang : this.default();
  }
}
