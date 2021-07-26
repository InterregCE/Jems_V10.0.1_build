import {Pipe, PipeTransform} from '@angular/core';
import {FormArray} from '@angular/forms';
import {NumberService} from '@common/services/number.service';
import {ProjectPartnerBudgetConstants} from '@project/partner/project-partner-detail-page/project-partner-budget-tab/project-partner-budget/project-partner-budget.constants';

@Pipe({
  name: 'periodsTotal',
  pure: false
})
export class PeriodsTotalPipe implements PipeTransform {

  transform(items: FormArray, periodIndex: number): number {
    return items.controls.reduce((total, control) => {
      const periods = control.get(ProjectPartnerBudgetConstants.FORM_CONTROL_NAMES.budgetPeriods) as FormArray;
      const periodAmount = periods?.at(periodIndex - 1)?.get(ProjectPartnerBudgetConstants.FORM_CONTROL_NAMES.amount)?.value;
      return NumberService.sum([periodAmount || 0, total]);
    },                           0);
  }
}
