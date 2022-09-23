import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {Observable} from 'rxjs';
import {PaymentDetailDTO} from '@cat/api';
import {PaymentsToProjectPageStore} from '../payments-to-projects-page.store';

@Component({
  selector: 'jems-payments-to-project-detail-page',
  templateUrl: './payments-to-project-detail-page.component.html',
  styleUrls: ['./payments-to-project-detail-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PaymentsToProjectDetailPageComponent implements OnInit {

  data$: Observable<PaymentDetailDTO> = this.paymentToProjectsStore.payment$;

  constructor(private paymentToProjectsStore: PaymentsToProjectPageStore) {
  }

  ngOnInit(): void {
    return;
  }
}
