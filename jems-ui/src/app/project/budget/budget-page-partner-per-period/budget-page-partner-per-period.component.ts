import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {ProjectStore} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {combineLatest, Observable} from 'rxjs';
import {ProjectPartnerBudgetPerPeriodDTO} from '@cat/api';
import {ProjectPartnerDetailPageStore} from '@project/partner/project-partner-detail-page/project-partner-detail-page.store';
import {map, tap} from 'rxjs/operators';
import {NumberService} from '@common/services/number.service';
import {Alert} from '@common/components/forms/alert';


@Component({
  selector: 'app-budget-page-partner-per-period',
  templateUrl: './budget-page-partner-per-period.component.html',
  styleUrls: ['./budget-page-partner-per-period.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class BudgetPagePartnerPerPeriodComponent implements OnInit {

  private readonly PERIOD_PREPARATION = 0;
  private readonly PERIOD_CLOSURE: number = 255;

  Alert = Alert;
  displayedColumns: string[] = [];
  displayedFooterPercentColumns: string[] = [];
  totalEligibleBudget: number = 0;
  periodsPercentOfTotalBudgets: number[] = [];
  periodsTotalBudgets: Map<number, number> = new Map<number, number>();
  periodsAvailable: boolean = false;

  data$: Observable<{
    periods: number[],
    partners: ProjectPartnerBudgetPerPeriodDTO[]
  }>

  constructor(public projectStore: ProjectStore,
              private projectPartnerDetailPageStore: ProjectPartnerDetailPageStore) {

    this.data$ = combineLatest([this.projectPartnerDetailPageStore.periods$,  this.projectStore.projectPartnersBudgetPerPeriods$])
      .pipe(
        map(([periods, projectPartnersBudgetPerPeriods]) => ({
            periods: [this.PERIOD_PREPARATION, ...periods.map(period => period.number), this.PERIOD_CLOSURE],
            partners: projectPartnersBudgetPerPeriods
          })),
        tap((data: any) => this.getDisplayColumns(data.periods)),
        tap(data => this.periodsAvailable = data.periods.length > 0))
  }

  ngOnInit(): void {}

  getDisplayColumns(periods: number[]): void {
    this.displayedColumns = [];
    this.displayedColumns.push('partner', 'country');
    periods.forEach(period => this.displayedColumns.push("period" + period));
    this.displayedColumns.push('totalEligibleBudget');

    this.displayedFooterPercentColumns.push('percentOfTotalBudget');
    this.displayedFooterPercentColumns.push('blankCell');
    periods.forEach(period => this.displayedFooterPercentColumns.push("budgetPercent" + period));
    this.displayedFooterPercentColumns.push('budgetPercentTotal');
  }

  getPeriodTotalBudgetForPartner(partner: ProjectPartnerBudgetPerPeriodDTO, period: number): number {
    const budget = partner.periodBudgets.find(budget => budget.periodNumber === period);
    return budget ? budget.totalBudgetPerPeriod : 0;
  }

  calculateTotalBudgetPerPeriod(period: number, partners: ProjectPartnerBudgetPerPeriodDTO[]): number {
    const periodPartnersBudgets = partners.flatMap(partner => partner.periodBudgets
      .filter(periodBudget => periodBudget.periodNumber == period)
      .map(periodBudget => periodBudget.totalBudgetPerPeriod));
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

  calculateTotalPeriodBudgetPercentage(period: number): number {
    const periodTotalBudget = this.periodsTotalBudgets.get(period);
    const periodPercentOfTotalBudget = NumberService.divide(periodTotalBudget? periodTotalBudget : null, this.totalEligibleBudget);
    this.periodsPercentOfTotalBudgets.push(periodPercentOfTotalBudget);
    return periodPercentOfTotalBudget;
  }

  calculateTotalEligibleBudgetPercentage(){
    return NumberService.sum(this.periodsPercentOfTotalBudgets);
  }

}
