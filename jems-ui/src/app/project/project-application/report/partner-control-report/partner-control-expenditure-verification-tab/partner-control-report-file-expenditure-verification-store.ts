import {Injectable} from '@angular/core';
import {BehaviorSubject, combineLatest, merge, Observable, Subject} from 'rxjs';
import {
  CurrencyDTO,
  IdNamePairDTO,
  ProgrammeTypologyOfErrorsService,
  ProjectPartnerBudgetOptionsDto,
  ProjectPartnerControlReportExpenditureVerificationDTO,
  ProjectPartnerControlReportExpenditureVerificationUpdateDTO,
  ProjectPartnerReportDTO,
  ProjectPartnerReportExpenditureCostsService,
  ProjectPartnerReportExpenditureVerificationService,
  ProjectPartnerReportInvestmentDTO,
  ProjectPartnerReportLumpSumDTO,
  ProjectPartnerReportUnitCostDTO,
  TypologyErrorsDTO
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
import {PartnerControlReportStore} from '@project/project-application/report/partner-control-report/partner-control-report-store.service';

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
  unparkable$: Observable<number[]>;
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
    this.unparkable$ = this.unparkable();
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
      this.partnerReportDetailPageStore.partnerReportId$
    ])
      .pipe(
        filter(([partnerId, partnerReportId]) => partnerId !== null && partnerReportId !== null),
        switchMap(([partnerId, partnerReportId]) => this.partnerReportExpenditureCostsService
          .getAvailableBudgetOptions(partnerId as number, partnerReportId)),
        map((it: ProjectPartnerBudgetOptionsDto) => BudgetOptions.fromDto(it)),
      );
  }

  private investmentSummariesForReport(): Observable<InvestmentSummary[]> {
    return combineLatest([
      this.partnerReportDetailPageStore.partnerId$,
      this.partnerReportDetailPageStore.partnerReportId$,
      this.projectStore.investmentChangeEvent$.pipe(startWith(null))])
      .pipe(
        filter(([partnerId, partnerReportId, changeEvent]) => partnerId !== null && partnerReportId !== null),
        switchMap(([partnerId, partnerReportId, changeEvent]) =>
          this.partnerReportExpenditureCostsService.getAvailableInvestments(partnerId as number, partnerReportId)),
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

  private reportLumpSums(): Observable<ProjectPartnerReportLumpSumDTO[]> {
    return combineLatest([
      this.partnerReportDetailPageStore.partnerId$,
      this.partnerReportDetailPageStore.partnerReportId$
    ])
      .pipe(
        filter(([partnerId, reportId]) => partnerId !== null && reportId !== null),
        switchMap(([partnerId, reportId]) => this.partnerReportExpenditureCostsService
          .getAvailableLumpSums(Number(partnerId), reportId)),
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

  private unparkable(): Observable<number[]> {
    return combineLatest([
      this.partnerReportDetailPageStore.partnerId$.pipe(map(Number)),
      this.partnerReportDetailPageStore.partnerReportId$.pipe(filter(Boolean), map(Number)),
      this.reportExpenditureControl$
    ]).pipe(
      take(1),
      switchMap(([partnerId, reportId, reportExpenditureControl]) =>
        this.projectPartnerReportExpenditureVerificationService.getParkedExpenditureIds(partnerId, reportId).pipe(
          map(it => it.concat(reportExpenditureControl.filter(value => !value.parked).map(value => value.id)))
        ),
      )
    );
  }
}
