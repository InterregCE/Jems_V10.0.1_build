import {Injectable} from '@angular/core';
import {Observable, of} from 'rxjs';
import {PaymentPartnerDTO, PaymentsApiService, PaymentToProjectDTO} from '@cat/api';
import {PermissionService} from '../../../security/permissions/permission.service';
import {RoutingService} from '@common/services/routing.service';

@Injectable({
  providedIn: 'root'
})
export class PaymentsDetailPageStore {

  partnerPayments$: Observable<PaymentPartnerDTO[]>;

  constructor(private paymentApiService: PaymentsApiService,
              private permissionService: PermissionService,
              private routingService: RoutingService) {
    this.partnerPayments$ = this.partnerPayments();
  }


  private partnerPayments(): Observable<PaymentPartnerDTO[]> {

    const partnerPayment: PaymentPartnerDTO[] = [{
      id: 1,
      projectId: 1,
      partnerId: 1,
      partnerType: PaymentPartnerDTO.PartnerTypeEnum.LEADPARTNER,
      partnerNumber: 1,
      partnerAbbreviation: 'Abbreviated name',
      amountApproved: 123.45
    },
      {
        id: 2,
        projectId: 2,
        partnerId: 2,
        partnerType: PaymentPartnerDTO.PartnerTypeEnum.PARTNER,
        partnerNumber: 3,
        partnerAbbreviation: 'Abbreviated name 2',
        amountApproved: 12344.45
      }
    ];

    return of(partnerPayment);

  }
}
