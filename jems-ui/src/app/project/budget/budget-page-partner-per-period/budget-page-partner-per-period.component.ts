import {ChangeDetectionStrategy, Component} from '@angular/core';
import {ProjectStore} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {combineLatest, Observable} from 'rxjs';
import {ProjectPartnerBudgetPerPeriodDTO} from '@cat/api';
import {map, tap} from 'rxjs/operators';
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
  displayedColumns: string[];
  displayedFooterPercentColumns: string[];
  totalEligibleBudget: number;
  periodsPercentOfTotalBudgets: number[];
  periodsTotalBudgets: Map<number, number>;
  periodsAvailable: boolean;
  totalPercent: number;


  data$: Observable<{
    periodNumbers: number[],
    partners: ProjectPartnerBudgetPerPeriodDTO[],
    displayColumns: string[],
    footerColumns: string[]
  }>;

  constructor(public projectStore: ProjectStore, private projectPartnerStore: ProjectPartnerStore) {

    this.data$ = combineLatest([this.projectStore.projectPeriods$, this.projectPartnerStore.projectPartnersBudgetPerPeriods$])
      .pipe(
        map(([periods, projectPartnersBudgetPerPeriods]) => ({
          periodNumbers: [this.PERIOD_PREPARATION, ...periods.map(period => period.number), this.PERIOD_CLOSURE],
          partners: projectPartnersBudgetPerPeriods,
          displayColumns: ['partner', 'country', 'period0', ...periods.map(period => `period${period.number}`), 'period255', 'totalEligibleBudget'],
          footerColumns: ['percentOfTotalBudget', 'blankCell', 'budgetPercent0', ...periods.map(period => `budgetPercent${period.number}`), 'budgetPercent255', 'budgetPercentTotal']
        })),
        tap(data => this.periodsAvailable = data.periodNumbers.length > 0),
      );
    this.displayedColumns = [];
    this.displayedFooterPercentColumns = [];
    this.totalEligibleBudget = 0;
    this.periodsPercentOfTotalBudgets = [];
    this.periodsTotalBudgets = new Map<number, number>();
    this.periodsAvailable = false;
    this.totalPercent = 100;
  }

  getPeriodTotalBudgetForPartner(partner: ProjectPartnerBudgetPerPeriodDTO, period: number): number {
    const budget = partner.periodBudgets.find((periodBudget: { periodNumber: number; }) => periodBudget.periodNumber === period);
    return budget ? budget.totalBudgetPerPeriod : 0;
  }

  calculateTotalBudgetPerPeriod(period: number, partners: ProjectPartnerBudgetPerPeriodDTO[]): number {
    const periodPartnersBudgets = partners.flatMap(partner => partner.periodBudgets
      .filter((periodBudget: { periodNumber: number; }) => periodBudget.periodNumber === period)
      .map((periodBudget: { totalBudgetPerPeriod: any; }) => periodBudget.totalBudgetPerPeriod));
    const totalPeriodBudget = NumberService.sum(periodPartnersBudgets);
    this.periodsTotalBudgets.set(period, totalPeriodBudget);
    return totalPeriodBudget;
  }

  calculateTotalEligibleBudget(partners: ProjectPartnerBudgetPerPeriodDTO[]): number {
    this.totalEligibleBudget = NumberService.sum(
      partners.map(partner => partner.totalPartnerBudget)
    );
    return this.totalEligibleBudget;
  }

  calculateTotalPeriodBudgetPercentage(periodNumber: number): number {
    const periodTotalBudget = this.periodsTotalBudgets.get(periodNumber);
    const periodPercentOfTotalBudget = NumberService
      .product([100, NumberService
        .divide(periodTotalBudget ? periodTotalBudget : null, this.totalEligibleBudget)]);
    this.periodsPercentOfTotalBudgets.push(periodPercentOfTotalBudget);
    return periodPercentOfTotalBudget;
  }
}
