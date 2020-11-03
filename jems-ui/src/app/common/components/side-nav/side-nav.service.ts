import {Injectable} from '@angular/core';
import {combineLatest, Observable, ReplaySubject, Subject} from 'rxjs';
import {delay, filter, take, tap} from 'rxjs/operators';
import {Log} from '../../utils/log';
import {HeadlineRoute} from '@common/components/side-nav/headline-route';
import {MatDialog} from '@angular/material/dialog';
import {ResolveEnd, Router} from '@angular/router';
import {Forms} from '../../utils/forms';

@Injectable({providedIn: 'root'})
export class SideNavService {
  private headlines$ = new ReplaySubject<HeadlineRoute[]>(1);
  private navigateTo$ = new Subject<HeadlineRoute>();
  private headlineRoot: string;
  private alertStatus: boolean;

  private routeChanged$ = this.router.events
    .pipe(
      filter(val => val instanceof ResolveEnd)
    );

  constructor(private router: Router,
              private dialog: MatDialog) {
    combineLatest([
      this.routeChanged$,
      this.navigateTo$,
    ])
      .pipe(
        filter(([val, to]) => to.route === (val as ResolveEnd).url),
        delay(500), // wait for dom to render
        tap(([val, to]) => this.scrollToRoute(to.scrollRoute as any))
      ).subscribe();

    this.routeChanged$
      .pipe(
        tap(route => this.resetOnLeave(route as ResolveEnd, this.headlineRoot))
      )
      .subscribe();
  }

  /**
   * Sets the sidebar links
   *
   * @param headlineRoot the common url headline path for which the sidebar will not destroy itself
   * @param newHeadlines the headlines (sidebar links)
   */
  setHeadlines(headlineRoot: string, newHeadlines: HeadlineRoute[]): void {
    this.headlineRoot = headlineRoot;
    this.headlines$.next(newHeadlines);
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
    } else if (headline.scrollToTop) {
      document.getElementById('main-content')?.scrollIntoView({behavior: 'smooth'});
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
    document.getElementById(scrollRoute)?.scrollIntoView({behavior: 'smooth'});
  }

  private getAllRoutes(headline: HeadlineRoute, allRoutes: string[]) {
    if (headline.route) {
      allRoutes.push(headline.route);
    }
    headline?.bullets?.forEach(child => this.getAllRoutes(child, allRoutes));
  }

  private resetOnLeave(val: ResolveEnd, headlinesRoot: string): void {
    if (headlinesRoot && val.url && val.url.startsWith(headlinesRoot)) {
      return;
    }
    this.headlines$.next([]);
    this.alertStatus = false;
  }
}
