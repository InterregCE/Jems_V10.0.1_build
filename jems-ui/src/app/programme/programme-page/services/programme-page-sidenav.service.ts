import {Injectable} from '@angular/core';
import {SideNavService} from '@common/components/side-nav/side-nav.service';
import {filter, tap} from 'rxjs/operators';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {RoutingService} from '@common/services/routing.service';
import {combineLatest} from 'rxjs';
import {PermissionService} from '../../../security/permissions/permission.service';
import {UserRoleDTO} from '@cat/api';
import PermissionsEnum = UserRoleDTO.PermissionsEnum;

@UntilDestroy()
@Injectable()
export class ProgrammePageSidenavService {
  public static PROGRAMME_DETAIL_PATH = '/app/programme';

  private indicatorsPage = {
    headline: {i18nKey: 'programme.tab.indicators'},
    route: `${ProgrammePageSidenavService.PROGRAMME_DETAIL_PATH}/indicators`,
    baseRoute: `${ProgrammePageSidenavService.PROGRAMME_DETAIL_PATH}/indicators`
  };

  private prioritiesPage = {
    headline: {i18nKey: 'programme.tab.priority'},
    route: `${ProgrammePageSidenavService.PROGRAMME_DETAIL_PATH}/priorities`,
    baseRoute: `${ProgrammePageSidenavService.PROGRAMME_DETAIL_PATH}/priorities`,
  };

  private costsPage = {
    headline: {i18nKey: 'programme.tab.costs.option'},
    route: `${ProgrammePageSidenavService.PROGRAMME_DETAIL_PATH}/costs`,
    baseRoute: `${ProgrammePageSidenavService.PROGRAMME_DETAIL_PATH}/costs`
  };

  constructor(private sideNavService: SideNavService,
              private permissionService: PermissionService,
              private routingService: RoutingService) {
    combineLatest([this.routingService.routeChanges(ProgrammePageSidenavService.PROGRAMME_DETAIL_PATH), this.permissionService.permissionsChanged()])
      .pipe(
        filter(([programmePath]) => programmePath),
        tap(([programmePath, permissions]) => this.init(permissions as PermissionsEnum[])),
        untilDestroyed(this)
      ).subscribe();
  }

  private init(permissions: PermissionsEnum[]): void {
    this.sideNavService.setHeadlines(ProgrammePageSidenavService.PROGRAMME_DETAIL_PATH, [
      ...permissions.some((permission: PermissionsEnum) => permission === PermissionsEnum.ProgrammeSetupRetrieve || permission === PermissionsEnum.ProgrammeSetupUpdate) ?
        [{
          headline: {i18nKey: 'programme.data.page.title'},
          bullets: [
            {
              headline: {i18nKey: 'programme.tab.data'},
              route: `${ProgrammePageSidenavService.PROGRAMME_DETAIL_PATH}`,
            },
            {
              headline: {i18nKey: 'programme.tab.languages'},
              route: `${ProgrammePageSidenavService.PROGRAMME_DETAIL_PATH}/languages`,
            },
            {
              headline: {i18nKey: 'programme.tab.translation.management'},
              route: `${ProgrammePageSidenavService.PROGRAMME_DETAIL_PATH}/translationManagement`,
            },
            {
              headline: {i18nKey: 'programme.tab.area'},
              route: `${ProgrammePageSidenavService.PROGRAMME_DETAIL_PATH}/areas`,
            },
            {
              headline: {i18nKey: 'programme.tab.funds'},
              route: `${ProgrammePageSidenavService.PROGRAMME_DETAIL_PATH}/funds`,
            },
            this.prioritiesPage,
            this.indicatorsPage,
            {
              headline: {i18nKey: 'programme.tab.strategies'},
              route: `${ProgrammePageSidenavService.PROGRAMME_DETAIL_PATH}/strategies`,
            },
            {
              headline: {i18nKey: 'programme.tab.legal.status'},
              route: `${ProgrammePageSidenavService.PROGRAMME_DETAIL_PATH}/legalStatus`,
            },
            this.costsPage,
            {
              headline: {i18nKey: 'programme.tab.state.aid'},
              route: `${ProgrammePageSidenavService.PROGRAMME_DETAIL_PATH}/stateAid`,
            },
          ]
        }] : [],
      ...permissions.includes(PermissionsEnum.ProgrammeDataExportRetrieve) ? [{
        headline: {i18nKey: 'programme.data.export.title'},
        route: `${ProgrammePageSidenavService.PROGRAMME_DETAIL_PATH}/export`,
      }] : []
    ]);
  }

  public goToIndicators(): void {
    this.sideNavService.navigate(this.indicatorsPage);
  }

  public goToPriorities(): void {
    this.sideNavService.navigate(this.prioritiesPage);
  }

  public goToCosts(): void {
    this.sideNavService.navigate(this.costsPage);
  }
}
