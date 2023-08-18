import {ChangeDetectionStrategy, Component} from '@angular/core';
import {Observable} from 'rxjs';
import {PaymentApplicationToEcDTO, UserRoleDTO} from '@cat/api';
import {UntilDestroy} from '@ngneat/until-destroy';
import {PaymentsToEcDetailPageStore} from './payments-to-ec-detail-page-store.service';
import PaymentEcStatusEnum = PaymentApplicationToEcDTO.StatusEnum;

@UntilDestroy()
@Component({
  selector: 'jems-payments-to-ec-detail-page',
  templateUrl: './payments-to-ec-detail-page.component.html',
  styleUrls: ['./payments-to-ec-detail-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PaymentsToEcDetailPageComponent {

  paymentStatusEnum = PaymentEcStatusEnum;
  PermissionEnum = UserRoleDTO.PermissionsEnum;
  userCanEdit$: Observable<boolean>;

  constructor(public pageStore: PaymentsToEcDetailPageStore) { }
}
