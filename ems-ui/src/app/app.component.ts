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
import {HeadlineRoute} from '@common/components/side-nav/headline-route';

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

  private confirmNavigate(headline: HeadlineRoute) {
    Forms.confirmDialog(
      this.dialog,
      'common.sidebar.dialog.title',
      'common.sidebar.dialog.message'
    ).pipe(
      take(1),
      takeUntil(this.destroyed$),
      filter(yes => !!yes)
    ).subscribe(() => this.navigateToRoute(headline));
  }

  private navigateToRoute(headline: HeadlineRoute){
    this.router.navigate([headline.route])
      .then(() => {
        if (!headline.scrollRoute) {
          return;
        }
        setTimeout(() => this.scrollToRoute(headline.scrollRoute as any), 650);
      });
  }

  private scrollToRoute(scrollRoute: string) {
    this.viewportScroller.scrollToAnchor(scrollRoute);
  }

  navigate(headline: HeadlineRoute): void{
    if(headline.route && this.alertStatus){
      this.confirmNavigate(headline);
      return;
    }
    if(headline.route){
      this.navigateToRoute(headline);
      return;
    }
    if(headline.scrollRoute){
      this.scrollToRoute(headline.scrollRoute);
    }
  }
}
