import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {Observable} from 'rxjs';
import {ProjectPartnerBudgetPerPeriodDTO} from '@cat/api';
import {map} from 'rxjs/operators';
import {NumberService} from '@common/services/number.service';
import {APPLICATION_FORM} from '@project/common/application-form-model';
import {TableConfig} from '@common/directives/table-config/TableConfig';
import {ProjectBudgetPeriodPageStore} from '@project/budget/budget-page-per-period/budget-period-page.store';


@Component({
  selector: 'app-budget-page-partner-per-period',
  templateUrl: './budget-page-partner-per-period.component.html',
  styleUrls: ['./budget-page-partner-per-period.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class BudgetPagePartnerPerPeriodComponent {

  readonly PERIOD_PREPARATION: number = 0;
  readonly PERIOD_CLOSURE: number = 255;

  @Input() tableConfig: TableConfig[];
  @Input() projectPeriodNumbers: number[];

  APPLICATION_FORM = APPLICATION_FORM;

  data$: Observable<{
    projectPartnersBudgetPerPeriods: ProjectPartnerBudgetPerPeriodDTO[],
    totalEligibleBudget: number,
    totalPercent: number,
    periodTotalBudgets: number[],
    periodTotalBudgetPercentages: number[]
    periodsAvailable: boolean
  }>;

  constructor(
    private budgetPeriodStore: ProjectBudgetPeriodPageStore,
  ) {
    this.data$ = this.budgetPeriodStore.projectPartnersBudgetPerPeriods$
      .pipe(
        map((projectPartnersBudgetPerPeriods) => {
          const periodTotalBudgets = this.projectPeriodNumbers.map(periodNumber => this.calculateTotalBudgetPerPeriod(periodNumber, projectPartnersBudgetPerPeriods));
          const totalEligibleBudget = NumberService.sum(projectPartnersBudgetPerPeriods.map(partner => partner.totalPartnerBudget));
          projectPartnersBudgetPerPeriods.sort((a, b) => a.partner.sortNumber - b.partner.sortNumber);
          return {
            projectPartnersBudgetPerPeriods,
            totalEligibleBudget,
            totalPercent: 100,
            periodTotalBudgets,
            periodTotalBudgetPercentages: this.calculateTotalPeriodBudgetPercentages(periodTotalBudgets, totalEligibleBudget),
            periodsAvailable: this.projectPeriodNumbers.length > 0
          };
        }),
      );
  }

  calculateTotalBudgetPerPeriod(periodNumber: number, projectPartnersBudgetPerPeriods: ProjectPartnerBudgetPerPeriodDTO[]): number {
    return NumberService.sum(projectPartnersBudgetPerPeriods.flatMap(partner => partner.periodBudgets
      .filter((periodBudget: { periodNumber: number; }) => periodBudget.periodNumber === periodNumber)
      .map((periodBudget: { totalBudgetPerPeriod: any; }) => periodBudget.totalBudgetPerPeriod)));
  }

  calculateTotalPeriodBudgetPercentages(periodBudgetTotals: number[], totalEligibleBudget: number): number[] {
    const totals = periodBudgetTotals.map(periodTotalBudget => this.calculateTotalPeriodBudgetPercentage(periodTotalBudget, totalEligibleBudget));
    if (totals.length > 3) {
      const lastPeriod = totals[totals.length - 2];
      // to get 100 percent in total take last period from 100 instead of calculated values
      totals[totals.length - 2] = NumberService.minus(100, NumberService.truncateNumber(NumberService.minus(NumberService.sum(totals), lastPeriod), 2));
    }
    return totals;
  }

  calculateTotalPeriodBudgetPercentage(periodBudgetTotal: number, totalEligibleBudget: number): number {
    return NumberService.product([NumberService.divide(periodBudgetTotal ? periodBudgetTotal : null, totalEligibleBudget), 100]);
  }
}
