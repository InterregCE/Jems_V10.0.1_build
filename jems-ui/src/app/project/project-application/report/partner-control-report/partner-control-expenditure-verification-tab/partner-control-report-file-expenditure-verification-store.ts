import {Injectable} from '@angular/core';
import {BehaviorSubject, combineLatest, merge, Observable, Subject} from 'rxjs';
import {
  CurrencyDTO,
  IdNamePairDTO,
  InvestmentSummaryDTO,
  ProgrammeTypologyOfErrorsService,
  ProjectPartnerBudgetOptionsDto,
  ProjectPartnerControlReportExpenditureVerificationDTO,
  ProjectPartnerControlReportExpenditureVerificationUpdateDTO,
  ProjectPartnerReportDTO,
  ProjectPartnerReportExpenditureCostsService,
  ProjectPartnerReportExpenditureVerificationService,
  ProjectPartnerReportLumpSumDTO,
  ProjectPartnerReportUnitCostDTO,
  TypologyErrorsDTO
} from '@cat/api';

import {
  PartnerReportDetailPageStore
} from '@project/project-application/report/partner-report-detail-page/partner-report-detail-page-store.service';
import {filter, map, shareReplay, startWith, switchMap, tap} from 'rxjs/operators';
import {
  ProjectStore
} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {AllowedBudgetCategories} from '@project/model/allowed-budget-category';
import {BudgetOptions} from '@project/model/budget/budget-options';
import {BudgetCostCategoryEnum, BudgetCostCategoryEnumUtils} from '@project/model/lump-sums/BudgetCostCategoryEnum';
import {
  InvestmentSummary
} from '@project/work-package/project-work-package-page/work-package-detail-page/workPackageInvestment';
import {ProjectPartnerBudgetStore} from '@project/budget/services/project-partner-budget.store';
import {
  PartnerReportProcurementsPageStore
} from '@project/project-application/report/partner-report-detail-page/partner-report-procurements-tab/partner-report-procurement-page-store.service';
import {CurrencyStore} from '@common/services/currency.store';
import {RoutingService} from '@common/services/routing.service';
import {PartnerReportPageStore} from '@project/project-application/report/partner-report-page-store.service';
import {Log} from '@common/utils/log';
import {
  PartnerControlReportStore
} from '@project/project-application/report/partner-control-report/partner-control-report-store.service';

@Injectable({providedIn: 'root'})
export class PartnerControlReportFileExpenditureVerificationStore {

  partnerId$: Observable<string | number | null>;
  isEditable$: Observable<boolean>;
  isFinalized$: Observable<boolean>;
  costCategories$: Observable<string[]>;
  contractIDs$: Observable<IdNamePairDTO[]>;
  investmentsSummary$: Observable<InvestmentSummary[]>;
  currencies$: Observable<CurrencyDTO[]>;
  currentReport$: Observable<ProjectPartnerReportDTO>;
  refreshExpenditures$ = new BehaviorSubject<void>(undefined);
  reportLumpSums$: Observable<ProjectPartnerReportLumpSumDTO[]>;
  reportUnitCosts$: Observable<ProjectPartnerReportUnitCostDTO[]>;
  reportExpenditureControl$: Observable<ProjectPartnerControlReportExpenditureVerificationDTO[]>;
  typologyOfErrors$: Observable<TypologyErrorsDTO[]>;
  private expenditureControlUpdated$ = new Subject<ProjectPartnerControlReportExpenditureVerificationDTO[]>();

  constructor(private partnerReportExpenditureCostsService: ProjectPartnerReportExpenditureCostsService,
              private partnerReportDetailPageStore: PartnerReportDetailPageStore,
              private projectStore: ProjectStore,
              private projectPartnerBudgetStore: ProjectPartnerBudgetStore,
              private reportProcurementPageStore: PartnerReportProcurementsPageStore,
              private currencyStore: CurrencyStore,
              private routingService: RoutingService,
              private partnerReportPageStore: PartnerReportPageStore,
              private projectPartnerReportExpenditureVerificationService: ProjectPartnerReportExpenditureVerificationService,
              private typologyOfErrorsService: ProgrammeTypologyOfErrorsService,
              private partnerControlReportStore: PartnerControlReportStore) {
    this.reportExpenditureControl$ = this.expenditureControl();
    this.typologyOfErrors$ = this.typologyOfErrorsService.getTypologyErrors();
    this.costCategories$ = this.costCategories();
    this.isEditable$ = this.partnerControlReportStore.controlReportEditable$;
    this.isFinalized$ = this.partnerControlReportStore.controlReportFinalized$;
    this.contractIDs$ = this.reportProcurementPageStore.getProcurementList();
    this.investmentsSummary$ = this.investmentSummariesForReport();
    this.currencies$ = this.currencyStore.currencies$;
    this.currentReport$ = this.partnerReportDetailPageStore.partnerReport$;
    this.partnerId$ = this.partnerReportPageStore.partnerId$;
    this.reportLumpSums$ = this.reportLumpSums();
    this.reportUnitCosts$ = this.reportUnitCosts();
  }

  updateExpendituresControl(expendituresControl: ProjectPartnerControlReportExpenditureVerificationUpdateDTO[]): Observable<ProjectPartnerControlReportExpenditureVerificationDTO[]> {
    return combineLatest([
      this.partnerReportDetailPageStore.partnerId$,
      this.partnerReportDetailPageStore.partnerReportId$,
    ]).pipe(
      switchMap(([partnerId, reportId]) =>
        this.projectPartnerReportExpenditureVerificationService.updatePartnerReportExpendituresVerification(partnerId as number, reportId, expendituresControl)
      ),
      tap(updatedExpendituresControl => this.expenditureControlUpdated$.next(updatedExpendituresControl)),
      tap(() => this.partnerReportDetailPageStore.refreshIdentification$.next(null)),
    );
  }

  private costCategories(): Observable<string[]> {
    return combineLatest([
      this.partnerReportDetailPageStore.partnerId$,
      this.projectStore.allowedBudgetCategories$,
      this.budgetOptionsForReport()
    ])
      .pipe(
        map(([partnerId, allowedCategories, budgetOptions]) =>
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
      this.projectStore.investmentChangeEvent$.pipe(startWith(null)),
      this.partnerReportDetailPageStore.partnerReport$])
      .pipe(
        switchMap(([partnerId, changeEvent, partnerReport]) =>
          this.partnerReportExpenditureCostsService.getAvailableInvestments(partnerId as number, partnerReport.id)),
        map((investmentSummaryDTOs: InvestmentSummaryDTO[]) => investmentSummaryDTOs
          .map(it => new InvestmentSummary(it.id, it.investmentNumber, it.workPackageNumber))),
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
      this.partnerReportDetailPageStore.partnerReport$
    ])
      .pipe(
        filter(([partnerId, partnerReport]) => partnerId !== null && partnerReport !== null),
        switchMap(([partnerId, partnerReport]) => this.partnerReportExpenditureCostsService
          .getAvailableUnitCosts(Number(partnerId), partnerReport.id)),
        map((unitCosts: ProjectPartnerReportUnitCostDTO[]) => unitCosts),
        shareReplay(1)
      );
  }

  private expenditureControl(): Observable<ProjectPartnerControlReportExpenditureVerificationDTO[]> {
    const initialExpenditureControl$ = combineLatest([
      this.partnerReportDetailPageStore.partnerId$,
      this.partnerReportDetailPageStore.partnerReportId$,
      this.refreshExpenditures$
    ]).pipe(
      switchMap(([partnerId, reportId, _]) =>
        this.projectPartnerReportExpenditureVerificationService.getProjectPartnerExpenditureVerification(partnerId as number, reportId)
      ),
      tap(data => Log.info('Fetched list of expenditures for partner report', this, data)),
    );

    return merge(initialExpenditureControl$, this.expenditureControlUpdated$);
  }
}
