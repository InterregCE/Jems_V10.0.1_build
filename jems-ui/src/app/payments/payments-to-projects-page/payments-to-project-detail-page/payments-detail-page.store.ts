import {Injectable} from '@angular/core';
import {merge, Observable, Subject} from 'rxjs';
import {PaymentDetailDTO, PaymentsAPIService,} from '@cat/api';
import {PermissionService} from '../../../security/permissions/permission.service';
import {RoutingService} from '@common/services/routing.service';
import {switchMap, tap} from 'rxjs/operators';
import {Log} from '@common/utils/log';

@Injectable({
  providedIn: 'root'
})
export class PaymentsDetailPageStore {
  public static PAYMENT_DETAIL_PATH = '/app/payments/';

  paymentDetail$: Observable<PaymentDetailDTO>;
  savedPaymentDetail$ = new Subject<PaymentDetailDTO>();

  constructor(private paymentApiService: PaymentsAPIService,
              private permissionService: PermissionService,
              private routingService: RoutingService) {
    this.paymentDetail$ = this.paymentDetail();
  }


  private paymentDetail(): Observable<PaymentDetailDTO> {
    const initialPaymentDetail$ = this.routingService.routeParameterChanges(PaymentsDetailPageStore.PAYMENT_DETAIL_PATH, 'paymentId')
      .pipe(
        switchMap((paymentId: number) => this.paymentApiService.getPaymentDetail(paymentId)),
        tap(data => Log.info('Fetched payment detail', this, data))
      );

    return merge(initialPaymentDetail$, this.savedPaymentDetail$);
  }

   updatePaymentInstallments(paymentId: number, paymentDetail: PaymentDetailDTO): Observable<PaymentDetailDTO> {
    return this.paymentApiService.updatePaymentInstallments(paymentId, paymentDetail);
  }
}
