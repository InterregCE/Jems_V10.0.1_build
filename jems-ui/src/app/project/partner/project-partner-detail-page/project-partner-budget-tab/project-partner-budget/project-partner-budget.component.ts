import {ChangeDetectionStrategy, ChangeDetectorRef, Component, Input, OnInit} from '@angular/core';
import {FormService} from '@common/components/section/form/form.service';
import {combineLatest, Observable} from 'rxjs';
import {catchError, distinctUntilChanged, map, startWith, tap} from 'rxjs/operators';
import {ProjectPartnerDetailPageStore} from '../../project-partner-detail-page.store';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {ProjectPartnerBudgetConstants} from './project-partner-budget.constants';
import {GeneralBudgetTableEntry} from '@project/model/budget/general-budget-table-entry';
import {HttpErrorResponse} from '@angular/common/http';
import {StaffCostsBudgetTable} from '@project/model/budget/staff-costs-budget-table';
import {StaffCostsBudgetTableEntry} from '@project/model/budget/staff-costs-budget-table-entry';
import {PartnerBudgetTables} from '@project/model/budget/partner-budget-tables';
import {GeneralBudgetTable} from '@project/model/budget/general-budget-table';
import {
  TravelAndAccommodationCostsBudgetTable
} from '@project/model/budget/travel-and-accommodation-costs-budget-table';
import {
  TravelAndAccommodationCostsBudgetTableEntry
} from '@project/model/budget/travel-and-accommodation-costs-budget-table-entry';
import {ProjectPeriodDTO} from '@cat/api';
import {UnitCostsBudgetTable} from '@project/model/budget/unit-costs-budget-table';
import {UnitCostsBudgetTableEntry} from '@project/model/budget/unit-costs-budget-table-entry';
import {ProjectPartnerBudgetTabService} from '../project-partner-budget-tab.service';
import {BudgetCostCategoryEnum} from '@project/model/lump-sums/BudgetCostCategoryEnum';
import {ProgrammeUnitCost} from '@project/model/programmeUnitCost';
import {
  InvestmentSummary
} from '@project/work-package/project-work-package-page/work-package-detail-page/workPackageInvestment';
import {AllowedBudgetCategories} from '@project/model/allowed-budget-category';

@UntilDestroy()
@Component({
  selector: 'jems-project-partner-budget',
  templateUrl: './project-partner-budget.component.html',
  styleUrls: ['./project-partner-budget.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectPartnerBudgetComponent implements OnInit {

  constants = ProjectPartnerBudgetConstants;
  BudgetCostCategoryEnum = BudgetCostCategoryEnum;

  @Input()
  allowedBudgetCategories: AllowedBudgetCategories;

  budgetsForm = this.initForm();

  data: {
    budgetTables: PartnerBudgetTables;
    investments: InvestmentSummary[];
    unitCosts: ProgrammeUnitCost[];
    staffCostsTotal: number;
    officeAndAdministrationFlatRateTotal: number;
    travelAndAccommodationTotal: number;
    otherCostsFlatRateTotal: number;
    periods: ProjectPeriodDTO[];
    isStaffCostFlatRateActive: boolean;
    isOfficeOnStaffFlatRateActive: boolean;
    isOfficeOnDirectFlatRateActive: boolean;
    isTravelAndAccommodationFlatRateActive: boolean;
    isOtherFlatRateBasedOnStaffCostActive: boolean;
    unitCostsWithMultipleCategoriesDefined: boolean;
    lumpSumsDefinedInCall: boolean;
  };

  private otherCostsFlatRateTotal$: Observable<number>;
  private staffCostsTotal$: Observable<number>;
  private officeAndAdministrationFlatRateTotal$: Observable<number>;
  private travelAndAccommodationTotal$: Observable<number>;

  constructor(private cdr: ChangeDetectorRef,
              private formService: FormService,
              private tabService: ProjectPartnerBudgetTabService,
              private formBuilder: FormBuilder,
              private pageStore: ProjectPartnerDetailPageStore) {
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

  ngOnInit(): void {
    this.formService.init(this.budgetsForm, combineLatest([this.pageStore.isProjectEditable$, this.tabService.isBudgetOptionsFormInEditMode$.pipe(startWith(false))]).pipe(map(([isProjectEditable, isBudgetOptionsFormInEditMode]) => isProjectEditable && !isBudgetOptionsFormInEditMode)));
    this.tabService.trackBudgetFormState(this.formService);

    this.pageStore.budgets$.pipe(untilDestroyed(this)).subscribe();

    this.staffCostsTotal$ = combineLatest([this.pageStore.budgetOptions$, this.budgetsForm.valueChanges]).pipe(
      map(([budgetOptions]) => ProjectPartnerDetailPageStore.calculateStaffCostsTotal(
        budgetOptions,
        this.getTotalOf(this.staff),
        this.getTotalOf(this.travel),
        this.getTotalOf(this.external),
        this.getTotalOf(this.equipment),
        this.getTotalOf(this.infrastructure),
      )),
      startWith(0)
    );

    this.travelAndAccommodationTotal$ = combineLatest([this.pageStore.budgetOptions$, this.staffCostsTotal$.pipe(distinctUntilChanged())]).pipe(
      map(([budgetOptions, staffCostTotal]) => ProjectPartnerDetailPageStore.calculateTravelAndAccommodationCostsTotal(
        budgetOptions.travelAndAccommodationOnStaffCostsFlatRate,
        staffCostTotal,
        this.getTotalOf(this.travel),
      )),
      startWith(0),
    );

    this.officeAndAdministrationFlatRateTotal$ = combineLatest([this.pageStore.budgetOptions$, this.staffCostsTotal$, this.travelAndAccommodationTotal$.pipe(distinctUntilChanged())]).pipe(
      map(([budgetOptions, staffCostTotal, travelCostTotal]) => ProjectPartnerDetailPageStore.calculateOfficeAndAdministrationFlatRateTotal(
        budgetOptions.officeAndAdministrationOnStaffCostsFlatRate,
        budgetOptions.officeAndAdministrationOnDirectCostsFlatRate,
        staffCostTotal,
        travelCostTotal,
        this.getTotalOf(this.external),
        this.getTotalOf(this.equipment),
        this.getTotalOf(this.infrastructure),
      )),
      startWith(0)
    );

    this.otherCostsFlatRateTotal$ = combineLatest([this.pageStore.budgetOptions$, this.staffCostsTotal$.pipe(distinctUntilChanged())]).pipe(
      map(([budgetOptions, staffCostTotal]) => ProjectPartnerDetailPageStore.calculateOtherCostsFlatRateTotal(budgetOptions.staffCostsFlatRate, budgetOptions.otherCostsOnStaffCostsFlatRate || 0, staffCostTotal)),
      startWith(0)
    );

    combineLatest([
      this.pageStore.budgets$,
      this.pageStore.budgetOptions$,
      this.pageStore.investmentSummaries$,
      this.pageStore.unitCosts$,
      this.staffCostsTotal$.pipe(distinctUntilChanged()),
      this.officeAndAdministrationFlatRateTotal$.pipe(distinctUntilChanged()),
      this.travelAndAccommodationTotal$.pipe(distinctUntilChanged()),
      this.otherCostsFlatRateTotal$.pipe(distinctUntilChanged()),
      this.pageStore.periods$,
      this.pageStore.projectCallLumpSums$
    ]).pipe(
      map(([budgetTables, budgetOptions, investments, unitCosts, staffCostsTotal, officeAndAdministrationFlatRateTotal, travelAndAccommodationTotal, otherCostsFlatRateTotal, periods, callLumpSums]: any) => {
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
          unitCostsWithMultipleCategoriesDefined: unitCosts.some((cost: ProgrammeUnitCost) => !cost.isOneCostCategory),
          lumpSumsDefinedInCall: callLumpSums.length > 0
        };
      }),
      untilDestroyed(this)
    ).subscribe(data => {
      this.data = data;
      setTimeout(() => {
        this.cdr.markForCheck();
      });
    });
  }

  updateBudgets(): void {
    this.pageStore.updateBudgets(this.formToBudgetTables()).pipe(
      tap(() => this.formService.setSuccess('project.partner.budget.save.success')),
      catchError((error: HttpErrorResponse) => this.formService.setError(error)),
      untilDestroyed(this)
    ).subscribe();
  }

  private formToBudgetTables(): PartnerBudgetTables {
    return new PartnerBudgetTables(
      new StaffCostsBudgetTable(this.getTotalOf(this.staff), this.staff?.value.items.map((item: any) => new StaffCostsBudgetTableEntry({
        ...item,
        unitCostId: item.unitCost?.id
      }))),
      new TravelAndAccommodationCostsBudgetTable(this.getTotalOf(this.travel), this.travel?.value.items.map((item: any) => new TravelAndAccommodationCostsBudgetTableEntry({
        ...item,
        unitCostId: item.unitCost?.id
      }))),
      new GeneralBudgetTable(this.getTotalOf(this.external), this.external?.value.items.map((item: any) => new GeneralBudgetTableEntry({
        ...item,
        unitCostId: item.unitCost?.id
      }))),
      new GeneralBudgetTable(this.getTotalOf(this.equipment), this.equipment?.value.items.map((item: any) => new GeneralBudgetTableEntry({
        ...item,
        unitCostId: item.unitCost?.id
      }))),
      new GeneralBudgetTable(this.getTotalOf(this.infrastructure), this.infrastructure?.value.items.map((item: any) => new GeneralBudgetTableEntry({
        ...item,
        unitCostId: item.unitCost?.id
      }))),
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

}
