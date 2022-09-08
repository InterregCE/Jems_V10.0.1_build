import {Injectable} from '@angular/core';
import {combineLatest, Observable} from 'rxjs';
import {BudgetOptions} from '../../model/budget/budget-options';
import {CallFlatRateSetting} from '../../model/call-flat-rate-setting';
import {filter, map, shareReplay, startWith, switchMap} from 'rxjs/operators';
import {
  ProjectStore
} from '../../project-application/containers/project-application-detail/services/project-store.service';
import {
  CallFundRateDTO,
  CallService,
  ProjectBudgetService,
  ProjectCostOptionService,
  ProjectLumpSumService,
  ProjectPartnerBudgetService,
  ProjectPartnerCoFinancingAndContributionInputDTO,
  ProjectPartnerCoFinancingAndContributionOutputDTO,
  ProjectPartnerDetailDTO,
  ProjectPartnerService,
  ProjectPartnerStateAidDTO,
  ProjectPeriodDTO,
  ProjectStatusDTO
} from '@cat/api';
import {
  ProjectPartnerStore
} from '../../project-application/containers/project-application-form-page/services/project-partner-store.service';
import {NumberService} from '@common/services/number.service';
import {PartnerBudgetTables} from '../../model/budget/partner-budget-tables';
import {
  WorkPackagePageStore
} from '../../work-package/project-work-package-page/work-package-detail-page/work-package-page-store.service';
import {
  InvestmentSummary
} from '../../work-package/project-work-package-page/work-package-detail-page/workPackageInvestment';
import {ProgrammeUnitCost} from '../../model/programmeUnitCost';
import {ProjectVersionStore} from '../../common/services/project-version-store.service';
import {AllowedBudgetCategories} from '@project/model/allowed-budget-category';
import {ProjectPartnerBudgetStore} from '@project/budget/services/project-partner-budget.store';
import {
  ProjectPartnerCoFinancingStore
} from '@project/partner/project-partner-detail-page/project-partner-co-financing-tab/services/project-partner-co-financing.store';
import {ProjectPartnerStateAidsStore} from '@project/partner/services/project-partner-state-aids.store';
import {PartnerLumpSum} from '@project/model/lump-sums/partnerLumpSum';
import {ProjectLumpSumsStore} from '@project/lump-sums/project-lump-sums-page/project-lump-sums-store.service';
import {ProgrammeLumpSum} from '@project/model/lump-sums/programmeLumpSum';
import {PartnerBudgetSpfTables} from '@project/model/budget/partner-budget-spf-tables';
import {ProjectUtil} from '@project/common/project-util';
import {
  ProjectPartnerCoFinancingSpfStore
} from './project-partner-co-financing-spf-tab/project-partner-co-financing-spf.store';
import {BudgetCostCategoryEnumUtils} from '@project/model/lump-sums/BudgetCostCategoryEnum';

@Injectable()
export class ProjectPartnerDetailPageStore {
  callFlatRatesSettings$: Observable<CallFlatRateSetting>;
  projectCallLumpSums$: Observable<ProgrammeLumpSum[]>;
  budgetOptions$: Observable<BudgetOptions>;
  partnerLumpSums$: Observable<PartnerLumpSum[]>;
  partnerTotalLumpSum$: Observable<number>;
  budgets$: Observable<PartnerBudgetTables>;
  spfBudgets$: Observable<PartnerBudgetSpfTables>;
  totalBudget$: Observable<number>;
  totalSpfBudget$: Observable<number>;
  isProjectEditable$: Observable<boolean>;
  investmentSummaries$: Observable<InvestmentSummary[]>;
  unitCosts$: Observable<ProgrammeUnitCost[]>;
  financingAndContribution$: Observable<ProjectPartnerCoFinancingAndContributionOutputDTO>;
  coFinancingSpf$: Observable<ProjectPartnerCoFinancingAndContributionOutputDTO>;
  callFunds$: Observable<Map<number, CallFundRateDTO>>;
  periods$: Observable<ProjectPeriodDTO[]>;
  multipleFundsAllowed$: Observable<boolean>;
  stateAid$: Observable<ProjectPartnerStateAidDTO>;
  partner$: Observable<ProjectPartnerDetailDTO>;
  allowedBudgetCategories$: Observable<AllowedBudgetCategories>;
  canChangeContractedFlatRates$: Observable<boolean>;

  constructor(
    private projectStore: ProjectStore,
    private partnerStore: ProjectPartnerStore,
    private callService: CallService,
    private projectWorkPackagePageStore: WorkPackagePageStore,
    private projectPartnerBudgetService: ProjectPartnerBudgetService,
    private projectPartnerService: ProjectPartnerService,
    private projectLumpSumService: ProjectLumpSumService,
    private projectVersionStore: ProjectVersionStore,
    private projectPartnerBudgetStore: ProjectPartnerBudgetStore,
    private projectLumpSumsStore: ProjectLumpSumsStore,
    private projectBudgetService: ProjectBudgetService,
    private projectCostOptionService: ProjectCostOptionService,
    private projectPartnerCoFinancingStore: ProjectPartnerCoFinancingStore,
    private projectPartnerCoFinancingSpfStore: ProjectPartnerCoFinancingSpfStore,
    private projectPartnerStateAidsStore: ProjectPartnerStateAidsStore,
  ) {
    this.investmentSummaries$ = this.projectStore.investmentSummaries$;
    this.unitCosts$ = this.projectUnitCosts();
    this.budgets$ = this.projectPartnerBudgetStore.budgets$;
    this.spfBudgets$ = this.projectPartnerBudgetStore.spfBudgets$;
    this.budgetOptions$ = this.projectPartnerBudgetStore.budgetOptions$;
    this.callFlatRatesSettings$ = this.callFlatRateSettings();
    this.totalBudget$ = this.projectPartnerBudgetStore.totalBudget$;
    this.totalSpfBudget$ = this.projectPartnerBudgetStore.totalSpfBudget$;
    this.financingAndContribution$ = this.projectPartnerCoFinancingStore.financingAndContribution$;
    this.coFinancingSpf$ = this.projectPartnerCoFinancingSpfStore.financingAndContribution$;
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
    this.canChangeContractedFlatRates$ = this.canChangeContractedFlatRates();
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

  updateSpfBudgets(budgets: PartnerBudgetSpfTables): Observable<any> {
    return this.projectPartnerBudgetStore.updateSpfBudgets(budgets);
  }

  updateCoFinancingAndContributions(model: ProjectPartnerCoFinancingAndContributionInputDTO): Observable<any> {
    return this.projectPartnerCoFinancingStore.updateCoFinancingAndContributions(model);
  }

  updateCoFinancingSpf(model: ProjectPartnerCoFinancingAndContributionInputDTO): Observable<any> {
    return this.projectPartnerCoFinancingSpfStore.updateCoFinancingAndContributions(model);
  }

  updateStateAid(partnerId: number, stateAid: ProjectPartnerStateAidDTO): Observable<ProjectPartnerStateAidDTO> {
    return this.projectPartnerStateAidsStore.updateStateAid(partnerId, stateAid);
  }

  private static sortById(a: CallFundRateDTO, b: CallFundRateDTO): number {
    return (a.programmeFund.id > b.programmeFund.id) ? 1 : -1;
  }

  private callFunds(): Observable<Map<number, CallFundRateDTO>> {
    return this.projectStore.project$
      .pipe(
        map(project => project.callSettings.callId),
        switchMap(callId => this.callService.getCallById(callId)),
        map(call => new Map(call.funds
            .sort(ProjectPartnerDetailPageStore.sortById)
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

  private projectUnitCosts(): Observable<ProgrammeUnitCost[]> {
    return combineLatest([
      this.projectStore.projectId$,
      this.projectVersionStore.selectedVersion$
    ]).pipe(
      switchMap(([projectId, version]) => this.projectCostOptionService.getProjectAvailableUnitCosts(projectId, version?.version)),
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

  private partnerLumpSums(): Observable<PartnerLumpSum[]> {
    return combineLatest([this.partner$, this.projectLumpSumsStore.projectLumpSums$, this.projectLumpSumsStore.projectCallLumpSums$]).pipe(
      map(([partner, projectLumpSums, callLumpSums]) =>
        projectLumpSums.filter(it => it.lumpSumContributions.some(contribution => contribution.partnerId === partner.id && contribution.amount > 0))
          .map(lumpSum => {
            const callLumpSum = callLumpSums.find(it => it.id === lumpSum.programmeLumpSumId);
            return new PartnerLumpSum(
              callLumpSum?.name || [],
              callLumpSum?.description || [],
              callLumpSum?.cost,
              lumpSum.period,
              lumpSum.lumpSumContributions.find(it => it.partnerId === partner.id)?.amount || 0
            );
          })
      ),
      shareReplay(1)
    );
  }

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

  private canChangeContractedFlatRates(): Observable<boolean> {
    return combineLatest([
      this.partnerStore.partner$,
      this.projectStore.currentVersionOfProjectStatus$,
      this.projectVersionStore.versions$
    ]).pipe(
      map(([partner, status, versions]) => {
        const isContracted = versions.find(version => version.status === ProjectStatusDTO.StatusEnum.CONTRACTED);
        if (!isContracted) {
          return true;
        }
        const latestContracted = versions.find(version => ProjectUtil.isContractedOrAnyStatusAfterContracted(version.status));
        return !latestContracted || partner.createdAt > latestContracted.createdAt;
      }),
    );
  }
}
