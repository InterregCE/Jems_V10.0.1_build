import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, CanActivate, RouterStateSnapshot} from '@angular/router';
import {Subject} from 'rxjs';
import {filter, tap} from 'rxjs/operators';
import {CallStore} from './call-store.service';

@Injectable()
export class CallNameBreadcrumbProvider implements CanActivate {
  private callBreadcrumb$: Subject<string>;

  constructor(private callStore: CallStore) {
    this.callStore.getCallName()
      .pipe(
        filter(() => !!this.callBreadcrumb$),
        tap(callName => this.callBreadcrumb$.next(callName))
      ).subscribe();
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    this.callBreadcrumb$ = route.data?.breadcrumb$;
    return true;
  }
}
