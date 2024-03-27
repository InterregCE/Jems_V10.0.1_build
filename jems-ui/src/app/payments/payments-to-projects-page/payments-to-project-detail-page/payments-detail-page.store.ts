import {Injectable} from '@angular/core';
import {merge, Observable, Subject} from 'rxjs';
import {
  AuditControlCorrectionDTO,
  AvailableCorrectionsForPaymentDTO,
  PaymentDetailDTO,
  PaymentPartnerDTO,
  PaymentsAPIService,
  ProjectAuditAndControlService,
} from '@cat/api';
import {RoutingService} from '@common/services/routing.service';
import {map, switchMap, tap} from 'rxjs/operators';
import {Log} from '@common/utils/log';

@Injectable({
  providedIn: 'root'
})
export class PaymentsDetailPageStore {
  public static PAYMENT_DETAIL_PATH = '/app/payments/';

  paymentDetail$: Observable<PaymentDetailDTO>;
  savedPaymentDetail$ = new Subject<PaymentDetailDTO>();
  availableCorrections$: Observable<AvailableCorrectionsForPaymentDTO[]>;

  constructor(private paymentApiService: PaymentsAPIService,
              private routingService: RoutingService,
              private auditControlService: ProjectAuditAndControlService) {
    this.paymentDetail$ = this.paymentDetail();
    this.availableCorrections$ = this.availableCorrections();
  }


  private paymentDetail(): Observable<PaymentDetailDTO> {
    const initialPaymentDetail$ = this.routingService.routeParameterChanges(PaymentsDetailPageStore.PAYMENT_DETAIL_PATH, 'paymentId')
      .pipe(
        switchMap((paymentId: number) => this.paymentApiService.getPaymentDetail(paymentId)),
        tap(data => Log.info('Fetched payment detail', this, data))
      );

    return merge(initialPaymentDetail$, this.savedPaymentDetail$);
  }

  updatePaymentInstallments(paymentId: number, partnerPayments: Array<PaymentPartnerDTO>): Observable<PaymentDetailDTO> {
    return this.paymentApiService.updatePaymentInstallments(paymentId, partnerPayments);
  }

  private availableCorrections(): Observable<AvailableCorrectionsForPaymentDTO[]> {
    return this.paymentDetail$.pipe(
      switchMap(payment => this.auditControlService.getAvailableCorrectionsForPayment(payment.id, payment.projectId))
    );
  }
}
