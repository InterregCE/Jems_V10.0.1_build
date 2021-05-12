import {Injectable} from '@angular/core';
import {combineLatest, Observable, ReplaySubject} from 'rxjs';
import {MenuItemConfiguration} from '../menu/model/menu-item.configuration';
import {PermissionService} from '../../../security/permissions/permission.service';
import {Permission} from '../../../security/permissions/permission';
import {map, take} from 'rxjs/operators';
import {SecurityService} from '../../../security/security.service';
import {OutputCurrentUser, UserRoleDTO} from '@cat/api';
import PermissionsEnum = UserRoleDTO.PermissionsEnum;

@Injectable()
export class TopBarService {

  private menuItems$ = new ReplaySubject<MenuItemConfiguration[]>(1);

  private dashboardItem: MenuItemConfiguration = {
    name: 'topbar.main.dashboard',
    isInternal: true,
    route: '/app/dashboard',
  };
  private applicationsItem: MenuItemConfiguration = {
    name: 'topbar.main.project',
    isInternal: true,
    route: '/app/project',
  };
  private programmItem: MenuItemConfiguration = {
    name: 'topbar.main.programme',
    isInternal: true,
    route: '/app/programme',
  };
  private callsItem: MenuItemConfiguration = {
    name: 'topbar.main.call',
    isInternal: true,
    route: '/app/call',
  };
  private systemItem: MenuItemConfiguration = {
    name: 'topbar.main.system',
    isInternal: true,
    route: '/app/system',
  };
  private editUserItem: MenuItemConfiguration;

  constructor(private permissionService: PermissionService,
              private securityService: SecurityService) {
    this.securityService.currentUser.subscribe((currentUser) => {
      this.adaptMenuItems(currentUser);
      this.assingMenuItemsToUser();
    });
  }

  menuItems(): Observable<MenuItemConfiguration[]> {
    return this.menuItems$.asObservable();
  }

  logout(): Observable<any> {
    return this.securityService.logout();
  }

  private adaptMenuItems(currentUser: OutputCurrentUser | null): void {
    if (!currentUser) {
      return;
    }
    this.editUserItem = {
      name: `${currentUser?.name} (${currentUser?.role.name})`,
      isInternal: true,
      route: `/app/profile`,
    };
  }

  assingMenuItemsToUser(): void {
    combineLatest([
      this.permissionService.hasPermission(Permission.SYSTEM_MODULE_PERMISSIONS),
      this.permissionService.hasPermission(PermissionsEnum.ProjectRetrieve),
      this.securityService.currentUser.pipe(map(currentUser => currentUser?.role)),
      // TODO remove when all permissions implemented
      this.permissionService.hasPermission(Permission.APPLICANT_USER),
      this.permissionService.hasPermission(Permission.PROGRAMME_USER),
      this.permissionService.hasPermission(Permission.ADMINISTRATOR),
    ]).pipe(
      take(1),
    ).subscribe(([systemEnabled, applicationsEnabled, role, isApplicant, isProgrammeUser, isAdmin]) => {
      const menuItems: MenuItemConfiguration[] = [];

      if (isApplicant) {
        menuItems.push(this.dashboardItem);
      }
      if (applicationsEnabled) {
        menuItems.push(this.applicationsItem);
      }
      if (isProgrammeUser || isAdmin) {
        menuItems.push(this.callsItem);
      }
      if (isProgrammeUser || isAdmin) {
        menuItems.push(this.programmItem);
      }
      if (systemEnabled) {
        if (role?.permissions.includes(PermissionsEnum.AuditRetrieve)) {
          this.systemItem.route = '/app/system/audit';
        } else if (role?.permissions.includes(PermissionsEnum.UserRetrieve)) {
          this.systemItem.route = '/app/system/user';
        } else if (role?.permissions.includes(PermissionsEnum.RoleRetrieve)) {
          this.systemItem.route = '/app/system/userRole';
        }
        menuItems.push(this.systemItem);
      }

      menuItems.push(this.editUserItem);

      this.menuItems$.next(menuItems);
    });
  }
}
