import {Injectable} from '@angular/core';
import {Observable, ReplaySubject} from 'rxjs';
import {MenuItemConfiguration} from '../menu/model/menu-item.configuration';
import {PermissionService} from '../../../security/permissions/permission.service';
import {Permission} from '../../../security/permissions/permission';
import {Router} from '@angular/router';
import {filter, take} from 'rxjs/operators';
import {SecurityService} from '../../../security/security.service';

@Injectable()
export class TopBarService {

  private menuItems$ = new ReplaySubject<MenuItemConfiguration[]>(1);
  private auditUrl = '';
  private applicationsItem =
    new MenuItemConfiguration({
      name: 'Project Applications',
      isInternal: true,
      route: '/',
      action: (internal: boolean, route: string) => this.handleNavigation(internal, route),
    });
  private auditItem =
    new MenuItemConfiguration({
      name: 'Audit Log',
      isInternal: false,
      route: this.auditUrl,
      action: (internal: boolean, route: string) => this.handleNavigation(internal, route),
    });
  private usersItem =
    new MenuItemConfiguration({
      name: 'User Management',
      isInternal: true,
      route: '/user',
      action: (internal: boolean, route: string) => this.handleNavigation(internal, route),
    });

  constructor(private permissionService: PermissionService,
              private securityService: SecurityService,
              private router: Router) {
    this.permissionService.permissionsChanged()
      .subscribe(() => this.adaptMenuItems())
  }

  menuItems(): Observable<MenuItemConfiguration[]> {
    return this.menuItems$.asObservable();
  }

  newAuditUrl(auditUrl: string): void {
    this.auditUrl = auditUrl;
  }

  logout(): void {
    this.securityService.logout();
    this.router.navigate(['/login']);
  }

  private adaptMenuItems(): void {
    this.permissionService.hasPermission(Permission.APPLICANT_USER)
      .pipe(
        take(1),
        filter(canSee => canSee),
      )
      .subscribe(() => this.menuItems$.next([this.applicationsItem]));

    this.permissionService.hasPermission(Permission.PROGRAMME_USER)
      .pipe(
        take(1),
        filter(canSee => canSee),
      )
      .subscribe(() => this.menuItems$.next([this.applicationsItem, this.auditItem]));

    this.permissionService.hasPermission(Permission.ADMINISTRATOR)
      .pipe(
        take(1),
        filter((canDoAnything: boolean) => canDoAnything),
      )
      .subscribe(() => this.menuItems$.next([this.applicationsItem, this.auditItem, this.usersItem]));
  }

  private handleNavigation(internalRoute: boolean, route: string): void {
    if (internalRoute) {
      this.router.navigate([route]);
    } else {
      window.open(route, '_blank');
    }
  }
}
