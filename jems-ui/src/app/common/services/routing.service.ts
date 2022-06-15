import {Injectable} from '@angular/core';
import {distinctUntilChanged, filter, map, take, tap} from 'rxjs/operators';
import {ActivatedRoute, NavigationEnd, NavigationExtras, ResolveEnd, Router} from '@angular/router';
import {Forms} from '../utils/forms';
import {MatDialog} from '@angular/material/dialog';
import {Log} from '../utils/log';
import {Observable, ReplaySubject} from 'rxjs';

@Injectable({providedIn: 'root'})
export class RoutingService {

  currentRoute = new ReplaySubject<ActivatedRoute>(1);
  confirmLeaveMap = new Map<string, boolean>();

  constructor(private router: Router,
              private dialog: MatDialog,
              private route: ActivatedRoute) {
    this.router.events
      .pipe(
        filter(val => val instanceof ResolveEnd),
        tap(() => this.confirmLeaveMap.clear()),
      ).subscribe();

    this.router.events
      .pipe(
        filter(val => val instanceof NavigationEnd),
        tap(() => this.currentRoute.next(this.getLeafRoute(this.route))),
      )
      .subscribe();
  }

  get url(): string {
    return this.router.url;
  }

  navigate(commands: any[], extras?: NavigationExtras): void {
    const navigationExtras = extras || {queryParamsHandling: 'merge'} as NavigationExtras;
    if (![...this.confirmLeaveMap.values()].some(val => val)) {
      this.router.navigate(commands, navigationExtras);
      return;
    }

    Forms.confirm(
      this.dialog,
      {
        title: 'common.sidebar.dialog.title',
        warnMessage: 'common.sidebar.dialog.message'
      }
    ).pipe(
      take(1),
      filter(yes => !!yes),
      tap(() => this.router.navigate(commands, navigationExtras)),
      tap(() => Log.debug('Navigating to route', this, commands, navigationExtras))
    ).subscribe();
  }

  routeChanges(url: string): Observable<boolean> {
    return this.currentRoute
      .pipe(
        map(route => this.containsPath(route, url)),
        distinctUntilChanged(),
        tap(param => Log.debug('Route changed', this, url)),
      );
  }

  routeParameterChanges(url: string, parameter: string): Observable<string | number | null> {
    return this.currentRoute
      .pipe(
        map(route => this.containsPath(route, url) ? this.getParameter(route, parameter) : null),
        distinctUntilChanged((o, n) => o === n),
        tap(param => Log.debug('Route param changed', this, url, param)),
      );
  }

  getParameter(route: ActivatedRoute, param: string): string | number | null {
    return route?.snapshot.params[param] || route?.snapshot.queryParams[param]
      || route?.parent?.snapshot.params[param] || route?.parent?.snapshot.queryParams[param];
  }

  private containsPath(route: ActivatedRoute, path: string): boolean {
    const url = (route?.snapshot as any)?._routerState?.url as string;
    return !!url?.includes(path);
  }

  private getLeafRoute(route: ActivatedRoute): ActivatedRoute {
    let localRoot = route;
    while (localRoot.firstChild) {
      localRoot = localRoot.firstChild;
    }
    return localRoot;
  }
}
