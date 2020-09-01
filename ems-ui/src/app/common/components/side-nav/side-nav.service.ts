import {Injectable} from '@angular/core';
import {combineLatest, Observable, ReplaySubject, Subject} from 'rxjs';
import {delay, filter, take, takeUntil, tap} from 'rxjs/operators';
import {Log} from '../../utils/log';
import {HeadlineRoute} from '@common/components/side-nav/headline-route';
import {MatDialog} from '@angular/material/dialog';
import {ViewportScroller} from '@angular/common';
import {ResolveEnd, Router} from '@angular/router';
import {Forms} from '../../utils/forms';

@Injectable()
export class SideNavService {
  private headlines$ = new ReplaySubject<HeadlineRoute[]>();
  private navigateTo$ = new Subject<HeadlineRoute>();
  private alertStatus: boolean;

  constructor(private router: Router,
              private dialog: MatDialog,
              private viewportScroller: ViewportScroller) {
  }

  setHeadlines(destroyed$: Subject<any>, newHeadlines: HeadlineRoute[]): void {
    setTimeout(() => this.headlines$.next(newHeadlines), 50);
    Log.debug('Setting headlines', this, this.headlines$);

    if (!destroyed$)
      return;

    destroyed$
      .pipe(
        take(1),
        tap(() => Log.debug('Setting headlines', this, [])),
        tap(() => this.headlines$.next([])),
        tap(() => this.alertStatus = false)
      ).subscribe();

    combineLatest([
      this.router.events,
      this.navigateTo$,
    ])
      .pipe(
        takeUntil(destroyed$),
        filter(([val, to]) => val instanceof ResolveEnd && to.route === val.url),
        delay(300), // wait for dom to render
        tap(([val, to]) => this.scrollToRoute(to.scrollRoute as any))
      ).subscribe();
  }

  getHeadlines(): Observable<HeadlineRoute[]> {
    return this.headlines$.asObservable();
  }

  setAlertStatus(newAlertStatus: boolean): void {
    this.alertStatus = newAlertStatus;
    Log.debug('Setting alert status', this, this.alertStatus);
  }

  navigate(headline: HeadlineRoute): void {
    if (this.router.url !== headline.route) {
      if (headline.route && this.alertStatus) {
        this.confirmNavigate(headline);
        return;
      }
      if (headline.route) {
        this.navigateToRoute(headline);
        return;
      }
    }
    if (headline.scrollRoute) {
      this.scrollToRoute(headline.scrollRoute);
    }
  }

  private confirmNavigate(headline: HeadlineRoute) {
    Forms.confirmDialog(
      this.dialog,
      'common.sidebar.dialog.title',
      'common.sidebar.dialog.message'
    ).pipe(
      take(1),
      filter(yes => !!yes),
      tap(() => this.navigateToRoute(headline))
    ).subscribe();
  }

  private navigateToRoute(headline: HeadlineRoute) {
    this.navigateTo$.next(headline);
    Log.debug('Navigating to route', this, headline.route);
    this.router.navigate([headline.route]);
  }

  private scrollToRoute(scrollRoute: string) {
    Log.debug('Scrolling to anchor', this, scrollRoute);
    this.viewportScroller.scrollToAnchor(scrollRoute);
  }
}
