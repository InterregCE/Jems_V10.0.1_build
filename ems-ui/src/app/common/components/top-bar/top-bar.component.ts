import {Component, Input, OnInit} from '@angular/core';
import {SecurityService} from '../../../security/security.service';
import {Router} from '@angular/router';
import {TranslateService} from '@ngx-translate/core';
import {Observable} from 'rxjs';
import {OutputCurrentUser} from '@cat/api';
import {MenuItemConfiguration} from '../menu/model/menu-item.configuration';
import {TopBarService} from '@common/components/top-bar/top-bar.service';

@Component({
  selector: 'app-top-bar',
  templateUrl: './top-bar.component.html',
  styleUrls: ['./top-bar.component.scss']
})
export class TopBarComponent implements OnInit {

  @Input() isLoginNeeded: boolean;
  menuItems: Observable<MenuItemConfiguration[]>;

  constructor(private securityService: SecurityService,
              private router: Router,
              private topBarService: TopBarService,
              public translate: TranslateService,) {
    const auditUrl = this.prepareAuditUrl(window.location.href);
    this.topBarService.newAuditUrl(auditUrl)
  }

  ngOnInit(): void {
    this.securityService.reloadCurrentUser();
    this.menuItems = this.topBarService.menuItems();
  }

  get currentUser(): Observable<OutputCurrentUser | null> {
    return this.securityService.currentUser;
  }

  prepareAuditUrl(url: string): string {
    const splitHttp = url.split('://');
    const splitAddress = splitHttp[1].split('/');
    return splitHttp[0] + '://audit-' + splitAddress[0] + '/app/kibana#/discover?_g=(filters:!(),' +
      'refreshInterval:(pause:!t,value:0),time:(from:now-24h,to:now))' +
      '&_a=(columns:!(user.id,user.email,action,projectId,description),filters:!(),interval:auto,' +
      'query:(language:kuery,query:\'\'),sort:!())';
  }

  logout(): void {
    this.topBarService.logout();
  }

  changeLanguage(newLang: string): void {
    this.translate.use(newLang);
  }
}
