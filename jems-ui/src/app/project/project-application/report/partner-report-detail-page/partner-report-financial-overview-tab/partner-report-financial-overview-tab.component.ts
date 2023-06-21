import {ChangeDetectionStrategy, Component} from '@angular/core';
import {UntilDestroy} from '@ngneat/until-destroy';
import {FormService} from '@common/components/section/form/form.service';
import {combineLatest, Observable} from 'rxjs';
import {
  CallFundRateDTO,
  ExpenditureCoFinancingBreakdownDTO,
  ExpenditureCostCategoryBreakdownDTO,
  ExpenditureLumpSumBreakdownDTO,
  ExpenditureUnitCostBreakdownDTO,
  ExpenditureInvestmentBreakdownDTO,
  ProjectPartnerReportUnitCostDTO, ProjectPartnerReportSummaryDTO
} from '@cat/api';
import {map} from 'rxjs/operators';
import {
  PartnerReportFinancialOverviewStoreService
} from '@project/project-application/report/partner-report-detail-page/partner-report-financial-overview-tab/partner-report-financial-overview-store.service';
import CategoryEnum = ProjectPartnerReportUnitCostDTO.CategoryEnum;
import {
  PartnerReportDetailPageStore
} from '@project/project-application/report/partner-report-detail-page/partner-report-detail-page-store.service';

@UntilDestroy()
@Component({
  selector: 'jems-partner-report-financial-overview',
  templateUrl: './partner-report-financial-overview-tab.component.html',
  styleUrls: ['./partner-report-financial-overview-tab.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PartnerReportFinancialOverviewTabComponent {

  data$: Observable<{
    perCoFinancing: ExpenditureCoFinancingBreakdownDTO;
    perCostCategory: ExpenditureCostCategoryBreakdownDTO;
    perLumpSum: ExpenditureLumpSumBreakdownDTO;
    perInvestment: ExpenditureInvestmentBreakdownDTO;
    perUnitCost: ExpenditureUnitCostBreakdownDTO;
    funds: CallFundRateDTO[];
    allowedCostCategories: Map<CategoryEnum | 'LumpSum' | 'UnitCost', boolean>;
    isCertified: boolean;
  }>;

  constructor(
    private financialOverviewStore: PartnerReportFinancialOverviewStoreService,
    private partnerReportDetailPageStore: PartnerReportDetailPageStore,
  ) {
    this.data$ = combineLatest([
      financialOverviewStore.perCoFinancing$,
      financialOverviewStore.perCostCategory$,
      financialOverviewStore.perLumpSum$,
      financialOverviewStore.perInvestment$,
      financialOverviewStore.perUnitCost$,
      financialOverviewStore.callFunds$,
      financialOverviewStore.allowedCostCategories$,
      partnerReportDetailPageStore.reportStatus$,
    ]).pipe(
      map(([perCoFinancing, perCostCategory, perLumpSum, perInvestment, perUnitCost, funds, allowedCostCategories, reportStatus]: any) => ({
        perCoFinancing,
        perCostCategory,
        perLumpSum,
        perInvestment,
        perUnitCost,
        funds,
        allowedCostCategories,
        isCertified: reportStatus === ProjectPartnerReportSummaryDTO.StatusEnum.Certified,
      })),
    );
  }

}
