import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {FormService} from '@common/components/section/form/form.service';
import {combineLatest, Observable} from 'rxjs';
import {catchError, map, startWith, tap} from 'rxjs/operators';
import {ProjectPartnerDetailPageStore} from '../../project-partner-detail-page.store';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {BudgetOptions} from '../../../../model/budget/budget-options';
import {MultiLanguageInputService} from '../../../../../common/services/multi-language-input.service';
import {NumberService} from '../../../../../common/services/number.service';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {ProjectPartnerBudgetConstants} from './project-partner-budget.constants';
import {GeneralBudgetTableEntry} from '../../../../model/budget/general-budget-table-entry';
import {HttpErrorResponse} from '@angular/common/http';
import {StaffCostsBudgetTable} from '../../../../model/budget/staff-costs-budget-table';
import {StaffCostsBudgetTableEntry} from '../../../../model/budget/staff-costs-budget-table-entry';
import {PartnerBudgetTables} from '../../../../model/budget/partner-budget-tables';
import {GeneralBudgetTable} from '../../../../model/budget/general-budget-table';
import {TravelAndAccommodationCostsBudgetTable} from '../../../../model/budget/travel-and-accommodation-costs-budget-table';
import {TravelAndAccommodationCostsBudgetTableEntry} from '../../../../model/budget/travel-and-accommodation-costs-budget-table-entry';
import {OutputProjectPeriod} from '@cat/api';
import {UnitCostsBudgetTable} from '../../../../model/budget/unit-costs-budget-table';
import {UnitCostsBudgetTableEntry} from '../../../../model/budget/unit-costs-budget-table-entry';
import {ProjectPartnerBudgetTabService} from '../project-partner-budget-tab.service';
import {BudgetCostCategoryEnum} from '../../../../model/lump-sums/BudgetCostCategoryEnum';
import {ProgrammeUnitCost} from '../../../../model/programmeUnitCost';
import {InvestmentSummary} from '../../../../work-package/work-package-detail-page/workPackageInvestment';

@UntilDestroy()
@Component({
  selector: 'app-project-partner-budget',
  templateUrl: './project-partner-budget.component.html',
  styleUrls: ['./project-partner-budget.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectPartnerBudgetComponent implements OnInit {

  constants = ProjectPartnerBudgetConstants;
  BudgetCostCategoryEnum = BudgetCostCategoryEnum;
  budgetsForm = this.initForm();

  data$: Observable<{
    budgetTables: PartnerBudgetTables,
    investments: InvestmentSummary[],
    unitCosts: ProgrammeUnitCost[],
    staffCostsTotal: number,
    officeAndAdministrationFlatRateTotal: number,
    travelAndAccommodationTotal: number,
    otherCostsFlatRateTotal: number,
    periods: OutputProjectPeriod[],
    isStaffCostFlatRateActive: boolean,
    isOfficeOnStaffFlatRateActive: boolean,
    isOfficeOnDirectFlatRateActive: boolean,
    isTravelAndAccommodationFlatRateActive: boolean,
    isOtherFlatRateBasedOnStaffCostActive: boolean,
  }>;

  private otherCostsFlatRateTotal$: Observable<number>;
  private staffCostsTotal$: Observable<number>;
  private officeAndAdministrationFlatRateTotal$: Observable<number>;
  private travelAndAccommodationTotal$: Observable<number>;

  constructor(private formService: FormService, private tabService: ProjectPartnerBudgetTabService, private formBuilder: FormBuilder, private multiLanguageInputService: MultiLanguageInputService, private pageStore: ProjectPartnerDetailPageStore) {
  }

  ngOnInit(): void {
    this.formService.init(this.budgetsForm, combineLatest([this.pageStore.isProjectEditable$, this.tabService.isBudgetOptionsFormInEditMode$.pipe(startWith(false))]).pipe(map(([isProjectEditable, isBudgetOptionsFormInEditMode]) => isProjectEditable && !isBudgetOptionsFormInEditMode)));
    this.tabService.trackBudgetFormState(this.formService);

    this.pageStore.budgets$.pipe(untilDestroyed(this)).subscribe();

    this.staffCostsTotal$ = combineLatest([this.pageStore.budgetOptions$, this.budgetsForm.valueChanges]).pipe(
      map(([budgetOptions]) => this.calculateStaffCostsTotal(budgetOptions)),
      startWith(0)
    );

    this.travelAndAccommodationTotal$ = combineLatest([this.pageStore.budgetOptions$, this.staffCostsTotal$]).pipe(
      map(([budgetOptions, staffCostTotal]) => this.calculateTravelAndAccommodationCostsTotal(budgetOptions.travelAndAccommodationOnStaffCostsFlatRate, staffCostTotal)),
      startWith(0),
    );

    this.officeAndAdministrationFlatRateTotal$ = combineLatest([this.pageStore.budgetOptions$, this.staffCostsTotal$, this.travelAndAccommodationTotal$]).pipe(
      map(([budgetOptions, staffCostTotal, travelCostTotal]) => this.calculateOfficeAndAdministrationFlatRateTotal(budgetOptions.officeAndAdministrationOnStaffCostsFlatRate, budgetOptions.officeAndAdministrationOnDirectCostsFlatRate, staffCostTotal, travelCostTotal)),
      startWith(0)
    );

    this.otherCostsFlatRateTotal$ = combineLatest([this.pageStore.budgetOptions$, this.staffCostsTotal$]).pipe(
      map(([budgetOptions, staffCostTotal]) => this.calculateOtherCostsFlatRateTotal(budgetOptions.staffCostsFlatRate, budgetOptions.otherCostsOnStaffCostsFlatRate || 0, staffCostTotal)),
      startWith(0)
    );

    this.data$ = combineLatest([
      this.pageStore.budgets$,
      this.pageStore.budgetOptions$,
      this.pageStore.investmentSummaries$,
      this.pageStore.unitCosts$,
      this.staffCostsTotal$,
      this.officeAndAdministrationFlatRateTotal$,
      this.travelAndAccommodationTotal$,
      this.otherCostsFlatRateTotal$,
      this.pageStore.periods$
    ]).pipe(
      map(([budgetTables, budgetOptions, investments, unitCosts, staffCostsTotal, officeAndAdministrationFlatRateTotal, travelAndAccommodationTotal, otherCostsFlatRateTotal, periods]: any) => {
        return {
          budgetTables,
          investments,
          unitCosts,
          staffCostsTotal,
          officeAndAdministrationFlatRateTotal,
          travelAndAccommodationTotal,
          otherCostsFlatRateTotal,
          periods,
          isStaffCostFlatRateActive: !!budgetOptions.staffCostsFlatRate,
          isOfficeOnStaffFlatRateActive: !!budgetOptions.officeAndAdministrationOnStaffCostsFlatRate,
          isOfficeOnDirectFlatRateActive: !!budgetOptions.officeAndAdministrationOnDirectCostsFlatRate,
          isTravelAndAccommodationFlatRateActive: !!budgetOptions.travelAndAccommodationOnStaffCostsFlatRate,
          isOtherFlatRateBasedOnStaffCostActive: !!budgetOptions.otherCostsOnStaffCostsFlatRate,
        };
      }));
  }


  updateBudgets(): void {
    this.pageStore.updateBudgets(this.formToBudgetTables()).pipe(
      tap(() => this.formService.setSuccess('project.partner.budget.save.success')),
      catchError((error: HttpErrorResponse) => this.formService.setError(error)),
      untilDestroyed(this)
    ).subscribe();
  }

  private calculateStaffCostsTotal(budgetOptions: BudgetOptions): number {
    if (!budgetOptions?.staffCostsFlatRate) {
      return this.getTotalOf(this.staff);
    }
    const travelTotal = budgetOptions.travelAndAccommodationOnStaffCostsFlatRate ? 0 : this.getTotalOf(this.travel);
    const externalTotal = this.getTotalOf(this.external);
    const equipmentTotal = this.getTotalOf(this.equipment);
    const infrastructureTotal = this.getTotalOf(this.infrastructure);

    return NumberService.truncateNumber(NumberService.product([
      NumberService.divide(budgetOptions.staffCostsFlatRate, 100),
      NumberService.sum([travelTotal, externalTotal, equipmentTotal, infrastructureTotal])
    ]));

  }

  private calculateOfficeAndAdministrationFlatRateTotal(officeFlatRateBasedOnStaffCost: number | null, officeFlatRateBasedOnDirectCosts: number | null, staffTotal: number, travelCostTotal: number): number {
    return NumberService.truncateNumber(NumberService.product([
      NumberService.divide(officeFlatRateBasedOnStaffCost || officeFlatRateBasedOnDirectCosts, 100),
      officeFlatRateBasedOnStaffCost !== null ? staffTotal :
        NumberService.sum([this.getTotalOf(this.external), this.getTotalOf(this.equipment), this.getTotalOf(this.infrastructure), staffTotal, travelCostTotal])]));
  }

  private calculateOtherCostsFlatRateTotal(staffCostsFlatRateBasedOnDirectCost: number | null, otherCostsFlatRateBasedOnStaffCost: number, staffTotal: number): number {
    if (staffCostsFlatRateBasedOnDirectCost != null) {
      return 0;
    }
    return NumberService.truncateNumber(NumberService.product([
      NumberService.divide(otherCostsFlatRateBasedOnStaffCost, 100),
      staffTotal
    ]));
  }

  private calculateTravelAndAccommodationCostsTotal(travelFlatRateBasedOnStaffCost: number | null, staffTotal: number): number {
    if (travelFlatRateBasedOnStaffCost === null) {
      return this.getTotalOf(this.travel);
    }
    return NumberService.truncateNumber(NumberService.product([
      NumberService.divide(travelFlatRateBasedOnStaffCost, 100),
      staffTotal
    ]));
  }

  private formToBudgetTables(): PartnerBudgetTables {
    return new PartnerBudgetTables(
      new StaffCostsBudgetTable(this.getTotalOf(this.staff), this.staff?.value.items.map((item: any) => new StaffCostsBudgetTableEntry({...item, unitCostId: item.unitCost?.id}))),
      new TravelAndAccommodationCostsBudgetTable(this.getTotalOf(this.travel), this.travel?.value.items.map((item: any) => new TravelAndAccommodationCostsBudgetTableEntry({...item, unitCostId: item.unitCost?.id}))),
      new GeneralBudgetTable(this.getTotalOf(this.external), this.external?.value.items.map((item: any) => new GeneralBudgetTableEntry({...item, unitCostId: item.unitCost?.id}))),
      new GeneralBudgetTable(this.getTotalOf(this.equipment), this.equipment?.value.items.map((item: any) => new GeneralBudgetTableEntry({...item, unitCostId: item.unitCost?.id}))),
      new GeneralBudgetTable(this.getTotalOf(this.infrastructure), this.infrastructure?.value.items.map((item: any) => new GeneralBudgetTableEntry({...item, unitCostId: item.unitCost?.id}))),
      new UnitCostsBudgetTable(this.getTotalOf(this.unitCosts), this.unitCosts?.value.items.map((item: any) => new UnitCostsBudgetTableEntry({...item}, item.unitCost?.id)))
    );
  }

  private initForm(): FormGroup {
    return this.formBuilder.group({
      staff: this.formBuilder.group({
        items: this.formBuilder.array([], [Validators.maxLength(this.constants.MAX_NUMBER_OF_ITEMS)]),
        total: [0, [Validators.max(this.constants.MAX_VALUE), Validators.min(this.constants.MIN_VALUE)]]
      }),
      travel: this.formBuilder.group({
        items: this.formBuilder.array([], [Validators.maxLength(this.constants.MAX_NUMBER_OF_ITEMS)]),
        total: [0, [Validators.max(this.constants.MAX_VALUE), Validators.min(this.constants.MIN_VALUE)]]
      }),
      infrastructure: this.formBuilder.group({
        items: this.formBuilder.array([], [Validators.maxLength(this.constants.MAX_NUMBER_OF_ITEMS)]),
        total: [0, [Validators.max(this.constants.MAX_VALUE), Validators.min(this.constants.MIN_VALUE)]]
      }),
      equipment: this.formBuilder.group({
        items: this.formBuilder.array([], [Validators.maxLength(this.constants.MAX_NUMBER_OF_ITEMS)]),
        total: [0, [Validators.max(this.constants.MAX_VALUE), Validators.min(this.constants.MIN_VALUE)]]
      }),
      external: this.formBuilder.group({
        items: this.formBuilder.array([], [Validators.maxLength(this.constants.MAX_NUMBER_OF_ITEMS)]),
        total: [0, [Validators.max(this.constants.MAX_VALUE), Validators.min(this.constants.MIN_VALUE)]]
      }),
      unitCost: this.formBuilder.group({
        items: this.formBuilder.array([], [Validators.maxLength(this.constants.MAX_NUMBER_OF_ITEMS)]),
        total: [0, [Validators.max(this.constants.MAX_VALUE), Validators.min(this.constants.MIN_VALUE)]]
      }),
    });
  }

  private getTotalOf(formGroup: FormGroup): number {
    return formGroup.get(this.constants.FORM_CONTROL_NAMES.total)?.value || 0;
  }

  get staff(): FormGroup {
    return this.budgetsForm.get(this.constants.FORM_CONTROL_NAMES.staff) as FormGroup;
  }

  get travel(): FormGroup {
    return this.budgetsForm.get(this.constants.FORM_CONTROL_NAMES.travel) as FormGroup;
  }

  get equipment(): FormGroup {
    return this.budgetsForm.get(this.constants.FORM_CONTROL_NAMES.equipment) as FormGroup;
  }

  get external(): FormGroup {
    return this.budgetsForm.get(this.constants.FORM_CONTROL_NAMES.external) as FormGroup;
  }

  get infrastructure(): FormGroup {
    return this.budgetsForm.get(this.constants.FORM_CONTROL_NAMES.infrastructure) as FormGroup;
  }

  get unitCosts(): FormGroup {
    return this.budgetsForm.get(this.constants.FORM_CONTROL_NAMES.unitCost) as FormGroup;
  }

}
