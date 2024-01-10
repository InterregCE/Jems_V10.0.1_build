import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {BehaviorSubject, combineLatest, Observable, of} from 'rxjs';
import {APIError} from '@common/models/APIError';
import {PaymentApplicationToEcDetailDTO, PaymentApplicationToEcDTO} from '@cat/api';
import {catchError, finalize, map, take} from 'rxjs/operators';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {PaymentsToEcDetailPageStore} from '../payment-to-ec-detail-page-store.service';
import PaymentEcStatusEnum = PaymentApplicationToEcDTO.StatusEnum;

@UntilDestroy()
@Component({
  selector: 'jems-payment-to-ec-finalize-tab',
  templateUrl: './payment-to-ec-finalize-tab.component.html',
  styleUrls: ['./payment-to-ec-finalize-tab.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PaymentToEcFinalizeTabComponent {

  paymentStatusEnum = PaymentEcStatusEnum;
  finalizeError$ = new BehaviorSubject<APIError | null>(null);
  statusChangePending$ = new BehaviorSubject(false);

  data$: Observable<{
    paymentDetail: PaymentApplicationToEcDetailDTO;
    userCanView: boolean;
    userCanEdit: boolean;
    finalizationDisabled: boolean;
    canFinalize: boolean;
  }>;

  constructor(public pageStore: PaymentsToEcDetailPageStore) {
    this.data$ = combineLatest([
      this.pageStore.paymentToEcDetail$,
      this.pageStore.updatedPaymentApplicationStatus$,
      this.pageStore.userCanEdit$,
      this.pageStore.userCanView$,
      this.pageStore.paymentAvailableToReOpen$
    ]).pipe(
      map(([paymentDetail, paymentStatus, userCanEdit, userCanView, availableToReOpen]) => ({
          paymentDetail: this.getUpdatePayment(paymentDetail, paymentStatus),
          userCanView,
          userCanEdit,
          finalizationDisabled: this.isFinalizationDisabled(paymentStatus, userCanEdit),
          canFinalize: (paymentDetail.id && paymentDetail.status === PaymentEcStatusEnum.Draft && userCanEdit) || false,
        })
      )
    );
  }

  getUpdatePayment(savedPayment: PaymentApplicationToEcDetailDTO, newStatus: PaymentApplicationToEcDetailDTO.StatusEnum): PaymentApplicationToEcDetailDTO {
    const updatedPayment = savedPayment;
    updatedPayment.status = newStatus;
    return updatedPayment;
  }

  finalizePaymentApplication(paymentId: number) {
    this.statusChangePending$.next(true);

    this.pageStore.finalizePaymentApplicationToEc(paymentId).pipe(
      take(1),
      catchError((err) =>
        this.showErrorMessage(err)
      ),
      finalize(() => this.statusChangePending$.next(false)),
      untilDestroyed(this)
    ).subscribe();
  }

  public showErrorMessage(error: APIError): Observable<null> {
    this.finalizeError$.next(error);
    setTimeout(() => {
      this.finalizeError$.next(null);
    }, 4000);
    return of(null);
  }

  private isFinalizationDisabled(paymentStatus: PaymentApplicationToEcDetailDTO.StatusEnum, userCanEdit: boolean): boolean {
    return paymentStatus == this.paymentStatusEnum.Finished || !userCanEdit;
  }

}
