import {Injectable} from '@angular/core';
import {SideNavService} from '@common/components/side-nav/side-nav.service';
import {RoutingService} from '@common/services/routing.service';
import {filter, tap} from 'rxjs/operators';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {combineLatest} from 'rxjs';
import {HeadlineRoute} from '@common/components/side-nav/headline-route';
import {UserRoleDTO, UserRoleSummaryDTO} from '@cat/api';

import PermissionsEnum = UserRoleDTO.PermissionsEnum;
import {PermissionService} from '../security/permissions/permission.service';
import {RoleStore} from '../system/services/role-store.service';

@UntilDestroy()
@Injectable()
export class ControllersPageSidenavService {
  public static CONTROLLERS_DETAIL_PATH = '/app/controller';


  constructor(private sideNavService: SideNavService,
              private routingService: RoutingService,
              private permissionService: PermissionService,
              private roleStore: RoleStore) {

    combineLatest([
      this.routingService.routeChanges(ControllersPageSidenavService.CONTROLLERS_DETAIL_PATH),
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
    if (permissions.includes(PermissionsEnum.InstitutionsRetrieve) ||
      permissions.includes(PermissionsEnum.InstitutionsUpdate)) {
      bulletsArray.push({
        headline: {i18nKey: 'topbar.main.institutions'},
        route: `${ControllersPageSidenavService.CONTROLLERS_DETAIL_PATH}`,
        scrollToTop: true
      });
    }

    if (permissions.includes(PermissionsEnum.InstitutionsAssignmentRetrieve) ||
      permissions.includes(PermissionsEnum.InstitutionsAssignmentUpdate)) {
      bulletsArray.push({
        headline: {i18nKey: 'topbar.main.institutions.assignment'},
        route: `${ControllersPageSidenavService.CONTROLLERS_DETAIL_PATH}/assignment`,
        scrollToTop: true
      });
    }

    this.sideNavService.setHeadlines(ControllersPageSidenavService.CONTROLLERS_DETAIL_PATH, [
      {
        headline: {i18nKey: 'topbar.main.controllers'},
        bullets: bulletsArray
      },
    ]);
  }
}
