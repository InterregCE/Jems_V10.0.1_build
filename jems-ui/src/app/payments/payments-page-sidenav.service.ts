import {Injectable} from '@angular/core';
import {SideNavService} from '@common/components/side-nav/side-nav.service';
import {filter, tap} from 'rxjs/operators';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {combineLatest} from 'rxjs';
import {UserRoleDTO} from '@cat/api';
import {PermissionService} from '../security/permissions/permission.service';
import {RoutingService} from '@common/services/routing.service';
import PermissionsEnum = UserRoleDTO.PermissionsEnum;

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

  private auditPage = {
    headline: {i18nKey: 'payments.audit.header'},
    route: `${PaymentsPageSidenavService.PAYMENTS_DETAIL_PATH}/audit`,
    baseRoute: `${PaymentsPageSidenavService.PAYMENTS_DETAIL_PATH}/audit`,
  };

  private accountsPage = {
    headline: {i18nKey: 'payments.accounts.header'},
    route: `${PaymentsPageSidenavService.PAYMENTS_DETAIL_PATH}/accounts`,
    baseRoute: `${PaymentsPageSidenavService.PAYMENTS_DETAIL_PATH}/accounts`,
  };


  constructor(private sideNavService: SideNavService,
              private permissionService: PermissionService,
              private routingService: RoutingService) {
    combineLatest([
      this.routingService.routeChanges(PaymentsPageSidenavService.PAYMENTS_DETAIL_PATH),
      this.permissionService.permissionsChanged()
    ]).pipe(
      filter(([paymentsPath, permissions]) => paymentsPath),
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
    if (permissions.some((permission: PermissionsEnum) => permission === PermissionsEnum.PaymentsAccountRetrieve || permission === PermissionsEnum.PaymentsAccountUpdate)) {
      bullets.push(this.accountsPage);
    }
    if (permissions.some((permission: PermissionsEnum) => permission === PermissionsEnum.PaymentsAuditRetrieve || permission === PermissionsEnum.PaymentsAuditUpdate)) {
      bullets.push(this.auditPage);
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
      permission === PermissionsEnum.PaymentsToEcRetrieve ||
      permission === PermissionsEnum.PaymentsAuditRetrieve ||
      permission === PermissionsEnum.PaymentsAccountRetrieve
    );
  }

  public goToPaymentsToProjects(): void {
    // replaceUrl because if you navigate back to /payments, you will be caught in infinite loop
    this.routingService.navigate([this.paymentsToProjectsPage.route], {replaceUrl: true});
  }

  public goToAdvancePayments(): void {
    this.routingService.navigate([this.advancePaymentsPage.route], {replaceUrl: true});
  }

  public goToPaymentsToEc(): void {
    this.routingService.navigate([this.paymentsToEcPage.route], {replaceUrl: true});
  }

  public goToAudit(): void {
    this.routingService.navigate([this.auditPage.route], {replaceUrl: true});
  }

  public goToAccounts(): void {
    this.routingService.navigate([this.accountsPage.route], {replaceUrl: true});
  }

}
