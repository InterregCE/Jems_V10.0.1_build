import {Component} from '@angular/core';
import {TranslateService} from '@ngx-translate/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html'
})
export class AppComponent {
  isLoginNeeded = true;

  constructor(public translate: TranslateService) {
    translate.setDefaultLang('en');
    translate.addLangs(['en', 'de']);
    translate.use('en');
  }
}
