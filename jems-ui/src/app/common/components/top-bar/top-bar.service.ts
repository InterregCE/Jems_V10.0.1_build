import {Injectable} from '@angular/core';
import {combineLatest, Observable} from 'rxjs';
import {MenuItemConfiguration} from './menu-item.configuration';
import {PermissionService} from '../../../security/permissions/permission.service';
import {Permission} from '../../../security/permissions/permission';
import {map} from 'rxjs/operators';
import {SecurityService} from '../../../security/security.service';
import {UserRoleDTO} from '@cat/api';
import PermissionsEnum = UserRoleDTO.PermissionsEnum;

@Injectable()
export class TopBarService {

  menuItems$: Observable<MenuItemConfiguration[]>;
  editUserItem$: Observable<MenuItemConfiguration | null>;

  private dashboardItem: MenuItemConfiguration = {
    name: 'topbar.main.dashboard',
    route: '/app/dashboard',
    icon: 'dashboard'
  };
  private applicationsItem: MenuItemConfiguration = {
    name: 'topbar.main.project',
    route: '/app/project',
    icon: 'description'
  };
  private programmeItem: MenuItemConfiguration = {
    name: 'topbar.main.programme',
    route: '/app/programme',
    icon: 'business'
  };
  private callsItem: MenuItemConfiguration = {
    name: 'topbar.main.call',
    route: '/app/call',
    icon: 'campaign'
  };
  private systemItem: MenuItemConfiguration = {
    name: 'topbar.main.system',
    route: '/app/system',
    icon: 'settings'
  };
  private controllersItem: MenuItemConfiguration = {
    name: 'topbar.main.controllers',
    route: '/app/controller',
    icon: 'rule_folder'
  };

  private controllerAssignmentItem: MenuItemConfiguration = {
    name: 'topbar.main.controllers',
    route: '/app/controller/assignment',
    icon: 'rule_folder'
  };

  constructor(private permissionService: PermissionService,
              private securityService: SecurityService) {
    this.menuItems$ = this.menuItems();
    this.editUserItem$ = this.editUserItem();
  }

  logout(): Observable<any> {
    return this.securityService.logout();
  }

  private editUserItem(): Observable<MenuItemConfiguration | null> {
    return this.securityService.currentUser
      .pipe(
        map(currentUser => currentUser
          ? {
            name: `${currentUser?.name} (${currentUser?.role.name})`,
            isInternal: true,
            route: `/app/profile`,
          }
          : null
        )
      );
  }

  private menuItems(): Observable<MenuItemConfiguration[]> {
    return combineLatest([
      this.permissionService.permissionsChanged(),
      this.editUserItem()
    ])
      .pipe(
        map(([permissions, editUserItem]) => {
          const menuItems: MenuItemConfiguration[] = [this.dashboardItem];

          if (permissions.includes(PermissionsEnum.ProjectRetrieve)) {
            menuItems.push(this.applicationsItem);
          }
          if (permissions.includes(PermissionsEnum.CallRetrieve)) {
            menuItems.push(this.callsItem);
          }
          if (Permission.PROGRAMME_SETUP_MODULE_PERMISSIONS.some(perm => permissions.includes(perm))) {
            menuItems.push(this.programmeItem);
          }

          if(Permission.CONTROLLERS_PERMISSIONS.some(perm => permissions.includes(perm))) {
            menuItems.push(this.controllersItem);
          } else
          if (Permission.CONTROLLERS_ASSIGNMENT_PERMISSIONS.some(perm => permissions.includes(perm))) {
            menuItems.push(this.controllerAssignmentItem);
          }

          if (Permission.SYSTEM_MODULE_PERMISSIONS.some(perm => permissions.includes(perm))) {
            if (permissions.includes(PermissionsEnum.AuditRetrieve)) {
              this.systemItem.route = '/app/system/audit';
            } else if (permissions.includes(PermissionsEnum.UserRetrieve)) {
              this.systemItem.route = '/app/system/user';
            } else if (permissions.includes(PermissionsEnum.RoleRetrieve)) {
              this.systemItem.route = '/app/system/role';
            }
            menuItems.push(this.systemItem);
          }

          return menuItems;
        })
      );
  }
}
