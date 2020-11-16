import {Injectable} from '@angular/core';
import {filter, take, tap} from 'rxjs/operators';
import {NavigationExtras, ResolveEnd, Router} from '@angular/router';
import {Forms} from '../utils/forms';
import {MatDialog} from '@angular/material/dialog';
import {Log} from '../utils/log';

@Injectable({providedIn: 'root'})
export class RoutingService {

  confirmLeave: boolean;

  constructor(private router: Router,
              private dialog: MatDialog) {
    this.router.events
      .pipe(
        filter(val => val instanceof ResolveEnd),
        tap(() => this.confirmLeave = false)
      ).subscribe();
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
}
