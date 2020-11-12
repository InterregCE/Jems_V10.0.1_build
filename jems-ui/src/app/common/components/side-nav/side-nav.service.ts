import {Injectable} from '@angular/core';
import {combineLatest, Observable, ReplaySubject, Subject} from 'rxjs';
import {delay, filter, take, tap} from 'rxjs/operators';
import {Log} from '../../utils/log';
import {HeadlineRoute} from '@common/components/side-nav/headline-route';
import {ResolveEnd, Router} from '@angular/router';
import {RoutingService} from '../../services/routing.service';

@Injectable({providedIn: 'root'})
export class SideNavService {
  private headlines$ = new ReplaySubject<HeadlineRoute[]>(1);
  private navigateTo$ = new Subject<HeadlineRoute>();
  private headlineRoot: string;

  private routeChanged$ = this.router.events
    .pipe(
      filter(val => val instanceof ResolveEnd)
    );

  constructor(private router: Router,
              private routingService: RoutingService) {
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
    // TODO: remove all usages
  }

  navigate(headline: HeadlineRoute): void {
    if (this.router.url !== headline.route) {
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

  private navigateToRoute(headline: HeadlineRoute): void {
    this.routingService.navigate([headline.route]);
    this.navigateTo$.next(headline);
  }

  private scrollToRoute(scrollRoute: string): void {
    Log.debug('Scrolling to anchor', this, scrollRoute);
    document.getElementById(scrollRoute)?.scrollIntoView({behavior: 'smooth'});
  }

  private getAllRoutes(headline: HeadlineRoute, allRoutes: string[]): void {
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
  }
}
