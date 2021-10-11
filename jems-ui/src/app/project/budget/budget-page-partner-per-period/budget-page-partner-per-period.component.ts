import {ChangeDetectionStrategy, Component} from '@angular/core';
import {ProjectStore} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {combineLatest, Observable} from 'rxjs';
import {ProjectPartnerBudgetPerPeriodDTO} from '@cat/api';
import {map} from 'rxjs/operators';
import {NumberService} from '@common/services/number.service';
import {Alert} from '@common/components/forms/alert';
import {ProjectPartnerStore} from '@project/project-application/containers/project-application-form-page/services/project-partner-store.service';
import {APPLICATION_FORM} from '@project/common/application-form-model';


@Component({
  selector: 'app-budget-page-partner-per-period',
  templateUrl: './budget-page-partner-per-period.component.html',
  styleUrls: ['./budget-page-partner-per-period.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class BudgetPagePartnerPerPeriodComponent {

  private readonly PERIOD_PREPARATION = 0;
  private readonly PERIOD_CLOSURE: number = 255;
  APPLICATION_FORM = APPLICATION_FORM;

  Alert = Alert;

  data$: Observable<{
    periodNumbers: number[],
    projectPartnersBudgetPerPeriods: ProjectPartnerBudgetPerPeriodDTO[],
    displayColumns: string[],
    footerColumns: string[],
    totalEligibleBudget: number,
    totalPercent: number,
    periodTotalBudgets: number[],
    periodTotalBudgetPercentages: number[]
    periodsAvailable: boolean
  }>;

  constructor(public projectStore: ProjectStore, private projectPartnerStore: ProjectPartnerStore) {

    this.data$ = combineLatest([this.projectStore.projectPeriods$, this.projectPartnerStore.projectPartnersBudgetPerPeriods$])
      .pipe(
        map(([periods, projectPartnersBudgetPerPeriods]) => {
          const periodTotalBudgets = [this.PERIOD_PREPARATION, ...periods.map(period => period.number), this.PERIOD_CLOSURE].map(periodNumber => this.calculateTotalBudgetPerPeriod(periodNumber, projectPartnersBudgetPerPeriods));
          const totalEligibleBudget = NumberService.sum(projectPartnersBudgetPerPeriods.map(partner => partner.totalPartnerBudget));
          projectPartnersBudgetPerPeriods.sort((a, b) => a.partner.sortNumber - b.partner.sortNumber);
          return {
              periodNumbers: [this.PERIOD_PREPARATION, ...periods.map(period => period.number), this.PERIOD_CLOSURE],
              projectPartnersBudgetPerPeriods,
              displayColumns: ['projectPartnerBudgetPerPeriod', 'country', 'period0', ...periods.map(period => `period${period.number}`), 'period255', 'totalEligibleBudget'],
              footerColumns: ['percentOfTotalBudget', 'blankCell', 'budgetPercent0', ...periods.map(period => `budgetPercent${period.number}`), 'budgetPercent255', 'budgetPercentTotal'],
              totalEligibleBudget,
              totalPercent: 100,
              periodTotalBudgets,
              periodTotalBudgetPercentages: periodTotalBudgets.map(periodTotalBudget => this.calculateTotalPeriodBudgetPercentage(periodTotalBudget, totalEligibleBudget)),
              periodsAvailable : periods.length > 0
          };
        }),
      );
  }

  calculateTotalBudgetPerPeriod(periodNumber: number, projectPartnersBudgetPerPeriods: ProjectPartnerBudgetPerPeriodDTO[]): number {
    return NumberService.sum(projectPartnersBudgetPerPeriods.flatMap(partner => partner.periodBudgets
      .filter((periodBudget: { periodNumber: number; }) => periodBudget.periodNumber === periodNumber)
      .map((periodBudget: { totalBudgetPerPeriod: any; }) => periodBudget.totalBudgetPerPeriod)));
  }

  calculateTotalPeriodBudgetPercentage(periodBudgetTotal: number, totalEligibleBudget: number): number {
    return NumberService.divide(periodBudgetTotal ? periodBudgetTotal : null, totalEligibleBudget);
  }
}
