import {Injectable} from '@angular/core';
import {
  InputUserProfile,
  OutputUserProfile,
  UserProfileService,
  ProgrammeLanguageDTO,
} from '@cat/api';
import {filter, map, take, tap} from 'rxjs/operators';
import {TranslateService} from '@ngx-translate/core';
import {SecurityService} from '../../security/security.service';
import {Log} from '../utils/log';
import {BehaviorSubject, ReplaySubject, Subject} from 'rxjs';

@Injectable({providedIn: 'root'})
export class LanguageService {
  private profileChanged$ = new Subject<OutputUserProfile>();

  systemLanguage$ = new BehaviorSubject<string>(this.default());
  inputLanguages$ = new ReplaySubject<string[]>(1);
  systemLanguages$ = new ReplaySubject<string[]>(1);
  fallbackLanguage$ = new ReplaySubject<string>(1);
  languageSelection$ = this.systemLanguages$
    .pipe(
      map(value => ({
        isEnglishAvailable: this.isDefaultLanguageIncluded(value),
        languages: value
      }))
    );

  constructor(private userProfileService: UserProfileService,
              private translate: TranslateService,
              private securityService: SecurityService) {

    this.securityService.currentUser.subscribe(response => {
      if (!response) {
        translate.use(this.default());
        return;
      }
      this.userProfileService.getUserProfile()
        .pipe(
          take(1),
          tap(profile => this.profileChanged$.next(profile))
        )
        .subscribe();
    });

    this.profileChanged$
      .pipe(
        tap((user: InputUserProfile) => {
          if (user == null) {
            translate.use(this.default());
            return;
          }
          translate.use(this.getDefaultIfNotAvailable(user.language));
          this.systemLanguage$.next(this.getDefaultIfNotAvailable(user.language));
        }),
      )
      .subscribe();
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
      });
  }

  updateLanguages(languages: ProgrammeLanguageDTO[]): void {
    this.systemLanguages$.next(languages.filter(selections => selections?.ui).map(selections => selections.code));
    this.inputLanguages$.next(languages.filter(selections => selections?.input).map(selections => selections.code));
    const fallbackLanguages = languages.filter(selections => selections?.fallback).map(selections => selections.code);
    this.fallbackLanguage$.next(fallbackLanguages.length === 1 ? fallbackLanguages[0] : undefined);
  }

  default(): string {
    return 'EN';
  }

  getDefaultIfNotAvailable(newLanguage: string): string {
    const currentLang = this.translate.getLangs().find(lang => lang === newLanguage);
    return currentLang ? currentLang : this.default();
  }

  private isDefaultLanguageIncluded(langs: string[]): boolean {
    return !!langs.find((language: string) => language === this.default());
  }
}
