import {Injectable} from '@angular/core';
import {SideNavService} from '@common/components/side-nav/side-nav.service';
import {RoutingService} from '@common/services/routing.service';
import {filter, tap} from 'rxjs/operators';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {PermissionService} from '../../security/permissions/permission.service';
import {combineLatest} from 'rxjs';
import {HeadlineRoute} from '@common/components/side-nav/headline-route';
import {UserRoleDTO, UserRoleSummaryDTO} from '@cat/api';
import {UserRoleDetailPageStore} from '../user-page-role/user-role-detail-page/user-role-detail-page-store.service';
import {RoleStore} from './role-store.service';
import PermissionsEnum = UserRoleDTO.PermissionsEnum;

@UntilDestroy()
@Injectable()
export class SystemPageSidenavService {
  public static SYSTEM_DETAIL_PATH = '/app/system';


  constructor(private sideNavService: SideNavService,
              private routingService: RoutingService,
              private permissionService: PermissionService,
              private roleStore: RoleStore) {
    const systemPath$ = this.routingService.routeChanges(SystemPageSidenavService.SYSTEM_DETAIL_PATH)
      .pipe(
        filter(systemPath => systemPath)
      );

    combineLatest([
      systemPath$,
      this.permissionService.permissionsChanged(),
      this.roleStore.roles$
    ]).pipe(
      filter(([systemPath]) => systemPath),
      tap(([systemPath, permissions, roles]) =>
        this.setHeadlines(permissions as PermissionsEnum[], roles)),
      untilDestroyed(this)
    ).subscribe();
  }

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
          baseRoute: `${SystemPageSidenavService.SYSTEM_DETAIL_PATH}/user`,
          scrollToTop: true,
        } as HeadlineRoute ]
      };
      if (permissions.includes(PermissionsEnum.RoleRetrieve)) {
        const rolesHeadline = {
          headline: {i18nKey: 'topbar.main.user.role.management'},
          route: `${SystemPageSidenavService.SYSTEM_DETAIL_PATH}/role`,
          scrollToTop: true,
          bullets: roles.map(role => ({
            headline: {i18nKey: role.name},
            route: `${UserRoleDetailPageStore.USER_ROLE_DETAIL_PATH}/${role.id}`,
            scrollToTop: true,
            badgeText: role.defaultForRegisteredUser && 'userRole.default.flag',
            badgeTooltip: 'userRole.default.flag.info',
          } as HeadlineRoute))
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
