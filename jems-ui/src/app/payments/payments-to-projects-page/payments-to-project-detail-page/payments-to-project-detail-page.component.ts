import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {combineLatest, Observable, of, Subject} from 'rxjs';
import {OutputUser, PaymentDetailDTO, PaymentPartnerDTO, PaymentPartnerInstallmentDTO, UserDTO} from '@cat/api';
import {PaymentsToProjectPageStore} from '../payments-to-projects-page.store';
import {ActivatedRoute} from '@angular/router';
import {AbstractControl, FormArray, FormBuilder, FormGroup, Validators} from '@angular/forms';
import {PaymentsToProjectDetailPageConstants} from './payments-to-project-detail-page.constants';
import {PaymentsDetailPageStore} from './payments-detail-page.store';
import {catchError, map, take, tap} from 'rxjs/operators';
import {animate, state, style, transition, trigger} from '@angular/animations';
import {FormService} from '@common/components/section/form/form.service';
import {SecurityService} from '../../../security/security.service';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {HttpErrorResponse} from '@angular/common/http';
import {LocaleDatePipe} from "@common/pipe/locale-date.pipe";

@UntilDestroy()
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
  providers: [FormService]
})
export class PaymentsToProjectDetailPageComponent implements OnInit {

  constants = PaymentsToProjectDetailPageConstants;
  tableData: AbstractControl[] = [];
  paymentId = this.activatedRoute.snapshot.params.paymentId;
  columnsToDisplay = ['partner', 'partnerName', 'amountApproved', 'addInstallment'];
  updateInstallmentsError$ = new Subject<HttpErrorResponse | null>();
  updateInstallmentsSuccess$ = new Subject<boolean>();

  partnerPaymentsForm = this.formBuilder.group({
    id: '',
    projectCustomIdentifier: '',
    fundName: '',
    partnerPayments: this.formBuilder.array([this.formBuilder.group({
        installments: this.formBuilder.array([])
      })
    ])
  });

  currentUserDetails: UserDTO;
  data$: Observable<{
    paymentDetail: PaymentDetailDTO;
    currentUser: UserDTO;
  }>;

  constructor(private paymentToProjectsStore: PaymentsToProjectPageStore,
              private activatedRoute: ActivatedRoute,
              private formBuilder: FormBuilder,
              private formService: FormService,
              private paymentsDetailPageStore: PaymentsDetailPageStore,
              private securityService: SecurityService,
              private localeDatePipe: LocaleDatePipe) {
  }

  ngOnInit(): void {
      this.data$ = combineLatest([
      this.paymentsDetailPageStore.paymentDetail$,
      this.securityService.currentUserDetails,
    ])
      .pipe(
        map(([paymentDetail, currentUser]: any) => ({
          paymentDetail,
          currentUser,
        })),
        tap(data => this.currentUserDetails = data.currentUser),
        tap(data => this.resetForm(data.paymentDetail))
      );

    this.updateInstallmentsError$.pipe(
      tap(error => {
        if(error) {
          this.formService.setError(error);
          this.updateInstallmentsError$.next(null);
        } else {
          this.formService.setError(null);
        }
      }),
      untilDestroyed(this)
    ).subscribe();

      this.updateInstallmentsSuccess$.pipe(
        tap(() => this.formService.setSuccess('payments.detail.table.have.success'))
      )
       .subscribe();

    combineLatest([
      this.updateInstallmentsError$,
      this.updateInstallmentsSuccess$
    ])
      .pipe(
        map(([paymentDetail, currentUser]: any) => ({
          paymentDetail,
          currentUser,
        })),
        tap(data => this.currentUserDetails = data.currentUser),
        tap(data => this.resetForm(data.paymentDetail))
      );
    this.formService.init(this.partnerPaymentsForm, of(true));
  }

  resetForm(paymentDetail: PaymentDetailDTO) {

    this.partnerPaymentsForm.get('id')?.setValue(this.paymentId);
    this.partnerPaymentsForm.get('projectCustomIdentifier')?.setValue(paymentDetail.projectCustomIdentifier);
    this.partnerPaymentsForm.get('fundName')?.setValue(paymentDetail.fundName);

    this.partnerPayments.clear();

    paymentDetail.partnerPayments.forEach((partnerPayment, index) => this.addPartnerPayment(partnerPayment, index));
  }

  addInstallment(installment: PaymentPartnerInstallmentDTO | null, paymentIndex: number): void {
    const group = this.formBuilder.group({
      id: installment?.id ? installment.id : null,
      amountPaid: installment?.amountPaid ? this.formBuilder.control(installment.amountPaid) : this.computeAvailableSum(paymentIndex),
      paymentDate: this.formBuilder.control(installment?.paymentDate ? installment.paymentDate : null, Validators.required),
      comment: this.formBuilder.control(installment?.comment ? installment.comment : '', [Validators.maxLength(500)]),
      savePaymentInfo: this.formBuilder.control(installment?.savePaymentInfo ? installment.savePaymentInfo : false),
      savePaymentInfoUser: this.formBuilder.control(installment?.savePaymentInfoUser ? this.getOutputUserObject(installment.savePaymentInfoUser) : null),
      savePaymentDate: this.formBuilder.control(installment?.savePaymentDate ? installment.savePaymentDate : null),
      paymentConfirmed: this.formBuilder.control(installment?.paymentConfirmed ? installment.paymentConfirmed : false),
      paymentConfirmedUser: this.formBuilder.control(installment?.paymentConfirmedUser ? this.getOutputUserObject(installment.paymentConfirmedUser) : null),
      paymentConfirmedDate: this.formBuilder.control(installment?.paymentConfirmedDate ? installment.paymentConfirmedDate : null),
    });
    this.disableFieldsIfPaymentIsSaved(group);
    this.installmentsArray(paymentIndex).push(group);
  }

  addPartnerPayment(partnerPayment: PaymentPartnerDTO, paymentIndex: number): void {
    this.partnerPayments.push(this.formBuilder.group({
        partnerId: partnerPayment.partnerId,
        partnerNumber: this.formBuilder.control(partnerPayment.partnerNumber),
        partnerType: this.formBuilder.control(partnerPayment.partnerType),
        partnerAbbreviation: this.formBuilder.control(partnerPayment.partnerAbbreviation),
        amountApproved: this.formBuilder.control(partnerPayment.amountApproved),
        installments: this.formBuilder.array([])
      })
    );
    partnerPayment.installments.forEach( (installment: PaymentPartnerInstallmentDTO) => {
      this.addInstallment(installment, paymentIndex);
    });
  }

  get partnerPayments(): FormArray {
    return this.partnerPaymentsForm.get(this.constants.FORM_CONTROL_NAMES.partnerPayments) as FormArray;
  }

  installmentsArray(paymentIndex: number): FormArray {
    return this.partnerPayments.at(paymentIndex).get(this.constants.FORM_CONTROL_NAMES.installments) as FormArray;
  }

  updatePaymentInstallments(paymentId: number, paymentDetail: PaymentDetailDTO) {
    paymentDetail.partnerPayments.forEach((payment) => {
      this.paymentsDetailPageStore.updatePaymentInstallmentsPerPartner(paymentId, payment.partnerId, payment.installments).pipe(
        take(1),
        tap(() => this.updateInstallmentsSuccess$.next(true)),
        catchError(error => {
          this.updateInstallmentsError$.next(error);
          throw error;
        }),
        untilDestroyed(this)
      )
      .subscribe(
        (updatedInstallments) => {
          paymentDetail.partnerPayments.filter(partnerPayment => partnerPayment.partnerId = payment.partnerId)[0].installments = updatedInstallments;
        }
      );
    });
   this.paymentsDetailPageStore.savedPaymentDetail$.next(paymentDetail);
  }

  computeAvailableSum(paymentIndex: number): number {
    const installments = this.installmentsArray(paymentIndex);
    let amountPaid = 0;
    installments.value.forEach( (installment: PaymentPartnerInstallmentDTO) =>
    amountPaid += installment.amountPaid
    );

    return this.partnerPayments.at(paymentIndex).value.amountApproved - amountPaid;
  }

  setSavePaymentDate(isChecked: boolean, paymentIndex: number, installmentIndex: number) {
    if (isChecked) {
      this.installmentsArray(paymentIndex).at(installmentIndex).get('amountPaid')?.disable();
      this.installmentsArray(paymentIndex).at(installmentIndex).get('paymentDate')?.disable();
      this.installmentsArray(paymentIndex).at(installmentIndex).get('savePaymentDate')?.setValue(this.getFormattedCurrentLocaleDate());
      this.installmentsArray(paymentIndex).at(installmentIndex).get('savePaymentInfoUser')?.setValue(this.getOutputUserObject(this.currentUserDetails));
    } else {
      this.installmentsArray(paymentIndex).at(installmentIndex).get('amountPaid')?.enable();
      this.installmentsArray(paymentIndex).at(installmentIndex).get('paymentDate')?.enable();
      this.installmentsArray(paymentIndex).at(installmentIndex).get('savePaymentDate')?.setValue(null);
      this.installmentsArray(paymentIndex).at(installmentIndex).get('savePaymentInfoUser')?.setValue(null);
    }
  }

  disableFieldsIfPaymentIsSaved(group: FormGroup) {
    if (group.get('savePaymentInfo')?.value) {
      group.get('amountPaid')?.disable();
      group.get('paymentDate')?.disable();
    }
  }

  getOutputUserObject(userDetails: UserDTO | OutputUser): OutputUser {
    return {
      id: userDetails.id,
      email: userDetails.email,
      name: userDetails.name,
      surname: userDetails.surname
    } as OutputUser;
  }

  getFormattedCurrentLocaleDate() {
    const date = new Date();
    return date.toISOString().substring(0,10);
  }

  setConfirmPaymentDate(isChecked: boolean, paymentIndex: number, installmentIndex: number) {
    if (isChecked) {
      this.installmentsArray(paymentIndex).at(installmentIndex).get('paymentConfirmedDate')?.setValue(this.getFormattedCurrentLocaleDate());
      this.installmentsArray(paymentIndex).at(installmentIndex).get('paymentConfirmedUser')?.setValue(this.getOutputUserObject(this.currentUserDetails));
    } else {
      this.installmentsArray(paymentIndex).at(installmentIndex).get('paymentConfirmedDate')?.setValue(null);
      this.installmentsArray(paymentIndex).at(installmentIndex).get('paymentConfirmedUser')?.setValue(null);
    }
  }

  removeItem(paymentIndex: number, installmentIndex: number) {
    this.installmentsArray(paymentIndex).removeAt(installmentIndex);
    this.formService.setDirty(true);
  }

  isPaymentSaved(paymentIndex: number, installmentIndex: number): boolean {
    return this.installmentsArray(paymentIndex).at(installmentIndex).get('savePaymentInfo')?.value;
  }

  isPaymentConfirmed(paymentIndex: number, installmentIndex: number): boolean {
    return this.installmentsArray(paymentIndex).at(installmentIndex).get('paymentConfirmed')?.value;
  }

  addInstallmentButtonClicked(installment: PaymentPartnerInstallmentDTO | null, paymentIndex: number): void {
    this.addInstallment(installment, paymentIndex);
    this.formService.setDirty(true);
  }

  getFormattedDate(value: string): any {
    return this.localeDatePipe.transform(value);
  }
}
