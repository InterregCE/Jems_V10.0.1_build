import {Injectable} from '@angular/core';
import {NgxPermissionsService} from 'ngx-permissions';
import {from, Observable, ReplaySubject} from 'rxjs';
import {SecurityService} from '../security.service';
import {OutputCurrentUser, UserRoleDTO} from '@cat/api';
import {tap} from 'rxjs/operators';
import {Log} from '../../common/utils/log';

@Injectable({providedIn: 'root'})
export class PermissionService {

  private permissionsChanged$: ReplaySubject<string[]> = new ReplaySubject(1);

  constructor(private ngxPermissionsService: NgxPermissionsService,
              private securityService: SecurityService) {
    this.securityService.currentUser
      .pipe(
        tap((user: OutputCurrentUser) => this.setPermissions(user ? [user.role] : []))
      )
      .subscribe();
  }

  setPermissions(roles: UserRoleDTO[]): void {
    Log.info('Setting new user permissions', this, roles);

    // TODO change this after all permissions are introduced, so we do not need role name as extra permission
    // const permissions = [...new Set(roles.map(role => role.permissions).flat(1))]
    const permissions = [...new Set(roles.map(role => [role.name, ...role.permissions]).flat(1))];

    this.ngxPermissionsService.flushPermissions();
    this.ngxPermissionsService.loadPermissions(permissions);
    this.permissionsChanged$.next(permissions);
  }

  hasPermission(permission: string): Observable<boolean> {
    return from(this.ngxPermissionsService.hasPermission(permission));
  }

  permissionsChanged(): Observable<string[]> {
    return this.permissionsChanged$.asObservable();
  }
}
