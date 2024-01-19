import {ChangeDetectionStrategy, Component} from '@angular/core';
import {combineLatest, Observable} from 'rxjs';
import {
  PaymentAccountDTO,
  PaymentAccountUpdateDTO,
} from '@cat/api';
import {AccountsPageStore} from '../../accounts-page.store';
import {catchError, map, take, tap} from 'rxjs/operators';
import {FormService} from '@common/components/section/form/form.service';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';

@UntilDestroy()
@Component({
  selector: 'jems-accounts-summary-tab',
  templateUrl: './accounts-summary-tab.component.html',
  styleUrls: ['./accounts-summary-tab.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [FormService]
})
export class AccountsSummaryTabComponent {

  data$: Observable<{
    accountDetail: PaymentAccountDTO;
    userCanView: boolean;
    userCanEdit: boolean;
  }>;

  summaryForm = this.formBuilder.group({
    nationalReference: this.formBuilder.control(''),
    technicalAssistance: this.formBuilder.control(''),
    submissionToSFCDate: this.formBuilder.control(''),
    sfcNumber: this.formBuilder.control(''),
    comment: this.formBuilder.control(''),
  });

  userCanEdit$: Observable<boolean>;

  constructor(
    public pageStore: AccountsPageStore,
    private formService: FormService,
    private formBuilder: FormBuilder,
    ) {
    this.userCanEdit$ = this.pageStore.userCanEdit$;
    this.data$ = combineLatest([
      this.pageStore.accountDetail$,
      this.pageStore.userCanEdit$,
      this.pageStore.userCanView$,
    ]).pipe(
      map(([accountDetail, userCanEdit, userCanView]) => ({
          accountDetail,
          userCanView,
          userCanEdit,
        })
      ),
      tap(data => this.resetForm(data.accountDetail)),
    );
    this.formService.init(this.summaryForm, this.userCanEdit$);
  }

  resetForm(accountDetail: PaymentAccountDTO) {
    this.summaryForm.get('nationalReference')?.setValue(accountDetail?.nationalReference ?? null);
    this.summaryForm.get('technicalAssistance')?.setValue(accountDetail?.technicalAssistance ?? 0.0);
    this.summaryForm.get('submissionToSFCDate')?.setValue(accountDetail?.submissionToSfcDate ?? null);
    this.summaryForm.get('sfcNumber')?.setValue(accountDetail?.sfcNumber ?? null);
    this.summaryForm.get('comment')?.setValue(accountDetail?.comment ?? null);

    this.setValidators();
  }

  setValidators() {
    this.summaryForm.get('nationalReference')?.setValidators([Validators.maxLength(50)]);
    this.summaryForm.get('sfcNumber')?.setValidators([Validators.maxLength(50)]);
    this.summaryForm.get('comment')?.setValidators([Validators.maxLength(5000)]);
  }

  get summary(): FormGroup {
    return this.summaryForm;
  }

  savePaymentAccount() {
    const formData = this.summaryForm.getRawValue();
    const dataToUpdate = this.prepareDataToUpdatePaymentApplication(formData);
    this.pageStore.updatePaymentAccountSummary(dataToUpdate).pipe(
      take(1),
      tap(() => this.formService.setSuccess('payments.accounts.detail.save.success')),
      catchError(err => this.formService.setError(err)),
      untilDestroyed(this)
    ).subscribe();
  }

  prepareDataToUpdatePaymentApplication(data: any): PaymentAccountUpdateDTO {
    return {
      nationalReference: data.nationalReference,
      technicalAssistance: data.technicalAssistance,
      submissionToSfcDate: data.submissionToSFCDate,
      sfcNumber: data.sfcNumber,
      comment: data.comment
    };
  }
}
