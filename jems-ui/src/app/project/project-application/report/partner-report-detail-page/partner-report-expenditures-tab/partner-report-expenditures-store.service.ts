import {Injectable} from '@angular/core';
import {combineLatest, merge, Observable, Subject} from 'rxjs';
import {
  IdNamePairDTO,
  InvestmentSummaryDTO,
  ProjectPartnerBudgetOptionsDto,
  ProjectPartnerReportExpenditureCostDTO,
  ProjectPartnerReportExpenditureCostsService
} from '@cat/api';

import {PartnerReportDetailPageStore} from '@project/project-application/report/partner-report-detail-page/partner-report-detail-page-store.service';
import {filter, map, shareReplay, startWith, switchMap, tap} from 'rxjs/operators';
import {ProjectStore} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {AllowedBudgetCategories} from '@project/model/allowed-budget-category';
import {BudgetOptions} from '@project/model/budget/budget-options';
import {BudgetCostCategoryEnum, BudgetCostCategoryEnumUtils} from '@project/model/lump-sums/BudgetCostCategoryEnum';
import {InvestmentSummary} from '@project/work-package/project-work-package-page/work-package-detail-page/workPackageInvestment';
import {ProjectPartnerBudgetStore} from '@project/budget/services/project-partner-budget.store';
import {
  PartnerReportProcurementsPageStore
} from '@project/project-application/report/partner-report-detail-page/partner-report-procurements-tab/partner-report-procurement-page-store.service';

@Injectable({providedIn: 'root'})
export class PartnerReportExpendituresStore {

  isEditable$: Observable<boolean>;
  costCategories$: Observable<string[]>;
  contractIDs$: Observable<IdNamePairDTO[]>;
  investmentsSummary$: Observable<InvestmentSummary[]>;
  expendituresCosts$: Observable<ProjectPartnerReportExpenditureCostDTO[]>;
  private expendituresUpdated$ = new Subject<ProjectPartnerReportExpenditureCostDTO[]>();

  constructor(private partnerReportExpenditureCostsService: ProjectPartnerReportExpenditureCostsService,
              private partnerReportDetailPageStore: PartnerReportDetailPageStore,
              private projectStore: ProjectStore,
              private projectPartnerBudgetStore: ProjectPartnerBudgetStore,
              private reportProcurementPageStore: PartnerReportProcurementsPageStore) {
    this.expendituresCosts$ = this.partnerReportExpenditureCosts();
    this.costCategories$ = this.costCategories();
    this.isEditable$ = this.partnerReportDetailPageStore.reportEditable$;
    this.contractIDs$ = this.reportProcurementPageStore.getProcurementList();
    this.investmentsSummary$ = this.investmentSummariesForReport();
  }

  updateExpenditures(partnerExpenditures: ProjectPartnerReportExpenditureCostDTO[]): Observable<ProjectPartnerReportExpenditureCostDTO[]> {
    return combineLatest([
      this.partnerReportDetailPageStore.partnerId$,
      this.partnerReportDetailPageStore.partnerReportId$
    ]).pipe(
      switchMap(([partnerId, reportId]) =>
        this.partnerReportExpenditureCostsService.updatePartnerReportExpenditures(partnerId as number, reportId, partnerExpenditures)
      ),
      tap(updatedExpenditureCosts => this.expendituresUpdated$.next(updatedExpenditureCosts))
    );
  }

  private partnerReportExpenditureCosts(): Observable<ProjectPartnerReportExpenditureCostDTO[]> {
    const initialExpenditureCosts$ = combineLatest([
      this.partnerReportDetailPageStore.partnerId$,
      this.partnerReportDetailPageStore.partnerReportId$
    ]).pipe(
      switchMap(([partnerId, reportId]) =>
        this.partnerReportExpenditureCostsService.getProjectPartnerReports(partnerId as number, reportId)
      )
    );

    return merge(initialExpenditureCosts$, this.expendituresUpdated$)
      .pipe(
        shareReplay(1)
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
        switchMap(([partnerId, partnerReport]) => this.projectPartnerBudgetStore
          .getBudgetOptions(partnerId as number, partnerReport.linkedFormVersion)),
        map((it: ProjectPartnerBudgetOptionsDto) => BudgetOptions.fromDto(it)),
      );
  }

  private investmentSummariesForReport(): Observable<InvestmentSummary[]> {
    return combineLatest([
      this.projectStore.project$,
      this.projectStore.investmentChangeEvent$.pipe(startWith(null)),
      this.partnerReportDetailPageStore.partnerReport$])
      .pipe(
        switchMap(([project, changeEvent, partnerReport]) =>
          this.projectStore.getProjectInvestmentSummaries(project, partnerReport.linkedFormVersion)),
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

}
