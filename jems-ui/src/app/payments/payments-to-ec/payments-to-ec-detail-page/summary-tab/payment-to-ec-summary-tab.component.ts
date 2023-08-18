import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {combineLatest, Observable} from 'rxjs';
import {
  AccountingYearDTO,
  PaymentApplicationToEcDetailDTO,
  PaymentApplicationToEcUpdateDTO,
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
import {PaymentsToEcDetailPageStore} from '../payments-to-ec-detail-page-store.service';

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
              private paymentsToEcDetailPageStoreStore: PaymentsToEcDetailPageStore,
              private router: RoutingService,
  ) {
    this.userCanEdit$ = this.paymentsToEcDetailPageStoreStore.userCanEdit$;
  }

  ngOnInit(): void {
    this.data$ = combineLatest([
      this.paymentsToEcDetailPageStoreStore.paymentToEcDetail$,
      this.paymentsToEcDetailPageStoreStore.programmeFunds$,
      this.paymentsToEcDetailPageStoreStore.accountingYears$,
    ])
      .pipe(
        map(([paymentDetail, programmeFunds, accountingYears]: any) => ({
          paymentDetail,
          programmeFunds,
          accountingYears,
        })),
        tap((data) => this.initialPaymentToEcDetail = data.paymentDetail),
        tap(data => this.resetForm(data.paymentDetail))
      );

    this.formService.init(this.summaryForm, this.userCanEdit$);
  }

  resetForm(paymentDetail: PaymentApplicationToEcDetailDTO) {
    this.summaryForm.get(this.constants.FORM_CONTROL_NAMES.id)?.setValue(this.paymentId ? this.paymentId : null);
    this.summaryForm.get(this.constants.FORM_CONTROL_NAMES.programmeFund)?.setValue(this.getValueOrEmpty(paymentDetail?.paymentApplicationsToEcSummary?.programmeFund.id));
    this.summaryForm.get(this.constants.FORM_CONTROL_NAMES.accountingYear)?.setValue(this.getValueOrEmpty(paymentDetail?.paymentApplicationsToEcSummary?.accountingYear.id));

    this.setValidators();
  }

  private getValueOrEmpty(object: any) {
    return object ? object : '';
  }

  setValidators() {
    this.summaryForm.get(this.constants.FORM_CONTROL_NAMES.programmeFund)?.setValidators([Validators.required]);
    this.summaryForm.get(this.constants.FORM_CONTROL_NAMES.accountingYear)?.setValidators([Validators.required]);
  }

  get summary(): FormGroup {
    return this.summaryForm;
  }

  updatePaymentApplicationToEc() {
    const dataToUpdate = this.prepareDataForSave(this.summaryForm.getRawValue());

    if(dataToUpdate.id === null) {
      this.paymentsToEcDetailPageStoreStore.createPaymentToEc(dataToUpdate).pipe(
        take(1),
        tap(() => this.formService.setSuccess('payments.detail.table.have.success')),
        tap(data =>  this.redirectToPartnerDetailAfterCreate(dataToUpdate.id === null, data.id)),
        catchError(err => this.formService.setError(err)),
        untilDestroyed(this)
      ).subscribe();
    } else {
      this.paymentsToEcDetailPageStoreStore.updatePaymentToEcSummary(dataToUpdate).pipe(
        take(1),
        tap(() => this.formService.setSuccess('payments.detail.table.have.success')),
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

  prepareDataForSave(data: any): PaymentApplicationToEcUpdateDTO {
    return {
      id: data.id,
      programmeFundId: data.programmeFund,
      accountingYearId: data.accountingYear,
    };
  }
}
