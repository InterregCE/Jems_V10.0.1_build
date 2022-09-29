import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {Observable} from 'rxjs';
import {PaymentDetailDTO, PaymentPartnerDTO, ProgrammePriorityDTO} from '@cat/api';
import {PaymentsToProjectPageStore} from '../payments-to-projects-page.store';
import {ActivatedRoute} from '@angular/router';
import {AbstractControl, FormArray, FormBuilder} from '@angular/forms';
import {PaymentsToProjectDetailPageConstants} from './payments-to-project-detail-page.constants';
import {PaymentsDetailPageStore} from './payments-detail-page.store';
import {tap} from 'rxjs/operators';
import {animate, state, style, transition, trigger} from '@angular/animations';

@Component({
  selector: 'jems-payments-to-project-detail-page',
  templateUrl: './payments-to-project-detail-page.component.html',
  styleUrls: ['./payments-to-project-detail-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  animations: [
    trigger('detailExpand', [
      state('collapsed', style({height: '0px', minHeight: '0'})),
      state('expanded', style({height: '*'})),
      transition('expanded <=> collapsed', animate('225ms cubic-bezier(0.4, 0.0, 0.2, 1)')),
    ]),
  ],
})
export class PaymentsToProjectDetailPageComponent implements OnInit {

  constants = PaymentsToProjectDetailPageConstants;
  tableData: AbstractControl[] = [];
  paymentId = this.activatedRoute.snapshot.params.paymentId;
  columnsToDisplay = ['partner', 'partnerName', 'amountApproved', 'addInstallment'];

  partnerPaymentsForm = this.formBuilder.group({
    partnerPayments: this.formBuilder.array([])
  });

  expandedElement: ProgrammePriorityDTO | null;

  data$: Observable<PaymentDetailDTO> = this.paymentToProjectsStore.payment$;

  constructor(private paymentToProjectsStore: PaymentsToProjectPageStore,
              private activatedRoute: ActivatedRoute,
              private formBuilder: FormBuilder,
              private paymentsDetailPageStore: PaymentsDetailPageStore) {
  }

  ngOnInit(): void {
    this.paymentToProjectsStore.payment$.pipe(
      tap(data => this.resetForm(data.partnerPayments))
    ).subscribe();
  }

  resetForm(partnerPayments: PaymentPartnerDTO[]) {
    this.partnerPayments.clear();
    partnerPayments.forEach(partnerPayment => this.addPartnerPayment(partnerPayment));
    partnerPayments.forEach(partnerPayment => {
      // TODO for each installment in the partnerPayment, call the addInstallment method. this is currently placed wrong for demo purposes
      // TODO find a way (probably with observables) to pass the installments of a payment and call the addInstallment method from the child component - payment-installment-table
      // this.addInstallment(partnerPayment)
    });
    this.tableData = [...this.partnerPayments.controls];
  }

  addPartnerPayment(partnerPayment: PaymentPartnerDTO): void {
    const group = this.formBuilder.group({
      partnerNumber: this.formBuilder.control(partnerPayment.partnerNumber),
      partnerType: this.formBuilder.control(partnerPayment.partnerType),
      partnerAbbreviation: this.formBuilder.control(partnerPayment.partnerAbbreviation),
      amountApproved: this.formBuilder.control(partnerPayment.amountApproved),
      installments: this.formBuilder.group({})
    });
    this.partnerPayments.push(group);
  }

  get partnerPayments(): FormArray {
    return this.partnerPaymentsForm.get(this.constants.FORM_CONTROL_NAMES.partnerPayments) as FormArray;
  }
}
