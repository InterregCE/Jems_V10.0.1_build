import {Injectable} from '@angular/core';
import {combineLatest, Observable} from 'rxjs';
import {BudgetOptions} from '../../model/budget/budget-options';
import {CallFlatRateSetting} from '../../model/call-flat-rate-setting';
import {filter, map, shareReplay, startWith, switchMap} from 'rxjs/operators';
import {ProjectStore} from '../../project-application/containers/project-application-detail/services/project-store.service';
import {
  CallFundRateDTO,
  CallService,
  ProjectLumpSumService,
  ProjectPartnerBudgetService,
  ProjectPartnerCoFinancingAndContributionInputDTO,
  ProjectPartnerCoFinancingAndContributionOutputDTO,
  ProjectPartnerDetailDTO,
  ProjectPartnerService,
  ProjectPartnerStateAidDTO,
  ProjectPeriodDTO
} from '@cat/api';
import {ProjectPartnerStore} from '../../project-application/containers/project-application-form-page/services/project-partner-store.service';
import {NumberService} from '@common/services/number.service';
import {PartnerBudgetTables} from '../../model/budget/partner-budget-tables';
import {WorkPackagePageStore} from '../../work-package/project-work-package-page/work-package-detail-page/work-package-page-store.service';
import {InvestmentSummary} from '../../work-package/project-work-package-page/work-package-detail-page/workPackageInvestment';
import {ProgrammeUnitCost} from '../../model/programmeUnitCost';
import {ProjectVersionStore} from '../../common/services/project-version-store.service';
import {AllowedBudgetCategories} from '@project/model/allowed-budget-category';
import {ProjectPartnerBudgetStore} from '@project/budget/services/project-partner-budget.store';
import {ProjectPartnerCoFinancingStore} from '@project/partner/project-partner-detail-page/project-partner-co-financing-tab/services/project-partner-co-financing.store';
import {ProjectPartnerStateAidsStore} from '@project/partner/services/project-partner-state-aids.store';
import {PartnerLumpSum} from '@project/model/lump-sums/partnerLumpSum';
import {ProjectLumpSumsStore} from '@project/lump-sums/project-lump-sums-page/project-lump-sums-store.service';
import {ProgrammeLumpSum} from '@project/model/lump-sums/programmeLumpSum';

@Injectable()
export class ProjectPartnerDetailPageStore {
  callFlatRatesSettings$: Observable<CallFlatRateSetting>;
  projectCallLumpSums$: Observable<ProgrammeLumpSum[]>;
  budgetOptions$: Observable<BudgetOptions>;
  partnerLumpSums$: Observable<PartnerLumpSum[]>;
  partnerTotalLumpSum$: Observable<number>;
  budgets$: Observable<PartnerBudgetTables>;
  totalBudget$: Observable<number>;
  isProjectEditable$: Observable<boolean>;
  investmentSummaries$: Observable<InvestmentSummary[]>;
  unitCosts$: Observable<ProgrammeUnitCost[]>;
  financingAndContribution$: Observable<ProjectPartnerCoFinancingAndContributionOutputDTO>;
  callFunds$: Observable<Map<number, CallFundRateDTO>>;
  periods$: Observable<ProjectPeriodDTO[]>;
  multipleFundsAllowed$: Observable<boolean>;
  stateAid$: Observable<ProjectPartnerStateAidDTO>;
  partner$: Observable<ProjectPartnerDetailDTO>;
  allowedBudgetCategories$: Observable<AllowedBudgetCategories>;

  constructor(private projectStore: ProjectStore,
              private partnerStore: ProjectPartnerStore,
              private callService: CallService,
              private projectWorkPackagePageStore: WorkPackagePageStore,
              private projectPartnerBudgetService: ProjectPartnerBudgetService,
              private projectPartnerService: ProjectPartnerService,
              private projectLumpSumService: ProjectLumpSumService,
              private projectVersionStore: ProjectVersionStore,
              private projectPartnerBudgetStore: ProjectPartnerBudgetStore,
              private projectLumpSumsStore: ProjectLumpSumsStore,
              private projectPartnerCoFinancingStore: ProjectPartnerCoFinancingStore,
              private projectPartnerStateAidsStore: ProjectPartnerStateAidsStore) {
    this.investmentSummaries$ = this.projectStore.investmentSummaries$;
    this.unitCosts$ = this.projectStore.projectCall$.pipe(
      map(projectCall => projectCall.unitCosts),
      shareReplay(1)
    );
    this.budgets$ = this.projectPartnerBudgetStore.budgets$;
    this.budgetOptions$ = this.projectPartnerBudgetStore.budgetOptions$;
    this.callFlatRatesSettings$ = this.callFlatRateSettings();
    this.totalBudget$ = this.projectPartnerBudgetStore.totalBudget$;
    this.financingAndContribution$ = this.projectPartnerCoFinancingStore.financingAndContribution$;
    this.callFunds$ = this.callFunds();
    this.isProjectEditable$ = this.projectStore.projectEditable$;
    this.periods$ = this.projectStore.projectPeriods$;
    this.multipleFundsAllowed$ = this.projectStore.projectCall$.pipe(map(it => it.multipleFundsAllowed));
    this.partner$ = this.partnerStore.partner$;
    this.stateAid$ = this.projectPartnerStateAidsStore.stateAid$;
    this.allowedBudgetCategories$ = this.projectStore.allowedBudgetCategories$;
    this.projectCallLumpSums$ = this.projectLumpSumsStore.projectCallLumpSums$;
    this.partnerLumpSums$ = this.partnerLumpSums();
    this.partnerTotalLumpSum$ = this.partnerTotalLumpSum();
  }

  public static calculateOfficeAndAdministrationFlatRateTotal(
    officeFlatRateBasedOnStaffCost: number | null,
    officeFlatRateBasedOnDirectCosts: number | null,
    staffTotal: number,
    travelCostTotal: number,
    externalTotal: number,
    equipmentTotal: number,
    infrastructureTotal: number,
  ): number {
    return NumberService.truncateNumber(NumberService.product([
      NumberService.divide(officeFlatRateBasedOnStaffCost || officeFlatRateBasedOnDirectCosts, 100),
      officeFlatRateBasedOnStaffCost !== null ? staffTotal :
        NumberService.sum([externalTotal, equipmentTotal, infrastructureTotal, staffTotal, travelCostTotal])]));
  }

  public static calculateOtherCostsFlatRateTotal(staffCostsFlatRateBasedOnDirectCost: number | null, otherCostsFlatRateBasedOnStaffCost: number, staffTotal: number): number {
    if (staffCostsFlatRateBasedOnDirectCost != null) {
      return 0;
    }
    return NumberService.truncateNumber(NumberService.product([
      NumberService.divide(otherCostsFlatRateBasedOnStaffCost, 100),
      staffTotal
    ]));
  }

  public static calculateStaffCostsTotal(
    budgetOptions: BudgetOptions,
    staffCostsTotal: number,
    travelCostsTotal: number,
    externalCostsTotal: number,
    equipmentCostsTotal: number,
    infrastructureCostsTotal: number,
  ): number {
    if (!budgetOptions?.staffCostsFlatRate) {
      return staffCostsTotal;
    }
    const travelTotal = budgetOptions.travelAndAccommodationOnStaffCostsFlatRate ? 0 : travelCostsTotal;

    return NumberService.truncateNumber(NumberService.product([
      NumberService.divide(budgetOptions.staffCostsFlatRate, 100),
      NumberService.sum([travelTotal, externalCostsTotal, equipmentCostsTotal, infrastructureCostsTotal])
    ]));
  }

  public static calculateTravelAndAccommodationCostsTotal(travelFlatRateBasedOnStaffCost: number | null, staffTotal: number, travelTotal: number): number {
    if (travelFlatRateBasedOnStaffCost === null) {
      return travelTotal;
    }
    return NumberService.truncateNumber(NumberService.product([
      NumberService.divide(travelFlatRateBasedOnStaffCost, 100),
      staffTotal
    ]));
  }

  updateBudgetOptions(budgetOptions: BudgetOptions): Observable<any> {
    return this.projectPartnerBudgetStore.updateBudgetOptions(budgetOptions);
  }

  updateBudgets(budgets: PartnerBudgetTables): Observable<any> {
    return this.projectPartnerBudgetStore.updateBudgets(budgets);
  }

  updateCoFinancingAndContributions(model: ProjectPartnerCoFinancingAndContributionInputDTO): Observable<any> {
    return this.projectPartnerCoFinancingStore.updateCoFinancingAndContributions(model);
  }

  updateStateAid(partnerId: number, stateAid: ProjectPartnerStateAidDTO): Observable<ProjectPartnerStateAidDTO> {
    return this.projectPartnerStateAidsStore.updateStateAid(partnerId, stateAid);
  }

  private callFunds(): Observable<Map<number, CallFundRateDTO>> {
    return this.projectStore.project$
      .pipe(
        map(project => project.callSettings.callId),
        switchMap(callId => this.callService.getCallById(callId)),
        map(call => new Map(call.funds
            .sort((a, b) => (a.programmeFund.id > b.programmeFund.id) ? 1 : -1)
            .map(fund => [fund.programmeFund.id, fund])
          )
        ),
        shareReplay(1)
      );
  }

  private callFlatRateSettings(): Observable<CallFlatRateSetting> {
    return this.projectStore.projectCall$.pipe(
      map(call => call.flatRates),
    );
  }

  private partnerLumpSums(): Observable<PartnerLumpSum[]> {
    return combineLatest([this.partner$, this.projectLumpSumsStore.projectLumpSums$, this.projectLumpSumsStore.projectCallLumpSums$]).pipe(
      map(([partner, projectLumpSums, callLumpSums]) =>
        projectLumpSums.filter(it => it.lumpSumContributions.some(contribution => contribution.partnerId === partner.id && contribution.amount > 0))
          .map(lumpSum => {
            const callLumpSum = callLumpSums.find(it => it.id === lumpSum.programmeLumpSumId);
            return new PartnerLumpSum(callLumpSum?.name || [], callLumpSum?.description || [], callLumpSum?.cost, lumpSum.period, lumpSum.lumpSumContributions.find(it=> it.partnerId === partner.id)?.amount || 0);
          })
      ),
      shareReplay(1)
    );
  };

  private partnerTotalLumpSum(): Observable<number> {
    return combineLatest([
      this.partnerStore.partner$,
      this.projectVersionStore.selectedVersionParam$,
      this.projectStore.projectId$,
      this.projectPartnerBudgetStore.updateBudgetOptionsEvent$.pipe(startWith(null))
    ])
      .pipe(
        filter(([partner]) => !!partner.id),
        switchMap(([partner, version, projectId]) => this.projectLumpSumService.getProjectLumpSumsTotalForPartner(partner.id, projectId, version)),
        shareReplay(1)
      );
  }
}
