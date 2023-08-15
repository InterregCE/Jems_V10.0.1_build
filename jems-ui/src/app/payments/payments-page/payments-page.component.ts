import {ChangeDetectionStrategy, Component} from '@angular/core';
import {PermissionService} from '../../security/permissions/permission.service';
import {combineLatest, Observable} from 'rxjs';
import {UserRoleDTO} from '@cat/api';
import {PaymentsPageSidenavService} from '../payments-page-sidenav.service';
import {map, tap} from 'rxjs/operators';

@Component({
  selector: 'jems-payments-page',
  templateUrl: './payments-page.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PaymentsPageComponent{

  userHasAccessToAdvancePayments$: Observable<boolean>;
  userHasAccessToPaymentsToProjects$: Observable<boolean>;

  constructor(private permissionService: PermissionService,
              private paymentsPageSidenav: PaymentsPageSidenavService
  ) {
    this.userHasAccessToAdvancePayments$ = this.permissionService.hasPermission(UserRoleDTO.PermissionsEnum.AdvancePaymentsRetrieve);
    this.userHasAccessToPaymentsToProjects$ = this.permissionService.hasPermission(UserRoleDTO.PermissionsEnum.PaymentsRetrieve);

    combineLatest([
      this.permissionService.hasPermission(UserRoleDTO.PermissionsEnum.PaymentsRetrieve),
      this.permissionService.hasPermission(UserRoleDTO.PermissionsEnum.AdvancePaymentsRetrieve),
      this.permissionService.hasPermission(UserRoleDTO.PermissionsEnum.PaymentsToEcRetrieve)
    ]).pipe(
      map(([paymentsToProjectRetrieve, advancePaymentsRetrieve, paymentsToEcRetrieve ]) => ({
          paymentsToProjectRetrieve,
          advancePaymentsRetrieve,
          paymentsToEcRetrieve
      })
      ),
      tap(data => this.redirectToAccessiblePaymentPage(data.paymentsToProjectRetrieve, data.advancePaymentsRetrieve, data.paymentsToEcRetrieve))
    ).subscribe();
  }

  redirectToAccessiblePaymentPage(paymentToProjectRetrieve: boolean, advancePaymentRetrieve: boolean, paymentsToEcRetrieve: boolean) {
    if (!paymentToProjectRetrieve && !advancePaymentRetrieve && paymentsToEcRetrieve) {
      this.paymentsPageSidenav.goToPaymentsToEc();
    } else if (!paymentToProjectRetrieve) {
      this.paymentsPageSidenav.goToAdvancePayments();
    } else {this.paymentsPageSidenav.goToPaymentsToProjects();}
  }
}
