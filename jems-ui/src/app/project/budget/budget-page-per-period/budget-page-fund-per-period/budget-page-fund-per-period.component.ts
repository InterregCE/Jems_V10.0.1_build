import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {TableConfig} from '@common/directives/table-config/TableConfig';
import {Observable} from 'rxjs';
import {ProjectPartnerFundsPerPeriodDTO} from '@cat/api';
import {map} from 'rxjs/operators';
import {NumberService} from '@common/services/number.service';
import {APPLICATION_FORM} from '@project/common/application-form-model';
import {ProjectBudgetPeriodPageStore} from '@project/budget/budget-page-per-period/budget-period-page.store';

@Component({
  selector: 'app-budget-page-funds-per-period',
  templateUrl: './budget-page-fund-per-period.component.html',
  styleUrls: ['./budget-page-fund-per-period.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class BudgetPageFundPerPeriodComponent {

  readonly PERIOD_PREPARATION: number = 0;
  readonly PERIOD_CLOSURE: number = 255;

  APPLICATION_FORM = APPLICATION_FORM;

  @Input() tableConfig: TableConfig[];
  @Input() projectPeriodNumbers: number[];

  data$: Observable<{
    euFundsDetails: ProjectPartnerFundsPerPeriodDTO[];
    nonEuFundsDetails: ProjectPartnerFundsPerPeriodDTO[];
    totalEuFundsPerPeriod: number[];
    totalFundsPerPeriod: [];
    totalEuFunds: number;
    totalFunds: number;
  }>;

  constructor(private budgetPeriodStore: ProjectBudgetPeriodPageStore) {
    this.data$ = this.budgetPeriodStore.projectBudgetFundsPerPeriod$
      .pipe(
        map((budgetFundsDetails): any => {
            const euFundsDetails = budgetFundsDetails.filter(budgetFundsDetail => budgetFundsDetail.fund.type !== 'OTHER');
            const nonEuFundsDetails = budgetFundsDetails.filter(budgetFundsDetail => budgetFundsDetail.fund.type === 'OTHER');
            const totalEuFundsPerPeriod = this.calculateTotalFundsPerPeriods(euFundsDetails, this.projectPeriodNumbers);
            const totalFundsPerPeriod = this.calculateTotalFundsPerPeriods(nonEuFundsDetails.concat(euFundsDetails), this.projectPeriodNumbers);
            return {
              euFundsDetails,
              nonEuFundsDetails,
              totalEuFundsPerPeriod,
              totalFundsPerPeriod,
              totalEuFunds: NumberService.sum(totalEuFundsPerPeriod),
              totalFunds: NumberService.sum(totalFundsPerPeriod),
              isAFPeriodsEnabled: this.projectPeriodNumbers.length > 0
            };
          }
        ));
  }

  getPeriodTranslation(periodNumber: number): string {
    if (periodNumber === this.PERIOD_PREPARATION) {
      return 'project.application.form.section.part.e.period.preparation';
    }

    if (periodNumber === this.PERIOD_CLOSURE) {
      return 'project.application.form.section.part.e.period.closure';
    }

    return 'project.partner.budget.table.period';
  }

  private calculateTotalFundsPerPeriods(budgetFundsDetails: ProjectPartnerFundsPerPeriodDTO[], projectPeriodNumbers: number[]): number[] {
    return projectPeriodNumbers.map(projectPeriod => NumberService.sum(budgetFundsDetails
      .flatMap(budgetFundsDetail => budgetFundsDetail.periodFunds
        .filter(periodFund => periodFund.periodNumber === projectPeriod)
        .map(periodFund => periodFund.totalFundsPerPeriod))));
  }
}
