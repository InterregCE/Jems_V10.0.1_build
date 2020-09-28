import {Injectable} from '@angular/core';
import {combineLatest, Observable, ReplaySubject} from 'rxjs';
import {MenuItemConfiguration} from '../menu/model/menu-item.configuration';
import {PermissionService} from '../../../security/permissions/permission.service';
import {Permission} from '../../../security/permissions/permission';
import {Router} from '@angular/router';
import {filter, take} from 'rxjs/operators';
import {SecurityService} from '../../../security/security.service';
import {OutputCurrentUser} from '@cat/api';

@Injectable()
export class TopBarService {

  private menuItems$ = new ReplaySubject<MenuItemConfiguration[]>(1);
  private newAuditUrl$ = new ReplaySubject<string>(1);

  private applicationsItem = {
    name: 'topbar.main.project',
    isInternal: true,
    route: '/app/project',
    action: (internal: boolean, route: string) => this.handleNavigation(internal, route),
  };
  private usersItem = {
    name: 'topbar.main.user.management',
    isInternal: true,
    route: '/app/user',
    action: (internal: boolean, route: string) => this.handleNavigation(internal, route),
  };
  private programmItem = {
    name: 'topbar.main.programme',
    isInternal: true,
    route: '/app/programme',
    action: (internal: boolean, route: string) => this.handleNavigation(internal, route),
  };
  private callsItem = {
    name: 'topbar.main.call',
    isInternal: true,
    route: '/app/call',
    action: (internal: boolean, route: string) => this.handleNavigation(internal, route),
  }
  private auditItem: MenuItemConfiguration;
  private editUserItem: MenuItemConfiguration;

  constructor(private permissionService: PermissionService,
              private securityService: SecurityService,
              private router: Router) {
    combineLatest([
      this.permissionService.permissionsChanged(),
      this.newAuditUrl$,
      this.securityService.currentUser
    ])
      .subscribe(([perm, auditUrl, currentUser]) => {
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
      action: (internal: boolean, route: string) => this.handleNavigation(internal, route),
    };
    if (!currentUser) {
      return;
    }
    this.editUserItem = {
      name: `${currentUser?.name} (${currentUser?.role})`,
      isInternal: true,
      route: `/app/profile`,
      action: (internal: boolean, route: string) => this.handleNavigation(internal, route)
    };
  }

  assingMenuItemsToUser(): void {
    this.permissionService.hasPermission(Permission.APPLICANT_USER)
      .pipe(
        take(1),
        filter(canSee => canSee),
      )
      .subscribe(() => this.menuItems$.next([
        this.applicationsItem,
        this.callsItem,
        this.editUserItem
      ]));

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

  private handleNavigation(internalRoute: boolean, route: string): void {
    if (internalRoute) {
      this.router.navigate([route]);
    } else {
      window.open(route, '_blank');
    }
  }
}
