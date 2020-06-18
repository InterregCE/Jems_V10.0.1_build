import {Component, Input, OnInit} from '@angular/core';
import {SecurityService} from '../../../security/security.service';
import {Router} from '@angular/router';
import {TranslateService} from '@ngx-translate/core';
import {Observable} from 'rxjs';
import {OutputCurrentUser} from '@cat/api';
import {MenuConfiguration} from '../configurations/menu.configuration';
import {MenuItemConfiguration} from '../configurations/menu-item.configuration';

@Component({
  selector: 'app-top-bar',
  templateUrl: './top-bar.component.html',
  styleUrls: ['./top-bar.component.scss']
})
export class TopBarComponent implements OnInit {

  @Input() isLoginNeeded: boolean;
  auditUrl = '';
  menuConfiguration: MenuConfiguration;

  constructor(private securityService: SecurityService,
              private router: Router,
              public translate: TranslateService) {
    this.prepareAuditUrl(window.location.href);
  }

  ngOnInit(): void {
    this.securityService.reloadCurrentUser();
    this.setUpMenuConfiguration();
  }

  get currentUser(): Observable<OutputCurrentUser | null> {
    return this.securityService.currentUser;
  }

  prepareAuditUrl(url: string): void {
    const splitHttp = url.split('://');
    const splitAddress = splitHttp[1].split('/');
    this.auditUrl = splitHttp[0] + '://audit-' + splitAddress[0] + '/app/kibana#/discover?_g=(filters:!(),' +
      'refreshInterval:(pause:!t,value:0),time:(from:now-24h,to:now))' +
      '&_a=(columns:!(username,action,projectId,description),filters:!(),interval:auto,' +
      'query:(language:kuery,query:\'\'),sort:!())';
  }

  logout(): void {
    this.securityService.logout();
    this.router.navigate(['/login']);
  }

  changeLanguage(newLang: string): void {
    this.translate.use(newLang);
  }

  setUpMenuConfiguration(): void {
    this.menuConfiguration = new MenuConfiguration({
      items: [
        new MenuItemConfiguration({
          name: 'Project Applications',
          isInternal: true,
          route: '/',
          action: (internal: boolean, route: string) => this.handleNavigation(internal, route),
        }),
        // TODO uncomment with the User management subtask (MP2-250) and add correct internal route.
        // new MenuItemConfiguration({
        //   name: 'User Management',
        //   isInternal: true,
        //   route: '/project/120',
        //   action: (internal: boolean, route: string) => this.handleNavigation(internal, route),
        // }),
        new MenuItemConfiguration({
          name: 'Audit Log',
          isInternal: false,
          route: this.auditUrl,
          action: (internal: boolean, route: string) => this.handleNavigation(internal, route),
        })
      ]
    });
  }

  handleNavigation(internalRoute: boolean, route: string): void {
    if (internalRoute) {
      this.router.navigate([route]);
    } else {
      window.open(route, '_blank');
    }
  }
}
