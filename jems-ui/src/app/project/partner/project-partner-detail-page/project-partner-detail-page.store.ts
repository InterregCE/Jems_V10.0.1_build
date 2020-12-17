import {Injectable} from '@angular/core';
import {combineLatest, forkJoin, Observable, of, Subject} from 'rxjs';
import {BudgetOptions} from '../../project-application/model/budget-options';
import {CallFlatRateSetting} from '../../project-application/model/call-flat-rate-setting';
import {filter, map, share, shareReplay, startWith, switchMap, tap, withLatestFrom} from 'rxjs/operators';
import {ProjectStore} from '../../project-application/containers/project-application-detail/services/project-store.service';
import {InputBudget, OutputCallWithDates, ProjectPartnerBudgetOptionsDto, ProjectPartnerBudgetService} from '@cat/api';
import {ProjectPartnerStore} from '../../project-application/containers/project-application-form-page/services/project-partner-store.service';
import {PartnerBudgetTable} from '../../project-application/model/partner-budget-table';
import {Log} from '../../../common/utils/log';
import {PartnerBudgetTableType} from '../../project-application/model/partner-budget-table-type';
import {PartnerBudgetTableEntry} from '../../project-application/model/partner-budget-table-entry';
import {NumberService} from '../../../common/services/number.service';

@Injectable()
export class ProjectPartnerDetailPageStore {

  callFlatRatesSettings$: Observable<CallFlatRateSetting>;
  budgetOptions$: Observable<BudgetOptions>;
  // todo define an object for the budget model
  budgets$: Observable<{ [key: string]: PartnerBudgetTable }>;
  totalBudget$: Observable<number>;
  isProjectEditable$: Observable<boolean>;

  private updateBudgetOptionsEvent$ = new Subject();
  private updateBudgetEvent$ = new Subject();

  constructor(private projectStore: ProjectStore,
              private partnerStore: ProjectPartnerStore,
              private projectPartnerBudgetService: ProjectPartnerBudgetService
  ) {
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

  updateBudgets(budgets: { [key: string]: PartnerBudgetTable }): Observable<any> {
    return of(budgets).pipe(withLatestFrom(this.partnerStore.partner$)).pipe(
      switchMap(([newBudgets, partner]: any) =>
        forkJoin({
          staff: this.projectPartnerBudgetService.updateBudgetStaffCosts(partner.id, this.getBudgetEntries(newBudgets.staff) as any),
          travel: this.projectPartnerBudgetService.updateBudgetTravel(partner.id, this.getBudgetEntries(newBudgets.travel) as any),
          external: this.projectPartnerBudgetService.updateBudgetExternal(partner.id, this.getBudgetEntries(newBudgets.external) as any),
          equipment: this.projectPartnerBudgetService.updateBudgetEquipment(partner.id, this.getBudgetEntries(newBudgets.equipment) as any),
          infrastructure: this.projectPartnerBudgetService.updateBudgetInfrastructure(partner.id, this.getBudgetEntries(newBudgets.infrastructure) as any),
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
      map((it: ProjectPartnerBudgetOptionsDto) => new BudgetOptions(it.officeAndAdministrationFlatRate, it.staffCostsFlatRate, it.travelAndAccommodationFlatRate)),
      shareReplay(1)
    );
  }

  private budgets(): Observable<{ [key: string]: PartnerBudgetTable }> {
    return combineLatest([this.updateBudgetEvent$.pipe(startWith(null)), this.updateBudgetOptionsEvent$.pipe(startWith(null)), this.partnerStore.partner$]).pipe(
      map(([, , partner]) => partner),
      filter(partner => !!partner.id),
      map(partner => partner.id),
      switchMap(id =>
        forkJoin({
          staff: this.projectPartnerBudgetService.getBudgetStaffCosts(id).pipe(
            tap(staff => Log.info('Fetched the staff budget', this, staff)),
            map(staff => this.getBudgetTable(PartnerBudgetTableType.STAFF, staff)),
          ),
          travel: this.projectPartnerBudgetService.getBudgetTravel(id).pipe(
            tap(travel => Log.info('Fetched the travel budget', this, travel)),
            map(travel => this.getBudgetTable(PartnerBudgetTableType.TRAVEL, travel))),

          external: this.projectPartnerBudgetService.getBudgetExternal(id).pipe(
            tap(external => Log.info('Fetched the external budget', this, external)),
            map(external => this.getBudgetTable(PartnerBudgetTableType.EXTERNAL, external))
          ),

          equipment: this.projectPartnerBudgetService.getBudgetEquipment(id).pipe(
            tap(equipment => Log.info('Fetched the equipment budget', this, equipment)),
            map(equipment => this.getBudgetTable(PartnerBudgetTableType.EQUIPMENT, equipment))
          ),
          infrastructure: this.projectPartnerBudgetService.getBudgetInfrastructure(id).pipe(
            tap(infrastructure => Log.info('Fetched the infrastructure budget', this, infrastructure)),
            map(infrastructure => this.getBudgetTable(PartnerBudgetTableType.INFRASTRUCTURE, infrastructure))
          ),
        })
      ),
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

  private getBudgetEntries(table: PartnerBudgetTable): InputBudget[] {
    return table.entries.map(entry => ({
      id: (entry.new ? null : entry.id) as any,
      description: entry.description as any,
      numberOfUnits: entry.numberOfUnits as any,
      pricePerUnit: entry.pricePerUnit as any,
      rowSum: entry.rowSum
    } as InputBudget));
  }

  private getBudgetTable(type: PartnerBudgetTableType, rawEntries: InputBudget[]): PartnerBudgetTable {
    const total = NumberService.truncateNumber(NumberService.sum(rawEntries.map(entry => entry.rowSum || 0)));
    const entries = rawEntries.map(entry => new PartnerBudgetTableEntry({...entry}));
    return new PartnerBudgetTable(type, total, entries);
  }
}
