import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, Resolve, RouterStateSnapshot} from '@angular/router';
import {Observable, of} from 'rxjs';
import {UserRoleDetailPageStore} from './user-role-detail-page-store.service';

@Injectable()
export class UserRoleNameResolver implements Resolve<Observable<string>> {

  constructor(private roleStore: UserRoleDetailPageStore) {
  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<Observable<string>> {
    return of(this.roleStore.getUserRoleName());
  }
}
