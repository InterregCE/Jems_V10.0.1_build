import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot} from '@angular/router';
import {SecurityService} from './security.service';
import {OutputCurrentUser, UserRoleDTO} from '@cat/api';
import {Observable} from 'rxjs';
import {filter, map} from 'rxjs/operators';

@Injectable({providedIn: 'root'})
export class SystemPermissionGuard implements CanActivate {

  constructor(private router: Router,
              private securityService: SecurityService) {
  }

  private checkUser(user: OutputCurrentUser | null) {
    if (user?.role?.permissions.includes(UserRoleDTO.PermissionsEnum.AuditRetrieve)) {
      this.router.navigate(['app/system/audit']);
      return true;
    }
    if (user?.role?.permissions.includes(UserRoleDTO.PermissionsEnum.UserRetrieve)) {
      this.router.navigate(['app/system/user']);
      return true;
    }
    return false;
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> {
    return this.securityService.currentUser
      .pipe(
        filter(user => !!user),
        map(user => this.checkUser(user))
      );
  }
}
