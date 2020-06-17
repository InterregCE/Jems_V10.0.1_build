import {Component, Input} from '@angular/core';
import {SecurityService} from '../../../security/security.service';
import {Router} from '@angular/router';
import {TranslateService} from '@ngx-translate/core';
import {Observable} from 'rxjs';
import {OutputCurrentUser} from '@cat/api';

@Component({
  selector: 'app-top-bar',
  templateUrl: './top-bar.component.html',
  styleUrls: ['./top-bar.component.scss']
})
export class TopBarComponent {
  @Input() isLoginNeeded: boolean;
  auditUrl = '';

  constructor(private securityService: SecurityService,
              private router: Router,
              public translate: TranslateService) {
    this.prepareAuditUrl(window.location.href);
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
}
