import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {FormService} from '@common/components/section/form/form.service';
import {combineLatest, Observable} from 'rxjs';
import {catchError, map, startWith, tap} from 'rxjs/operators';
import {ProjectPartnerDetailPageStore} from '../../project-partner-detail-page.store';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {BudgetOptions} from '../../../../project-application/model/budget-options';
import {MultiLanguageInputService} from '../../../../../common/services/multi-language-input.service';
import {NumberService} from '../../../../../common/services/number.service';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {ProjectPartnerBudgetConstants} from './project-partner-budget.constants';
import {GeneralBudgetTableEntry} from '../../../../project-application/model/general-budget-table-entry';
import {HttpErrorResponse} from '@angular/common/http';
import {StaffCostsBudgetTable} from '../../../../project-application/model/staff-costs-budget-table';
import {StaffCostsBudgetTableEntry} from '../../../../project-application/model/staff-costs-budget-table-entry';
import {PartnerBudgetTables} from '../../../../project-application/model/partner-budget-tables';
import {GeneralBudgetTable} from '../../../../project-application/model/general-budget-table';
import {TravelAndAccommodationCostsBudgetTable} from '../../../../project-application/model/travel-and-accommodation-costs-budget-table';
import {TravelAndAccommodationCostsBudgetTableEntry} from '../../../../project-application/model/travel-and-accommodation-costs-budget-table-entry';
import {UnitCostsBudgetTableEntry} from '../../../../project-application/model/unit-costs-budget-table-entry';
import {UnitCostsBudgetTable} from '../../../../project-application/model/unit-costs-budget-table';
import { ProgrammeUnitCostDTO } from '@cat/api';

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
  budgetsForm = this.initForm();

  data$: Observable<{
    budgetTables: PartnerBudgetTables,
    investments: number[],
    unitCostIds: ProgrammeUnitCostDTO[],
    staffCostsFlatRateTotal: number,
    officeAndAdministrationFlatRateTotal: number,
    travelAndAccommodationFlatRateTotal: number,
    otherCostsFlatRateTotal: number,
    isStaffCostFlatRateActive: boolean,
    isOfficeAdministrationFlatRateActive: boolean,
    isTravelAndAccommodationFlatRateActive: boolean,
    isOtherFlatRateBasedOnStaffCostActive: boolean,
  }>;

  private otherCostsFlatRateTotal$: Observable<number>;
  private staffCostsFlatRateTotal$: Observable<number>;
  private officeAndAdministrationFlatRateTotal$: Observable<number>;
  private travelAndAccommodationFlatRateTotal$: Observable<number>;

  constructor(private formService: FormService, private formBuilder: FormBuilder, private multiLanguageInputService: MultiLanguageInputService, private pageStore: ProjectPartnerDetailPageStore) {
  }

  ngOnInit(): void {
    this.formService.init(this.budgetsForm, this.pageStore.isProjectEditable$);

    this.pageStore.budgets$.pipe(untilDestroyed(this)).subscribe();

    this.staffCostsFlatRateTotal$ = combineLatest([this.pageStore.budgetOptions$, this.budgetsForm.valueChanges]).pipe(
      map(([budgetOptions]) => this.calculateStaffCostsFlatRateTotal(budgetOptions)),
      startWith(0)
    );

    this.officeAndAdministrationFlatRateTotal$ = combineLatest([this.pageStore.budgetOptions$, this.staffCostsFlatRateTotal$]).pipe(
      map(([budgetOptions, staffCostTotal]) => this.calculateOfficeAndAdministrationFlatRateTotal(budgetOptions.officeAndAdministrationOnStaffCostsFlatRate || 0, staffCostTotal)),
      startWith(0)
    );

    this.travelAndAccommodationFlatRateTotal$ = combineLatest([this.pageStore.budgetOptions$, this.staffCostsFlatRateTotal$]).pipe(
      map(([budgetOptions, staffCostTotal]) => this.calculateTravelAndAccommodationFlatRateTotal(budgetOptions.travelAndAccommodationOnStaffCostsFlatRate || 0, staffCostTotal)),
      startWith(0),
    );

    this.otherCostsFlatRateTotal$ = combineLatest([this.pageStore.budgetOptions$, this.staffCostsFlatRateTotal$]).pipe(
      map(([budgetOptions, staffCostTotal]) => this.calculateOtherCostsFlatRateTotal(budgetOptions.staffCostsFlatRate, budgetOptions.otherCostsOnStaffCostsFlatRate || 0, staffCostTotal)),
      startWith(0)
    );

    this.data$ = combineLatest([
      this.pageStore.budgets$,
      this.pageStore.budgetOptions$,
      this.pageStore.investmentIds$,
      this.pageStore.unitCostIds$,
      this.pageStore.isProjectEditable$.pipe(startWith(false)),
      this.staffCostsFlatRateTotal$,
      this.officeAndAdministrationFlatRateTotal$,
      this.travelAndAccommodationFlatRateTotal$,
      this.otherCostsFlatRateTotal$
    ]).pipe(
      map(([budgetTables, budgetOptions, investments, unitCostIds, staffCostsFlatRateTotal, officeAndAdministrationFlatRateTotal, travelAndAccommodationFlatRateTotal, otherCostsFlatRateTotal]: any) => {
        return {
          budgetTables,
          investments,
          unitCostIds,
          staffCostsFlatRateTotal,
          officeAndAdministrationFlatRateTotal,
          travelAndAccommodationFlatRateTotal,
          otherCostsFlatRateTotal,
          isStaffCostFlatRateActive: !!budgetOptions.staffCostsFlatRate,
          isOfficeAdministrationFlatRateActive: !!budgetOptions.officeAndAdministrationOnStaffCostsFlatRate,
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

  private calculateStaffCostsFlatRateTotal(budgetOptions: BudgetOptions): number {
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

  private calculateOfficeAndAdministrationFlatRateTotal(officeFlatRateBasedOnStaffCost: number, staffTotal: number): number {
    return NumberService.truncateNumber(NumberService.product([
      NumberService.divide(officeFlatRateBasedOnStaffCost, 100),
      staffTotal
    ]));
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

  private calculateTravelAndAccommodationFlatRateTotal(travelFlatRateBasedOnStaffCost: number, staffTotal: number): number {
    return NumberService.truncateNumber(NumberService.product([
      NumberService.divide(travelFlatRateBasedOnStaffCost, 100),
      staffTotal
    ]));
  }

  private formToBudgetTables(): PartnerBudgetTables {
    return new PartnerBudgetTables(
      new StaffCostsBudgetTable(this.getTotalOf(this.staff), this.staff?.value.items.map((item: any) => new StaffCostsBudgetTableEntry({...item}))),
      new TravelAndAccommodationCostsBudgetTable(this.getTotalOf(this.travel), this.travel?.value.items.map((item: any) => new TravelAndAccommodationCostsBudgetTableEntry({...item}))),
      new GeneralBudgetTable(this.getTotalOf(this.external), this.external?.value.items.map((item: any) => new GeneralBudgetTableEntry({...item}))),
      new GeneralBudgetTable(this.getTotalOf(this.equipment), this.equipment?.value.items.map((item: any) => new GeneralBudgetTableEntry({...item}))),
      new GeneralBudgetTable(this.getTotalOf(this.infrastructure), this.infrastructure?.value.items.map((item: any) => new GeneralBudgetTableEntry({...item}))),
      new UnitCostsBudgetTable(this.getTotalOf(this.unitCosts), this.unitCosts?.value.items.map((item: any) => new UnitCostsBudgetTableEntry({...item})))
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
      unitCosts: this.formBuilder.group({
        items: this.formBuilder.array([], [Validators.maxLength(this.constants.MAX_NUMBER_OF_ITEMS)]),
        total: [0, [Validators.max(this.constants.MAX_VALUE), Validators.min(this.constants.MIN_VALUE)]]
      })
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
    return this.budgetsForm.get(this.constants.FORM_CONTROL_NAMES.unitCosts) as FormGroup;
  }

}
