import {ChangeDetectionStrategy, Component} from '@angular/core';
import {UntilDestroy} from '@ngneat/until-destroy';
import {FormService} from '@common/components/section/form/form.service';
import {combineLatest, Observable} from 'rxjs';
import {
  CallFundRateDTO,
  ExpenditureCoFinancingBreakdownDTO,
  ExpenditureCostCategoryBreakdownDTO,
  ProjectPartnerReportUnitCostDTO
} from '@cat/api';
import {map} from 'rxjs/operators';
import {
  PartnerReportFinancialOverviewStoreService
} from '@project/project-application/report/partner-report-detail-page/partner-report-financial-overview-tab/partner-report-financial-overview-store.service';
import CategoryEnum = ProjectPartnerReportUnitCostDTO.CategoryEnum;

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
    funds: CallFundRateDTO[];
    allowedCostCategories: Map<CategoryEnum | 'LumpSum' | 'UnitCost', boolean>;
  }>;

  constructor(
    private financialOverviewStore: PartnerReportFinancialOverviewStoreService,
  ) {
    this.data$ = combineLatest([
      financialOverviewStore.perCoFinancing$,
      financialOverviewStore.perCostCategory$,
      financialOverviewStore.callFunds$,
      financialOverviewStore.allowedCostCategories$,
    ]).pipe(
      map(([perCoFinancing, perCostCategory, funds, allowedCostCategories]) => ({
        perCoFinancing,
        perCostCategory,
        funds,
        allowedCostCategories,
      })),
    );
  }

}
