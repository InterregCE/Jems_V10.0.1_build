import {ChangeDetectionStrategy, Component} from '@angular/core';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {BehaviorSubject, combineLatest, Observable, of} from 'rxjs';
import {PaymentAccountDTO} from '@cat/api';
import {Alert} from '@common/components/forms/alert';
import {RoutingService} from '@common/services/routing.service';
import {ActivatedRoute} from '@angular/router';
import {PaymentsPageSidenavService} from '../../payments-page-sidenav.service';
import {catchError, finalize, map, take, tap} from 'rxjs/operators';
import {AccountsPageStore} from '../accounts-page.store';
import {APIError} from '@common/models/APIError';
import PaymentAccountStatusEnum = PaymentAccountDTO.StatusEnum;

@UntilDestroy()
@Component({
  selector: 'jems-account-detail',
  templateUrl: './account-detail.component.html',
  styleUrls: ['./account-detail.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AccountDetailComponent {
  Alert = Alert;
  accountStatusEnum = PaymentAccountDTO.StatusEnum;
  statusChangePending$ = new BehaviorSubject(false);
  reOpenError$ = new BehaviorSubject<APIError | null>(null);

  data$: Observable<{
    accountDetail: PaymentAccountDTO;
    userCanView: boolean;
    userCanEdit: boolean;
    canReOpen: boolean;
  }>;

  constructor(public pageStore: AccountsPageStore,
              private router: RoutingService,
              private activatedRoute: ActivatedRoute,
              private paymentsPageSidenav: PaymentsPageSidenavService) {
    this.data$ = combineLatest([
      this.pageStore.accountDetail$,
      this.pageStore.userCanEdit$,
      this.pageStore.userCanView$,
      this.pageStore.updatedAccountStatus$
    ]).pipe(
      map(([accountDetail, userCanEdit, userCanView, updatedStatus]) => ({
          accountDetail: this.getUpdatedAccount(accountDetail, updatedStatus) ,
          userCanView,
          userCanEdit,
          canReOpen: accountDetail.status === PaymentAccountStatusEnum.FINISHED && userCanEdit,
        })
      )
    );
  }

  getUpdatedAccount(savedAccount: PaymentAccountDTO, newStatus: PaymentAccountStatusEnum): PaymentAccountDTO {
    const updatedAccount = savedAccount;
    updatedAccount.status = newStatus;
    return updatedAccount;
  }

  activeTab(route: string): boolean {
    return this.router.url?.includes(route);
  }

  routeTo(route: string): void {
    this.router.navigate([route], {relativeTo: this.activatedRoute, queryParamsHandling: 'merge'});
  }

  public showErrorMessage(error: APIError): Observable<null> {

    this.reOpenError$.next(error);
    setTimeout(() => {
      this.reOpenError$.next(null);
    }, 4000);
    return of(null);
  }

  setPaymentAccountBackToDraft(paymentId: number) {
    this.statusChangePending$.next(true);

    this.pageStore.reOpenFinalizedPaymentAccount(paymentId).pipe(
      take(1),
      catchError((err) =>
        this.showErrorMessage(err)
      ),
      finalize(() => this.statusChangePending$.next(false)),
      untilDestroyed(this)
    ).subscribe();
  }

}
