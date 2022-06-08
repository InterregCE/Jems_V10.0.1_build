import {Injectable} from '@angular/core';
import {
  ProjectPartnerStore
} from '@project/project-application/containers/project-application-form-page/services/project-partner-store.service';
import {
  BaseBudgetEntryDTO,
  BudgetGeneralCostEntryDTO,
  BudgetSpfCostEntryDTO,
  BudgetStaffCostEntryDTO,
  BudgetTravelAndAccommodationCostEntryDTO,
  BudgetUnitCostEntryDTO,
  ProjectPartnerBudgetOptionsDto,
  ProjectPartnerBudgetService,
  ProjectPartnerDetailDTO,
} from '@cat/api';
import {ProjectVersionStore} from '@project/common/services/project-version-store.service';
import {filter, map, share, shareReplay, startWith, switchMap, tap, withLatestFrom} from 'rxjs/operators';
import {combineLatest, forkJoin, Observable, of, Subject} from 'rxjs';
import {PartnerBudgetTables} from '@project/model/budget/partner-budget-tables';
import {StaffCostsBudgetTable} from '@project/model/budget/staff-costs-budget-table';
import {StaffCostsBudgetTableEntry} from '@project/model/budget/staff-costs-budget-table-entry';
import {
  TravelAndAccommodationCostsBudgetTable
} from '@project/model/budget/travel-and-accommodation-costs-budget-table';
import {
  TravelAndAccommodationCostsBudgetTableEntry
} from '@project/model/budget/travel-and-accommodation-costs-budget-table-entry';
import {UnitCostsBudgetTable} from '@project/model/budget/unit-costs-budget-table';
import {UnitCostsBudgetTableEntry} from '@project/model/budget/unit-costs-budget-table-entry';
import {GeneralBudgetTable} from '@project/model/budget/general-budget-table';
import {GeneralBudgetTableEntry} from '@project/model/budget/general-budget-table-entry';
import {NumberService} from '@common/services/number.service';
import {BudgetOptions} from '@project/model/budget/budget-options';
import {Log} from '@common/utils/log';
import {SpfPartnerBudgetTableEntry} from '@project/model/budget/spf-partner-budget-table-entry';
import {SpfPartnerBudgetTable} from '@project/model/budget/spf-partner-budget-table';
import {PartnerBudgetSpfTables} from '@project/model/budget/partner-budget-spf-tables';

@Injectable({providedIn: 'root'})
export class ProjectPartnerBudgetStore {
  budgets$: Observable<PartnerBudgetTables>;
  spfBudgets$: Observable<PartnerBudgetSpfTables>;
  budgetOptions$: Observable<BudgetOptions>;
  totalBudget$: Observable<number>;
  totalSpfBudget$: Observable<number>;

  updateBudgetOptionsEvent$ = new Subject();
  private updateBudgetEvent$ = new Subject();

  constructor(private partnerStore: ProjectPartnerStore,
              private projectPartnerBudgetService: ProjectPartnerBudgetService,
              private projectVersionStore: ProjectVersionStore) {
    this.budgets$ = this.budgets();
    this.spfBudgets$ = this.spfBudgets();
    this.budgetOptions$ = this.budgetOptions();
    this.totalBudget$ = this.totalBudget();
    this.totalSpfBudget$ = this.totalSpfBudget();
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

  updateSpfBudgets(budgets: PartnerBudgetSpfTables): Observable<any> {
    return of(budgets).pipe(withLatestFrom(this.partnerStore.partner$, this.budgetOptions$)).pipe(
      switchMap(([newBudgets, partner, options]: any) =>
        forkJoin(this.getSpfBudgetsToSave(partner, newBudgets, options))),
      tap(() => this.updateBudgetEvent$.next(true)),
      share()
    );
  }

  private budgets(): Observable<PartnerBudgetTables> {
    return combineLatest([
      this.partnerStore.partner$,
      this.projectVersionStore.selectedVersionParam$,
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
        tap(fetched => Log.info('Fetched budget data:', this, fetched)),
        shareReplay(1)
      );
  }

  private spfBudgets(): Observable<PartnerBudgetSpfTables> {
    return combineLatest([
      this.partnerStore.partner$,
      this.projectVersionStore.selectedVersionParam$,
      this.updateBudgetEvent$.pipe(startWith(null)),
      this.updateBudgetOptionsEvent$.pipe(startWith(null))
    ])
      .pipe(
        filter(([partner]) => !!partner.id),
        switchMap(([partner, version]) => this.projectPartnerBudgetService.getBudgetCosts(partner.id, version)),
        map(data => new PartnerBudgetSpfTables(
          this.toSpfCostsTable(data.spfCosts)
        )),
        tap(fetched => Log.info('Fetched budget data:', this, fetched)),
        shareReplay(1)
      );
  }

  private budgetOptions(): Observable<BudgetOptions> {
    return combineLatest([
      this.partnerStore.partner$,
      this.projectVersionStore.selectedVersionParam$,
      this.updateBudgetOptionsEvent$.pipe(startWith(null))
    ])
      .pipe(
        filter(([partner, version]) => !!partner.id),
        map(([partner, version]) => [partner.id, version]),
        switchMap(([partnerId, version]) => this.getBudgetOptions(partnerId as number, version as string)),
        map((it: ProjectPartnerBudgetOptionsDto) => BudgetOptions.fromDto(it)),
        shareReplay(1)
      );
  }

  getBudgetOptions(partnerId: number, version: string): Observable<any> {
    return this.projectPartnerBudgetService.getBudgetOptions(partnerId, version);
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

  private getSpfBudgetsToSave(partner: ProjectPartnerDetailDTO, newBudgets: PartnerBudgetSpfTables, options: BudgetOptions): { [key: string]: Observable<any> } {
    return {
      spf: this.projectPartnerBudgetService.updateBudgetSpfCosts(partner.id, this.toGeneralBudgetEntryDTOArray(newBudgets.spfCosts)),
    };
  }

  private totalBudget(): Observable<number> {
    return combineLatest([
      this.partnerStore.partner$,
      this.projectVersionStore.selectedVersionParam$,
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

  private totalSpfBudget(): Observable<number> {
    return combineLatest([
      this.partnerStore.partner$,
      this.projectVersionStore.selectedVersionParam$,
      this.updateBudgetOptionsEvent$.pipe(startWith(null)),
      this.updateBudgetEvent$.pipe(startWith(null))
    ])
      .pipe(
        switchMap(
          ([partner, version]) => partner?.id ? this.projectPartnerBudgetService.getSpfTotal(partner.id, version) : of(0)
        ),
        map(total => NumberService.truncateNumber(total)),
        shareReplay(1)
      );
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

  private toSpfCostsTable(rawEntries: BudgetSpfCostEntryDTO[]): SpfPartnerBudgetTable {
    const entries = rawEntries.map(entry => new SpfPartnerBudgetTableEntry({...entry}));
    return new SpfPartnerBudgetTable(this.calculateTableTotal(rawEntries), entries);
  }

  private calculateTableTotal(rawEntries: BaseBudgetEntryDTO[]): number {
    return NumberService.truncateNumber(NumberService.sum(rawEntries.map(entry => entry.rowSum || 0)));
  }

  private calculateUnitCostTableTotal(rawEntries: BudgetUnitCostEntryDTO[]): number {
    return NumberService.truncateNumber(NumberService.sum(rawEntries.map(entry => entry.rowSum || 0)));
  }

  private toBudgetStaffCostEntryDTOArray(table: StaffCostsBudgetTable): BudgetStaffCostEntryDTO[] {
    return table.entries.map(entry => ({
      id: entry.id as any,
      description: entry.description as any,
      unitType: entry.unitType as any,
      unitCostId: entry.unitCostId,
      comments: entry.comments as any,
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
      comments: entry.comments as any,
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
      comments: entry.comments as any,
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
}
