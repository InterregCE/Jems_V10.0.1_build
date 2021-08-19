import {ChangeDetectionStrategy, Component} from '@angular/core';
import {CallBudgetSettingsPageStore} from '../call-budget-settings-page-store.service';
import {FormBuilder} from '@angular/forms';
import {AllowRealCostsDTO} from '@cat/api';
import {Observable} from 'rxjs';
import {FormService} from '@common/components/section/form/form.service';
import {catchError, take, tap} from 'rxjs/operators';

@Component({
  selector: 'app-call-allow-real-costs',
  templateUrl: './call-allow-real-costs.component.html',
  styleUrls: ['./call-allow-real-costs.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [FormService]
})
export class CallAllowRealCostsComponent {

  form = this.formBuilder.group({
    allowRealStaffCosts: this.formBuilder.control(''),
    allowRealTravelAndAccommodationCosts: this.formBuilder.control(''),
    allowRealExternalExpertiseAndServicesCosts: this.formBuilder.control(''),
    allowRealEquipmentCosts: this.formBuilder.control(''),
    allowRealInfrastructureCosts: this.formBuilder.control('')
  });

  allowRealCosts$: Observable<AllowRealCostsDTO>;

  constructor(private pageStore: CallBudgetSettingsPageStore,
              private formBuilder: FormBuilder,
              private formService: FormService) {
    this.formService.init(this.form, this.pageStore.callIsEditable$);
    this.allowRealCosts$ = this.pageStore.allowRealCosts$
      .pipe(
        tap(allowRealCosts => this.resetForm(allowRealCosts))
      );
  }

  updateAllowRealCosts(): void {
    this.pageStore.updateAllowRealCosts(this.form.value)
      .pipe(
        take(1),
        tap(() => this.formService.setSuccess('call.allow.real.costs.saved')),
        catchError(err => this.formService.setError(err))
      ).subscribe();
  }

  resetForm(allowRealCosts: AllowRealCostsDTO): void {
    this.form.patchValue(allowRealCosts);
  }
}
