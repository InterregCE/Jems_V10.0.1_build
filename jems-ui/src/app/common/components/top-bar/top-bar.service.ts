import {Injectable} from '@angular/core';
import {combineLatest, Observable, ReplaySubject} from 'rxjs';
import {MenuItemConfiguration} from '../menu/model/menu-item.configuration';
import {PermissionService} from '../../../security/permissions/permission.service';
import {Permission} from '../../../security/permissions/permission';
import {filter, take} from 'rxjs/operators';
import {SecurityService} from '../../../security/security.service';
import {OutputCurrentUser} from '@cat/api';

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
  private auditItem: MenuItemConfiguration;
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
    this.permissionService.hasPermission(Permission.APPLICANT_USER)
      .pipe(
        take(1),
        filter(canSee => canSee),
      )
      .subscribe(() => this.menuItems$.next([this.dashboardItem, this.editUserItem]));

    this.permissionService.hasPermission(Permission.PROGRAMME_USER)
      .pipe(
        take(1),
        filter(canSee => canSee),
      )
      .subscribe(() => this.menuItems$.next([
        this.applicationsItem,
        this.callsItem,
        this.programmItem,
        this.systemItem,
        this.editUserItem,
      ]));

    this.permissionService.hasPermission(Permission.ADMINISTRATOR)
      .pipe(
        take(1),
        filter(canSee => canSee),
      )
      .subscribe(() => this.menuItems$.next([
        this.applicationsItem,
        this.callsItem,
        this.programmItem,
        this.systemItem,
        this.editUserItem,
      ]));
  }
}
