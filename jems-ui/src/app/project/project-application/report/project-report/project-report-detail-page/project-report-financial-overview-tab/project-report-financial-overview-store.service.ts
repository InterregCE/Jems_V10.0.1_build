import {Injectable} from '@angular/core';
import {combineLatest, Observable, of} from 'rxjs';
import {
  CallFundRateDTO,
  CallService,
  CertificateCoFinancingBreakdownDTO,
  CertificateCostCategoryBreakdownDTO,
  CertificateInvestmentBreakdownDTO,
  CertificateLumpSumBreakdownDTO,
  CertificateUnitCostBreakdownDTO,
  PerPartnerCostCategoryBreakdownDTO,
  ProjectCostOptionService,
  ProjectPartnerReportUnitCostDTO,
  ProjectReportFinancialOverviewService,
} from '@cat/api';

import {filter, map, switchMap, tap} from 'rxjs/operators';
import {Log} from '@common/utils/log';
import {
  ProjectStore
} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {
  ProjectReportDetailPageStore
} from '@project/project-application/report/project-report/project-report-detail-page/project-report-detail-page-store.service';
import {ProgrammeUnitCost} from '@project/model/programmeUnitCost';
import {BudgetCostCategoryEnum, BudgetCostCategoryEnumUtils} from '@project/model/lump-sums/BudgetCostCategoryEnum';
import CategoryEnum = ProjectPartnerReportUnitCostDTO.CategoryEnum;

@Injectable({providedIn: 'root'})
export class ProjectReportFinancialOverviewStoreService {

  perCoFinancing$: Observable<CertificateCoFinancingBreakdownDTO>;
  perCostCategory$: Observable<CertificateCostCategoryBreakdownDTO>;
  perLumpSum$: Observable<CertificateLumpSumBreakdownDTO>;
  perUnitCost$: Observable<CertificateUnitCostBreakdownDTO>;
  perInvestment$: Observable<CertificateInvestmentBreakdownDTO>;
  callFunds$: Observable<CallFundRateDTO[]>;
  perPartnerCostCategory$: Observable<PerPartnerCostCategoryBreakdownDTO>;
  allowedCostCategories$: Observable<Map<ProjectPartnerReportUnitCostDTO.CategoryEnum | 'LumpSum' | 'UnitCost', boolean>>;

  constructor(
    private financialOverviewService: ProjectReportFinancialOverviewService,
    private projectReportDetailPageStore: ProjectReportDetailPageStore,
    private projectStore: ProjectStore,
    private callService: CallService,
    private projectCostOptionService: ProjectCostOptionService,
  ) {
    this.perCoFinancing$ = this.perCoFinancing();
    this.callFunds$ = this.callFunds();
    this.perLumpSum$ = this.perLumpSum();
    this.perUnitCost$ = this.perUnitCost();
    this.perInvestment$ = this.perInvestment();
    this.allowedCostCategories$ = this.allowedCostCategories();
    this.perCostCategory$ = this.perCostCategory();
    this.perPartnerCostCategory$ = this.perPartnerCostCategory();
  }

  private perCoFinancing(): Observable<CertificateCoFinancingBreakdownDTO> {
    return combineLatest([
      this.projectStore.projectId$,
      this.projectReportDetailPageStore.projectReportId$,
    ])
      .pipe(
        switchMap(([projectId, reportId]) =>
          this.financialOverviewService.getCoFinancingBreakdown(projectId, reportId)
        ),
        tap(data => Log.info('Fetched overview breakdown per co-financing', this, data)),
      );
  }

  private callFunds(): Observable<CallFundRateDTO[]> {
    return combineLatest([
      this.projectStore.projectCallSettings$,
    ])
      .pipe(
        map(([call]) => call.callId),
        switchMap(callId => this.callService.getCallById(callId)),
        map(call => call.funds),
        tap(data => Log.info('Fetched call funds for financial overview', this, data)),
      );
  }

  private perLumpSum(): Observable<CertificateLumpSumBreakdownDTO> {
    return combineLatest([
      this.projectStore.projectId$,
      this.projectReportDetailPageStore.projectReportId$,
    ])
      .pipe(
        switchMap(([projectId, reportId]) =>
          this.financialOverviewService.getLumpSumsBreakdown(projectId, reportId)
        ),
        tap(data => Log.info('Fetched overview breakdown per lump sum', this, data)),
      );
  }

  private perUnitCost(): Observable<CertificateUnitCostBreakdownDTO> {
    return combineLatest([
      this.projectStore.projectId$,
      this.projectReportDetailPageStore.projectReportId$,
    ])
      .pipe(
        switchMap(([projectId, reportId]) =>
          this.financialOverviewService.getUnitCostsBreakdown(projectId, reportId)
        ),
        tap(data => Log.info('Fetched overview breakdown per unit cost', this, data)),
      );
  }

  private perInvestment(): Observable<CertificateInvestmentBreakdownDTO> {
    return combineLatest([
      this.projectStore.projectId$,
      this.projectReportDetailPageStore.projectReportId$,
    ])
      .pipe(
        switchMap(([projectId, reportId]) =>
          this.financialOverviewService.getInvestmentsBreakdown(projectId, reportId)
        ),
        tap(data => Log.info('Fetched overview breakdown per investment', this, data)),
      );
  }

  private allowedCostCategories(): Observable<Map<CategoryEnum | 'LumpSum' | 'UnitCost', boolean>> {
    return combineLatest([
      this.projectStore.projectCallSettings$,
      this.projectStore.projectId$,
      this.projectReportDetailPageStore.projectReportId$,
    ])
      .pipe(
        filter(([call, partnerId, reportId]) => partnerId != null && reportId != null),
        switchMap(([call, partnerId, reportId]) => combineLatest([
          this.callService.getAllowedRealCosts(call.callId),
          of(call.flatRates),
          of(call.lumpSums),
          this.projectUnitCosts(),
        ])),
        map(([allowedRealCosts, flatRates, lumpSumsFromCall, unitCosts]) => {
          const setting = new Map<ProjectPartnerReportUnitCostDTO.CategoryEnum | 'LumpSum' | 'UnitCost', boolean>();

          setting.set(CategoryEnum.StaffCosts,
            allowedRealCosts.allowRealStaffCosts || flatRates.staffCostFlatRateSetup != null
            || this.anySingleCostCategory(unitCosts, BudgetCostCategoryEnum.STAFF_COSTS));
          setting.set(CategoryEnum.OfficeAndAdministrationCosts,
            flatRates.officeAndAdministrationOnStaffCostsFlatRateSetup != null || flatRates.officeAndAdministrationOnDirectCostsFlatRateSetup != null);
          setting.set(CategoryEnum.TravelAndAccommodationCosts,
            allowedRealCosts.allowRealTravelAndAccommodationCosts || flatRates.travelAndAccommodationOnStaffCostsFlatRateSetup != null
            || this.anySingleCostCategory(unitCosts, BudgetCostCategoryEnum.TRAVEL_AND_ACCOMMODATION_COSTS));
          setting.set(CategoryEnum.ExternalCosts,
            allowedRealCosts.allowRealExternalExpertiseAndServicesCosts || this.anySingleCostCategory(unitCosts, BudgetCostCategoryEnum.EXTERNAL_COSTS));
          setting.set(CategoryEnum.EquipmentCosts,
            allowedRealCosts.allowRealEquipmentCosts || this.anySingleCostCategory(unitCosts, BudgetCostCategoryEnum.EQUIPMENT_COSTS));
          setting.set(CategoryEnum.InfrastructureCosts,
            allowedRealCosts.allowRealInfrastructureCosts || this.anySingleCostCategory(unitCosts, BudgetCostCategoryEnum.INFRASTRUCTURE_COSTS));
          setting.set(CategoryEnum.Multiple, flatRates.otherCostsOnStaffCostsFlatRateSetup != null);
          setting.set('LumpSum', !!lumpSumsFromCall.length);
          setting.set('UnitCost', !!unitCosts?.find(cost => !cost.isOneCostCategory));

          return setting;
        }),
        tap(data => Log.info('Fetched call budget visibility settings', this, data)),
      );
  }

  private anySingleCostCategory(unitCosts: ProgrammeUnitCost[], costCategory: BudgetCostCategoryEnum) {
    return unitCosts.some(unitCost => unitCost.isOneCostCategory && unitCost.categories.includes(costCategory));
  }

  private perCostCategory(): Observable<CertificateCostCategoryBreakdownDTO> {
    return combineLatest([
      this.projectStore.projectId$,
      this.projectReportDetailPageStore.projectReportId$,
    ])
      .pipe(
        switchMap(([projectId, reportId]) =>
          this.financialOverviewService.getCostCategoriesBreakdown(projectId, reportId)
        ),
        tap(data => Log.info('Fetched overview breakdown per cost category', this, data)),
      );
  }

  private projectUnitCosts(): Observable<ProgrammeUnitCost[]> {
    return combineLatest([
      this.projectStore.projectId$,
      this.projectReportDetailPageStore.reportVersion$,
    ]).pipe(
      switchMap(([projectId, version]) => this.projectCostOptionService.getProjectAvailableUnitCosts(projectId, version)),
      map(unitCosts => unitCosts.map(unitCost =>
        new ProgrammeUnitCost(
          unitCost.id,
          unitCost.name,
          unitCost.description,
          unitCost.type,
          unitCost.costPerUnit,
          unitCost.oneCostCategory,
          BudgetCostCategoryEnumUtils.toBudgetCostCategoryEnums(unitCost.categories),
          unitCost.projectDefined
        )
      )),
    );
  }

  private perPartnerCostCategory(): Observable<PerPartnerCostCategoryBreakdownDTO> {
    return combineLatest([
      this.projectStore.projectId$,
      this.projectReportDetailPageStore.projectReportId$,
    ])
      .pipe(
        switchMap(([projectId, reportId]) =>
          this.financialOverviewService.getCostCategoriesPerPartnerBreakdown(projectId, reportId)
        ),
        tap(data => Log.info('Fetched overview breakdown per partner cost category', this, data)),
      );
  }
}

