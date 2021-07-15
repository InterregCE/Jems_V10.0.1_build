import {Injectable} from '@angular/core';
import {SideNavService} from '@common/components/side-nav/side-nav.service';
import {RoutingService} from '@common/services/routing.service';
import {filter, map, startWith, switchMap, tap} from 'rxjs/operators';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {PermissionService} from '../../security/permissions/permission.service';
import {combineLatest, of, Subject} from 'rxjs';
import {HeadlineRoute} from '@common/components/side-nav/headline-route';
import {UserRoleDTO, UserRoleService, UserRoleSummaryDTO} from '@cat/api';
import {Tables} from '@common/utils/tables';
import {Log} from '@common/utils/log';
import {UserRoleStore} from '../user-page-role/user-role-detail-page/user-role-store.service';
import PermissionsEnum = UserRoleDTO.PermissionsEnum;

@UntilDestroy()
@Injectable()
export class SystemPageSidenavService {
  public static SYSTEM_DETAIL_PATH = '/app/system';

  rolesChanged$ = new Subject<void>();

  constructor(private sideNavService: SideNavService,
              private routingService: RoutingService,
              private permissionService: PermissionService,
              private roleService: UserRoleService) {
    combineLatest([
      this.systemPath$,
      this.permissionService.permissionsChanged(),
      this.roles$
    ]).pipe(
      filter(([systemPath]) => systemPath),
      tap(([systemPath, permissions, roles]) =>
        this.setHeadlines(permissions as PermissionsEnum[], roles)),
      untilDestroyed(this)
    ).subscribe();
  }

  private systemPath$ = this.routingService.routeChanges(SystemPageSidenavService.SYSTEM_DETAIL_PATH)
    .pipe(
      filter(systemPath => systemPath)
    );

  private roles$ = combineLatest([
    this.permissionService.permissionsChanged(),
    this.rolesChanged$.pipe(startWith(null))
  ]).pipe(
    switchMap(([perms]) =>
      perms.includes(PermissionsEnum.RoleRetrieve) ? this.roleService.list(
        Tables.DEFAULT_INITIAL_PAGE_INDEX,
        Tables.DEFAULT_INITIAL_PAGE_SIZE,
        Tables.DEFAULT_INITIAL_SORT.active
      ) : of({content: []})),
    map(page => page?.content),
    tap(roles => Log.info('Fetched roles for sidenav', this, roles))
  );

  private setHeadlines(permissions: PermissionsEnum[], roles: UserRoleSummaryDTO[]): void {
    const bulletsArray: HeadlineRoute[] = [];
    if (permissions.includes(PermissionsEnum.AuditRetrieve)) {
      bulletsArray.push({
        headline: {i18nKey: 'topbar.main.audit'},
        route: `${SystemPageSidenavService.SYSTEM_DETAIL_PATH}/audit`,
      });
    }

    if (permissions.includes(PermissionsEnum.UserRetrieve)) {
      const userManagementHeadline = {
        headline: {i18nKey: 'topbar.main.user.management'},
        bullets: [{
          headline: {i18nKey: 'topbar.main.user.user.management'},
          route: `${SystemPageSidenavService.SYSTEM_DETAIL_PATH}/user`,
          scrollToTop: true,
        }]
      };
      if (permissions.includes(PermissionsEnum.RoleRetrieve)) {
        const rolesHeadline = {
          headline: {i18nKey: 'topbar.main.user.role.management'},
          route: `${SystemPageSidenavService.SYSTEM_DETAIL_PATH}/userRole`,
          scrollToTop: true,
          bullets: roles.map(role => ({
            headline: {i18nKey: role.name},
            route: `${UserRoleStore.USER_ROLE_DETAIL_PATH}/${role.id}`,
            scrollToTop: true,
            badgeText: role.defaultForRegisteredUser && 'userRole.default.flag',
            badgeTooltip: 'userRole.default.flag.info',
          }))
        };
        userManagementHeadline.bullets.push(rolesHeadline);
      }
      bulletsArray.push(userManagementHeadline);
    }

    this.sideNavService.setHeadlines(SystemPageSidenavService.SYSTEM_DETAIL_PATH, [
      {
        headline: {i18nKey: 'system.page.title'},
        bullets: bulletsArray
      },
    ]);
  }
}
