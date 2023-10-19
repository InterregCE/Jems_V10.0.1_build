import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {combineLatest, Observable} from 'rxjs';
import {
  AccountingYearDTO, PaymentApplicationToEcCreateDTO,
  PaymentApplicationToEcDetailDTO, PaymentApplicationToEcDTO,
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
  summaryForm = this.formBuilder.group({
    id: '',
    programmeFundId: this.formBuilder.control(''),
    accountingYearId: this.formBuilder.control(''),
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
  }>;

  isCreate: boolean;
  userCanEdit$: Observable<boolean>;
  availableAccountingYearsForFund: AccountingYearDTO[] = [];

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
      this.paymentsToEcDetailPageStore.updatedPaymentApplicationStatus$,
    ])
        .pipe(
            map(([paymentDetail, programmeFunds, updatedPaymentStatus]: any) => ({
              paymentDetail: this.getUpdatePayment(paymentDetail, updatedPaymentStatus),
              programmeFunds,
            })),
            tap((data) => this.initialPaymentToEcDetail = data.paymentDetail),
            tap(data => this.resetForm(data.paymentDetail)),
            tap(data => {
                  this.isCreate = !data.paymentDetail.id;
                  this.formService.setCreation(this.isCreate);
                }
            ),
        );

    this.formService.init(this.summaryForm, this.userCanEdit$);
  }

  getUpdatePayment(savedPayment: PaymentApplicationToEcDetailDTO, newStatus: PaymentApplicationToEcDetailDTO.StatusEnum): PaymentApplicationToEcDetailDTO {
    const updatedPayment = savedPayment;
    updatedPayment.status = newStatus;
    return updatedPayment;
  }

  resetForm(paymentDetail: PaymentApplicationToEcDetailDTO) {
    this.summaryForm.get(this.constants.FORM_CONTROL_NAMES.id)?.setValue(paymentDetail ? paymentDetail.id : null);
    this.summaryForm.get(this.constants.FORM_CONTROL_NAMES.programmeFundId)?.setValue(paymentDetail?.paymentApplicationToEcSummary?.programmeFund.id ?? '');
    this.summaryForm.get(this.constants.FORM_CONTROL_NAMES.accountingYearId)?.setValue(paymentDetail?.paymentApplicationToEcSummary?.accountingYear.id ?? '');

    this.summaryForm.get(this.constants.FORM_CONTROL_NAMES.nationalReference)?.setValue(paymentDetail?.paymentApplicationToEcSummary?.nationalReference ?? null);
    this.summaryForm.get(this.constants.FORM_CONTROL_NAMES.technicalAssistanceEur)?.setValue(paymentDetail?.paymentApplicationToEcSummary?.technicalAssistanceEur ?? 0.0);
    this.summaryForm.get(this.constants.FORM_CONTROL_NAMES.submissionToSFCDate)?.setValue(paymentDetail?.paymentApplicationToEcSummary?.submissionToSfcDate ?? null);
    this.summaryForm.get(this.constants.FORM_CONTROL_NAMES.sfcNumber)?.setValue(paymentDetail?.paymentApplicationToEcSummary?.sfcNumber ?? null);
    this.summaryForm.get(this.constants.FORM_CONTROL_NAMES.comment)?.setValue(paymentDetail?.paymentApplicationToEcSummary?.comment ?? null);

    this.setValidators();
    this.disableFieldsOnFinishStatus(paymentDetail);
  }

  private disableFieldsOnFinishStatus(paymentDetail: PaymentApplicationToEcDetailDTO) {
    if (paymentDetail?.status == PaymentApplicationToEcDetailDTO.StatusEnum.Finished) {
      this.summaryForm.get(this.constants.FORM_CONTROL_NAMES.programmeFundId)?.disable();
      this.summaryForm.get(this.constants.FORM_CONTROL_NAMES.accountingYearId)?.disable();
    }
  }

  setValidators() {
    this.summaryForm.get(this.constants.FORM_CONTROL_NAMES.programmeFundId)?.setValidators([Validators.required]);
    this.summaryForm.get(this.constants.FORM_CONTROL_NAMES.accountingYearId)?.setValidators([Validators.required]);
    this.summaryForm.get(this.constants.FORM_CONTROL_NAMES.nationalReference)?.setValidators([Validators.maxLength(50)]);
    this.summaryForm.get(this.constants.FORM_CONTROL_NAMES.sfcNumber)?.setValidators([Validators.maxLength(50)]);
    this.summaryForm.get(this.constants.FORM_CONTROL_NAMES.comment)?.setValidators([Validators.maxLength(5000)]);
  }

  get summary(): FormGroup {
    return this.summaryForm;
  }

  savePaymentApplication() {
    const formData = this.summaryForm.getRawValue();

    if (formData.id) {
      const dataToUpdate = this.prepareDataToUpdatePaymentApplication(formData);
      this.paymentsToEcDetailPageStore.updatePaymentToEcSummary(dataToUpdate).pipe(
          take(1),
          tap(() => this.formService.setSuccess('payments.to.ec.detail.save.success')),
          catchError(err => this.formService.setError(err)),
          untilDestroyed(this)
      ).subscribe();
    } else {
      const dataToCreatePaymentApplication = this.prepareDataToCreatePaymentApplication(formData);
      this.paymentsToEcDetailPageStore.createPaymentToEc(dataToCreatePaymentApplication).pipe(
          take(1),
          tap(() => this.formService.setSuccess('payments.to.ec.detail.save.success')),
          tap(data => this.redirectToPartnerDetailAfterCreate(data.id)),
          catchError(err => this.formService.setError(err)),
          untilDestroyed(this)
      ).subscribe();
    }
  }

    redirectToPartnerDetailAfterCreate(paymentId: number) {
      this.router.navigate(['/app/payments/paymentApplicationsToEc/', paymentId]);
    }

    prepareDataToUpdatePaymentApplication(data: any): PaymentApplicationToEcSummaryUpdateDTO {
      return {
        id: data.id,
        nationalReference: data.nationalReference,
        technicalAssistanceEur: data.technicalAssistanceEur,
        submissionToSfcDate: data.submissionToSFCDate,
        sfcNumber: data.sfcNumber,
        comment: data.comment
      };
    }

    prepareDataToCreatePaymentApplication(data: any): PaymentApplicationToEcCreateDTO {
      return {
        programmeFundId: data.programmeFundId,
        accountingYearId: data.accountingYearId,
        nationalReference: data.nationalReference,
        technicalAssistanceEur: data.technicalAssistanceEur,
        submissionToSfcDate: data.submissionToSFCDate,
        sfcNumber: data.sfcNumber,
        comment: data.comment
      };
    }

  fetchAvailableAccountingYearsForFund(fund: any) {
    const programmeFundId = fund.value;
    return this.paymentsToEcDetailPageStore.getProgrammeFundAvailableAccountingYears(programmeFundId).pipe(
        tap(data => this.availableAccountingYearsForFund = data),
        untilDestroyed(this)
    ).subscribe();
  }
}
