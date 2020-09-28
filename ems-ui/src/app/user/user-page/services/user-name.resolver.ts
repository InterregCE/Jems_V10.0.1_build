import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, Resolve, RouterStateSnapshot} from '@angular/router';
import {Observable} from 'rxjs';
import {map, take} from 'rxjs/operators';
import {UserStore} from './user-store.service';

@Injectable()
export class UserNameResolver implements Resolve<string> {

  constructor(private userStore: UserStore) {
  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<string> {
    this.userStore.init(route.params.userId);
    return this.userStore
      .getUser()
      .pipe(
        map(user => `${user.name} ${user.surname}`),
        take(1),
      )
  }
}
