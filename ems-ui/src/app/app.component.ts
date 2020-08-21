import {Component} from '@angular/core';
import {TranslateService} from '@ngx-translate/core';
import {Title} from '@angular/platform-browser';
import {Router} from '@angular/router';
import {Location} from '@angular/common';
import {SideNavService} from '@common/components/side-nav/side-nav.service';
import {ViewportScroller} from '@angular/common';
import {HeadlineType} from '@common/components/side-nav/headline-type';
import {Forms} from './common/utils/forms';
import {filter, take, takeUntil, tap} from 'rxjs/operators';
import {MatDialog} from '@angular/material/dialog';
import {BaseComponent} from '@common/components/base-component';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html'
})
export class AppComponent extends BaseComponent {
  isLoginNeeded = true;

  headlines$ = this.sideNavService.getHeadlines();
  alertStatus: boolean;
  headlineType = HeadlineType;

  constructor(public translate: TranslateService,
              private titleService: Title,
              private router: Router,
              private location: Location,
              private dialog: MatDialog,
              private sideNavService: SideNavService,
              private viewportScroller: ViewportScroller) {
    super();

    this.router.routeReuseStrategy.shouldReuseRoute = () => false;
    this.titleService.setTitle('Ems');

    this.sideNavService.getAlertStatus()
      .pipe(
        tap(status => this.alertStatus = status)
      ).subscribe();
  }

  navigateToBackRoute(route: string) {
    Forms.confirmDialog(
      this.dialog,
      'common.sidebar.dialog.title',
      'common.sidebar.dialog.message'
    ).pipe(
      take(1),
      takeUntil(this.destroyed$),
      filter(yes => !!yes)
    ).subscribe(() => {
      this.router.navigate([route]);
    });
  }

  navigateToRoute(route: string) {
    this.viewportScroller.scrollToAnchor(route);
  }
}
