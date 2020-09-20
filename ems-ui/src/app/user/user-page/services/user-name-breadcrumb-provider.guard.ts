import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, CanActivate, RouterStateSnapshot} from '@angular/router';
import {Subject} from 'rxjs';
import {UserStore} from './user-store.service';
import {filter, tap} from 'rxjs/operators';

@Injectable()
export class UserNameBreadcrumbProvider implements CanActivate {
  private userBreadcrumb$: Subject<string>;

  constructor(private userStore: UserStore) {
    this.userStore.getUserName()
      .pipe(
        filter(() => !!this.userBreadcrumb$),
        tap(userName => this.userBreadcrumb$.next(userName))
      ).subscribe();
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    this.userBreadcrumb$ = route.data?.breadcrumb$;
    return true;
  }
}
