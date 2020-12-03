import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {PartnerBudgetTable} from '../../../../project-application/model/partner-budget-table';
import {Numbers} from '../../../../../common/utils/numbers';
import {FormService} from '@common/components/section/form/form.service';
import {combineLatest, merge, Observable, of, Subject} from 'rxjs';
import {HttpErrorResponse} from '@angular/common/http';
import {catchError, map, startWith, tap, withLatestFrom} from 'rxjs/operators';
import {ProjectPartnerDetailPageStore} from '../../project-partner-detail-page.store';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {BudgetOptions} from '../../../../project-application/model/budget-options';
import {PartnerBudgetTableType} from '../../../../project-application/model/partner-budget-table-type';
import {PartnerBudgetTableEntry} from '../../../../project-application/model/partner-budget-table-entry';
import {MultiLanguageInputService} from '../../../../../common/services/multi-language-input.service';
import {MultiLanguageInput} from '@common/components/forms/multi-language/multi-language-input';
import {InputTranslation} from '@cat/api';

@UntilDestroy()
@Component({
  selector: 'app-project-partner-budget',
  templateUrl: './project-partner-budget.component.html',
  styleUrls: ['./project-partner-budget.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectPartnerBudgetComponent implements OnInit {

  // todo this should be replaced with a form when ag-grid's removed
  tableChange$ = new Subject<{ [key: string]: PartnerBudgetTable }>();

  data$: Observable<{
    budgetTables: { [key: string]: PartnerBudgetTable },
    staffCostTotal: number,
    officeFlatRateBasedOnStaffCostTotal: number,
    travelFlatRateBasedOnStaffCostTotal: number,
    isProjectEditable: boolean,
    isStaffCostFlatRateActive: boolean,
    isOfficeAdministrationFlatRateActiveActive: boolean,
    isTravelAndAccommodationFlatRateActive: boolean,
  }>;

  private budgetTables$: Observable<{ [key: string]: PartnerBudgetTable }>;
  private staffCostTotal$: Observable<number>;
  private officeFlatRateBasedOnStaffCostTotal$: Observable<number>;
  private travelFlatRateBasedOnStaffCostTotal$: Observable<number>;


  constructor(private formService: FormService, private multiLanguageInputService: MultiLanguageInputService, private pageStore: ProjectPartnerDetailPageStore) {
  }


  ngOnInit(): void {
    this.pageStore.budgets$.pipe(untilDestroyed(this)).subscribe();

    this.budgetTables$ = merge(this.pageStore.budgets$.pipe(map((budgets) => this.deepCloneBudgets(budgets))), this.tableChange$.pipe(map(budgets => this.deepCloneBudgets(budgets))));

    this.staffCostTotal$ = combineLatest([this.pageStore.budgetOptions$, this.budgetTables$]).pipe(
      map(([budgetOptions, budgets]) => this.calculateStaffCostsTotal(budgetOptions, budgets)),
      startWith(0)
    );
    this.officeFlatRateBasedOnStaffCostTotal$ = combineLatest([this.pageStore.budgetOptions$, this.staffCostTotal$]).pipe(
      map(([budgetOptions, staffCostTotal]) => this.calculateOfficeAndAdministrationTotal(budgetOptions.officeFlatRateBasedOnStaffCost || 0, staffCostTotal)),
      startWith(0)
    );

    this.travelFlatRateBasedOnStaffCostTotal$ = combineLatest([this.pageStore.budgetOptions$, this.staffCostTotal$]).pipe(
      map(([budgetOptions, staffCostTotal]) => this.calculateTravelAndAccommodationTotal(budgetOptions.travelFlatRateBasedOnStaffCost || 0, staffCostTotal)),
      startWith(0),
    );


    this.data$ = combineLatest([
      this.budgetTables$,
      this.pageStore.budgetOptions$,
      this.pageStore.isProjectEditable$.pipe(startWith(false)),
      this.staffCostTotal$,
      this.officeFlatRateBasedOnStaffCostTotal$,
      this.travelFlatRateBasedOnStaffCostTotal$
    ]).pipe(
      map(([budgetTables, budgetOptions, isProjectEditable, staffCostTotal, officeFlatRateBasedOnStaffCostTotal, travelFlatRateBasedOnStaffCostTotal]: any) => {
        return {
          budgetTables,
          officeFlatRateBasedOnStaffCostTotal,
          travelFlatRateBasedOnStaffCostTotal,
          staffCostTotal,
          isProjectEditable,
          isStaffCostFlatRateActive: !!budgetOptions.staffCostsFlatRateBasedOnDirectCost,
          isOfficeAdministrationFlatRateActiveActive: !!budgetOptions.officeFlatRateBasedOnStaffCost,
          isTravelAndAccommodationFlatRateActive: !!budgetOptions.travelFlatRateBasedOnStaffCost,
        };
      }));
  }


  updateBudgets(budgets: { [key: string]: PartnerBudgetTable }): void {
    this.pageStore.updateBudgets(budgets).pipe(
      tap(() => this.formService.setSuccess('project.partner.budget.save.success')),
      catchError((error: HttpErrorResponse) => this.formService.setError(error)),
      untilDestroyed(this)
    ).subscribe();
  }

  tableChanged(budgets: { [key: string]: PartnerBudgetTable }): void {
    this.tableChange$.next(budgets);
    this.formService.setDirty(true);
    this.formService.setValid(Object.values(budgets).every(table => table.valid()));
  }

  discard(): void {
    of(null).pipe(
      withLatestFrom(this.pageStore.budgets$),
      tap(([, budgets]) => this.tableChange$.next(budgets)),
      untilDestroyed(this),
    ).subscribe();
  }

  private calculateStaffCostsTotal(budgetOptions: BudgetOptions, budgets: { [key: string]: PartnerBudgetTable }): number {

    const travelTotal = budgetOptions.travelFlatRateBasedOnStaffCost ? 0 : budgets.travel?.total || 0;
    const externalTotal = budgets.external?.total || 0;
    const equipmentTotal = budgets.equipment?.total || 0;
    const infrastructureTotal = budgets.infrastructure?.total || 0;

    return Numbers.truncateNumber(Numbers.product([
      Numbers.divide(budgetOptions.staffCostsFlatRateBasedOnDirectCost, 100),
      Numbers.sum([travelTotal, externalTotal, equipmentTotal, infrastructureTotal])
    ]));

  }

  private calculateOfficeAndAdministrationTotal(officeFlatRateBasedOnStaffCost: number, staffTotal: number): number {
    return Numbers.truncateNumber(Numbers.product([
      Numbers.divide(officeFlatRateBasedOnStaffCost, 100),
      staffTotal
    ]));
  }

  private calculateTravelAndAccommodationTotal(travelFlatRateBasedOnStaffCost: number, staffTotal: number): number {
    return Numbers.truncateNumber(Numbers.product([
      Numbers.divide(travelFlatRateBasedOnStaffCost, 100),
      staffTotal
    ]));
  }

  // since ag-grid mutate the data  we have to create new objects to avoid mutability issues
  // todo this should be removed after ag-grid's removed
  private deepCloneBudgets(budgets: { [key: string]: PartnerBudgetTable }): { [key: string]: PartnerBudgetTable } {
    // since ag-grid mutate the data here we have to create new objects to avoid mutability issues
    // todo this should change after ag-grid's removed
    return {
      staff: this.createBudgetTable(PartnerBudgetTableType.STAFF, budgets.staff.entries),
      travel: this.createBudgetTable(PartnerBudgetTableType.TRAVEL, budgets.travel.entries),
      external: this.createBudgetTable(PartnerBudgetTableType.EXTERNAL, budgets.external.entries),
      equipment: this.createBudgetTable(PartnerBudgetTableType.EQUIPMENT, budgets.equipment.entries),
      infrastructure: this.createBudgetTable(PartnerBudgetTableType.INFRASTRUCTURE, budgets.infrastructure.entries),
    };
  }

  public createBudgetTable(type: PartnerBudgetTableType, partnerBudgetTableEntries: PartnerBudgetTableEntry[]): PartnerBudgetTable {

    const entries = partnerBudgetTableEntries.map(entry => {
      return new PartnerBudgetTableEntry(
        {
          ...entry,
          description: new MultiLanguageInput(entry.description?.inputs.map(input => {
            return {language: input.language, translation: input.translation} as InputTranslation;
          }) || [])
        });
    });
    return new PartnerBudgetTable(type, entries);
  }

}
