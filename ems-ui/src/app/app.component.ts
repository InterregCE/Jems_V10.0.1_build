import {Component} from '@angular/core';
import {TranslateService} from '@ngx-translate/core';
import {Title} from '@angular/platform-browser';
import {Router} from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html'
})
export class AppComponent {
  isLoginNeeded = true;

  constructor(public translate: TranslateService,
              private titleService: Title,
              private router: Router) {
    this.router.routeReuseStrategy.shouldReuseRoute = () => false;
    translate.setDefaultLang('en');
    translate.addLangs(['en', 'de']);
    translate.use('en');
    this.titleService.setTitle('Ems');
  }
}
