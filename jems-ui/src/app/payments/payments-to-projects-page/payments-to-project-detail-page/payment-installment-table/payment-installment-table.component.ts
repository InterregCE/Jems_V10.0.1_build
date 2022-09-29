import {ChangeDetectionStrategy, Component, Input, OnInit} from '@angular/core';
import {PaymentPartnerDTO} from "@cat/api";
import {FormArray, FormBuilder, FormGroup} from "@angular/forms";
import {PaymentsToProjectDetailPageConstants} from "../payments-to-project-detail-page.constants";

@Component({
  selector: 'jems-payment-installment-table',
  templateUrl: './payment-installment-table.component.html',
  styleUrls: ['./payment-installment-table.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PaymentInstallmentTableComponent implements OnInit {

  constants = PaymentsToProjectDetailPageConstants;

  @Input()
  installmentsForm: FormGroup;

  constructor(private formBuilder: FormBuilder,) {
  }

  ngOnInit(): void {
  }

  //TODO - use in the definition of the method the Installment DTO (it did not exist at time of writing) or null. this method will be used to both add existing and a new installment.
  addInstallment(installment: PaymentPartnerDTO | null): void {
    if (installment) {
      const group = this.formBuilder.group({
        installmentNumber: this.formBuilder.control(installment.partnerNumber), //TODO replace the data in this form builder with data from Installment DTO
        amountPaid: this.formBuilder.control(installment.amountApproved),
        paymentDate: this.formBuilder.control(null),
        comment: this.formBuilder.control(installment.partnerAbbreviation),
        savePaymentInfo: this.formBuilder.control(true),
        userSavingPaymentInfo: this.formBuilder.control(installment.partnerAbbreviation),
        savePaymentDate: this.formBuilder.control(null),
        confirmPayment: this.formBuilder.control(true),
        userConfirmingPayment: this.formBuilder.control(installment.partnerAbbreviation),
        paymentConfirmationDate: this.formBuilder.control(null),
      });
      this.installments.push(group);
    } else {
      const group = this.formBuilder.group({
        installmentNumber: this.formBuilder.control(0), //TODO replace this 0 with the installment counter specific for that partner payment
        amountPaid: this.formBuilder.control(0),
        paymentDate: this.formBuilder.control(null),
        comment: this.formBuilder.control(''),
        savePaymentInfo: this.formBuilder.control(false),
        userSavingPaymentInfo: this.formBuilder.control(''),
        savePaymentDate: this.formBuilder.control(null),
        confirmPayment: this.formBuilder.control(false),
        userConfirmingPayment: this.formBuilder.control(''),
        paymentConfirmationDate: this.formBuilder.control(null),
      });
      this.installments.push(group);
    }
  }

  removeItem(installment: any): void {
    return;
  }

  get installments(): FormArray {
    return this.installmentsForm.get(this.constants.FORM_CONTROL_NAMES.installments) as FormArray;
  }

}
