import {ChangeDetectionStrategy, Component} from '@angular/core';
import {FormService} from '@common/components/section/form/form.service';
import {combineLatest, Observable} from 'rxjs';
import {
  CallFundRateDTO,
  CertificateCoFinancingBreakdownDTO,
  CertificateCostCategoryBreakdownDTO,
  CertificateInvestmentBreakdownDTO,
  CertificateLumpSumBreakdownDTO,
  CertificateUnitCostBreakdownDTO,
  PerPartnerCostCategoryBreakdownDTO,
  ProjectPartnerReportUnitCostDTO,
  ProjectReportDTO,
} from '@cat/api';
import {map} from 'rxjs/operators';
import {
  ProjectReportFinancialOverviewStoreService
} from '@project/project-application/report/project-report/project-report-detail-page/project-report-financial-overview-tab/project-report-financial-overview-store.service';
import CategoryEnum = ProjectPartnerReportUnitCostDTO.CategoryEnum;
import {
  ProjectReportDetailPageStore
} from '@project/project-application/report/project-report/project-report-detail-page/project-report-detail-page-store.service';

@Component({
  selector: 'jems-project-report-financial-overview-tab',
  templateUrl: './project-report-financial-overview-tab.component.html',
  styleUrls: ['./project-report-financial-overview-tab.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectReportFinancialOverviewTabComponent {

  data$: Observable<{
    perCoFinancing: CertificateCoFinancingBreakdownDTO;
    perCostCategory: CertificateCostCategoryBreakdownDTO;
    perLumpSum: CertificateLumpSumBreakdownDTO;
    perUnitCost: CertificateUnitCostBreakdownDTO;
    perInvestment: CertificateInvestmentBreakdownDTO;
    allowedCostCategories: Map<CategoryEnum | 'LumpSum' | 'UnitCost', boolean>;
    funds: CallFundRateDTO[];
    perPartnerCostCategory: PerPartnerCostCategoryBreakdownDTO;
    isVerified: boolean;
  }>;

  constructor(
    private financialOverviewStore: ProjectReportFinancialOverviewStoreService,
    private projectReportDetailPageStore: ProjectReportDetailPageStore,
  ) {
    this.data$ = combineLatest([
      financialOverviewStore.perCoFinancing$,
      financialOverviewStore.perCostCategory$,
      financialOverviewStore.perLumpSum$,
      financialOverviewStore.perUnitCost$,
      financialOverviewStore.perInvestment$,
      financialOverviewStore.allowedCostCategories$,
      financialOverviewStore.callFunds$,
      financialOverviewStore.perPartnerCostCategory$,
      projectReportDetailPageStore.reportStatus$,
    ]).pipe(
      map(([perCoFinancing, perCostCategory, perLumpSum, perUnitCost, perInvestment, allowedCostCategories, funds, perPartnerCostCategory, status]: any) => ({
        perCoFinancing,
        perCostCategory,
        perLumpSum,
        perUnitCost,
        perInvestment,
        allowedCostCategories,
        funds,
        perPartnerCostCategory,
        isVerified: status === ProjectReportDTO.StatusEnum.Finalized,
      })),
    );
  }
}
