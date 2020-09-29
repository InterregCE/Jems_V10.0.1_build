import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, Resolve, RouterStateSnapshot} from '@angular/router';
import {Observable, of} from 'rxjs';
import {UserStore} from './user-store.service';

@Injectable()
export class UserNameResolver implements Resolve<Observable<string>> {

  constructor(private userStore: UserStore) {
  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<Observable<string>> {
    return of(this.userStore.getUserName());
  }
}
