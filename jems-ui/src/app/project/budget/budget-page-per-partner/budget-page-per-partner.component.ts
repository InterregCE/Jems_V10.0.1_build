import {ChangeDetectionStrategy, Component} from '@angular/core';
import {
  PartnerBudgetPerFundDTO,
  ProgrammeFundDTO,
  ProjectCallSettingsDTO,
  ProjectPartnerBudgetPerFundDTO
} from '@cat/api';
import {map, startWith, tap} from 'rxjs/operators';
import {ProjectStore} from '../../project-application/containers/project-application-detail/services/project-store.service';
import {ActivatedRoute} from '@angular/router';
import {NumberService} from '@common/services/number.service';
import {ProjectPartnerDetailPageStore} from '../../partner/project-partner-detail-page/project-partner-detail-page.store';
import {ProjectPartnerBudgetModel} from './models/ProjectPartnerBudgetModel';
import {ProjectPartnerBudgetAndContribution} from './models/ProjectPartnerBudgetAndContribution';
import {FormVisibilityStatusService} from '@project/common/services/form-visibility-status.service';
import {APPLICATION_FORM} from '@project/common/application-form-model';
import {TableConfig} from '@common/directives/table-config/TableConfig';
import {combineLatest, Observable} from 'rxjs';
import CallTypeEnum = ProjectCallSettingsDTO.CallTypeEnum;

@Component({
  selector: 'jems-budget-page-per-partner',
  templateUrl: './budget-page-per-partner.component.html',
  styleUrls: ['./budget-page-per-partner.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class BudgetPagePerPartnerComponent {
  APPLICATION_FORM = APPLICATION_FORM;
  tableConfig$: Observable<TableConfig[]>;
  isCallTypeSpf$: Observable<boolean> = this.projectStore.projectCallType$.pipe(
    map(callType => callType === CallTypeEnum.SPF)
  );

  chosenProjectFunds$ = this.pageStore.callFunds$
    .pipe(
      startWith([]),
      map(funds => [...funds.values()].map(fund => fund.programmeFund))
    );
  budgetColumns: ProjectPartnerBudgetAndContribution[] = [];

  budgets$ = this.projectStore.getProjectBudgetPerFund()
    .pipe(
      tap((data: ProjectPartnerBudgetPerFundDTO[]) => this.constructBudgetColumns(data)),
      tap(() => this.setTotalValue(this.budgetColumns))
    );


  totalPublicContribution = 0;
  totalAutoPublicContribution = 0;
  totalPrivateContribution = 0;
  totalPartnerContribution = 0;
  totalEligibleBudget = 0;
  totalPercentage = 100;

  constructor(public projectStore: ProjectStore,
              private activatedRoute: ActivatedRoute,
              private pageStore: ProjectPartnerDetailPageStore,
              private visibilityStatusService: FormVisibilityStatusService) {
    this.tableConfig$ = combineLatest([this.chosenProjectFunds$, this.isCallTypeSpf$])
      .pipe(map( ([funds, isSpf]) => [
        {minInRem: 4, maxInRem: 4},  // partner id
        {minInRem: 7, maxInRem: 10}, // partner abbreviation
        {minInRem: 6, maxInRem: 12}, // country
        ...isSpf ? [{minInRem: 3}] : [],
        ...funds.flatMap(() => [{minInRem: 7}, {minInRem: 7}]),
        {minInRem: 5},
        {minInRem: 5},
        {minInRem: 5},
        ...this.visibilityStatusService.isVisible(APPLICATION_FORM.SECTION_B.BUDGET_AND_CO_FINANCING.PARTNER_ADD_NEW_CONTRIBUTION_ORIGIN)
          ? [{minInRem: 5}] : [],
        {minInRem: 9, maxInRem: 9}, // total eligible budget
        {minInRem: 4, maxInRem: 4}, // % of total eligible budget
        {minInRem: 5}
      ]));
  }

  getBudgetAmountForFund(fund: ProgrammeFundDTO, budgets: ProjectPartnerBudgetModel[]): number {
    const filteredBudgets = budgets.filter(budget => budget.budgetFundId === fund.id);
    if (!filteredBudgets || filteredBudgets.length === 0) {
      return 0;
    }
    return filteredBudgets[0].budgetTotal;
  }

  getBudgetPercentageByFund(fund: ProgrammeFundDTO, budgets: ProjectPartnerBudgetModel[]): number {
    const filteredBudgets = budgets.filter(budget => budget.budgetFundId === fund.id);
    if (!filteredBudgets || filteredBudgets.length === 0) {
      return 0;
    }
    return filteredBudgets[0].budgetPercentageOfTotal;
  }

  getTotalBudgetPercentageByFund(fund: ProgrammeFundDTO, budgets: ProjectPartnerBudgetModel[]): number {
    const filteredBudgets = budgets.filter(budget => budget.budgetFundId === fund.id);
    if (!filteredBudgets || filteredBudgets.length === 0) {
      return 0;
    }
    return filteredBudgets[0].budgetPercentage;
  }

  getPercentageAmountForFund(fund: ProgrammeFundDTO, budgets: ProjectPartnerBudgetModel[]): number {
    const filteredBudgets = budgets.filter(budget => budget.budgetFundId === fund.id);
    if (!filteredBudgets || filteredBudgets.length === 0) {
      return 0;
    }
    return filteredBudgets[0].budgetPercentage;
  }

  private constructBudgetColumns(budgets: ProjectPartnerBudgetPerFundDTO[]): void {
    this.budgetColumns = [];
    budgets.forEach((budget: ProjectPartnerBudgetPerFundDTO) => {
      this.budgetColumns.push({
        partnerSortNumber: budget?.partner?.sortNumber,
        partnerAbbreviation: budget?.partner?.abbreviation,
        partnerRole: budget?.partner?.role,
        partnerCountry: budget?.partner?.country,
        isPartnerActive: budget?.partner?.active,
        costType: budget.costType,
        budgets: this.getPartnerBudgetList(budget.budgetPerFund, budget.totalEligibleBudget),
        publicContribution: budget.publicContribution,
        autoPublicContribution: budget.autoPublicContribution,
        privateContribution: budget.privateContribution,
        totalContribution: budget.totalPartnerContribution,
        totalEligibleBudget: NumberService.truncateNumber(budget.totalEligibleBudget),
        percentOfTotalBudget: budget.percentageOfTotalEligibleBudget
      });
    });
  }

  private getPartnerBudgetList(finances: PartnerBudgetPerFundDTO[], totalBudget: number): ProjectPartnerBudgetModel[] {
    const budgets: ProjectPartnerBudgetModel[] = [];
    finances.forEach((finance: PartnerBudgetPerFundDTO) => {
      if (finance.fund) {
        budgets.push({
          budgetFundId: finance.fund.id,
          budgetPercentage: finance.percentage,
          budgetPercentageOfTotal: finance.percentageOfTotal,
          budgetTotal: finance.value
        });
      }
    });
    return budgets;
  }

  private setTotalValue(budgets: ProjectPartnerBudgetAndContribution[]): void {
    this.totalPublicContribution = budgets[budgets.length - 1]?.publicContribution;
    this.totalAutoPublicContribution = budgets[budgets.length - 1]?.autoPublicContribution;
    this.totalPrivateContribution = budgets[budgets.length - 1]?.privateContribution;
    this.totalPartnerContribution = budgets[budgets.length - 1]?.totalContribution;
    this.totalEligibleBudget = budgets[budgets.length - 1]?.totalEligibleBudget;
  }

}
