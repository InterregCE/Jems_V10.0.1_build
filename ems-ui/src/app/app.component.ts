import {Component} from '@angular/core';
import {TranslateService} from '@ngx-translate/core';
import {Title} from '@angular/platform-browser';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html'
})
export class AppComponent {
  isLoginNeeded = true;

  constructor(public translate: TranslateService,
              private titleService: Title) {
    translate.setDefaultLang('en');
    translate.addLangs(['en', 'de']);
    translate.use('en');
    this.titleService.setTitle('Ems');
  }
}
