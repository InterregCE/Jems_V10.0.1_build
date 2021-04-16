import {Injectable} from '@angular/core';
import {SideNavService} from '@common/components/side-nav/side-nav.service';
import {RoutingService} from '../../common/services/routing.service';
import {filter, tap} from 'rxjs/operators';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {PermissionService} from '../../security/permissions/permission.service';
import {Permission} from '../../security/permissions/permission';
import {combineLatest} from 'rxjs';
import {HeadlineRoute} from '@common/components/side-nav/headline-route';

@UntilDestroy()
@Injectable()
export class SystemPageSidenavService {
  public static SYSTEM_DETAIL_PATH = '/app/system';

  routing$ = this.routingService.routeChanges(SystemPageSidenavService.SYSTEM_DETAIL_PATH)
    .pipe(
      filter(systemPath => systemPath)
    );

  constructor(private sideNavService: SideNavService,
              private routingService: RoutingService,
              private permissionService: PermissionService) {
    combineLatest([
      this.routing$,
      this.permissionService.permissionsChanged(),
    ]).pipe(
      tap(([routing, permissions]) => {
        this.setHeadlines(permissions.some(perm => perm === Permission.ADMINISTRATOR));
      }),
      untilDestroyed(this)
    )
      .subscribe();
  }

  private setHeadlines(isAdministrator: boolean): void {
    const bulletsArray: HeadlineRoute[] = [{
      headline: {i18nKey: 'topbar.main.audit'},
      route: `${SystemPageSidenavService.SYSTEM_DETAIL_PATH}`,
    }];

    if (isAdministrator) {
      bulletsArray.push({
        headline: {i18nKey: 'topbar.main.user.management'},
        route: `${SystemPageSidenavService.SYSTEM_DETAIL_PATH}/user`,
        scrollToTop: true,
      });
    }

    this.sideNavService.setHeadlines(SystemPageSidenavService.SYSTEM_DETAIL_PATH, [
      {
        headline: {i18nKey: 'system.page.title'},
        bullets: bulletsArray
      },
    ]);
  }
}
