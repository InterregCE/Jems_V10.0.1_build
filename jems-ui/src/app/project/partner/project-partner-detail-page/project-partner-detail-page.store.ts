import {Injectable} from '@angular/core';
import {combineLatest, forkJoin, merge, Observable, of, Subject} from 'rxjs';
import {BudgetOptions} from '../../model/budget/budget-options';
import {CallFlatRateSetting} from '../../model/call-flat-rate-setting';
import {
  filter,
  map,
  share,
  shareReplay,
  startWith,
  switchMap,
  tap,
  withLatestFrom
} from 'rxjs/operators';
import {ProjectStore} from '../../project-application/containers/project-application-detail/services/project-store.service';
import {
  BaseBudgetEntryDTO,
  BudgetGeneralCostEntryDTO,
  BudgetStaffCostEntryDTO,
  BudgetTravelAndAccommodationCostEntryDTO,
  BudgetUnitCostEntryDTO,
  CallDetailDTO, CallFundRateDTO,
  CallService,
  ProgrammeFundDTO, ProjectLumpSumService,
  ProjectPartnerBudgetOptionsDto,
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
import {StaffCostsBudgetTable} from '../../model/budget/staff-costs-budget-table';
import {GeneralBudgetTable} from '../../model/budget/general-budget-table';
import {StaffCostsBudgetTableEntry} from '../../model/budget/staff-costs-budget-table-entry';
import {GeneralBudgetTableEntry} from '../../model/budget/general-budget-table-entry';
import {TravelAndAccommodationCostsBudgetTable} from '../../model/budget/travel-and-accommodation-costs-budget-table';
import {TravelAndAccommodationCostsBudgetTableEntry} from '../../model/budget/travel-and-accommodation-costs-budget-table-entry';
import {WorkPackagePageStore} from '../../work-package/project-work-package-page/work-package-detail-page/work-package-page-store.service';
import {UnitCostsBudgetTable} from '../../model/budget/unit-costs-budget-table';
import {UnitCostsBudgetTableEntry} from '../../model/budget/unit-costs-budget-table-entry';
import {InvestmentSummary} from '../../work-package/project-work-package-page/work-package-detail-page/workPackageInvestment';
import {ProgrammeUnitCost} from '../../model/programmeUnitCost';
import {ProjectVersionStore} from '../../common/services/project-version-store.service';
import {Log} from '@common/utils/log';
import {AllowedBudgetCategories} from '@project/model/allowed-budget-category';

@Injectable()
export class ProjectPartnerDetailPageStore {
  callFlatRatesSettings$: Observable<CallFlatRateSetting>;
  budgetOptions$: Observable<BudgetOptions>;
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

  private updateBudgetOptionsEvent$ = new Subject();
  private updateBudgetEvent$ = new Subject();
  private updateFinancingAndContributionEvent = new Subject();
  private updatedStateAid$ = new Subject<ProjectPartnerStateAidDTO>();

  constructor(private projectStore: ProjectStore,
              private partnerStore: ProjectPartnerStore,
              private callService: CallService,
              private projectWorkPackagePageStore: WorkPackagePageStore,
              private projectPartnerBudgetService: ProjectPartnerBudgetService,
              private projectPartnerService: ProjectPartnerService,
              private projectLumpSumService: ProjectLumpSumService,
              private projectVersionStore: ProjectVersionStore) {
    this.investmentSummaries$ = this.projectStore.investmentSummaries$;
    this.unitCosts$ = this.projectStore.projectCall$.pipe(
      map(projectCall => projectCall.unitCosts),
      shareReplay(1)
    );
    this.budgets$ = this.budgets();
    this.budgetOptions$ = this.budgetOptions();
    this.partnerTotalLumpSum$ = this.partnerTotalLumpSum();
    this.callFlatRatesSettings$ = this.callFlatRateSettings();
    this.totalBudget$ = this.totalBudget();
    this.financingAndContribution$ = this.financingAndContribution();
    this.callFunds$ = this.callFunds();
    this.isProjectEditable$ = this.projectStore.projectEditable$;
    this.periods$ = this.projectStore.projectPeriods$;
    this.multipleFundsAllowed$ = this.projectStore.projectCall$.pipe(map(it => it.multipleFundsAllowed));
    this.partner$ = this.partnerStore.partner$;
    this.stateAid$ = this.stateAid();
    this.allowedBudgetCategories$ = this.projectStore.allowedBudgetCategories$;
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
    return of(budgetOptions).pipe(withLatestFrom(this.partnerStore.partner$)).pipe(
      switchMap(([options, partner]) => this.projectPartnerBudgetService.updateBudgetOptions(partner.id, {
        officeAndAdministrationOnStaffCostsFlatRate: options.officeAndAdministrationOnStaffCostsFlatRate,
        officeAndAdministrationOnDirectCostsFlatRate: options.officeAndAdministrationOnDirectCostsFlatRate,
        staffCostsFlatRate: options.staffCostsFlatRate,
        travelAndAccommodationOnStaffCostsFlatRate: options.travelAndAccommodationOnStaffCostsFlatRate,
        otherCostsOnStaffCostsFlatRate: options.otherCostsOnStaffCostsFlatRate
      } as ProjectPartnerBudgetOptionsDto)),
      tap(() => this.updateBudgetOptionsEvent$.next()),
      share()
    );
  }

  updateBudgets(budgets: PartnerBudgetTables): Observable<any> {
    return of(budgets).pipe(withLatestFrom(this.partnerStore.partner$, this.budgetOptions$)).pipe(
      switchMap(([newBudgets, partner, options]: any) =>
        forkJoin(this.getBudgetsToSave(partner, newBudgets, options))),
      tap(() => this.updateBudgetEvent$.next(true)),
      share()
    );
  }

  updateCoFinancingAndContributions(model: ProjectPartnerCoFinancingAndContributionInputDTO): Observable<any> {
    return of(model).pipe(
      withLatestFrom(this.partnerStore.partner$),
      switchMap(([finances, partner]) =>
        this.projectPartnerBudgetService.updateProjectPartnerCoFinancing(partner.id, finances)
      ),
      tap(() => this.updateFinancingAndContributionEvent.next(true)),
      share()
    );
  }

  updateStateAid(partnerId: number, stateAid: ProjectPartnerStateAidDTO): Observable<ProjectPartnerStateAidDTO> {
    return this.projectPartnerService.updateProjectPartnerStateAid(partnerId, stateAid)
      .pipe(
        tap(saved => this.updatedStateAid$.next(saved)),
        tap(saved => Log.info('Updated the partner state aid', this, saved))
      );
  }

  private getBudgetsToSave(partner: ProjectPartnerDetailDTO, newBudgets: PartnerBudgetTables, options: BudgetOptions): { [key: string]: Observable<any> } {
    if (options.otherCostsOnStaffCostsFlatRate) {
      return {staff: this.projectPartnerBudgetService.updateBudgetStaffCosts(partner.id, this.toBudgetStaffCostEntryDTOArray(newBudgets.staffCosts))};
    } else {
      const requests: any = {
        external: this.projectPartnerBudgetService.updateBudgetExternal(partner.id, this.toGeneralBudgetEntryDTOArray(newBudgets.externalCosts)),
        equipment: this.projectPartnerBudgetService.updateBudgetEquipment(partner.id, this.toGeneralBudgetEntryDTOArray(newBudgets.equipmentCosts)),
        infrastructure: this.projectPartnerBudgetService.updateBudgetInfrastructure(partner.id, this.toGeneralBudgetEntryDTOArray(newBudgets.infrastructureCosts)),
        unitCosts: this.projectPartnerBudgetService.updateBudgetUnitCosts(partner.id, this.toBudgetUnitCostEntryDTOArray(newBudgets.unitCosts))
      };
      if (!options.staffCostsFlatRate) {
        requests.staff = this.projectPartnerBudgetService.updateBudgetStaffCosts(partner.id, this.toBudgetStaffCostEntryDTOArray(newBudgets.staffCosts));
      }
      if (!options.travelAndAccommodationOnStaffCostsFlatRate) {
        requests.travel = this.projectPartnerBudgetService.updateBudgetTravel(partner.id, this.toBudgetTravelAndAccommodationCostEntryDTOArray(newBudgets.travelCosts));
      }
      return requests;
    }
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

  private financingAndContribution(): Observable<ProjectPartnerCoFinancingAndContributionOutputDTO> {
    return combineLatest([
      this.partnerStore.partner$,
      this.projectVersionStore.currentRouteVersion$,
      this.updateFinancingAndContributionEvent.pipe(startWith(null))
    ])
      .pipe(
        filter(([partner, version]) => !!partner.id),
        switchMap(([partner, version]) =>
          this.projectPartnerBudgetService.getProjectPartnerCoFinancing(partner.id, version)
        ),
        tap(financing => Log.info('Fetched partner financing and contribution', this, financing)),
        shareReplay(1)
      );
  }

  private budgetOptions(): Observable<BudgetOptions> {
    return combineLatest([
      this.partnerStore.partner$,
      this.projectVersionStore.currentRouteVersion$,
      this.updateBudgetOptionsEvent$.pipe(startWith(null))
    ])
      .pipe(
        filter(([partner, version]) => !!partner.id),
        switchMap(([partner, version]) => this.projectPartnerBudgetService.getBudgetOptions(partner.id, version)),
        map((it: ProjectPartnerBudgetOptionsDto) => new BudgetOptions(
          it.officeAndAdministrationOnStaffCostsFlatRate,
          it.officeAndAdministrationOnDirectCostsFlatRate,
          it.staffCostsFlatRate,
          it.travelAndAccommodationOnStaffCostsFlatRate,
          it.otherCostsOnStaffCostsFlatRate
        )),
        shareReplay(1)
      );
  }

  private partnerTotalLumpSum(): Observable<number> {
    return combineLatest([
      this.partnerStore.partner$,
      this.projectVersionStore.currentRouteVersion$,
      this.projectStore.projectId$,
      this.updateBudgetOptionsEvent$.pipe(startWith(null))
    ])
      .pipe(
        filter(([partner]) => !!partner.id),
        switchMap(([partner, version, projectId]) => this.projectLumpSumService.getProjectLumpSumsTotalForPartner(partner.id, projectId, version)),
        shareReplay(1)
      );
  }

  private budgets(): Observable<PartnerBudgetTables> {
    return combineLatest([
      this.partnerStore.partner$,
      this.projectVersionStore.currentRouteVersion$,
      this.updateBudgetEvent$.pipe(startWith(null)),
      this.updateBudgetOptionsEvent$.pipe(startWith(null))
    ])
      .pipe(
        filter(([partner, version]) => !!partner.id),
        switchMap(([partner, version]) => this.projectPartnerBudgetService.getBudgetCosts(partner.id, version)),
        map(data => new PartnerBudgetTables(
          this.toStaffCostsTable(data.staffCosts),
          this.toTravelAndAccommodationCostsTable(data.travelCosts),
          this.toBudgetTable(data.externalCosts),
          this.toBudgetTable(data.equipmentCosts),
          this.toBudgetTable(data.infrastructureCosts),
          this.toUnitCostsTable(data.unitCosts)
        )),
        shareReplay(1)
      );

  }

  private callFlatRateSettings(): Observable<CallFlatRateSetting> {
    return this.projectStore.projectCall$.pipe(
      map(call => call.flatRates),
    );
  }

  private totalBudget(): Observable<number> {
    return combineLatest([
      this.partnerStore.partner$,
      this.projectVersionStore.currentRouteVersion$,
      this.updateBudgetOptionsEvent$.pipe(startWith(null)),
      this.updateBudgetEvent$.pipe(startWith(null))
    ])
      .pipe(
        switchMap(
          ([partner, version]) => partner?.id ? this.projectPartnerBudgetService.getTotal(partner.id, version) : of(0)
        ),
        map(total => NumberService.truncateNumber(total)),
        shareReplay(1)
      );
  }

  private toBudgetStaffCostEntryDTOArray(table: StaffCostsBudgetTable): BudgetStaffCostEntryDTO[] {
    return table.entries.map(entry => ({
      id: entry.id as any,
      description: entry.description as any,
      unitType: entry.unitType as any,
      unitCostId: entry.unitCostId,
      comment: entry.comment,
      numberOfUnits: entry.numberOfUnits as any,
      pricePerUnit: entry.pricePerUnit as any,
      rowSum: entry.rowSum,
      budgetPeriods: entry.budgetPeriods as any,
    } as BudgetStaffCostEntryDTO));
  }

  private toBudgetTravelAndAccommodationCostEntryDTOArray(table: TravelAndAccommodationCostsBudgetTable): BudgetTravelAndAccommodationCostEntryDTO[] {
    return table.entries.map(entry => ({
      id: entry.id as any,
      description: entry.description as any,
      unitType: entry.unitType as any,
      unitCostId: entry.unitCostId,
      numberOfUnits: entry.numberOfUnits as any,
      pricePerUnit: entry.pricePerUnit as any,
      rowSum: entry.rowSum,
      budgetPeriods: entry.budgetPeriods as any,
    } as BudgetTravelAndAccommodationCostEntryDTO));
  }

  private toBudgetUnitCostEntryDTOArray(table: UnitCostsBudgetTable): BudgetUnitCostEntryDTO[] {
    return table.entries.map(entry => ({
      id: entry.id as any,
      unitCostId: entry.unitCostId as any,
      numberOfUnits: entry.numberOfUnits as any,
      rowSum: entry.rowSum,
      budgetPeriods: entry.budgetPeriods as any,
    } as BudgetUnitCostEntryDTO));
  }

  private toGeneralBudgetEntryDTOArray(table: GeneralBudgetTable): BudgetGeneralCostEntryDTO[] {
    return table.entries.map(entry => ({
      id: entry.id as any,
      description: entry.description as any,
      unitType: entry.unitType as any,
      unitCostId: entry.unitCostId,
      awardProcedures: entry.awardProcedures as any,
      investmentId: entry.investmentId as any,
      numberOfUnits: entry.numberOfUnits as any,
      pricePerUnit: entry.pricePerUnit as any,
      rowSum: entry.rowSum,
      budgetPeriods: entry.budgetPeriods as any,
    } as BudgetGeneralCostEntryDTO));
  }

  private toStaffCostsTable(rawEntries: BudgetStaffCostEntryDTO[]): StaffCostsBudgetTable {
    const entries = rawEntries.map(entry => new StaffCostsBudgetTableEntry({...entry}));
    return new StaffCostsBudgetTable(this.calculateTableTotal(rawEntries), entries);
  }

  private toTravelAndAccommodationCostsTable(rawEntries: BudgetTravelAndAccommodationCostEntryDTO[]): TravelAndAccommodationCostsBudgetTable {
    const entries = rawEntries.map(entry => new TravelAndAccommodationCostsBudgetTableEntry({...entry}));
    return new TravelAndAccommodationCostsBudgetTable(this.calculateTableTotal(rawEntries), entries);
  }

  private toUnitCostsTable(rawEntries: BudgetUnitCostEntryDTO[]): UnitCostsBudgetTable {
    const entries = rawEntries.map(entry => new UnitCostsBudgetTableEntry({...entry}, entry.unitCostId));
    return new UnitCostsBudgetTable(this.calculateUnitCostTableTotal(rawEntries), entries);
  }

  private toBudgetTable(rawEntries: BudgetGeneralCostEntryDTO[]): GeneralBudgetTable {
    const entries = rawEntries.map(entry => new GeneralBudgetTableEntry({...entry}));
    return new GeneralBudgetTable(this.calculateTableTotal(rawEntries), entries);
  }

  private calculateTableTotal(rawEntries: BaseBudgetEntryDTO[]): number {
    return NumberService.truncateNumber(NumberService.sum(rawEntries.map(entry => entry.rowSum || 0)));
  }

  private calculateUnitCostTableTotal(rawEntries: BudgetUnitCostEntryDTO[]): number {
    return NumberService.truncateNumber(NumberService.sum(rawEntries.map(entry => entry.rowSum || 0)));
  }

  private stateAid(): Observable<ProjectPartnerStateAidDTO> {
    const initialStateAid$ = combineLatest([
      this.partnerStore.partner$,
      this.projectVersionStore.currentRouteVersion$
    ])
      .pipe(
        filter(([partner]) => !!partner.id),
        switchMap(([partner, version]) => this.projectPartnerService.getProjectPartnerStateAid(partner.id, version)),
        tap(stateAid => Log.info('Fetched the partner state aid', this, stateAid)),
      );

    return merge(initialStateAid$, this.updatedStateAid$).pipe(shareReplay(1));
  }

}
