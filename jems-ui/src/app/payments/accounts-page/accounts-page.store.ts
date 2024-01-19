import {Injectable} from '@angular/core';
import {
  PaymentAccountDTO,
  PaymentAccountOverviewDTO,
  PaymentAccountService, PaymentAccountUpdateDTO,
  UserRoleCreateDTO,
} from '@cat/api';
import {PermissionService} from '../../security/permissions/permission.service';
import {BehaviorSubject, combineLatest, merge, Observable, Subject} from 'rxjs';
import {map, switchMap, tap} from 'rxjs/operators';
import {Log} from '@common/utils/log';
import {UntilDestroy} from '@ngneat/until-destroy';
import PermissionsEnum = UserRoleCreateDTO.PermissionsEnum;
import {RoutingService} from '@common/services/routing.service';

@UntilDestroy()
@Injectable({
  providedIn: 'root'
})
export class AccountsPageStore {

  accountId$: Observable<number>;
  accountDetail$: Observable<PaymentAccountDTO>;
  userCanView$: Observable<boolean>;
  userCanEdit$: Observable<boolean>;
  accountsByFund$: Observable<PaymentAccountOverviewDTO[]>;
  tabChanged$ = new BehaviorSubject(true);
  savedAccountDetail$ = new Subject<PaymentAccountDTO>();

  constructor(private paymentAccountService: PaymentAccountService,
              private permissionService: PermissionService,
              private router: RoutingService,
  ) {
    this.accountId$ = this.router.routeParameterChanges('/app/payments/accounts/', 'id')
      .pipe(map(Number));
    this.accountsByFund$ = this.accountsByFund();
    this.userCanView$ = this.userCanView();
    this.userCanEdit$ = this.userCanEdit();
    this.accountDetail$ = this.accountDetail();
  }

  private accountsByFund(): Observable<PaymentAccountOverviewDTO[]> {
    return this.paymentAccountService.listPaymentAccount()
      .pipe(
        tap(accounts => Log.info('Fetched the accounts to payments:', this, accounts)),
      );
  }

  private accountDetail(): Observable<PaymentAccountDTO> {
    const initialAccountDetail$ = this.accountId$
      .pipe(
        switchMap(id => this.paymentAccountService.getPaymentAccount(Number(id))),
        tap(account => Log.info('Fetched the account detail:', this, account))
      );

    return merge(initialAccountDetail$, this.savedAccountDetail$);
  }

  private userCanView(): Observable<boolean> {
    return combineLatest([
      this.permissionService.hasPermission(PermissionsEnum.PaymentsAccountRetrieve),
      this.permissionService.hasPermission(PermissionsEnum.PaymentsAccountUpdate),
    ])
      .pipe(
        map(([canRetrieve, canUpdate]) => canRetrieve || canUpdate)
      );
  }

  private userCanEdit(): Observable<boolean> {
    return combineLatest([
      this.permissionService.hasPermission(PermissionsEnum.PaymentsAccountUpdate),
    ])
      .pipe(
        map(([canUpdate]) => canUpdate)
      );
  }

  updatePaymentAccountSummary(paymentAccountData: PaymentAccountUpdateDTO): Observable<PaymentAccountDTO> {
    return this.accountId$
      .pipe(
        switchMap(id => this.paymentAccountService.updatePaymentAccount(Number(id), paymentAccountData)),
        tap(saved => Log.info('Payment Account summary data updated!', saved)),
        tap(data => this.savedAccountDetail$.next(data))
      );
  }
}
