import {Injectable} from '@angular/core';
import {InputTranslation, InputUserProfile, UserProfileService} from '@cat/api';
import {catchError, filter, map, mergeMap, startWith, tap, withLatestFrom} from 'rxjs/operators';
import {TranslateService} from '@ngx-translate/core';
import {SecurityService} from '../../security/security.service';
import {BehaviorSubject, Observable, of} from 'rxjs';
import {Log} from '../utils/log';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import LanguageEnum = InputTranslation.LanguageEnum;

export const DEFAULT_FALLBACK_LANGUAGE = LanguageEnum.EN;

@UntilDestroy()
@Injectable({providedIn: 'root'})
export class LanguageStore {

  inputLanguages$: Observable<string[]>;
  systemLanguages$: Observable<string[]>;
  fallbackLanguage$: Observable<string>;
  currentSystemLanguage$: Observable<string>;

  private inputLanguagesSubject = new BehaviorSubject<string[]>([]);
  private systemLanguagesSubject = new BehaviorSubject<string[]>([]);
  private fallbackLanguageSubject = new BehaviorSubject<string>(DEFAULT_FALLBACK_LANGUAGE);
  private currentSystemLanguageSubject = new BehaviorSubject<string>(DEFAULT_FALLBACK_LANGUAGE);

  constructor(private userProfileService: UserProfileService,
              private translate: TranslateService,
              private securityService: SecurityService) {
    this.inputLanguages$ = this.inputLanguagesSubject.asObservable();
    this.systemLanguages$ = this.systemLanguagesSubject.asObservable();
    this.currentSystemLanguage$ = this.currentSystemLanguageSubject.asObservable();
    this.fallbackLanguage$ = this.fallbackLanguageSubject.asObservable();

    this.securityService.currentUser.pipe(
      mergeMap(user => user ? this.userProfileService.getUserProfile().pipe(map(profile => profile?.language)) : this.fallbackLanguage$),
      tap(language => this.setSystemLanguage(language)),
      untilDestroyed(this)
    ).subscribe();
  }

  setSystemLanguageAndUpdateProfile(newLanguage: string): void {
    this.setSystemLanguage(newLanguage);
    this.securityService.currentUser
      .pipe(
        filter(user => !!user),
        mergeMap(() => this.userProfileService.updateUserProfile({language: newLanguage} as InputUserProfile)),
        tap(profile => Log.info('Updated user profile', this, profile)),
        catchError(error => {
          Log.error('Failed to update user profile language', error);
          return of(null);
        }),
        untilDestroyed(this)
      ).subscribe();
  }

  setLanguages(systemLanguages: string[], inputLanguages: string[], fallbackLanguage: string | undefined): void {
    this.systemLanguagesSubject.next(systemLanguages);
    this.inputLanguagesSubject.next(inputLanguages);
    this.fallbackLanguageSubject.next(fallbackLanguage || DEFAULT_FALLBACK_LANGUAGE.valueOf());
    if (!this.isSystemLanguageExist(this.currentSystemLanguageSubject.getValue())) {
      this.currentSystemLanguageSubject.next(this.determineSystemLanguage(systemLanguages, this.fallbackLanguageSubject.getValue()));
    }
    this.translate.addLangs(systemLanguages);
    this.translate.setDefaultLang(fallbackLanguage || DEFAULT_FALLBACK_LANGUAGE);
  }

  isInputLanguageExist(language: string): boolean {
    return !!this.inputLanguagesSubject.getValue().find(lang => language === lang);
  }

  isSystemLanguageExist(language: string): boolean {
    return !!this.inputLanguagesSubject.getValue().find(lang => language === lang);
  }

  getSystemLanguagesValue(): string[] {
    return this.systemLanguagesSubject.getValue();
  }

  getInputLanguagesValue(): string[] {
    return this.inputLanguagesSubject.getValue();
  }

  getFallbackLanguageValue(): string {
    return this.fallbackLanguageSubject.getValue();
  }

  private setSystemLanguage(newLanguage: string): void {
    of(newLanguage).pipe(
      withLatestFrom(this.fallbackLanguage$.pipe(startWith(DEFAULT_FALLBACK_LANGUAGE))),
      map(([newLang, fallbackLanguage]) => newLang || fallbackLanguage),
      tap(language => this.translate.use(language)),
      tap(language => this.currentSystemLanguageSubject.next(language)),
      untilDestroyed(this)
    ).subscribe();
  }

  private determineSystemLanguage(systemLanguages: string[], fallbackLanguage: string): string {
    if (!!systemLanguages.find((lang: string) => lang === fallbackLanguage)) {
      return fallbackLanguage;
    } else {
      return systemLanguages.length > 0 ? systemLanguages[0] : DEFAULT_FALLBACK_LANGUAGE;
    }
  }
}
