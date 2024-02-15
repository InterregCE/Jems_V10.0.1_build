import {ChangeDetectionStrategy, Component} from '@angular/core';
import {BehaviorSubject, combineLatest, Observable, of} from 'rxjs';
import {PaymentAccountDTO, PaymentApplicationToEcDetailDTO,} from '@cat/api';
import {AccountsPageStore} from '../../accounts-page.store';
import {FormService} from '@common/components/section/form/form.service';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {APIError} from '@common/models/APIError';
import PaymentAccountStatusEnum = PaymentAccountDTO.StatusEnum;
import {catchError, filter, finalize, map, switchMap, take, tap} from 'rxjs/operators';
import {Forms} from '@common/utils/forms';
import {MatDialog} from '@angular/material/dialog';
import {Alert} from '@common/components/forms/alert';
import {AccountingYearPipe} from '@common/pipe/accounting-year.pipe';

@UntilDestroy()
@Component({
  selector: 'jems-accounts-finalize-tab',
  templateUrl: './accounts-finalize-tab.component.html',
  styleUrls: ['./accounts-finalize-tab.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [FormService]
})
export class AccountsFinalizeTabComponent {
  Alert = Alert;

  paymentAccountStatusEnum = PaymentAccountStatusEnum;
  finalizeError$ = new BehaviorSubject<APIError | null>(null);
  statusChangePending$ = new BehaviorSubject(false);

  data$: Observable<{
    accountDetail: PaymentAccountDTO;
    userCanView: boolean;
    userCanEdit: boolean;
    finalizationDisabled: boolean;
    canFinalize: boolean;
  }>;

  constructor(public pageStore: AccountsPageStore,
              private confirmDialog: MatDialog,
              private accountingYearPipe: AccountingYearPipe) {
    this.data$ = combineLatest([
      this.pageStore.accountDetail$,
      this.pageStore.updatedAccountStatus$,
      this.pageStore.userCanEdit$,
      this.pageStore.userCanView$,
    ]).pipe(
      map(([accountDetail, paymentStatus, userCanEdit, userCanView]) => ({
          accountDetail: this.getUpdatedAccount(accountDetail, paymentStatus),
          userCanView,
          userCanEdit,
          finalizationDisabled: this.isFinalizationDisabled(paymentStatus, userCanEdit),
          canFinalize: (accountDetail.id && accountDetail.status === PaymentAccountStatusEnum.DRAFT && userCanEdit) || false,
        })
      )
    );
  }

  getUpdatedAccount(savedAccount: PaymentAccountDTO, newStatus: PaymentAccountDTO.StatusEnum): PaymentAccountDTO {
    const updatedAccount = savedAccount;
    updatedAccount.status = newStatus;
    return updatedAccount;
  }

  finalizePaymentAccount(accountDetail: PaymentAccountDTO) {
    this.statusChangePending$.next(true);

    Forms.confirm(
      this.confirmDialog,
      {
        title: 'payments.accounts.finalize.tab.finalize.dialog.title',
        message: {
          i18nKey: 'payments.accounts.finalize.tab.finalize.dialog.message',
          i18nArguments: {
            programmeFund: accountDetail.fund.type,
            accountingYear: this.accountingYearPipe.transform(accountDetail.accountingYear)
          }
        },
      }).pipe(
      take(1),
      tap(() => this.statusChangePending$.next(false)),
      filter(Boolean),
      switchMap(() => this.pageStore.finalizePaymentAccount(accountDetail.id)
        .pipe(
          catchError((error) => this.showErrorMessage(error.error)),
        ))
    ).subscribe();
  }

  public showErrorMessage(error: APIError): Observable<null> {
    this.finalizeError$.next(error);
    setTimeout(() => {
      this.finalizeError$.next(null);
    }, 5000);
    return of(null);
  }

  private isFinalizationDisabled(paymentAccountStatus: PaymentAccountStatusEnum, userCanEdit: boolean): boolean {
    return paymentAccountStatus == this.paymentAccountStatusEnum.FINISHED || !userCanEdit;
  }

}
