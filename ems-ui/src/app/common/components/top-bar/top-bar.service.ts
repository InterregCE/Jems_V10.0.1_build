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
    name: 'Project Applications',
    isInternal: true,
    route: '/',
    action: (internal: boolean, route: string) => this.handleNavigation(internal, route),
  };
  private usersItem = {
    name: 'User Management',
    isInternal: true,
    route: '/user',
    action: (internal: boolean, route: string) => this.handleNavigation(internal, route),
  };
  private programmItem = {
    name: 'Programme Setup',
    isInternal: true,
    route: '/programme',
    action: (internal: boolean, route: string) => this.handleNavigation(internal, route),
  };
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

  logout(): void {
    this.securityService.logout();
    this.router.navigate(['/login']);
  }

  private adaptMenuItems(auditUrl: string, currentUser: OutputCurrentUser | null): void {
    this.auditItem = {
      name: 'Audit Log',
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
      route: `/user/${currentUser?.id}`,
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
        this.editUserItem
      ]));

    this.permissionService.hasPermission(Permission.PROGRAMME_USER)
      .pipe(
        take(1),
        filter(canSee => canSee),
      )
      .subscribe(() => this.menuItems$.next([
        this.applicationsItem,
        this.auditItem,
        this.programmItem,
        this.editUserItem,
      ]));

    this.permissionService.hasPermission(Permission.ADMINISTRATOR)
      .pipe(
        take(1),
        filter(canSee => canSee),
      )
      .subscribe(() => this.menuItems$.next([
        this.applicationsItem,
        this.auditItem,
        this.usersItem,
        this.programmItem,
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
