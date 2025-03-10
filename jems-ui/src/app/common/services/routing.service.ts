import {inject, Injectable} from '@angular/core';
import {distinctUntilChanged, filter, finalize, map, take, tap} from 'rxjs/operators';
import {ActivatedRoute, NavigationEnd, NavigationExtras, NavigationStart, ResolveEnd, Router} from '@angular/router';
import {Forms} from '../utils/forms';
import {MatDialog, MatDialogRef} from '@angular/material/dialog';
import {Log} from '../utils/log';
import {Observable, of, ReplaySubject, Subject} from 'rxjs';
import {ConfirmDialogComponent} from '@common/components/modals/confirm-dialog/confirm-dialog.component';
import {ConfirmDialogData} from '@common/components/modals/confirm-dialog/confirm-dialog.data';

@Injectable({providedIn: 'root'})
export class RoutingService {

  currentRoute = new ReplaySubject<ActivatedRoute>(1);
  confirmLeaveSet = new Set<string>();

  constructor(private router: Router,
              private route: ActivatedRoute) {
    this.router.events
      .pipe(
        filter(val => val instanceof ResolveEnd),
        tap(() => this.confirmLeaveSet.clear()),
      )
      .subscribe();

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
    this.router.navigate(commands, navigationExtras);
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
