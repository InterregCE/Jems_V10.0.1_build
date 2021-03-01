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
  confirmLeave: boolean;

  constructor(private router: Router,
              private dialog: MatDialog,
              private route: ActivatedRoute) {
    this.router.events
      .pipe(
        filter(val => val instanceof ResolveEnd),
        tap(() => this.confirmLeave = false)
      ).subscribe();

    this.router.events
      .pipe(
        filter(val => val instanceof NavigationEnd),
        tap(() => this.currentRoute.next(this.getLeafRoute(this.route))),
      )
      .subscribe();
  }

  navigate(commands: any[], extras?: NavigationExtras): void {
    if (!this.confirmLeave) {
      this.router.navigate(commands, extras);
      return;
    }

    Forms.confirmDialog(
      this.dialog,
      'common.sidebar.dialog.title',
      'common.sidebar.dialog.message'
    ).pipe(
      take(1),
      filter(yes => !!yes),
      tap(() => this.router.navigate(commands, extras)),
      tap(() => Log.debug('Navigating to route', this, commands, extras))
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

  routeParameterChanges(url: string, parameter: string): Observable<string | number> {
    return this.currentRoute
      .pipe(
        map(route => this.containsPath(route, url) ? route.snapshot.params[parameter] : null),
        distinctUntilChanged(),
        tap(param => Log.debug('Route param changed', this, url, param)),
      );
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
