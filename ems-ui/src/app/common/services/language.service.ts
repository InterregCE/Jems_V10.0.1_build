import {Injectable} from '@angular/core';
import {
  InputUserProfile,
  OutputProgrammeData,
  OutputUserProfile,
  ProgrammeDataService,
  UserProfileService
} from '@cat/api'
import {filter, map, shareReplay, take, tap} from 'rxjs/operators';
import {TranslateService} from '@ngx-translate/core';
import {SecurityService} from '../../security/security.service';
import {Log} from '../utils/log';
import {merge, ReplaySubject, Subject} from 'rxjs';

@Injectable({providedIn: 'root'})
export class LanguageService {
  private profileChanged$ = new Subject<OutputUserProfile>();
  public languagesChanged$: ReplaySubject<OutputProgrammeData> = new ReplaySubject(1);

  languagesInitialized$ = this.programmeDataService.get()
    .pipe(
      take(1),
      tap(programmeData => Log.info('Fetched programmeData', this, programmeData)),
      shareReplay(1)
    );

  languageList$ = merge(this.languagesInitialized$, this.languagesChanged$)
    .pipe(
      map(programmeData => {
        return programmeData.systemLanguageSelections
          .filter(selections => selections?.selected)
          .map(selections => selections.name)
      }),
      map(value => ({isEnglishAvailable: this.isDefaultLanguageIncluded(value), languages: value})));

  isDefaultLanguageIncluded(langs: string[]): boolean {
    return !!langs.find((language: string) => language === this.default());
  }

  constructor(private userProfileService: UserProfileService,
              private translate: TranslateService,
              private securityService: SecurityService,
              private programmeDataService: ProgrammeDataService) {

    this.programmeDataService.get()
      .pipe(
        take(1),
      )
      .subscribe( response => {
        const availableLanguages = response.systemLanguageSelections
          .filter(value => value.selected)
          .map(value => value.name);
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
          const currentLang = translate.getLangs().find( lang => lang === user.language);
          if (currentLang) {
            translate.use(user.language);
            return;
          }
          translate.use(this.default());
        })
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
}
