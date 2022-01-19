import {Component, Input} from '@angular/core';
import {TranslateService} from '@ngx-translate/core';
import {Observable} from 'rxjs';
import {OutputCurrentUser} from '@cat/api';
import {MenuItemConfiguration} from './menu-item.configuration';
import {TopBarService} from '@common/components/top-bar/top-bar.service';
import {LanguageStore} from '../../services/language-store.service';
import {finalize, map, withLatestFrom} from 'rxjs/operators';
import {ResourceStoreService} from '@common/services/resource-store.service';
import {RoutingService} from '@common/services/routing.service';

@Component({
  selector: 'app-top-bar',
  templateUrl: './top-bar.component.html',
  styleUrls: ['./top-bar.component.scss'],
  providers: [TopBarService]
})
export class TopBarComponent {

  @Input()
  currentUser: OutputCurrentUser;

  @Input()
  isAuthenticated: boolean;

  menuItems$: Observable<MenuItemConfiguration[]>;
  editUserItem$: Observable<MenuItemConfiguration | null>;
  logoutOngoing = false;
  isNavBarCollapsed = true;

  largeLogo$ = this.resourceStore.largeLogo$;

  languageSettings$ = this.languageStore.systemLanguages$
    .pipe(
      withLatestFrom(this.languageStore.fallbackLanguage$),
      map(([languages, fallbackLanguage]) => ({
        languages,
        fallbackLanguage,
        isDefaultAvailable: !!languages.find((lang: string) => lang === fallbackLanguage)
      }))
    );

  constructor(public router: RoutingService,
              private topBarService: TopBarService,
              public resourceStore: ResourceStoreService,
              public languageStore: LanguageStore,
              public translate: TranslateService) {
    this.menuItems$ = this.topBarService.menuItems$;
    this.editUserItem$ = this.topBarService.editUserItem$;
  }

  logout(): void {
    this.logoutOngoing = true;
    this.topBarService.logout()
      .pipe(finalize(() => this.logoutOngoing = false))
      .subscribe(() => {
        this.isNavBarCollapsed = true;
        this.router.navigate(['/login']);
      });
  }

  changeLanguage(newLang: string): void {
    this.languageStore.setSystemLanguageAndUpdateProfile(newLang);
    this.isNavBarCollapsed = true;
  }
}
