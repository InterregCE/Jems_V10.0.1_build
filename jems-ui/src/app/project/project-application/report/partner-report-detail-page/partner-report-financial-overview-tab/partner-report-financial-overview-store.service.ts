import {Injectable} from '@angular/core';
import {combineLatest, Observable, of} from 'rxjs';
import {
  CallService,
  ExpenditureCostCategoryBreakdownDTO,
  ProjectPartnerReportExpenditureCostsService,
  ProjectPartnerReportFinancialOverviewService,
  ProjectPartnerReportUnitCostDTO,
} from '@cat/api';

import {PartnerReportDetailPageStore} from '@project/project-application/report/partner-report-detail-page/partner-report-detail-page-store.service';
import {map, switchMap, tap} from 'rxjs/operators';
import {Log} from '@common/utils/log';
import {ProjectStore} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import CategoryEnum = ProjectPartnerReportUnitCostDTO.CategoryEnum;

@Injectable({providedIn: 'root'})
export class PartnerReportFinancialOverviewStoreService {

  perCostCategory$: Observable<ExpenditureCostCategoryBreakdownDTO>;
  allowedCostCategories$: Observable<Map<ProjectPartnerReportUnitCostDTO.CategoryEnum | 'LumpSum' | 'UnitCost', boolean>>;

  constructor(
    private financialOverviewService: ProjectPartnerReportFinancialOverviewService,
    private partnerReportDetailPageStore: PartnerReportDetailPageStore,
    private projectStore: ProjectStore,
    private callService: CallService,
    private partnerReportExpenditureCostsService: ProjectPartnerReportExpenditureCostsService,
  ) {
    this.perCostCategory$ = this.perCostCategory();
    this.allowedCostCategories$ = this.allowedCostCategories();
  }

  private perCostCategory(): Observable<ExpenditureCostCategoryBreakdownDTO> {
    return combineLatest([
      this.partnerReportDetailPageStore.partnerId$,
      this.partnerReportDetailPageStore.partnerReportId$,
    ])
      .pipe(
        switchMap(([partnerId, reportId]) =>
          this.financialOverviewService.getCostCategoriesBreakdown(partnerId as number, reportId)
        ),
        tap(data => Log.info('Fetched overview breakdown per cost category', this, data)),
      );
  }

  private allowedCostCategories(): Observable<Map<CategoryEnum | 'LumpSum' | 'UnitCost', boolean>> {
    return combineLatest([
      this.projectStore.projectCall$,
      this.partnerReportDetailPageStore.partnerId$,
      this.partnerReportDetailPageStore.partnerReportId$,
    ])
      .pipe(
        switchMap(([call, partnerId, reportId]) => combineLatest([
          this.callService.getAllowedRealCosts(call.callId),
          of(call.flatRates),
          this.partnerReportExpenditureCostsService.getAvailableLumpSums(Number(partnerId), reportId),
          this.partnerReportExpenditureCostsService.getAvailableUnitCosts(Number(partnerId), reportId),
        ])),
        map(([allowedRealCosts, flatRates, lumpSums, unitCosts]) => {
          const setting = new Map<ProjectPartnerReportUnitCostDTO.CategoryEnum | 'LumpSum' | 'UnitCost', boolean>();

          setting.set(CategoryEnum.StaffCosts, allowedRealCosts.allowRealStaffCosts || flatRates.staffCostFlatRateSetup != null);
          setting.set(CategoryEnum.OfficeAndAdministrationCosts,
            flatRates.officeAndAdministrationOnStaffCostsFlatRateSetup != null || flatRates.officeAndAdministrationOnDirectCostsFlatRateSetup != null);
          setting.set(CategoryEnum.TravelAndAccommodationCosts, allowedRealCosts.allowRealTravelAndAccommodationCosts || flatRates.travelAndAccommodationOnStaffCostsFlatRateSetup != null);
          setting.set(CategoryEnum.ExternalCosts, allowedRealCosts.allowRealExternalExpertiseAndServicesCosts);
          setting.set(CategoryEnum.EquipmentCosts, allowedRealCosts.allowRealEquipmentCosts);
          setting.set(CategoryEnum.InfrastructureCosts, allowedRealCosts.allowRealInfrastructureCosts);
          setting.set(CategoryEnum.Multiple, flatRates.otherCostsOnStaffCostsFlatRateSetup != null);
          setting.set('LumpSum', !!lumpSums.length);
          setting.set('UnitCost', !!unitCosts.length);

          return setting;
        }),
        tap(data => Log.info('Fetched call budget visibility settings', this, data)),
      );
  }

}

export interface CostCategory {
  category: ProjectPartnerReportUnitCostDTO.CategoryEnum;
  allowed: boolean;
}
