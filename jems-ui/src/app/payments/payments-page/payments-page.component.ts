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
      this.permissionService.hasPermission(UserRoleDTO.PermissionsEnum.PaymentsToEcRetrieve)
    ]).pipe(
      take(1),
      map(([paymentsToProjectRetrieve, advancePaymentsRetrieve, paymentsToEcRetrieve]) => ({
        paymentsToProjectRetrieve,
        advancePaymentsRetrieve,
        paymentsToEcRetrieve
      })),
      tap(data => this.redirectToAccessiblePaymentPage(data.paymentsToProjectRetrieve, data.advancePaymentsRetrieve, data.paymentsToEcRetrieve))
    ).subscribe();
  }

  redirectToAccessiblePaymentPage(paymentToProjectRetrieve: boolean, advancePaymentRetrieve: boolean, paymentsToEcRetrieve: boolean) {
    if (paymentToProjectRetrieve) {
      this.paymentsPageSidenav.goToPaymentsToProjects();
    } else if (advancePaymentRetrieve) {
      this.paymentsPageSidenav.goToAdvancePayments();
    } else if (paymentsToEcRetrieve) {
      this.paymentsPageSidenav.goToPaymentsToEc();
    }
  }
}
