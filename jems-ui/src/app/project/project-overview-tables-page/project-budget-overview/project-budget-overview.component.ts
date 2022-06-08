import {ChangeDetectionStrategy, Component} from '@angular/core';
import {
  ProjectOverviewTablesPageStore
} from '@project/project-overview-tables-page/project-overview-tables-page-store.service';
import {ProjectCoFinancingByFundOverviewDTO, ProjectCoFinancingOverviewDTO} from '@cat/api';
import {combineLatest, Observable} from 'rxjs';
import {MatTableDataSource} from '@angular/material/table';
import {map, tap} from 'rxjs/operators';
import {Alert} from '@common/components/forms/alert';
import {NumberService} from '@common/services/number.service';
import { APPLICATION_FORM } from '@project/common/application-form-model';
import {FormVisibilityStatusService} from '@project/common/services/form-visibility-status.service';

@Component({
  selector: 'jems-project-budget-overview',
  templateUrl: './project-budget-overview.component.html',
  styleUrls: ['./project-budget-overview.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectBudgetOverviewComponent {
  APPLICATION_FORM = APPLICATION_FORM;
  Alert = Alert;
  displayedColumns: string[] = this.buildDisplayColumns();
  headerColumns = ['programmeFunding', 'contribution', 'total'];
  allColumns = [...this.displayedColumns, 'total'];

  dataSource: MatTableDataSource<ProjectCoFinancingByFundOverviewDTO>;
  projectCoFinancingOverview$: Observable<{
    overview: ProjectCoFinancingOverviewDTO;
    isCallSpf: boolean;
  }>;
  multipleFundsAllowed$: Observable<boolean>;

  constructor(private pageStore: ProjectOverviewTablesPageStore, private formVisibilityStatusService: FormVisibilityStatusService) {
    this.projectCoFinancingOverview$ = combineLatest([
      this.pageStore.projectCoFinancingOverview$,
      this.pageStore.isCallSpf$
    ])
      .pipe(
        map(([overview, isCallSpf]) => ({
          overview,
          isCallSpf
        })),
        tap(data => this.dataSource = new MatTableDataSource(this.getFundOverviews(data.overview, data.isCallSpf))),
      );
    this.multipleFundsAllowed$ = this.pageStore.callMultipleFundsAllowed$;
  }

  private buildDisplayColumns(): string[] {
    return [
      'fundingSource',
      'fundingAmount',
      'coFinancingRate',
      ...this.isAutomaticPublicContributionAllowed() ? ['autoPublicContribution'] : [],
      'otherPublicContribution',
      'totalPublicContribution',
      'privateContribution',
      'totalContribution'
    ];
  }

  isAutomaticPublicContributionAllowed(): boolean {
    return this.formVisibilityStatusService.isVisible(APPLICATION_FORM.SECTION_B.BUDGET_AND_CO_FINANCING.PARTNER_ADD_NEW_CONTRIBUTION_ORIGIN);
  }

  private getFundOverviews(overview: ProjectCoFinancingOverviewDTO, isCallSpf: boolean): ProjectCoFinancingByFundOverviewDTO[] {
    const datasource = [];
    const euFundsForSpf = this.getEuFunds(overview.projectSpfCoFinancing.fundOverviews);
    const otherFundsForSpf = this.getOtherFunds(overview.projectSpfCoFinancing.fundOverviews);
    const totalEuForSpf = {
      label: 'project.application.form.overview.budget.table.total.eu',
      fundingAmount: overview.projectSpfCoFinancing.totalEuFundingAmount,
      coFinancingRate: overview.projectSpfCoFinancing.averageEuFinancingRate,
      autoPublicContribution: overview.projectSpfCoFinancing.totalEuAutoPublicContribution,
      otherPublicContribution: overview.projectSpfCoFinancing.totalEuOtherPublicContribution,
      totalPublicContribution: overview.projectSpfCoFinancing.totalEuPublicContribution,
      privateContribution: overview.projectSpfCoFinancing.totalEuPrivateContribution,
      totalContribution: overview.projectSpfCoFinancing.totalEuContribution,
      totalFundAndContribution: overview.projectSpfCoFinancing.totalEuFundAndContribution,
    };

    const totalSpfBudget = {
      label: 'project.application.form.overview.budget.table.total.spf',
      fundingAmount: overview.projectSpfCoFinancing.totalFundingAmount,
      coFinancingRate: overview.projectSpfCoFinancing.averageCoFinancingRate,
      autoPublicContribution: overview.projectSpfCoFinancing.totalAutoPublicContribution,
      otherPublicContribution: overview.projectSpfCoFinancing.totalOtherPublicContribution,
      totalPublicContribution: overview.projectSpfCoFinancing.totalPublicContribution,
      privateContribution: overview.projectSpfCoFinancing.totalPrivateContribution,
      totalContribution: overview.projectSpfCoFinancing.totalContribution,
      totalFundAndContribution: overview.projectSpfCoFinancing.totalFundAndContribution,
    };

    if (isCallSpf) {
      datasource.push(
          {
            label:'project.application.form.overview.budget.table.spf.cofinacing',
            extended: true,
            allowedAutoContribution: this.isAutomaticPublicContributionAllowed()},
            ...euFundsForSpf, totalEuForSpf as any,
            ...otherFundsForSpf,
            totalSpfBudget,
          {
            label:'project.application.form.overview.budget.table.cofinacing',
            extended: true,
            allowedAutoContribution: this.isAutomaticPublicContributionAllowed()
          }
        );
    }

    const euFundsForManagement = this.getEuFunds(overview.projectManagementCoFinancing.fundOverviews);
    const otherFundsForManagement = this.getOtherFunds(overview.projectManagementCoFinancing.fundOverviews);

    const totalEuForManagement = {
      label: 'project.application.form.overview.budget.table.total.eu',
      fundingAmount: overview.projectManagementCoFinancing.totalEuFundingAmount,
      coFinancingRate: overview.projectManagementCoFinancing.averageEuFinancingRate,
      autoPublicContribution: overview.projectManagementCoFinancing.totalEuAutoPublicContribution,
      otherPublicContribution: overview.projectManagementCoFinancing.totalEuOtherPublicContribution,
      totalPublicContribution: overview.projectManagementCoFinancing.totalEuPublicContribution,
      privateContribution: overview.projectManagementCoFinancing.totalEuPrivateContribution,
      totalContribution: overview.projectManagementCoFinancing.totalEuContribution,
      totalFundAndContribution: overview.projectManagementCoFinancing.totalEuFundAndContribution,
    };

    const totalManagementBudget = {
      label: 'project.application.form.overview.budget.table.total.management',
      fundingAmount: overview.projectManagementCoFinancing.totalFundingAmount,
      coFinancingRate: overview.projectManagementCoFinancing.averageCoFinancingRate,
      autoPublicContribution: overview.projectManagementCoFinancing.totalAutoPublicContribution,
      otherPublicContribution: overview.projectManagementCoFinancing.totalOtherPublicContribution,
      totalPublicContribution: overview.projectManagementCoFinancing.totalPublicContribution,
      privateContribution: overview.projectManagementCoFinancing.totalPrivateContribution,
      totalContribution: overview.projectManagementCoFinancing.totalContribution,
      totalFundAndContribution: overview.projectManagementCoFinancing.totalFundAndContribution,
    };

    const total = {
      label: 'project.application.form.overview.budget.table.total',
      fundingAmount: totalSpfBudget.fundingAmount + totalManagementBudget.fundingAmount,
      coFinancingRate: NumberService.divide(
        NumberService.product([totalSpfBudget.fundingAmount + totalManagementBudget.fundingAmount, 100]),
        totalSpfBudget.totalFundAndContribution + totalManagementBudget.totalFundAndContribution
      ),
      autoPublicContribution: totalSpfBudget.autoPublicContribution + totalManagementBudget.autoPublicContribution,
      otherPublicContribution: totalSpfBudget.otherPublicContribution + totalManagementBudget.otherPublicContribution,
      totalPublicContribution: totalSpfBudget.totalPublicContribution + totalManagementBudget.totalPublicContribution,
      privateContribution: totalSpfBudget.privateContribution + totalManagementBudget.privateContribution,
      totalContribution: totalSpfBudget.totalContribution + totalManagementBudget.totalContribution,
      totalFundAndContribution: totalSpfBudget.totalFundAndContribution + totalManagementBudget.totalFundAndContribution,
    };

    datasource.push(...euFundsForManagement, totalEuForManagement as any, ...otherFundsForManagement);

    if (isCallSpf) {
      datasource.push(totalManagementBudget);
    }

    datasource.push(total);
    return datasource;
  }

  private getEuFunds(funds: ProjectCoFinancingByFundOverviewDTO[]):  ProjectCoFinancingByFundOverviewDTO[] {
    return funds
      .filter(fund => fund.fundType !== ProjectCoFinancingByFundOverviewDTO.FundTypeEnum.OTHER)
      .sort((fund1, fund2) => ProjectBudgetOverviewComponent.euFundsComparator(fund1, fund2));
  }

  private getOtherFunds(funds: ProjectCoFinancingByFundOverviewDTO[]): ProjectCoFinancingByFundOverviewDTO[] {
    return funds
      .filter(fund => fund.fundType === ProjectCoFinancingByFundOverviewDTO.FundTypeEnum.OTHER)
      .sort((fund1, fund2) => fund1.fundId - fund2.fundId);
  }

  private static euFundsComparator(f1: ProjectCoFinancingByFundOverviewDTO, f2: ProjectCoFinancingByFundOverviewDTO): number {
    if (f1.fundType === f2.fundType) {
      return f1.fundId - f2.fundId;
    }
    return f1.fundType > f2.fundType ? 1 : -1;
  }
}
