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
  private newAuditUrl$ = new ReplaySubject<string>(1);

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
  private usersItem: MenuItemConfiguration = {
    name: 'topbar.main.user.management',
    isInternal: true,
    route: '/app/user',
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
  private auditItem: MenuItemConfiguration;
  private editUserItem: MenuItemConfiguration;

  constructor(private permissionService: PermissionService,
              private securityService: SecurityService) {
    combineLatest([
      this.newAuditUrl$,
      this.securityService.currentUser
    ])
      .subscribe(([auditUrl, currentUser]) => {
        this.adaptMenuItems(auditUrl, currentUser);
        this.assingMenuItemsToUser();
      });
  }

  menuItems(): Observable<MenuItemConfiguration[]> {
    return this.menuItems$.asObservable();
  }

  newAuditUrl(auditUrl: string): void {
    this.newAuditUrl$.next(auditUrl);
  }

  logout(): Observable<any> {
    return this.securityService.logout();
  }

  private adaptMenuItems(auditUrl: string, currentUser: OutputCurrentUser | null): void {
    this.auditItem = {
      name: 'topbar.main.audit',
      isInternal: false,
      route: auditUrl,
    };
    if (!currentUser) {
      return;
    }
    this.editUserItem = {
      name: `${currentUser?.name} (${currentUser?.role})`,
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
        this.auditItem,
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
        this.usersItem,
        this.auditItem,
        this.editUserItem,
      ]));
  }
}
