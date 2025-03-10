import {Injectable} from '@angular/core';
import {
  PaymentAccountAmountSummaryDTO,
  PaymentAccountDTO,
  PaymentAccountOverviewDTO,
  PaymentAccountService,
  PaymentAccountUpdateDTO,
  UserRoleCreateDTO,
} from '@cat/api';
import {PermissionService} from '../../security/permissions/permission.service';
import {BehaviorSubject, combineLatest, merge, Observable, Subject} from 'rxjs';
import {filter, map, shareReplay, switchMap, tap} from 'rxjs/operators';
import {Log} from '@common/utils/log';
import {UntilDestroy} from '@ngneat/until-destroy';
import {RoutingService} from '@common/services/routing.service';
import PermissionsEnum = UserRoleCreateDTO.PermissionsEnum;
import PaymentAccountStatusEnum = PaymentAccountDTO.StatusEnum;

@UntilDestroy()
@Injectable({
  providedIn: 'root'
})
export class AccountsPageStore {

  accountId$: Observable<number>;
  accountDetail$: Observable<PaymentAccountDTO>;
  accountAmountSummary$: Observable<PaymentAccountAmountSummaryDTO>;
  userCanView$: Observable<boolean>;
  userCanEdit$: Observable<boolean>;
  accountsByFund$: Observable<PaymentAccountOverviewDTO[]>;
  tabChanged$ = new BehaviorSubject(true);
  savedAccountDetail$ = new Subject<PaymentAccountDTO>();
  updatedAccountStatus$ = new BehaviorSubject<PaymentAccountDTO.StatusEnum>(PaymentAccountStatusEnum.DRAFT);

  constructor(private paymentAccountService: PaymentAccountService,
              private permissionService: PermissionService,
              private router: RoutingService,
  ) {
    this.accountId$ = this.router.routeParameterChanges('/app/payments/accounts/', 'id').pipe(
      filter(Boolean),
      map(Number),
      shareReplay(1)
    );
    this.accountsByFund$ = this.accountsByFund();
    this.userCanView$ = this.userCanView();
    this.userCanEdit$ = this.userCanEdit();
    this.accountDetail$ = this.accountDetail();
    this.accountAmountSummary$ = this.accountAmountSummary();
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
        tap(data => this.updatedAccountStatus$.next(data.status)),
        tap(account => Log.info('Fetched the account detail:', this, account)),
        shareReplay(1),
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

  finalizePaymentAccount(accountId: number): Observable<PaymentAccountDTO.StatusEnum> {
    return this.paymentAccountService.finalizePaymentAccount(accountId)
      .pipe(
        map(status => status as PaymentAccountDTO.StatusEnum),
        tap(status => this.updatedAccountStatus$.next(status)),
        tap(status => Log.info('Changed status for payment account', accountId, status))
      );
  }

  reOpenFinalizedPaymentAccount(accountId: number): Observable<PaymentAccountDTO.StatusEnum> {
    return this.paymentAccountService.reOpenPaymentAccount(accountId)
      .pipe(
        map(status => status as PaymentAccountDTO.StatusEnum),
        tap(status => this.updatedAccountStatus$.next(status)),
        tap(status => Log.info('Changed status for payment account', accountId, status))
      );
  }

  private accountAmountSummary(): Observable<PaymentAccountAmountSummaryDTO> {
    return this.accountId$
      .pipe(
        switchMap(accountId => this.paymentAccountService.getPaymentAccountAmountSummary(accountId)),
        tap(accountAmountSummary => Log.info('Fetched payment account amount summary', accountAmountSummary))
      );
  }
}
