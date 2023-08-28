import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {combineLatest, Observable} from 'rxjs';
import {
  AccountingYearDTO,
  PaymentApplicationToEcDetailDTO,
  PaymentApplicationToEcSummaryUpdateDTO,
  ProgrammeFundDTO
} from '@cat/api';
import {ActivatedRoute} from '@angular/router';
import {AbstractControl, FormBuilder, FormGroup, Validators} from '@angular/forms';
import {catchError, map, take, tap} from 'rxjs/operators';
import {FormService} from '@common/components/section/form/form.service';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {Alert} from '@common/components/forms/alert';
import {RoutingService} from '@common/services/routing.service';
import {PaymentsToEcSummaryTabConstants} from './payments-to-ec-summary-tab.constants';
import {PaymentsToEcDetailPageStore} from '../payment-to-ec-detail-page-store.service';

@UntilDestroy()
@Component({
  selector: 'jems-payments-application-to-ec-summary-tab',
  templateUrl: './payment-to-ec-summary-tab.component.html',
  styleUrls: ['./payment-to-ec-summary-tab.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [FormService]
})
export class PaymentToEcSummaryTabComponent implements OnInit {
  Alert = Alert;
  constants = PaymentsToEcSummaryTabConstants;
  tableData: AbstractControl[] = [];
  paymentId = this.activatedRoute.snapshot.params.paymentToEcId;
  summaryForm = this.formBuilder.group({
    id: '',
    programmeFund: this.formBuilder.control(''),
    accountingYear: this.formBuilder.control(''),
    nationalReference: this.formBuilder.control(''),
    technicalAssistanceEur: this.formBuilder.control(''),
    submissionToSFCDate: this.formBuilder.control(''),
    sfcNumber: this.formBuilder.control(''),
    comment: this.formBuilder.control(''),
  });
  initialPaymentToEcDetail: PaymentApplicationToEcDetailDTO;
  data$: Observable<{
    paymentDetail: PaymentApplicationToEcDetailDTO;
    programmeFunds: ProgrammeFundDTO[];
    accountingYears: AccountingYearDTO[];
  }>;

  userCanEdit$: Observable<boolean>;

  constructor(private activatedRoute: ActivatedRoute,
              private formBuilder: FormBuilder,
              private formService: FormService,
              private paymentsToEcDetailPageStore: PaymentsToEcDetailPageStore,
              private router: RoutingService,
  ) {
    this.userCanEdit$ = this.paymentsToEcDetailPageStore.userCanEdit$;
  }

  ngOnInit(): void {
    this.data$ = combineLatest([
      this.paymentsToEcDetailPageStore.paymentToEcDetail$,
      this.paymentsToEcDetailPageStore.programmeFunds$,
      this.paymentsToEcDetailPageStore.accountingYears$,
      this.paymentsToEcDetailPageStore.updatedPaymentApplicationStatus$
    ])
      .pipe(
        map(([paymentDetail, programmeFunds, accountingYears, updatedPaymentStatus]: any) => ({
          paymentDetail: this.getUpdatePayment(paymentDetail, updatedPaymentStatus),
          programmeFunds,
          accountingYears,
        })),
        tap((data) => this.initialPaymentToEcDetail = data.paymentDetail),
        tap(data => this.resetForm(data.paymentDetail))
      );

    this.formService.init(this.summaryForm, this.userCanEdit$);
  }

  getUpdatePayment(savedPayment: PaymentApplicationToEcDetailDTO, newStatus: PaymentApplicationToEcDetailDTO.StatusEnum): PaymentApplicationToEcDetailDTO {
    const updatedPayment = savedPayment;
    updatedPayment.status = newStatus;
    return updatedPayment;
  }

  resetForm(paymentDetail: PaymentApplicationToEcDetailDTO) {
    this.summaryForm.get(this.constants.FORM_CONTROL_NAMES.id)?.setValue(this.paymentId ? this.paymentId : null);
    this.summaryForm.get(this.constants.FORM_CONTROL_NAMES.programmeFund)?.setValue(paymentDetail?.paymentApplicationToEcSummary?.programmeFund.id ?? '');
    this.summaryForm.get(this.constants.FORM_CONTROL_NAMES.accountingYear)?.setValue(paymentDetail?.paymentApplicationToEcSummary?.accountingYear.id ?? '');

    this.summaryForm.get(this.constants.FORM_CONTROL_NAMES.nationalReference)?.setValue(paymentDetail?.paymentApplicationToEcSummary?.nationalReference ?? null);
    this.summaryForm.get(this.constants.FORM_CONTROL_NAMES.technicalAssistanceEur)?.setValue(paymentDetail?.paymentApplicationToEcSummary?.technicalAssistanceEur ?? 0.0);
    this.summaryForm.get(this.constants.FORM_CONTROL_NAMES.submissionToSFCDate)?.setValue(paymentDetail?.paymentApplicationToEcSummary?.submissionToSfcDate ?? null);
    this.summaryForm.get(this.constants.FORM_CONTROL_NAMES.sfcNumber)?.setValue(paymentDetail?.paymentApplicationToEcSummary?.sfcNumber ?? null);
    this.summaryForm.get(this.constants.FORM_CONTROL_NAMES.comment)?.setValue(paymentDetail?.paymentApplicationToEcSummary?.comment ?? null);

    this.setValidators();
    this.disableFieldsOnFinishStatus(paymentDetail);
  }

  private disableFieldsOnFinishStatus(paymentDetail: PaymentApplicationToEcDetailDTO) {
    if(paymentDetail?.status == PaymentApplicationToEcDetailDTO.StatusEnum.Finished) {
      this.summaryForm.get(this.constants.FORM_CONTROL_NAMES.programmeFund)?.disable();
      this.summaryForm.get(this.constants.FORM_CONTROL_NAMES.accountingYear)?.disable();
    }
  }

  setValidators() {
    this.summaryForm.get(this.constants.FORM_CONTROL_NAMES.programmeFund)?.setValidators([Validators.required]);
    this.summaryForm.get(this.constants.FORM_CONTROL_NAMES.accountingYear)?.setValidators([Validators.required]);
    this.summaryForm.get(this.constants.FORM_CONTROL_NAMES.nationalReference)?.setValidators([Validators.maxLength(50)]);
    this.summaryForm.get(this.constants.FORM_CONTROL_NAMES.sfcNumber)?.setValidators([Validators.maxLength(50)]);
    this.summaryForm.get(this.constants.FORM_CONTROL_NAMES.comment)?.setValidators([Validators.maxLength(5000)]);
  }

  get summary(): FormGroup {
    return this.summaryForm;
  }
  updatePaymentApplicationToEc() {
    const dataToUpdate = this.prepareDataForSave(this.summaryForm.getRawValue());

    if(dataToUpdate.id === null) {
      this.paymentsToEcDetailPageStore.createPaymentToEc(dataToUpdate).pipe(
        take(1),
        tap(() => this.formService.setSuccess('payments.to.ec.detail.save.success')),
        tap(data =>  this.redirectToPartnerDetailAfterCreate(dataToUpdate.id === null, data.id)),
        catchError(err => this.formService.setError(err)),
        untilDestroyed(this)
      ).subscribe();
    } else {
      this.paymentsToEcDetailPageStore.updatePaymentToEcSummary(dataToUpdate).pipe(
        take(1),
        tap(() => this.formService.setSuccess('payments.to.ec.detail.save.success')),
        catchError(err => this.formService.setError(err)),
        untilDestroyed(this)
      ).subscribe();
    }
  }

  redirectToPartnerDetailAfterCreate(isCreate: boolean, paymentId: number) {
    if(isCreate) {
      this.router.navigate(
        ['..', paymentId],
        {relativeTo: this.activatedRoute}
      );
    }
  }

  prepareDataForSave(data: any): PaymentApplicationToEcSummaryUpdateDTO {
    return {
      id: data.id,
      programmeFundId: data.programmeFund,
      accountingYearId: data.accountingYear,
      nationalReference: data.nationalReference,
      technicalAssistanceEur: data.technicalAssistanceEur,
      submissionToSfcDate: data.submissionToSFCDate,
      sfcNumber: data.sfcNumber,
      comment: data.comment
    };
  }
}
