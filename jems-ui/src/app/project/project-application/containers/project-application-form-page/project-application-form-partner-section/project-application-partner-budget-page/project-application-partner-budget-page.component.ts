import {ChangeDetectionStrategy, Component} from '@angular/core';
import {InputBudget, ProjectPartnerBudgetOptionsDto, ProjectPartnerBudgetService, InputCallFlatRateSetup, OutputCallWithDates} from '@cat/api';
import {ActivatedRoute} from '@angular/router';
import {Log} from '../../../../../../common/utils/log';
import {
  catchError,
  distinctUntilChanged,
  filter,
  map,
  mergeMap,
  share,
  startWith,
  switchMap,
  tap,
  withLatestFrom
} from 'rxjs/operators';
import {combineLatest, forkJoin, merge, Observable, of, Subject} from 'rxjs';
import {I18nValidationError} from '@common/validation/i18n-validation-error';
import {HttpErrorResponse} from '@angular/common/http';
import {PartnerBudgetTable} from '../../../../model/partner-budget-table';
import {PartnerBudgetTableType} from '../../../../model/partner-budget-table-type';
import {ProjectPartnerStore} from '../../services/project-partner-store.service';
import {BudgetOptions} from '../../../../model/budget-options';
import {ProjectStore} from '../../../project-application-detail/services/project-store.service';
import {BudgetOption} from '../../../../model/budget-option';

@Component({
  selector: 'app-project-application-partner-budget-page',
  templateUrl: './project-application-partner-budget-page.component.html',
  styleUrls: ['./project-application-partner-budget-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationPartnerBudgetPageComponent {

  projectId = this.activatedRoute?.snapshot?.params?.projectId;
  partnerId = this.activatedRoute?.snapshot?.params?.partnerId;

  optionsSaveError$ = new Subject<HttpErrorResponse | null>();
  optionsSaveSuccess$ = new Subject<boolean>();
  saveBudgetOptions = new Subject<BudgetOptions>();

  private callFlatRatesOptions$ = this.projectStore.projectCall$.pipe(
    map((call: OutputCallWithDates ) => {
      const result: BudgetOption[] = [];
      call.flatRates.forEach(flatRate => {
        result.push(new BudgetOption(flatRate.rate, !flatRate.isAdjustable, true, flatRate.type));
      });
      return result;
    })
  );

  private initialBudgetOptionsFromPartner$ = this.partnerStore.partner$    .pipe(
      filter(partner => !!partner.id),
      map(partner => partner.id),
      switchMap(id =>
        this.projectPartnerBudgetService.getBudgetOptions(id)
      ),
      map((it: ProjectPartnerBudgetOptionsDto) => new BudgetOptions(it.officeAdministrationFlatRate, it.staffCostsFlatRate))
    );

  private initialBudgetOptions$ = combineLatest([
    this.callFlatRatesOptions$,
    this.initialBudgetOptionsFromPartner$
  ])
    .pipe(
      map(([options, budget]) => ({
        flatRates: this.updateFlatRatesValues(options, budget)
      }))
    );

  private saveBudgetOptionsFromPartner$ = this.saveBudgetOptions
    .pipe(
      withLatestFrom(this.partnerStore.partner$),
      switchMap(([budgetOptions, partner]) =>
        this.projectPartnerBudgetService.updateBudgetOptions(partner.id, budgetOptions).pipe(map(() => budgetOptions))
      ),
      tap(() => this.fetchBudgetsFor$.next([PartnerBudgetTableType.STAFF])),
      tap(() => this.optionsSaveSuccess$.next(true)),
      tap(() => this.optionsSaveError$.next(null)),
      catchError((error: HttpErrorResponse) => {
        this.optionsSaveError$.next(error);
        throw error;
      }),
      tap(() => this.partnerStore.totalAmountChanged$.next()),
    );

  private saveBudgetOptions$ = combineLatest([
    this.callFlatRatesOptions$,
    this.saveBudgetOptionsFromPartner$
  ])
    .pipe(
      map(([options, budget]) => ({
        flatRates: this.updateFlatRatesValues(options, budget)
      }))
    );

  saveBudgets$ = new Subject<{ [key: string]: PartnerBudgetTable }>();
  fetchBudgetsFor$ = new Subject<PartnerBudgetTableType[]>();
  saveError$ = new Subject<HttpErrorResponse | null>();
  saveSuccess$ = new Subject<boolean>();
  cancelEdit$ = new Subject<void>();

  private initialBudgets$ = this.partnerStore.partner$
    .pipe(
      filter(partner => !!partner.id),
      map(partner => partner.id),
      distinctUntilChanged(),
      mergeMap(id =>
        forkJoin({
          staff: this.projectPartnerBudgetService.getBudgetStaffCost(id)
            .pipe(tap(staff => Log.info('Fetched the staff budget', this, staff))),
          travel: this.projectPartnerBudgetService.getBudgetTravel(id)
            .pipe(tap(travel => Log.info('Fetched the travel budget', this, travel))),
          external: this.projectPartnerBudgetService.getBudgetExternal(id)
            .pipe(tap(external => Log.info('Fetched the external budget', this, external))),
          equipment: this.projectPartnerBudgetService.getBudgetEquipment(id)
            .pipe(tap(equipment => Log.info('Fetched the equipment budget', this, equipment))),
          infrastructure: this.projectPartnerBudgetService.getBudgetInfrastructure(id)
            .pipe(tap(infrastructure => Log.info('Fetched the infrastructure budget', this, infrastructure))),
        })
      ),
      share()
    );

  private savedBudgets$ = this.saveBudgets$
    .pipe(
      withLatestFrom(this.partnerStore.partner$),
      switchMap(([budgets, partner]) =>
        forkJoin({
          staff: this.updateBudget(partner.id, PartnerBudgetTableType.STAFF, this.getBudgetEntries(budgets.staff)),
          travel: this.updateBudget(partner.id, PartnerBudgetTableType.TRAVEL, this.getBudgetEntries(budgets.travel)),
          external: this.updateBudget(partner.id, PartnerBudgetTableType.EXTERNAL, this.getBudgetEntries(budgets.external)),
          equipment: this.updateBudget(partner.id, PartnerBudgetTableType.EQUIPMENT, this.getBudgetEntries(budgets.equipment)),
          infrastructure: this.updateBudget(partner.id, PartnerBudgetTableType.INFRASTRUCTURE, this.getBudgetEntries(budgets.infrastructure))
        })),
      tap(() => this.saveSuccess$.next(true)),
      tap(() => this.saveError$.next(null)),
      share(),
      catchError((error: HttpErrorResponse) => {
        this.cancelEdit$.next();
        this.saveError$.next(error);
        throw error;
      }),
      tap(() => this.partnerStore.totalAmountChanged$.next()),
    );

  fetchedBudgets$ = this.fetchBudgetsFor$
    .pipe(
      withLatestFrom(this.partnerStore.partner$, merge(this.savedBudgets$, this.initialBudgets$)),
      switchMap(([budgetsKeys, partner, currentBudgets]) =>
        forkJoin({
          staff: budgetsKeys.indexOf(PartnerBudgetTableType.STAFF) >= 0 ? this.projectPartnerBudgetService.getBudgetStaffCost(partner.id) : of(currentBudgets.staff),
          travel: budgetsKeys.indexOf(PartnerBudgetTableType.TRAVEL) >= 0 ? this.projectPartnerBudgetService.getBudgetTravel(partner.id) : of(currentBudgets.travel),
          external: budgetsKeys.indexOf(PartnerBudgetTableType.EXTERNAL) >= 0 ? this.projectPartnerBudgetService.getBudgetExternal(partner.id) : of(currentBudgets.external),
          equipment: budgetsKeys.indexOf(PartnerBudgetTableType.EQUIPMENT) >= 0 ? this.projectPartnerBudgetService.getBudgetEquipment(partner.id) : of(currentBudgets.equipment),
          infrastructure: budgetsKeys.indexOf(PartnerBudgetTableType.INFRASTRUCTURE) >= 0 ? this.projectPartnerBudgetService.getBudgetInfrastructure(partner.id) : of(currentBudgets.infrastructure),
        })
      ),
      tap(() => this.saveSuccess$.next(true)),
      tap(() => this.saveError$.next(null)),
      catchError((error: HttpErrorResponse) => {
        this.cancelEdit$.next();
        this.saveError$.next(error);
        throw error;
      }),
    );

  details$ = combineLatest([
    merge(this.initialBudgetOptions$, this.saveBudgetOptions$),
    merge(this.initialBudgets$, this.savedBudgets$, this.fetchedBudgets$),
    this.cancelEdit$.pipe(startWith(null))
  ])
    .pipe(
      map(([budgetOptions, budgets]) => ({
        flatRates: budgetOptions.flatRates,
        budgets: {
          staff: new PartnerBudgetTable(PartnerBudgetTableType.STAFF, budgets.staff),
          travel: new PartnerBudgetTable(PartnerBudgetTableType.TRAVEL, budgets.travel),
          external: new PartnerBudgetTable(PartnerBudgetTableType.EXTERNAL, budgets.external),
          equipment: new PartnerBudgetTable(PartnerBudgetTableType.EQUIPMENT, budgets.equipment),
          infrastructure: new PartnerBudgetTable(PartnerBudgetTableType.INFRASTRUCTURE, budgets.infrastructure)
        }
      }))
    );

  constructor(private projectPartnerBudgetService: ProjectPartnerBudgetService,
              private activatedRoute: ActivatedRoute,
              public projectStore: ProjectStore,
              public partnerStore: ProjectPartnerStore) {
    this.projectStore.init(this.projectId);
  }

  private getBudgetEntries(table: PartnerBudgetTable): InputBudget[] {
    return table.entries.map(entry => ({
      id: (entry.new ? null : entry.id) as any,
      description: entry.description as any,
      numberOfUnits: entry.numberOfUnits as any,
      pricePerUnit: entry.pricePerUnit as any,
    } as InputBudget));
  }

  private updateBudget(partnerId: number, type: string, entries: InputBudget[]): Observable<Array<InputBudget>> {
    let update$;
    if (type === PartnerBudgetTableType.STAFF) {
      update$ = this.projectPartnerBudgetService.updateBudgetStaffCost(partnerId, entries);
    }
    if (type === PartnerBudgetTableType.TRAVEL) {
      update$ = this.projectPartnerBudgetService.updateBudgetTravel(partnerId, entries);
    }
    if (type === PartnerBudgetTableType.EXTERNAL) {
      update$ = this.projectPartnerBudgetService.updateBudgetExternal(partnerId, entries);
    }
    if (type === PartnerBudgetTableType.EQUIPMENT) {
      update$ = this.projectPartnerBudgetService.updateBudgetEquipment(partnerId, entries);
    }
    if (type === PartnerBudgetTableType.INFRASTRUCTURE) {
      update$ = this.projectPartnerBudgetService.updateBudgetInfrastructure(partnerId, entries);
    }

    return !update$ ? of([]) : update$
      .pipe(
        tap(budget => Log.info('Updated the' + type + ' budget', this, budget))
      );
  }

  private updateFlatRatesValues(flatRates: BudgetOption[], values: BudgetOptions): BudgetOption[] {
    flatRates.forEach((option: BudgetOption) => {
      if (option.key === InputCallFlatRateSetup.TypeEnum.StaffCost && values.staffCostsFlatRate) {
        option.value = values.staffCostsFlatRate;
        option.isDefault = false;
      }
      if (option.key === InputCallFlatRateSetup.TypeEnum.OfficeOnStaff && values.officeAdministrationFlatRate) {
        option.value = values.officeAdministrationFlatRate;
        option.isDefault = false;
      }
      if (option.key === InputCallFlatRateSetup.TypeEnum.StaffCost && values.staffCostsFlatRate === null) {
        option.isDefault = true;
      }
      if (option.key === InputCallFlatRateSetup.TypeEnum.OfficeOnStaff && values.officeAdministrationFlatRate === null) {
        option.isDefault = true;
      }
    });
    return flatRates;
  }

}
