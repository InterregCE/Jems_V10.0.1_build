import {ChangeDetectionStrategy, Component} from '@angular/core';
import {PermissionService} from '../../security/permissions/permission.service';
import {combineLatest} from 'rxjs';
import {UserRoleDTO} from '@cat/api';
import {PaymentsPageSidenavService} from '../payments-page-sidenav.service';
import {map, take, tap} from 'rxjs/operators';

@Component({
  selector: 'jems-payments-page',
  templateUrl: './payments-page.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PaymentsPageComponent {

  constructor(private permissionService: PermissionService,
              private paymentsPageSidenav: PaymentsPageSidenavService
  ) {
    combineLatest([
      this.permissionService.hasPermission(UserRoleDTO.PermissionsEnum.PaymentsRetrieve),
      this.permissionService.hasPermission(UserRoleDTO.PermissionsEnum.AdvancePaymentsRetrieve),
      this.permissionService.hasPermission(UserRoleDTO.PermissionsEnum.PaymentsToEcRetrieve),
      this.permissionService.hasPermission(UserRoleDTO.PermissionsEnum.PaymentsAuditRetrieve),
      this.permissionService.hasPermission(UserRoleDTO.PermissionsEnum.PaymentsAccountRetrieve)
    ]).pipe(
      take(1),
      map(([paymentsToProjectRetrieve, advancePaymentsRetrieve, paymentsToEcRetrieve, paymentsAuditRetrieve, accountsRetrieve]) => ({
        paymentsToProjectRetrieve,
        advancePaymentsRetrieve,
        paymentsToEcRetrieve,
        paymentsAuditRetrieve,
        accountsRetrieve
      })),
      tap(data => this.redirectToAccessiblePaymentPage(data.paymentsToProjectRetrieve, data.advancePaymentsRetrieve, data.paymentsToEcRetrieve, data.paymentsAuditRetrieve, data.accountsRetrieve))
    ).subscribe();
  }

  redirectToAccessiblePaymentPage(paymentToProjectRetrieve: boolean, advancePaymentRetrieve: boolean, paymentsToEcRetrieve: boolean, auditRetrieve: boolean, accountsRetrieve: boolean) {
    if (paymentToProjectRetrieve) {
      this.paymentsPageSidenav.goToPaymentsToProjects();
    } else if (advancePaymentRetrieve) {
      this.paymentsPageSidenav.goToAdvancePayments();
    } else if (paymentsToEcRetrieve) {
      this.paymentsPageSidenav.goToPaymentsToEc();
    } else if (auditRetrieve) {
      this.paymentsPageSidenav.goToAudit();
    } else if (accountsRetrieve) {
        this.paymentsPageSidenav.goToAccounts();
    }
  }
}
