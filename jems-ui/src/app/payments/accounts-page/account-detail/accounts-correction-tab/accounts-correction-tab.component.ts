import {Component, OnInit} from '@angular/core';
import {BehaviorSubject, combineLatest, Observable, of} from 'rxjs';
import {
  PagePaymentAccountCorrectionLinkingDTO,
  PaymentAccountAmountSummaryDTO, PaymentAccountCorrectionExtensionDTO,
  PaymentAccountCorrectionLinkingUpdateDTO,
} from '@cat/api';
import {AccountsCorrectionTabStore} from './accounts-correction-tab.store';
import {catchError, map, take, tap} from 'rxjs/operators';
import {Alert} from '@common/components/forms/alert';
import {APIError} from '@common/models/APIError';
import {AccountsPageStore} from '../../accounts-page.store';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {PaymentAccountCorrectionSelected} from '../accounts-correction-select-table/payment-account-correction-selected';
import PaymentAccountStatusEnum = PaymentAccountCorrectionExtensionDTO.PaymentAccountStatusEnum;

@UntilDestroy()
@Component({
  selector: 'jems-accounts-correction-tab',
  templateUrl: './accounts-correction-tab.component.html',
  styleUrls: ['./accounts-correction-tab.component.scss']
})
export class AccountsCorrectionTabComponent implements OnInit {

  data$: Observable<{
    paymentAccountId: number;
    correctionLinking: PagePaymentAccountCorrectionLinkingDTO;
    isEditable: boolean;
  }>;

  overviewForCurrentTab$: Observable<{
    data: PaymentAccountAmountSummaryDTO;
  }>;

  Alert = Alert;
  error$ = new BehaviorSubject<APIError | null>(null);
  success$ = new BehaviorSubject(false);

  constructor(
    public pageStore: AccountsCorrectionTabStore,
    private detailPageStore: AccountsPageStore,
  ) {
    this.data$ = combineLatest([
      pageStore.correctionPage$,
      detailPageStore.accountDetail$,
      detailPageStore.updatedAccountStatus$,
      detailPageStore.userCanEdit$,
    ]).pipe(
      map(([correctionLinking, accountDetail, status,  userCanEdit]) => ({
        paymentAccountId: accountDetail.id,
        correctionLinking,
        isEditable: userCanEdit && status === PaymentAccountStatusEnum.DRAFT,
      }))
    );

    this.overviewForCurrentTab$ = this.pageStore.currentOverview$.pipe(
      map((overviewForCurrentTab) => ({data: overviewForCurrentTab}))
    );
  }

  ngOnInit() {
    this.pageStore.correctionRetrieveListError$.pipe(untilDestroyed(this)).subscribe(value => {
      if (value) {
        this.showErrorMessage(value);
      }
    });
  }

  selectionChanged($event: PaymentAccountCorrectionSelected): void {
    if ($event.selected) {
      this.selectCorrection($event.paymentAccountId, $event.correctionId);
    } else {
      this.deselectCorrection($event.correctionId);
    }
  }

  private selectCorrection(paymentAccountId: number, correctionId: number) {
    this.pageStore.selectCorrection(paymentAccountId, correctionId).pipe(
      catchError((error) => this.showErrorMessage(error.error))
    ).subscribe();
  }


  private deselectCorrection(correctionId: number) {
    this.pageStore.deselectCorrection(correctionId).pipe(
      catchError((error) => this.showErrorMessage(error.error))
    ).subscribe();
  }

  private showErrorMessage(error: APIError): Observable<null> {
    this.error$.next(error);
    setTimeout(() => {
      if (this.error$.value?.id === error.id) {
        this.error$.next(null);
      }
    }, 10000);
    return of(null);
  }

  updateLinkedCorrection(correctionId: number, updateDto: PaymentAccountCorrectionLinkingUpdateDTO) {
    this.pageStore.updateLinkedCorrection(correctionId, updateDto).pipe(
      take(1),
      tap(_ => this.showSuccessMessageAfterUpdate()),
      catchError(err => this.showErrorMessage(err.error)),
      untilDestroyed(this)
    ).subscribe();
  }

  private showSuccessMessageAfterUpdate(): void {
    this.success$.next(true);
    setTimeout(() => this.success$.next(false), 4000);
  }
}
