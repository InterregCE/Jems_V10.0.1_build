import {Injectable} from '@angular/core';
import {SideNavService} from '@common/components/side-nav/side-nav.service';
import {filter, tap} from 'rxjs/operators';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {combineLatest} from 'rxjs';
import {UserRoleDTO} from '@cat/api';
import {PermissionService} from '../security/permissions/permission.service';
import PermissionsEnum = UserRoleDTO.PermissionsEnum;
import {RoutingService} from '@common/services/routing.service';

@UntilDestroy()
@Injectable()
export class PaymentsPageSidenavService {
  public static PAYMENTS_DETAIL_PATH = '/app/payments';

  private paymentsToProjectsPage = {
    headline: {i18nKey: 'payments.projects.header'},
    route: `${PaymentsPageSidenavService.PAYMENTS_DETAIL_PATH}/paymentsToProjects`,
    baseRoute: `${PaymentsPageSidenavService.PAYMENTS_DETAIL_PATH}/paymentsToProjects`,
  };

  private advancePaymentsPage = {
    headline: {i18nKey: 'payments.advance.header'},
    route: `${PaymentsPageSidenavService.PAYMENTS_DETAIL_PATH}/advancePayments`,
    baseRoute: `${PaymentsPageSidenavService.PAYMENTS_DETAIL_PATH}/advancePayments`,
  };

  private paymentsToEcPage = {
    headline: {i18nKey: 'payments.to.ec.header'},
    route: `${PaymentsPageSidenavService.PAYMENTS_DETAIL_PATH}/paymentApplicationsToEc`,
    baseRoute: `${PaymentsPageSidenavService.PAYMENTS_DETAIL_PATH}/paymentApplicationsToEc`,
  };


  constructor(private sideNavService: SideNavService,
              private permissionService: PermissionService,
              private routingService: RoutingService) {
    combineLatest([this.routingService.routeChanges(PaymentsPageSidenavService.PAYMENTS_DETAIL_PATH), this.permissionService.permissionsChanged()])
      .pipe(
        filter(([paymentsPath]) => paymentsPath),
        tap(([paymentsPath, permissions]) => this.init(permissions as PermissionsEnum[])),
        untilDestroyed(this)
      ).subscribe();
  }

  private init(permissions: PermissionsEnum[]): void {
      this.setSideNavLinks(permissions);
  }

  setSideNavLinks(permissions: PermissionsEnum[]) {
    const bullets = [];
    if (permissions.some((permission: PermissionsEnum) => permission === PermissionsEnum.PaymentsRetrieve || permission === PermissionsEnum.PaymentsUpdate)) {
      bullets.push(this.paymentsToProjectsPage);
    }
    if (permissions.some((permission: PermissionsEnum) => permission === PermissionsEnum.AdvancePaymentsRetrieve || permission === PermissionsEnum.AdvancePaymentsUpdate)) {
      bullets.push(this.advancePaymentsPage);
    }
    if (permissions.some((permission: PermissionsEnum) => permission === PermissionsEnum.PaymentsToEcRetrieve || permission === PermissionsEnum.PaymentsToEcUpdate)) {
      bullets.push(this.paymentsToEcPage);
    }

    this.sideNavService.setHeadlines(PaymentsPageSidenavService.PAYMENTS_DETAIL_PATH, [
      ...this.hasAccessToPayments(permissions) ?
        [{
          headline: {i18nKey: 'payments.breadcrumb'},
          bullets
        },
        ] : [],
    ]);
  }

  hasAccessToPayments(permissions: PermissionsEnum[]): boolean {
    return permissions.some((permission: PermissionsEnum) => permission === PermissionsEnum.PaymentsRetrieve ||
      permission === PermissionsEnum.AdvancePaymentsRetrieve ||
      permission === PermissionsEnum.PaymentsToEcRetrieve
    );
  }

  public goToPaymentsToProjects(): void {
    this.sideNavService.navigate(this.paymentsToProjectsPage);
  }

  public goToAdvancePayments(): void {
    this.sideNavService.navigate(this.advancePaymentsPage);
  }

  public goToPaymentsToEc(): void{
    this.sideNavService.navigate(this.paymentsToEcPage);
  }

}
