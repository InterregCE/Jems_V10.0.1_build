import {Injectable} from '@angular/core';
import {combineLatest, forkJoin, Observable, of, Subject} from 'rxjs';
import {BudgetOptions} from '../../project-application/model/budget-options';
import {CallFlatRateSetting} from '../../project-application/model/call-flat-rate-setting';
import {filter, map, share, shareReplay, startWith, switchMap, tap, withLatestFrom} from 'rxjs/operators';
import {ProjectStore} from '../../project-application/containers/project-application-detail/services/project-store.service';
import {
  InputBudget,
  InputGeneralBudget,
  InputStaffCostBudget,
  InputTravelBudget,
  OutputCallWithDates,
  ProjectPartnerBudgetOptionsDto,
  ProjectPartnerBudgetService,
  WorkPackageInvestmentDTO
} from '@cat/api';
import {ProjectPartnerStore} from '../../project-application/containers/project-application-form-page/services/project-partner-store.service';
import {Log} from '../../../common/utils/log';
import {NumberService} from '../../../common/services/number.service';
import {PartnerBudgetTables} from '../../project-application/model/partner-budget-tables';
import {StaffCostsBudgetTable} from '../../project-application/model/staff-costs-budget-table';
import {GeneralBudgetTable} from '../../project-application/model/general-budget-table';
import {StaffCostsBudgetTableEntry} from '../../project-application/model/staff-costs-budget-table-entry';
import {GeneralBudgetTableEntry} from '../../project-application/model/general-budget-table-entry';
import {TravelAndAccommodationCostsBudgetTable} from '../../project-application/model/travel-and-accommodation-costs-budget-table';
import {TravelAndAccommodationCostsBudgetTableEntry} from '../../project-application/model/travel-and-accommodation-costs-budget-table-entry';

@Injectable()
export class ProjectPartnerDetailPageStore {

  callFlatRatesSettings$: Observable<CallFlatRateSetting>;
  budgetOptions$: Observable<BudgetOptions>;
  budgets$: Observable<PartnerBudgetTables>;
  totalBudget$: Observable<number>;
  isProjectEditable$: Observable<boolean>;
  investments$: Observable<WorkPackageInvestmentDTO[]>;

  private updateBudgetOptionsEvent$ = new Subject();
  private updateBudgetEvent$ = new Subject();

  constructor(private projectStore: ProjectStore,
              private partnerStore: ProjectPartnerStore,
              private projectPartnerBudgetService: ProjectPartnerBudgetService
  ) {
    // todo this should be updated after #MP2-920
    this.investments$ = of([]).pipe(shareReplay(1));
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
          staff: this.projectPartnerBudgetService.updateBudgetStaffCosts(partner.id, this.toInputStaffCostBudgetArray(newBudgets.staffCosts)),
          travel: this.projectPartnerBudgetService.updateBudgetTravel(partner.id, this.toInputTravelBudgetArray(newBudgets.travelCosts)),
          external: this.projectPartnerBudgetService.updateBudgetExternal(partner.id, this.toInputGeneralBudgetArray(newBudgets.externalCosts)),
          equipment: this.projectPartnerBudgetService.updateBudgetEquipment(partner.id, this.toInputGeneralBudgetArray(newBudgets.equipmentCosts)),
          infrastructure: this.projectPartnerBudgetService.updateBudgetInfrastructure(partner.id, this.toInputGeneralBudgetArray(newBudgets.infrastructureCosts)),
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
      switchMap(id =>
        forkJoin({
          staffCostsTable: this.projectPartnerBudgetService.getBudgetStaffCosts(id).pipe(
            tap(staff => Log.info('Fetched the staff budget', this, staff)),
            map(staff => this.toStaffCostsTable(staff)),
          ),
          travelCostsTable: this.projectPartnerBudgetService.getBudgetTravel(id).pipe(
            tap(travel => Log.info('Fetched the travel budget', this, travel)),
            map(travel => this.toTravelAndAccommodationCostsTable(travel))),

          externalCostsTable: this.projectPartnerBudgetService.getBudgetExternal(id).pipe(
            tap(external => Log.info('Fetched the external budget', this, external)),
            map(external => this.toBudgetTable(external))
          ),
          equipmentCostsTable: this.projectPartnerBudgetService.getBudgetEquipment(id).pipe(
            tap(equipment => Log.info('Fetched the equipment budget', this, equipment)),
            map(equipment => this.toBudgetTable(equipment))
          ),
          infrastructureCostsTable: this.projectPartnerBudgetService.getBudgetInfrastructure(id).pipe(
            tap(infrastructure => Log.info('Fetched the infrastructure budget', this, infrastructure)),
            map(infrastructure => this.toBudgetTable(infrastructure))
          ),
        })
      ),
      map(data => new PartnerBudgetTables(data.staffCostsTable, data.travelCostsTable, data.externalCostsTable, data.equipmentCostsTable, data.infrastructureCostsTable)),
      shareReplay(1)
    );

  }

  private callFlatRateSettings(): Observable<CallFlatRateSetting> {
    return this.projectStore.projectCall$.pipe(
      map((call: OutputCallWithDates) =>
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

  private toInputStaffCostBudgetArray(table: StaffCostsBudgetTable): InputStaffCostBudget[] {
    return table.entries.map(entry => ({
      id: (entry.new ? null : entry.id) as any,
      description: entry.description as any,
      typeOfStaff: entry.typeOfStaff,
      unitType: entry.unitType,
      comments: entry.comments,
      numberOfUnits: entry.numberOfUnits as any,
      pricePerUnit: entry.pricePerUnit as any,
      rowSum: entry.rowSum
    } as InputStaffCostBudget));
  }

  private toInputTravelBudgetArray(table: StaffCostsBudgetTable): InputTravelBudget[] {
    return table.entries.map(entry => ({
      id: (entry.new ? null : entry.id) as any,
      description: entry.description as any,
      unitType: entry.unitType,
      numberOfUnits: entry.numberOfUnits as any,
      pricePerUnit: entry.pricePerUnit as any,
      rowSum: entry.rowSum
    } as InputTravelBudget));
  }

  private toInputGeneralBudgetArray(table: GeneralBudgetTable): InputGeneralBudget[] {
    return table.entries.map(entry => ({
      id: (entry.new ? null : entry.id) as any,
      description: entry.description as any,
      numberOfUnits: entry.numberOfUnits as any,
      pricePerUnit: entry.pricePerUnit as any,
      rowSum: entry.rowSum
    } as InputGeneralBudget));
  }

  private toStaffCostsTable(rawEntries: InputBudget[]): StaffCostsBudgetTable {
    const entries = rawEntries.map(entry => new StaffCostsBudgetTableEntry({...entry}));
    return new StaffCostsBudgetTable(this.calculateTableTotal(rawEntries), entries);
  }

  private toTravelAndAccommodationCostsTable(rawEntries: InputBudget[]): TravelAndAccommodationCostsBudgetTable {
    const entries = rawEntries.map(entry => new TravelAndAccommodationCostsBudgetTableEntry({...entry}));
    return new TravelAndAccommodationCostsBudgetTable(this.calculateTableTotal(rawEntries), entries);
  }

  private toBudgetTable(rawEntries: InputBudget[]): GeneralBudgetTable {
    const entries = rawEntries.map(entry => new GeneralBudgetTableEntry({...entry}));
    return new GeneralBudgetTable(this.calculateTableTotal(rawEntries), entries);
  }

  private calculateTableTotal(rawEntries: InputBudget[]): number {
    return NumberService.truncateNumber(NumberService.sum(rawEntries.map(entry => entry.rowSum || 0)));
  }
}
