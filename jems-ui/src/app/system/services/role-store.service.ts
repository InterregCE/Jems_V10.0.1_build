import {Injectable} from '@angular/core';
import {map, shareReplay, startWith, switchMap, tap} from 'rxjs/operators';
import {UserRoleDTO, UserRoleService, UserRoleSummaryDTO} from '@cat/api';
import {combineLatest, Observable, of, Subject} from 'rxjs';
import {Log} from '@common/utils/log';
import {PermissionService} from '../../security/permissions/permission.service';
import PermissionsEnum = UserRoleDTO.PermissionsEnum;


@Injectable({providedIn: 'root'})
export class RoleStore {

  roles$: Observable<UserRoleSummaryDTO[]>;

  rolesChanged$ = new Subject<void>();

  constructor(private userRoleService: UserRoleService,
              private permissionService: PermissionService) {
    this.roles$ = this.roles();
  }

  private roles(): Observable<UserRoleSummaryDTO[]> {
    return combineLatest([
      this.permissionService.permissionsChanged(),
      this.rolesChanged$.pipe(startWith(null))]
    ).pipe(
      switchMap(([permissions]) =>
        permissions.includes(PermissionsEnum.RoleRetrieve) ? this.userRoleService.list() : of(null)
      ),
      map(page => page ? page.content : []),
      tap(roles => Log.info('Fetched the user roles:', this, roles)),
      shareReplay(1)
    );
  }
}
