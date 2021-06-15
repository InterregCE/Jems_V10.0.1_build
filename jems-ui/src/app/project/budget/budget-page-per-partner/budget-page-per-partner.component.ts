import {ChangeDetectionStrategy, Component} from '@angular/core';
import {
  ProgrammeFundDTO,
  ProjectPartnerBudgetCoFinancingDTO,
  ProjectPartnerCoFinancingOutputDTO,
  ProjectPartnerContributionDTO
} from '@cat/api';
import {tap} from 'rxjs/operators';
import {ProjectStore} from '../../project-application/containers/project-application-detail/services/project-store.service';
import {ActivatedRoute} from '@angular/router';
import {NumberService} from '../../../common/services/number.service';
import {ProjectPartnerDetailPageStore} from '../../partner/project-partner-detail-page/project-partner-detail-page.store';
import {ProjectPartnerBudgetModel} from './models/ProjectPartnerBudgetModel';
import {ProjectPartnerBudgetAndContribution} from './models/ProjectPartnerBudgetAndContribution';
import {take} from 'rxjs/internal/operators';

@Component({
  selector: 'app-budget-page-per-partner',
  templateUrl: './budget-page-per-partner.component.html',
  styleUrls: ['./budget-page-per-partner.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class BudgetPagePerPartnerComponent {
  displayedColumns: string[] = [];

  chosenProjectFunds$ = this.pageStore.callFunds$
    .pipe(
      tap((funds) => {
        this.getColumnsToDisplay(funds);
      }),
    );

  budgets$ = this.projectStore.getProjectCoFinancing()
    .pipe(
      tap((data: ProjectPartnerBudgetCoFinancingDTO[]) => this.calculateTotal(data)),
      tap((data: ProjectPartnerBudgetCoFinancingDTO[]) => this.constructBudgetColumns(data)),
      tap(() => this.calculateContributionSums(this.budgetColumns)),
      take(1)
    );

  budgetColumns: ProjectPartnerBudgetAndContribution[] = [];
  totalPublicContribution = 0;
  totalAutoPublicContribution = 0;
  totalPrivateContribution = 0;
  totalPartnerContribution = 0;
  totalEligibleBudget = 0;
  totalPercentage = 100;

  constructor(public projectStore: ProjectStore,
              private activatedRoute: ActivatedRoute,
              private pageStore: ProjectPartnerDetailPageStore) {
  }

  getBudgetAmountForFund(fund: ProgrammeFundDTO, budgets: ProjectPartnerBudgetModel[]): number {
    const filteredBudgets = budgets.filter(budget => budget.budgetFundId === fund.id);
    if (!filteredBudgets || filteredBudgets.length === 0) {
      return 0;
    }
    return filteredBudgets[0].budgetTotal;
  }

  getPercentageAmountForFund(fund: ProgrammeFundDTO, budgets: ProjectPartnerBudgetModel[]): number {
    const filteredBudgets = budgets.filter(budget => budget.budgetFundId === fund.id);
    if (!filteredBudgets || filteredBudgets.length === 0) {
      return 0;
    }
    return filteredBudgets[0].budgetPercentage;
  }

  getTotalBudgetAmountForFund(fund: ProgrammeFundDTO): number {
    let totalSum = 0;
    if (this.budgetColumns) {
      this.budgetColumns.forEach(column => {
        column.budgets
          .filter((budget: ProjectPartnerBudgetModel) => budget.budgetFundId === fund.id)
          .forEach((budget: ProjectPartnerBudgetModel) => {
            totalSum = totalSum + budget.budgetTotal;
          });
      });
    }
    return NumberService.truncateNumber(totalSum);
  }

  private constructBudgetColumns(budgets: ProjectPartnerBudgetCoFinancingDTO[]): void {
    this.budgetColumns = [];
    budgets.forEach((budget: ProjectPartnerBudgetCoFinancingDTO) => {
      this.budgetColumns.push({
        partnerSortNumber: budget?.partner.sortNumber,
        partnerRole: budget?.partner.role,
        partnerCountry: budget?.partner.country,
        budgets: this.getPartnerBudgetList(budget.projectPartnerCoFinancingAndContributionOutputDTO.finances, budget.total),
        publicContribution: this.getPartnerContributionTotal(budget.projectPartnerCoFinancingAndContributionOutputDTO.partnerContributions, ProjectPartnerContributionDTO.StatusEnum.Public),
        autoPublicContribution: this.getPartnerContributionTotal(budget.projectPartnerCoFinancingAndContributionOutputDTO.partnerContributions, ProjectPartnerContributionDTO.StatusEnum.AutomaticPublic),
        privateContribution: this.getPartnerContributionTotal(budget.projectPartnerCoFinancingAndContributionOutputDTO.partnerContributions, ProjectPartnerContributionDTO.StatusEnum.Private),
        totalContribution: this.getPartnerContributionTotal(budget.projectPartnerCoFinancingAndContributionOutputDTO.partnerContributions),
        totalEligibleBudget: NumberService.truncateNumber(budget.total),
        percentOfTotalBudget: this.getPercentOfTotalBudget(budget.total)
      });
    });
  }

  private getPartnerBudgetList(finances: ProjectPartnerCoFinancingOutputDTO[], totalBudget: number): ProjectPartnerBudgetModel[] {
    const budgets: ProjectPartnerBudgetModel[] = [];
    finances.forEach((finance: ProjectPartnerCoFinancingOutputDTO) => {
      if (finance.fund) {
        budgets.push({
          budgetFundId: finance.fund.id,
          budgetPercentage: finance.percentage,
          budgetTotal: NumberService.truncateNumber(NumberService.product([totalBudget, (finance.percentage / 100)]))
        });
      }
    });
    return budgets;
  }

  private getPartnerContributionTotal(partnerContributions: ProjectPartnerContributionDTO[], partnerStatus?: ProjectPartnerContributionDTO.StatusEnum): number {
    return NumberService.truncateNumber(NumberService.sum(partnerContributions
      .filter(source => source.status === partnerStatus || !partnerStatus)
      .map(item => item.amount ? item.amount : 0)
    ));
  }

  private calculateTotal(budgets: ProjectPartnerBudgetCoFinancingDTO[]): void {
    this.totalEligibleBudget = NumberService.sum(budgets.map(budget => budget.total));
  }

  private calculateContributionSums(budgets: ProjectPartnerBudgetAndContribution[]): void {
    this.totalPublicContribution = NumberService.sum(budgets.map(budget => budget.publicContribution));
    this.totalAutoPublicContribution = NumberService.sum(budgets.map(budget => budget.autoPublicContribution));
    this.totalPrivateContribution = NumberService.sum(budgets.map(budget => budget.privateContribution));
    this.totalPartnerContribution = NumberService.sum(budgets.map(budget => budget.totalContribution));
  }

  private getColumnsToDisplay(funds: ProgrammeFundDTO[]): void {
    this.displayedColumns.push('partner', 'country');
    funds.forEach(fund => {
      this.displayedColumns.push('budget' + fund.id, 'percentage' + fund.id);
    });
    this.displayedColumns.push('publicContribution', 'autoPublicContribution', 'privateContribution', 'totalContribution', 'totalEligibleBudget', 'percentOfTotalBudget');
  }

  private getPercentOfTotalBudget(budgetTotal: number): number {
    const perEligibleBudget = NumberService.divide(NumberService.truncateNumber(budgetTotal), this.totalEligibleBudget);
    return NumberService.truncateNumber(NumberService.product([100, perEligibleBudget]), 0);
  }

}
