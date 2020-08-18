import {Injectable} from '@angular/core';
import {UserProfileService, InputUserProfile, OutputUserProfile} from '@cat/api'
import {filter, take, tap} from 'rxjs/operators';
import {TranslateService} from '@ngx-translate/core';
import {SecurityService} from '../../security/security.service';
import {Log} from '../utils/log';
import {Subject} from 'rxjs';

@Injectable()
export class LanguageService {
  private profileChanged$ = new Subject<OutputUserProfile>();

  constructor(private userProfileService: UserProfileService,
              private translate: TranslateService,
              private securityService: SecurityService) {

    translate.setDefaultLang('en');
    translate.addLangs(['en', 'de', 'ja_JP']);

    this.securityService.currentUser.subscribe(response => {
      if (!response) {
        translate.use(translate.defaultLang);
        return;
      }
      this.userProfileService.getUserProfile()
        .pipe(
          take(1),
          tap( profile => this.profileChanged$.next(profile))
          )
        .subscribe()
    })

    this.profileChanged$
      .pipe(
        tap((user: InputUserProfile) => {
          if (user == null ){
            translate.use(translate.defaultLang)
            return;
          }
          const currentLang = translate.getLangs().find( lang => lang === user.language)
          if (currentLang) {
            translate.use(user.language)
            return;
          }
          translate.use(translate.defaultLang);
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

}
