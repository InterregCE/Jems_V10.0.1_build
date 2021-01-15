import {Injectable} from '@angular/core';
import {combineLatest, forkJoin, Observable, of, Subject} from 'rxjs';
import {BudgetOptions} from '../../project-application/model/budget-options';
import {CallFlatRateSetting} from '../../project-application/model/call-flat-rate-setting';
import {filter, map, share, shareReplay, startWith, switchMap, tap, withLatestFrom} from 'rxjs/operators';
import {ProjectStore} from '../../project-application/containers/project-application-detail/services/project-store.service';
import {
  BaseBudgetEntryDTO,
  BudgetGeneralCostEntryDTO,
  BudgetTravelAndAccommodationCostEntryDTO,
  BudgetUnitCostEntryDTO,
  ProgrammeUnitCostDTO,
  ProjectCallSettingsDTO,
  ProjectPartnerBudgetOptionsDto,
  ProjectPartnerBudgetService
} from '@cat/api';
import {ProjectPartnerStore} from '../../project-application/containers/project-application-form-page/services/project-partner-store.service';
import {NumberService} from '../../../common/services/number.service';
import {PartnerBudgetTables} from '../../project-application/model/partner-budget-tables';
import {StaffCostsBudgetTable} from '../../project-application/model/staff-costs-budget-table';
import {GeneralBudgetTable} from '../../project-application/model/general-budget-table';
import {StaffCostsBudgetTableEntry} from '../../project-application/model/staff-costs-budget-table-entry';
import {GeneralBudgetTableEntry} from '../../project-application/model/general-budget-table-entry';
import {TravelAndAccommodationCostsBudgetTable} from '../../project-application/model/travel-and-accommodation-costs-budget-table';
import {TravelAndAccommodationCostsBudgetTableEntry} from '../../project-application/model/travel-and-accommodation-costs-budget-table-entry';
import {ProjectWorkPackagePageStore} from '../../work-package/work-package-detail-page/project-work-package-page-store.service';
import {BudgetStaffCostEntryDTO} from 'build/generated-sources/openapi/model/budgetStaffCostEntryDTO';
import {UnitCostsBudgetTable} from '../../project-application/model/unit-costs-budget-table';
import {UnitCostsBudgetTableEntry} from '../../project-application/model/unit-costs-budget-table-entry';

@Injectable()
export class ProjectPartnerDetailPageStore {

  callFlatRatesSettings$: Observable<CallFlatRateSetting>;
  budgetOptions$: Observable<BudgetOptions>;
  budgets$: Observable<PartnerBudgetTables>;
  totalBudget$: Observable<number>;
  isProjectEditable$: Observable<boolean>;
  investmentIds$: Observable<number[]>;
  unitCosts$: Observable<ProgrammeUnitCostDTO[]>;

  private updateBudgetOptionsEvent$ = new Subject();
  private updateBudgetEvent$ = new Subject();

  constructor(private projectStore: ProjectStore,
              private partnerStore: ProjectPartnerStore,
              private projectWorkPackagePageStore: ProjectWorkPackagePageStore,
              private projectPartnerBudgetService: ProjectPartnerBudgetService
  ) {
    this.investmentIds$ = this.projectWorkPackagePageStore.workPackageInvestmentIdsOfProject$.pipe(shareReplay(1));
    this.unitCosts$ = this.projectStore.projectCall$.pipe(
      map(projectCall => projectCall.unitCosts),
      shareReplay(1)
    );
    this.budgets$ = this.budgets();
    this.budgetOptions$ = this.budgetOptions();
    this.callFlatRatesSettings$ = this.callFlatRateSettings();
    this.totalBudget$ = this.totalBudget();
    this.isProjectEditable$ = this.projectStore.projectEditable$;
  }

  updateBudgetOptions(budgetOptions: BudgetOptions): Observable<any> {
    return of(budgetOptions).pipe(withLatestFrom(this.partnerStore.partner$)).pipe(
      switchMap(([options, partner]) => this.projectPartnerBudgetService.updateBudgetOptions(partner.id, {
        officeAndAdministrationOnStaffCostsFlatRate: options.officeAndAdministrationOnStaffCostsFlatRate,
        staffCostsFlatRate: options.staffCostsFlatRate,
        travelAndAccommodationOnStaffCostsFlatRate: options.travelAndAccommodationOnStaffCostsFlatRate,
        otherCostsOnStaffCostsFlatRate: options.otherCostsOnStaffCostsFlatRate
      } as ProjectPartnerBudgetOptionsDto)),
      tap(() => this.updateBudgetOptionsEvent$.next()),
      share()
    );
  }

  updateBudgets(budgets: PartnerBudgetTables): Observable<any> {
    return of(budgets).pipe(withLatestFrom(this.partnerStore.partner$)).pipe(
      switchMap(([newBudgets, partner]: any) =>
        forkJoin({
          staff: this.projectPartnerBudgetService.updateBudgetStaffCosts(partner.id, this.toBudgetStaffCostEntryDTOArray(newBudgets.staffCosts)),
          travel: this.projectPartnerBudgetService.updateBudgetTravel(partner.id, this.toBudgetTravelAndAccommodationCostEntryDTOArray(newBudgets.travelCosts)),
          external: this.projectPartnerBudgetService.updateBudgetExternal(partner.id, this.toGeneralBudgetEntryDTOArray(newBudgets.externalCosts)),
          equipment: this.projectPartnerBudgetService.updateBudgetEquipment(partner.id, this.toGeneralBudgetEntryDTOArray(newBudgets.equipmentCosts)),
          infrastructure: this.projectPartnerBudgetService.updateBudgetInfrastructure(partner.id, this.toGeneralBudgetEntryDTOArray(newBudgets.infrastructureCosts)),
          unitCosts: this.projectPartnerBudgetService.updateBudgetUnitCosts(partner.id, this.toBudgetUnitCostEntryDTOArray(newBudgets.unitCosts))
        })),
      tap(() => this.updateBudgetEvent$.next(true)),
      share()
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
      map((it: ProjectPartnerBudgetOptionsDto) => new BudgetOptions(it.officeAndAdministrationOnStaffCostsFlatRate, it.staffCostsFlatRate, it.travelAndAccommodationOnStaffCostsFlatRate, it.otherCostsOnStaffCostsFlatRate)),
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
      map((call: ProjectCallSettingsDTO) =>
        new CallFlatRateSetting(
          call.flatRates.staffCostFlatRateSetup,
          call.flatRates.officeAndAdministrationOnStaffCostsFlatRate,
          call.flatRates.officeAndAdministrationOnOtherCostsFlatRateSetup,
          call.flatRates.travelAndAccommodationOnStaffCostsFlatRateSetup,
          call.flatRates.otherCostsOnStaffCostsFlatRateSetup)
      ),
    );
  }

  private totalBudget(): Observable<number> {
    return combineLatest([this.partnerStore.partner$, this.updateBudgetOptionsEvent$.pipe(startWith(null)), this.updateBudgetEvent$.pipe(startWith(null))]).pipe(
      switchMap(([partner]) => this.projectPartnerBudgetService.getTotal(partner.id)),
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
      rowSum: entry.rowSum
    } as BudgetStaffCostEntryDTO));
  }

  private toBudgetTravelAndAccommodationCostEntryDTOArray(table: TravelAndAccommodationCostsBudgetTable): BudgetTravelAndAccommodationCostEntryDTO[] {
    return table.entries.map(entry => ({
      id: entry.id as any,
      description: entry.description as any,
      unitType: entry.unitType as any,
      numberOfUnits: entry.numberOfUnits as any,
      pricePerUnit: entry.pricePerUnit as any,
      rowSum: entry.rowSum
    } as BudgetTravelAndAccommodationCostEntryDTO));
  }

  private toBudgetUnitCostEntryDTOArray(table: UnitCostsBudgetTable): BudgetUnitCostEntryDTO[] {
    return table.entries.map(entry => ({
      id: entry.id as any,
      unitCostId: entry.unitCostId as any,
      numberOfUnits: entry.numberOfUnits as any,
      rowSum: entry.rowSum
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
      rowSum: entry.rowSum
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
