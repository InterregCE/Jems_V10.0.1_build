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

  constructor(private securityService: SecurityService,
              private router: Router,
              translate: TranslateService) {
    // this language will be used as a fallback when a translation isn't found in the current language
    translate.setDefaultLang('de');
    // the lang to use, if the lang isn't available, it will use the current loader to get them
    translate.use('de');
  }

  get currentUser(): Observable<OutputUser | null> {
    return this.securityService.currentUser;
  }

  logout() {
    this.securityService.logout();
    this.router.navigate(['/login']);
  }
}
