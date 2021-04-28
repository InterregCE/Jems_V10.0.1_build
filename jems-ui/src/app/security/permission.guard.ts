import {Injectable} from '@angular/core';
import {OutputCurrentUser, UserRoleDTO} from '@cat/api';
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot} from '@angular/router';
import {Observable} from 'rxjs';
import {filter, map} from 'rxjs/operators';
import {SecurityService} from './security.service';
import {Log} from '../common/utils/log';

@Injectable({providedIn: 'root'})
export class PermissionGuard implements CanActivate {

  constructor(private router: Router,
              private securityService: SecurityService) {
  }

  private checkUser(user: OutputCurrentUser | null, childRoute: ActivatedRouteSnapshot): boolean {
    let allowed = true;
    const permissionsOnly = childRoute?.data?.permissionsOnly;
    if (permissionsOnly) {
      // TODO after switching all use cases to Permissions, remove 'role name' check
      allowed = permissionsOnly.some((only: string) => user?.role?.name === only)
        || permissionsOnly.some((only: string) => user?.role?.permissions.includes(only as UserRoleDTO.PermissionsEnum));
    }
    if (!allowed) {
      Log.info('Current user role cannot access this route:', this, user?.role);
      this.router.navigate(['app']);
    }
    return allowed;
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> {
    return this.securityService.currentUser
      .pipe(
        filter(user => !!user),
        map(user => this.checkUser(user, route))
      );
  }
}
