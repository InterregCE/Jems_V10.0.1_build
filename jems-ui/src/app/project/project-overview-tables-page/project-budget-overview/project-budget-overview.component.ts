import {ChangeDetectionStrategy, Component} from '@angular/core';
import {
  ProjectOverviewTablesPageStore
} from '@project/project-overview-tables-page/project-overview-tables-page-store.service';
import {ProjectCoFinancingByFundOverviewDTO, ProjectCoFinancingOverviewDTO} from '@cat/api';
import {Observable} from 'rxjs';
import {MatTableDataSource} from '@angular/material/table';
import {tap} from 'rxjs/operators';
import {Alert} from '@common/components/forms/alert';

@Component({
  selector: 'jems-project-budget-overview',
  templateUrl: './project-budget-overview.component.html',
  styleUrls: ['./project-budget-overview.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectBudgetOverviewComponent {
  Alert = Alert;
  displayedColumns = [
    'fundingSource', 'fundingAmount', 'coFinancingRate',
    'autoPublicContribution', 'otherPublicContribution', 'totalPublicContribution',
    'privateContribution', 'totalContribution'
  ];
  headerColumns = ['programmeFunding', 'contribution', 'total'];
  allColumns = [...this.displayedColumns, 'total'];

  dataSource: MatTableDataSource<ProjectCoFinancingByFundOverviewDTO>;
  projectCoFinancingOverview$: Observable<ProjectCoFinancingOverviewDTO>;
  multipleFundsAllowed$: Observable<boolean>;

  constructor(private pageStore: ProjectOverviewTablesPageStore) {
    this.projectCoFinancingOverview$ = pageStore.projectCoFinancingOverview$
      .pipe(
        tap(overview => this.dataSource = new MatTableDataSource(this.getFundOverviews(overview)))
      );
    this.multipleFundsAllowed$ = this.pageStore.callMultipleFundsAllowed$;
  }

  private getFundOverviews(overview: ProjectCoFinancingOverviewDTO): ProjectCoFinancingByFundOverviewDTO[] {
    const euFunds = this.getEuFunds(overview.fundOverviews);
    const otherFunds = this.getOtherFunds(overview.fundOverviews);
    const totalEu = {
      label: 'project.application.form.overview.budget.table.total.eu',
      fundingAmount: overview.totalEuFundingAmount,
      coFinancingRate: overview.averageEuFinancingRate,
      autoPublicContribution: overview.totalEuAutoPublicContribution,
      otherPublicContribution: overview.totalEuOtherPublicContribution,
      totalPublicContribution: overview.totalEuPublicContribution,
      privateContribution: overview.totalEuPrivateContribution,
      totalContribution: overview.totalEuContribution,
      totalFundAndContribution: overview.totalEuFundAndContribution,
    };
    const total = {
      label: 'project.application.form.overview.budget.table.total',
      fundingAmount: overview.totalFundingAmount,
      coFinancingRate: overview.averageCoFinancingRate,
      autoPublicContribution: overview.totalAutoPublicContribution,
      otherPublicContribution: overview.totalOtherPublicContribution,
      totalPublicContribution: overview.totalPublicContribution,
      privateContribution: overview.totalPrivateContribution,
      totalContribution: overview.totalContribution,
      totalFundAndContribution: overview.totalFundAndContribution,
    };
    return [...euFunds, totalEu as any, ...otherFunds, total];
  }

  private getEuFunds(funds: ProjectCoFinancingByFundOverviewDTO[]):  ProjectCoFinancingByFundOverviewDTO[] {
    return funds
      .filter(fund => fund.fundType !== ProjectCoFinancingByFundOverviewDTO.FundTypeEnum.OTHER)
      .sort((fund1, fund2) => this.euFundsComparator(fund1, fund2));
  }

  private getOtherFunds(funds: ProjectCoFinancingByFundOverviewDTO[]): ProjectCoFinancingByFundOverviewDTO[] {
    return funds
      .filter(fund => fund.fundType === ProjectCoFinancingByFundOverviewDTO.FundTypeEnum.OTHER)
      .sort((fund1, fund2) => fund1.fundId - fund2.fundId);
  }

  private euFundsComparator(f1: ProjectCoFinancingByFundOverviewDTO, f2: ProjectCoFinancingByFundOverviewDTO): number {
    if (f1.fundType === f2.fundType) {
      return f1.fundId - f2.fundId;
    }
    return f1.fundType > f2.fundType ? 1 : -1;
  }
}
