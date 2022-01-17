import {ChangeDetectionStrategy, Component} from '@angular/core';
import {CallBudgetSettingsPageStore} from '../call-budget-settings-page-store.service';
import {FormBuilder} from '@angular/forms';
import {AllowedRealCostsDTO} from '@cat/api';
import {combineLatest, Observable} from 'rxjs';
import {FormService} from '@common/components/section/form/form.service';
import {catchError, map, take, tap} from 'rxjs/operators';

@Component({
  selector: 'app-call-allow-real-costs',
  templateUrl: './call-allow-real-costs.component.html',
  styleUrls: ['./call-allow-real-costs.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [FormService]
})
export class CallAllowedRealCostsComponent {

  form = this.formBuilder.group({
    allowRealStaffCosts: this.formBuilder.control(''),
    allowRealTravelAndAccommodationCosts: this.formBuilder.control(''),
    allowRealExternalExpertiseAndServicesCosts: this.formBuilder.control(''),
    allowRealEquipmentCosts: this.formBuilder.control(''),
    allowRealInfrastructureCosts: this.formBuilder.control('')
  });

  data$: Observable<{
    allowedRealCosts: AllowedRealCostsDTO;
    callIsPublished: boolean;
  }>;

  constructor(private pageStore: CallBudgetSettingsPageStore,
              private formBuilder: FormBuilder,
              private formService: FormService) {
    this.formService.init(this.form, this.pageStore.callIsEditable$);
    this.data$ = combineLatest([this.pageStore.allowedRealCosts$, this.pageStore.callIsPublished$])
      .pipe(
        map(([allowedRealCosts, callIsPublished]) => ({allowedRealCosts, callIsPublished})),
        tap(data => this.resetForm(data.allowedRealCosts))
      );
  }

  updateAllowedRealCosts(): void {
    this.pageStore.updateAllowedRealCosts(this.form.value)
      .pipe(
        take(1),
        tap(() => this.formService.setSuccess('call.allow.real.costs.saved')),
        catchError(err => this.formService.setError(err))
      ).subscribe();
  }

  resetForm(allowedRealCosts: AllowedRealCostsDTO): void {
    this.form.patchValue(allowedRealCosts);
  }
}
