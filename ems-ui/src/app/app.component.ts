import {Component} from '@angular/core';
import {TranslateService} from '@ngx-translate/core';
import {Title} from '@angular/platform-browser';
import {Router} from '@angular/router';
import {Location} from '@angular/common';
import {SideNavService} from '@common/components/side-nav/side-nav.service';
import { ViewportScroller } from '@angular/common';
import {HeadlineType} from '@common/components/side-nav/headline-type';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html'
})
export class AppComponent {
  isLoginNeeded = true;

  headlines$ = this.sideNavService.getHeadlines();
  headlineType = HeadlineType;

  constructor(public translate: TranslateService,
              private titleService: Title,
              private router: Router,
              private location: Location,
              private sideNavService: SideNavService,
              private viewportScroller: ViewportScroller) {
    this.router.routeReuseStrategy.shouldReuseRoute = () => false;
    translate.setDefaultLang('en');
    translate.addLangs(['en', 'de', 'ja_JP']);
    translate.use('en');
    this.titleService.setTitle('Ems');
  }

  navigateToRoute(route: string) {
    this.viewportScroller.scrollToAnchor(route);
  }
}
