import {Component, Input, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {TranslateService} from '@ngx-translate/core';
import {Observable} from 'rxjs';
import {OutputCurrentUser} from '@cat/api';
import {MenuItemConfiguration} from '../menu/model/menu-item.configuration';
import {TopBarService} from '@common/components/top-bar/top-bar.service';
import {LanguageStore} from '../../services/language-store.service';
import {finalize, map, withLatestFrom} from 'rxjs/operators';

@Component({
  selector: 'app-top-bar',
  templateUrl: './top-bar.component.html',
  styleUrls: ['./top-bar.component.scss'],
})
export class TopBarComponent implements OnInit {

  @Input()
  currentUser: OutputCurrentUser;

  @Input()
  isAuthenticated: boolean;

  menuItems: Observable<MenuItemConfiguration[]>;
  logoutOngoing = false;
  isNavBarCollapsed = true;

  languageSettings$ = this.languageStore.systemLanguages$
    .pipe(
      withLatestFrom(this.languageStore.fallbackLanguage$),
      map(([languages, fallbackLanguage]) => ({
        languages,
        fallbackLanguage,
        isDefaultAvailable: !!languages.find((lang: string) => lang === fallbackLanguage)
      }))
    );

  constructor(public router: Router,
              private topBarService: TopBarService,
              public languageStore: LanguageStore,
              public translate: TranslateService) {
    const auditUrl = this.prepareAuditUrl(window.location.href);
    this.topBarService.newAuditUrl(auditUrl);
  }

  ngOnInit(): void {
    this.menuItems = this.topBarService.menuItems();
  }

  prepareAuditUrl(url: string): string {
    const splitHttp = url.split('://');
    const splitAddress = splitHttp[1].split('/');
    const auditFilter = 'discover?_g=(filters:!(),refreshInterval:(pause:!t,value:0),time:(from:now-24h,to:now))' +
      '&_a=(columns:!(user.id,user.email,action,projectId,description),filters:!(),interval:auto,query:(language:kuery,query:\'\'),sort:!())';
    return `${splitHttp[0]}://audit-${splitAddress[0]}/app/kibana#/${auditFilter}`;
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
