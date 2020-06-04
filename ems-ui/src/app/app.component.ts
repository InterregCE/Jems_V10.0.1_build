import {Component} from '@angular/core';
import {OutputUser} from '@cat/api';
import {SecurityService} from './security/security.service';
import {Router} from '@angular/router';
import {TranslateService} from '@ngx-translate/core';
import {Observable} from 'rxjs';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html'
})
export class AppComponent {
  title = 'frontend';
  isLoginNeeded = false;
  auditUrl = '';

  constructor(private securityService: SecurityService,
              private router: Router,
              translate: TranslateService) {
    // this language will be used as a fallback when a translation isn't found in the current language
    translate.setDefaultLang('de');
    // the lang to use, if the lang isn't available, it will use the current loader to get them
    translate.use('de');
    this.prepareAuditUrl(window.location.href);
  }

  get currentUser(): Observable<OutputUser | null> {
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

  logout() {
    this.securityService.logout();
    this.router.navigate(['/login']);
  }
}
