import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, Resolve, RouterStateSnapshot} from '@angular/router';
import {Observable, of} from 'rxjs';
import {UserRoleStore} from './user-role-store.service';

@Injectable()
export class UserRoleNameResolver implements Resolve<Observable<string>> {

  constructor(private roleStore: UserRoleStore) {
  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<Observable<string>> {
    return of(this.roleStore.userRoleName$);
  }
}
