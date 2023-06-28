import {Injectable} from '@angular/core';
import {combineLatest, Observable, of} from 'rxjs';
import {
  CallFundRateDTO,
  CallService,
  ExpenditureCoFinancingBreakdownDTO,
  ExpenditureCostCategoryBreakdownDTO,
  ExpenditureLumpSumBreakdownDTO,
  ExpenditureUnitCostBreakdownDTO,
  ExpenditureInvestmentBreakdownDTO,
  ProjectPartnerReportExpenditureCostsService,
  ProjectPartnerReportFinancialOverviewService,
  ProjectPartnerReportUnitCostDTO,
} from '@cat/api';

import {PartnerReportDetailPageStore} from '@project/project-application/report/partner-report-detail-page/partner-report-detail-page-store.service';
import {filter, map, switchMap, tap} from 'rxjs/operators';
import {Log} from '@common/utils/log';
import {ProjectStore} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import CategoryEnum = ProjectPartnerReportUnitCostDTO.CategoryEnum;

@Injectable({providedIn: 'root'})
export class PartnerReportFinancialOverviewStoreService {

  perCoFinancing$: Observable<ExpenditureCoFinancingBreakdownDTO>;
  perCostCategory$: Observable<ExpenditureCostCategoryBreakdownDTO>;
  perLumpSum$: Observable<ExpenditureLumpSumBreakdownDTO>;
  perUnitCost$: Observable<ExpenditureUnitCostBreakdownDTO>;
  perInvestment$: Observable<ExpenditureInvestmentBreakdownDTO>;
  callFunds$: Observable<CallFundRateDTO[]>;
  allowedCostCategories$: Observable<Map<ProjectPartnerReportUnitCostDTO.CategoryEnum | 'LumpSum' | 'UnitCost', boolean>>;

  constructor(
    private financialOverviewService: ProjectPartnerReportFinancialOverviewService,
    private partnerReportDetailPageStore: PartnerReportDetailPageStore,
    private projectStore: ProjectStore,
    private callService: CallService,
    private partnerReportExpenditureCostsService: ProjectPartnerReportExpenditureCostsService,
  ) {
    this.perCoFinancing$ = this.perCoFinancing();
    this.perCostCategory$ = this.perCostCategory();
    this.perLumpSum$ = this.perLumpSum();
    this.perUnitCost$ = this.perUnitCost();
    this.perInvestment$ = this.perInvestment();
    this.callFunds$ = this.callFunds();
    this.allowedCostCategories$ = this.allowedCostCategories();
  }

  private perCoFinancing(): Observable<ExpenditureCoFinancingBreakdownDTO> {
    return combineLatest([
      this.partnerReportDetailPageStore.partnerId$,
      this.partnerReportDetailPageStore.partnerReportId$,
    ])
      .pipe(
        switchMap(([partnerId, reportId]) =>
          this.financialOverviewService.getCoFinancingBreakdown(partnerId as number, reportId)
        ),
        tap(data => Log.info('Fetched overview breakdown per co-financing', this, data)),
      );
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

  private perLumpSum(): Observable<ExpenditureLumpSumBreakdownDTO> {
    return combineLatest([
      this.partnerReportDetailPageStore.partnerId$,
      this.partnerReportDetailPageStore.partnerReportId$,
    ])
      .pipe(
        switchMap(([partnerId, reportId]) =>
          this.financialOverviewService.getLumpSumBreakdown(partnerId as number, reportId)
        ),
        tap(data => Log.info('Fetched overview breakdown per lump sum', this, data)),
      );
  }

  private perUnitCost(): Observable<ExpenditureUnitCostBreakdownDTO> {
    return combineLatest([
      this.partnerReportDetailPageStore.partnerId$,
      this.partnerReportDetailPageStore.partnerReportId$,
    ])
      .pipe(
        switchMap(([partnerId, reportId]) =>
          this.financialOverviewService.getUnitCostBreakdown(partnerId as number, reportId)
        ),
        tap(data => Log.info('Fetched overview breakdown per unit cost', this, data)),
      );
  }

  private perInvestment(): Observable<ExpenditureInvestmentBreakdownDTO> {
    return combineLatest([
      this.partnerReportDetailPageStore.partnerId$,
      this.partnerReportDetailPageStore.partnerReportId$,
    ])
      .pipe(
        switchMap(([partnerId, reportId]) =>
          this.financialOverviewService.getInvestmentsBreakdown(partnerId as number, reportId)
        ),
        tap(data => Log.info('Fetched overview breakdown per investment', this, data)),
      );
  }

  private callFunds(): Observable<CallFundRateDTO[]> {
    return combineLatest([
      this.projectStore.projectCall$,
    ])
      .pipe(
        map(([call]) => call.callId),
        switchMap(callId => this.callService.getCallById(callId)),
        map(call => call.funds),
        tap(data => Log.info('Fetched call funds for financial overview', this, data)),
      );
  }

  private allowedCostCategories(): Observable<Map<CategoryEnum | 'LumpSum' | 'UnitCost', boolean>> {
    return combineLatest([
      this.projectStore.projectCall$,
      this.partnerReportDetailPageStore.partnerId$,
      this.partnerReportDetailPageStore.partnerReportId$,
    ])
      .pipe(
        filter(([call, partnerId, reportId]) => partnerId != null && reportId != null),
        switchMap(([call, partnerId, reportId]) => combineLatest([
          this.callService.getAllowedRealCosts(call.callId),
          of(call.flatRates),
          this.partnerReportExpenditureCostsService.getAvailableLumpSums(Number(partnerId), reportId),
          of(call.lumpSums),
          this.partnerReportExpenditureCostsService.getAvailableUnitCosts(Number(partnerId), reportId),
        ])),
        map(([allowedRealCosts, flatRates, lumpSums, lumpSumsFromCall, unitCosts]) => {
          const setting = new Map<ProjectPartnerReportUnitCostDTO.CategoryEnum | 'LumpSum' | 'UnitCost', boolean>();

          setting.set(CategoryEnum.StaffCosts,
            allowedRealCosts.allowRealStaffCosts || flatRates.staffCostFlatRateSetup != null || this.anySingleCostCategory(unitCosts, CategoryEnum.StaffCosts));
          setting.set(CategoryEnum.OfficeAndAdministrationCosts,
            flatRates.officeAndAdministrationOnStaffCostsFlatRateSetup != null || flatRates.officeAndAdministrationOnDirectCostsFlatRateSetup != null);
          setting.set(CategoryEnum.TravelAndAccommodationCosts,
            allowedRealCosts.allowRealTravelAndAccommodationCosts || flatRates.travelAndAccommodationOnStaffCostsFlatRateSetup != null || this.anySingleCostCategory(unitCosts, CategoryEnum.TravelAndAccommodationCosts));
          setting.set(CategoryEnum.ExternalCosts,
            allowedRealCosts.allowRealExternalExpertiseAndServicesCosts || this.anySingleCostCategory(unitCosts, CategoryEnum.ExternalCosts));
          setting.set(CategoryEnum.EquipmentCosts,
            allowedRealCosts.allowRealEquipmentCosts || this.anySingleCostCategory(unitCosts, CategoryEnum.EquipmentCosts));
          setting.set(CategoryEnum.InfrastructureCosts,
            allowedRealCosts.allowRealInfrastructureCosts || this.anySingleCostCategory(unitCosts, CategoryEnum.InfrastructureCosts));
          setting.set(CategoryEnum.Multiple,
            flatRates.otherCostsOnStaffCostsFlatRateSetup != null);
          setting.set('LumpSum', !!lumpSums.length || !!lumpSumsFromCall.length);
          setting.set('UnitCost', !!unitCosts.length);

          return setting;
        }),
        tap(data => Log.info('Fetched call budget visibility settings', this, data)),
      );
  }

  private anySingleCostCategory(unitCosts: Array<ProjectPartnerReportUnitCostDTO>, costCategory: CategoryEnum): boolean {
    return unitCosts.some(unitCost => unitCost.category === costCategory);
  }
}

export interface CostCategory {
  category: ProjectPartnerReportUnitCostDTO.CategoryEnum;
  allowed: boolean;
}
