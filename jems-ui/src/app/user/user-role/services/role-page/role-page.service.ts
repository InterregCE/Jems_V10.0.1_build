import {Injectable} from '@angular/core';
import {mergeMap, map, shareReplay, tap} from 'rxjs/operators';
import {OutputUserRole, UserRoleService} from '@cat/api';
import {Observable, of} from 'rxjs';
import {Log} from '../../../../common/utils/log';
import {PermissionService} from '../../../../security/permissions/permission.service';
import {Permission} from '../../../../security/permissions/permission';

@Injectable({providedIn: 'root'})
export class RolePageService {

  private userRoles$ = this.permissionService.permissionsChanged()
    .pipe(
      mergeMap(permissions => {
        if (permissions.includes(Permission.ADMINISTRATOR)) { return this.userRoleService.list(); }
        return of(null);
      }),
      map(page => page ? page.content : []),
      tap(roles => Log.info('Fetched the user roles:', this, roles)),
      shareReplay(1)
    );

  constructor(private userRoleService: UserRoleService,
              private permissionService: PermissionService) {
  }

  /**
   * Returns a shared list of user roles observable. The roles are only fetched if the
   * current user has The ADMINISTRATOR privilege. Otherwise an empty list is returned.
   * The last fetched list is emitted to all/late subscribers - shareReplay(1).
   * The list is refreshed when:
   * - the permissions change
   */
  userRoles(): Observable<OutputUserRole[]> {
    return this.userRoles$;
  }
}
