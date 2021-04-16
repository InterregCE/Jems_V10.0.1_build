import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, Resolve, RouterStateSnapshot} from '@angular/router';
import {Observable, of} from 'rxjs';
import {UserDetailPageStore} from './user-detail-page-store.service';

@Injectable()
export class UserNameResolver implements Resolve<Observable<string>> {

  constructor(private userStore: UserDetailPageStore) {
  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<Observable<string>> {
    return of(this.userStore.getUserName());
  }
}
