import {ChangeDetectionStrategy, Component} from '@angular/core';
import {FormService} from '@common/components/section/form/form.service';
import {combineLatest, Observable} from 'rxjs';
import {
  CallFundRateDTO,
  CertificateCoFinancingBreakdownDTO,
  CertificateCostCategoryBreakdownDTO,
  CertificateLumpSumBreakdownDTO,
  CertificateUnitCostBreakdownDTO,
  PerPartnerCostCategoryBreakdownDTO,
  ProjectPartnerReportUnitCostDTO
} from '@cat/api';
import {map} from 'rxjs/operators';
import {
  ProjectReportFinancialOverviewStoreService
} from '@project/project-application/report/project-report/project-report-detail-page/project-report-financial-overview-tab/project-report-financial-overview-store.service';
import CategoryEnum = ProjectPartnerReportUnitCostDTO.CategoryEnum;

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
    allowedCostCategories: Map<CategoryEnum | 'LumpSum' | 'UnitCost', boolean>;
    funds: CallFundRateDTO[];
    perPartnerCostCategory: PerPartnerCostCategoryBreakdownDTO;
  }>;

  constructor(
    private financialOverviewStore: ProjectReportFinancialOverviewStoreService,
  ) {
    this.data$ = combineLatest([
      financialOverviewStore.perCoFinancing$,
      financialOverviewStore.perCostCategory$,
      financialOverviewStore.perLumpSum$,
      financialOverviewStore.perUnitCost$,
      financialOverviewStore.allowedCostCategories$,
      financialOverviewStore.callFunds$,
      financialOverviewStore.perPartnerCostCategory$
    ]).pipe(
      map(([perCoFinancing, perCostCategory, perLumpSum, perUnitCost, allowedCostCategories, funds, perPartnerCostCategory]: any) => ({
        perCoFinancing,
        perCostCategory,
        perLumpSum,
        perUnitCost,
        allowedCostCategories,
        funds,
        perPartnerCostCategory
      })),
    );
  }
}
