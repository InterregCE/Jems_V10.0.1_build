import {Injectable} from '@angular/core';
import {combineLatest, forkJoin, Observable, of, Subject} from 'rxjs';
import {BudgetOptions} from '../../model/budget/budget-options';
import {CallFlatRateSetting} from '../../model/call-flat-rate-setting';
import {filter, map, share, shareReplay, startWith, switchMap, tap, withLatestFrom} from 'rxjs/operators';
import {ProjectStore} from '../../project-application/containers/project-application-detail/services/project-store.service';
import {
  BaseBudgetEntryDTO,
  BudgetGeneralCostEntryDTO,
  BudgetStaffCostEntryDTO,
  BudgetTravelAndAccommodationCostEntryDTO,
  BudgetUnitCostEntryDTO,
  CallService,
  OutputCall,
  OutputProjectPartnerDetail,
  OutputProjectPeriod,
  ProgrammeFundDTO,
  ProgrammeUnitCostDTO,
  ProjectPartnerBudgetOptionsDto,
  ProjectPartnerBudgetService,
  ProjectPartnerCoFinancingAndContributionInputDTO,
  ProjectPartnerCoFinancingAndContributionOutputDTO
} from '@cat/api';
import {ProjectPartnerStore} from '../../project-application/containers/project-application-form-page/services/project-partner-store.service';
import {NumberService} from '../../../common/services/number.service';
import {PartnerBudgetTables} from '../../model/budget/partner-budget-tables';
import {StaffCostsBudgetTable} from '../../model/budget/staff-costs-budget-table';
import {GeneralBudgetTable} from '../../model/budget/general-budget-table';
import {StaffCostsBudgetTableEntry} from '../../model/budget/staff-costs-budget-table-entry';
import {GeneralBudgetTableEntry} from '../../model/budget/general-budget-table-entry';
import {TravelAndAccommodationCostsBudgetTable} from '../../model/budget/travel-and-accommodation-costs-budget-table';
import {TravelAndAccommodationCostsBudgetTableEntry} from '../../model/budget/travel-and-accommodation-costs-budget-table-entry';
import {ProjectWorkPackagePageStore} from '../../work-package/work-package-detail-page/project-work-package-page-store.service';
import {UnitCostsBudgetTable} from '../../model/budget/unit-costs-budget-table';
import {UnitCostsBudgetTableEntry} from '../../model/budget/unit-costs-budget-table-entry';
import {InvestmentSummary} from '../../work-package/work-package-detail-page/workPackageInvestment';

@Injectable()
export class ProjectPartnerDetailPageStore {

  callFlatRatesSettings$: Observable<CallFlatRateSetting>;
  budgetOptions$: Observable<BudgetOptions>;
  budgets$: Observable<PartnerBudgetTables>;
  totalBudget$: Observable<number>;
  isProjectEditable$: Observable<boolean>;
  investmentSummaries$: Observable<InvestmentSummary[]>;
  unitCosts$: Observable<ProgrammeUnitCostDTO[]>;
  financingAndContribution$: Observable<ProjectPartnerCoFinancingAndContributionOutputDTO>;
  callFunds$: Observable<ProgrammeFundDTO[]>;
  periods$: Observable<OutputProjectPeriod[]>;
  multipleFundsAllowed$: Observable<boolean>;

  private updateBudgetOptionsEvent$ = new Subject();
  private updateBudgetEvent$ = new Subject();
  private updateFinancingAndContributionEvent = new Subject();

  constructor(private projectStore: ProjectStore,
              private partnerStore: ProjectPartnerStore,
              private callService: CallService,
              private projectWorkPackagePageStore: ProjectWorkPackagePageStore,
              private projectPartnerBudgetService: ProjectPartnerBudgetService
  ) {
    this.investmentSummaries$ = this.projectWorkPackagePageStore.projectInvestmentSummaries$.pipe(shareReplay(1));
    this.unitCosts$ = this.projectStore.projectCall$.pipe(
      map(projectCall => projectCall.unitCosts),
      shareReplay(1)
    );
    this.budgets$ = this.budgets();
    this.budgetOptions$ = this.budgetOptions();
    this.callFlatRatesSettings$ = this.callFlatRateSettings();
    this.totalBudget$ = this.totalBudget();
    this.financingAndContribution$ = this.financingAndContribution();
    this.callFunds$ = this.callFunds();
    this.isProjectEditable$ = this.projectStore.projectEditable$;
    this.periods$ = this.projectStore.getProject()
      .pipe(
        map(project => project.periods)
      );
    this.multipleFundsAllowed$ = this.projectStore.projectCall$.pipe(map(it => it.multipleFundsAllowed));
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

  private getBudgetsToSave(partner: OutputProjectPartnerDetail, newBudgets: PartnerBudgetTables, options: BudgetOptions): { [key: string]: Observable<any> } {
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

  private callFunds(): Observable<ProgrammeFundDTO[]> {
    return this.projectStore.getProject()
      .pipe(
        map(project => project.callSettings.callId),
        switchMap(callId => this.callService.getCallById(callId)),
        map((call: OutputCall) => call.funds),
        map((funds: ProgrammeFundDTO[]) => [...funds].sort((a, b) => (a.id > b.id) ? 1 : -1)),
      );
  }

  private financingAndContribution(): Observable<ProjectPartnerCoFinancingAndContributionOutputDTO> {
    return combineLatest([this.updateFinancingAndContributionEvent.pipe(startWith(null)), this.partnerStore.partner$])
      .pipe(
        map(([, partner]) => partner),
        filter(partner => !!partner.id),
        switchMap(partner =>
          this.projectPartnerBudgetService.getProjectPartnerCoFinancing(partner.id)
        ),
      );
  }

  private budgetOptions(): Observable<BudgetOptions> {
    return combineLatest([this.updateBudgetOptionsEvent$.pipe(startWith(null)), this.partnerStore.partner$]).pipe(
      map(([, partner]) => partner),
      filter(partner => !!partner.id),
      map(partner => partner.id),
      switchMap(id =>
        this.projectPartnerBudgetService.getBudgetOptions(id)
      ),
      map((it: ProjectPartnerBudgetOptionsDto) => new BudgetOptions(it.officeAndAdministrationOnStaffCostsFlatRate, it.officeAndAdministrationOnDirectCostsFlatRate, it.staffCostsFlatRate, it.travelAndAccommodationOnStaffCostsFlatRate, it.otherCostsOnStaffCostsFlatRate)),
      shareReplay(1)
    );
  }

  private budgets(): Observable<PartnerBudgetTables> {
    return combineLatest([this.updateBudgetEvent$.pipe(startWith(null)), this.updateBudgetOptionsEvent$.pipe(startWith(null)), this.partnerStore.partner$]).pipe(
      map(([, , partner]) => partner),
      filter(partner => !!partner.id),
      map(partner => partner.id),
      switchMap(id => this.projectPartnerBudgetService.getBudgetCosts(id)),
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
      this.updateBudgetOptionsEvent$.pipe(startWith(null)),
      this.updateBudgetEvent$.pipe(startWith(null))
    ])
      .pipe(
        switchMap(([partner]) => partner?.id ? this.projectPartnerBudgetService.getTotal(partner.id) : of(0)),
        map(total => NumberService.truncateNumber(total)),
        shareReplay(1)
      );
  }

  private toBudgetStaffCostEntryDTOArray(table: StaffCostsBudgetTable): BudgetStaffCostEntryDTO[] {
    return table.entries.map(entry => ({
      id: entry.id as any,
      description: entry.description as any,
      type: entry.type,
      unitType: entry.unitType,
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
}
