import {ChangeDetectionStrategy, Component} from '@angular/core';
import {FormService} from '@common/components/section/form/form.service';
import {combineLatest, Observable} from 'rxjs';
import {CallCostOptionDTO} from '@cat/api';
import {CallBudgetSettingsPageStore} from '../call-budget-settings-page-store.service';
import {FormBuilder} from '@angular/forms';
import {catchError, map, take, tap} from 'rxjs/operators';

@Component({
  selector: 'jems-call-draft-budget',
  templateUrl: './call-draft-budget.component.html',
  styleUrls: ['./call-draft-budget.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [FormService]
})
export class CallDraftBudgetComponent {

  form = this.formBuilder.group({
    projectDefinedUnitCostAllowed: this.formBuilder.control(''),
  });

  data$: Observable<{
    allowedCostOptions: CallCostOptionDTO;
    callIsPublished: boolean;
    isSPFCall: boolean;
  }>;

  constructor(private pageStore: CallBudgetSettingsPageStore,
              private formBuilder: FormBuilder,
              private formService: FormService) {
    this.formService.init(this.form, this.pageStore.callIsEditable$);
    this.data$ = combineLatest([this.pageStore.allowedCostOptions$, this.pageStore.callIsPublished$, this.pageStore.isSPFCall$])
      .pipe(
        map(([allowedCostOptions, callIsPublished, isSPFCall]) => ({allowedCostOptions, callIsPublished, isSPFCall})),
        tap(data => this.resetForm(data.allowedCostOptions))
      );
  }

  updateAllowedCostOptions(): void {
    this.pageStore.updateAllowedCostOptions(this.form.value)
      .pipe(
        take(1),
        tap(() => this.formService.setSuccess('call.allow.cost.options.saved')),
        catchError(err => this.formService.setError(err))
      ).subscribe();
  }

  resetForm(allowedCostOptions: CallCostOptionDTO): void {
    this.form.patchValue(allowedCostOptions);
  }
}
