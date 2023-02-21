import {Injectable} from '@angular/core';
import {BehaviorSubject, combineLatest, merge, Observable, Subject} from 'rxjs';
import {
  CurrencyDTO,
  IdNamePairDTO,
  ProjectPartnerBudgetOptionsDto,
  ProjectPartnerReportDTO,
  ProjectPartnerReportExpenditureCostDTO,
  ProjectPartnerReportExpenditureCostsService,
  ProjectPartnerReportInvestmentDTO,
  ProjectPartnerReportLumpSumDTO,
  ProjectPartnerReportParkedExpenditureDTO,
  ProjectPartnerReportUnitCostDTO,
  ProjectReportFileMetadataDTO
} from '@cat/api';

import {PartnerReportDetailPageStore} from '@project/project-application/report/partner-report-detail-page/partner-report-detail-page-store.service';
import {filter, map, shareReplay, startWith, switchMap, take, tap} from 'rxjs/operators';
import {ProjectStore} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {AllowedBudgetCategories} from '@project/model/allowed-budget-category';
import {BudgetOptions} from '@project/model/budget/budget-options';
import {BudgetCostCategoryEnum, BudgetCostCategoryEnumUtils} from '@project/model/lump-sums/BudgetCostCategoryEnum';
import {InvestmentSummary} from '@project/work-package/project-work-package-page/work-package-detail-page/workPackageInvestment';
import {ProjectPartnerBudgetStore} from '@project/budget/services/project-partner-budget.store';
import {
  PartnerReportProcurementsPageStore
} from '@project/project-application/report/partner-report-detail-page/partner-report-procurements-tab/partner-report-procurement-page-store.service';
import {CurrencyStore} from '@common/services/currency.store';
import {RoutingService} from '@common/services/routing.service';
import {PartnerReportPageStore} from '@project/project-application/report/partner-report-page-store.service';
import {Log} from '@common/utils/log';

@Injectable({providedIn: 'root'})
export class PartnerReportExpendituresStore {

  partnerId$: Observable<string | number | null>;
  isEditable$: Observable<boolean>;
  costCategories$: Observable<string[]>;
  contractIDs$: Observable<IdNamePairDTO[]>;
  investmentsSummary$: Observable<InvestmentSummary[]>;
  expendituresCosts$: Observable<ProjectPartnerReportExpenditureCostDTO[]>;
  parkedExpenditures$: Observable<ProjectPartnerReportParkedExpenditureDTO[]>;
  currencies$: Observable<CurrencyDTO[]>;
  currentReport$: Observable<ProjectPartnerReportDTO>;
  refreshExpenditures$ = new BehaviorSubject<void>(undefined);
  reportLumpSums$: Observable<ProjectPartnerReportLumpSumDTO[]>;
  reportUnitCosts$: Observable<ProjectPartnerReportUnitCostDTO[]>;
  private expendituresUpdated$ = new Subject<ProjectPartnerReportExpenditureCostDTO[]>();

  constructor(private partnerReportExpenditureCostsService: ProjectPartnerReportExpenditureCostsService,
              private partnerReportDetailPageStore: PartnerReportDetailPageStore,
              private projectStore: ProjectStore,
              private projectPartnerBudgetStore: ProjectPartnerBudgetStore,
              private reportProcurementPageStore: PartnerReportProcurementsPageStore,
              private currencyStore: CurrencyStore,
              private routingService: RoutingService,
              private partnerReportPageStore: PartnerReportPageStore) {
    this.expendituresCosts$ = this.partnerReportExpenditureCosts();
    this.parkedExpenditures$ = this.parkedExpenditures();
    this.costCategories$ = this.costCategories();
    this.isEditable$ = this.partnerReportDetailPageStore.reportEditable$;
    this.contractIDs$ = this.reportProcurementPageStore.getProcurementList();
    this.investmentsSummary$ = this.investmentSummariesForReport();
    this.currencies$ = this.currencyStore.currencies$;
    this.currentReport$ = this.partnerReportDetailPageStore.partnerReport$;
    this.partnerId$ = this.partnerReportPageStore.partnerId$;
    this.reportLumpSums$ = this.reportLumpSums();
    this.reportUnitCosts$ = this.reportUnitCosts();
  }

  updateExpenditures(partnerExpenditures: ProjectPartnerReportExpenditureCostDTO[]): Observable<ProjectPartnerReportExpenditureCostDTO[]> {
    return combineLatest([
      this.partnerReportDetailPageStore.partnerId$,
      this.partnerReportDetailPageStore.partnerReportId$,
    ]).pipe(
      switchMap(([partnerId, reportId]) =>
        this.partnerReportExpenditureCostsService.updatePartnerReportExpenditures(partnerId as number, reportId, partnerExpenditures)
      ),
      tap(updatedExpenditureCosts => this.expendituresUpdated$.next(updatedExpenditureCosts)),
      tap(() => this.partnerReportDetailPageStore.refreshIdentification$.next(null)),
    );
  }

  deleteParkedExpenditure(expenditureId: number): Observable<any> {
    return combineLatest([
      this.partnerReportDetailPageStore.partnerId$,
      this.partnerReportDetailPageStore.partnerReportId$,
    ]).pipe(
      switchMap(([partnerId, reportId]) =>
        this.partnerReportExpenditureCostsService.deleteParkedExpenditure(expenditureId, partnerId as number, reportId)
      ),
      tap(() => this.refreshExpenditures$.next(undefined)),
    );
  }

  reIncludeParkedExpenditure(expenditureId: number): Observable<any> {
    return combineLatest([
      this.partnerReportDetailPageStore.partnerId$,
      this.partnerReportDetailPageStore.partnerReportId$,
    ]).pipe(
      switchMap(([partnerId, reportId]) => this.partnerReportExpenditureCostsService.reIncludeParkedExpenditure(expenditureId, partnerId as number, reportId)),
      tap(() => this.refreshExpenditures$.next(undefined)),
    );
  }

  private partnerReportExpenditureCosts(): Observable<ProjectPartnerReportExpenditureCostDTO[]> {
    const initialExpenditureCosts$ = combineLatest([
      this.partnerReportDetailPageStore.partnerId$,
      this.partnerReportDetailPageStore.partnerReportId$,
      this.refreshExpenditures$
    ]).pipe(
      switchMap(([partnerId, reportId, _]) =>
        this.partnerReportExpenditureCostsService.getProjectPartnerReports(partnerId as number, reportId)
      ),
      tap(data => Log.info('Fetched list of expenditures for partner report', this, data)),
    );

    return merge(initialExpenditureCosts$, this.expendituresUpdated$);
  }

  private parkedExpenditures(): Observable<ProjectPartnerReportParkedExpenditureDTO[]> {
    return combineLatest([
      this.partnerReportDetailPageStore.partnerId$,
      this.partnerReportDetailPageStore.partnerReportId$,
      this.refreshExpenditures$,
    ]).pipe(
      switchMap(([partnerId, reportId, _]) =>
        this.partnerReportExpenditureCostsService.getAvailableParkedExpenditures(partnerId as number, reportId, 0, 25, 'id,asc')
      ),
      tap(data => Log.info('Fetched list of expenditures for partner report', this, data)),
      map(data => data.content),
    );
  }

  private costCategories(): Observable<string[]> {
    return combineLatest([
      this.projectStore.allowedBudgetCategories$,
      this.budgetOptionsForReport()
    ]).pipe(
      map(([allowedCategories, budgetOptions]) =>
        this.mapCategoryCosts(allowedCategories, budgetOptions))
    );
  }

  private budgetOptionsForReport(): Observable<BudgetOptions> {
    return combineLatest([
      this.partnerReportDetailPageStore.partnerId$,
      this.partnerReportDetailPageStore.partnerReport$
    ])
      .pipe(
        filter(([partnerId, partnerReport]) => partnerId !== null && partnerReport !== null),
        switchMap(([partnerId, partnerReport]) => this.partnerReportExpenditureCostsService
          .getAvailableBudgetOptions(partnerId as number, partnerReport.id)),
        map((it: ProjectPartnerBudgetOptionsDto) => BudgetOptions.fromDto(it)),
      );
  }

  private investmentSummariesForReport(): Observable<InvestmentSummary[]> {
    return combineLatest([
      this.partnerReportDetailPageStore.partnerId$,
      this.partnerReportDetailPageStore.partnerReport$,
      this.projectStore.investmentChangeEvent$.pipe(startWith(null)),
    ]).pipe(
      switchMap(([partnerId, partnerReport, changeEvent]) =>
        this.partnerReportExpenditureCostsService.getAvailableInvestments(partnerId as number, partnerReport.id)),
      map((investmentSummaryDTOs: ProjectPartnerReportInvestmentDTO[]) => investmentSummaryDTOs
        .map(it => new InvestmentSummary(it.id, it.investmentNumber, it.workPackageNumber, it.deactivated))),
      map((investmentSummaries: InvestmentSummary[]) => investmentSummaries),
      shareReplay(1)
    );
  }

  private mapCategoryCosts(allowedBudgetCategories: AllowedBudgetCategories, budgetOptions: BudgetOptions):
    string[] {
    const costCategories: string[] = [];
    for (const [key, value] of allowedBudgetCategories.categories.entries()) {
      switch (BudgetCostCategoryEnumUtils.toBudgetCostCategoryEnum(key)) {
        case BudgetCostCategoryEnum.STAFF_COSTS:
          if (value.realCostsEnabled && budgetOptions.staffCostsFlatRate == null) {
            costCategories.push(key);
          }
          break;
        case BudgetCostCategoryEnum.TRAVEL_AND_ACCOMMODATION_COSTS:
          if (value.realCostsEnabled && budgetOptions.travelAndAccommodationOnStaffCostsFlatRate == null
            && budgetOptions.otherCostsOnStaffCostsFlatRate == null) {
            costCategories.push(key);
          }
          break;
        case BudgetCostCategoryEnum.EXTERNAL_COSTS:
          if (value.realCostsEnabled && budgetOptions.otherCostsOnStaffCostsFlatRate == null) {
            costCategories.push(key);
          }
          break;
        case BudgetCostCategoryEnum.EQUIPMENT_COSTS:
          if (value.realCostsEnabled && budgetOptions.otherCostsOnStaffCostsFlatRate == null) {
            costCategories.push(key);
          }
          break;
        case BudgetCostCategoryEnum.INFRASTRUCTURE_COSTS:
          if (value.realCostsEnabled && budgetOptions.otherCostsOnStaffCostsFlatRate == null) {
            costCategories.push(key);
          }
          break;
      }
    }
    return costCategories;
  }

  uploadFile(file: File, expenditureId: number): Observable<ProjectReportFileMetadataDTO> {
    return combineLatest([
      this.partnerId$.pipe(map(id => Number(id))),
      this.currentReport$
    ]).pipe(
      take(1),
      switchMap(([partnerId, currentReport]) => this.partnerReportExpenditureCostsService
        .uploadFileToExpenditureForm(file, expenditureId, partnerId, currentReport.id)
      ),
    );
  }

  private reportLumpSums(): Observable<ProjectPartnerReportLumpSumDTO[]> {
    return combineLatest([
      this.partnerReportDetailPageStore.partnerId$,
      this.partnerReportDetailPageStore.partnerReport$
    ])
      .pipe(
        filter(([partnerId, partnerReport]) => partnerId !== null && partnerReport !== null),
        switchMap(([partnerId, partnerReport]) => this.partnerReportExpenditureCostsService
          .getAvailableLumpSums(Number(partnerId), partnerReport.id)),
        map((lumpSums: ProjectPartnerReportLumpSumDTO[]) => lumpSums),
        shareReplay(1)
      );
  }

  private reportUnitCosts(): Observable<ProjectPartnerReportUnitCostDTO[]> {
    return combineLatest([
      this.partnerReportDetailPageStore.partnerId$,
      this.partnerReportDetailPageStore.partnerReportId$
    ])
      .pipe(
        filter(([partnerId, reportId]) => partnerId !== null && reportId !== null),
        switchMap(([partnerId, reportId]) => this.partnerReportExpenditureCostsService
          .getAvailableUnitCosts(Number(partnerId), reportId)),
        map((unitCosts: ProjectPartnerReportUnitCostDTO[]) => unitCosts),
        shareReplay(1)
      );
  }
}
