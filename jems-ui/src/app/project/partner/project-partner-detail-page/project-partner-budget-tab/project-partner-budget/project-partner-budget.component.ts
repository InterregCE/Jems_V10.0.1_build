import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {PartnerBudgetTable} from '../../../../project-application/model/partner-budget-table';
import {FormService} from '@common/components/section/form/form.service';
import {combineLatest, Observable} from 'rxjs';
import {catchError, map, startWith, tap} from 'rxjs/operators';
import {ProjectPartnerDetailPageStore} from '../../project-partner-detail-page.store';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {BudgetOptions} from '../../../../project-application/model/budget-options';
import {MultiLanguageInputService} from '../../../../../common/services/multi-language-input.service';
import {NumberService} from '../../../../../common/services/number.service';
import {FormArray, FormBuilder, FormGroup, Validators} from '@angular/forms';
import {ProjectPartnerBudgetConstants} from './project-partner-budget.constants';
import {PartnerBudgetTableType} from '../../../../project-application/model/partner-budget-table-type';
import {PartnerBudgetTableEntry} from '../../../../project-application/model/partner-budget-table-entry';
import {HttpErrorResponse} from '@angular/common/http';

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
  budgetsForm = this.creatForm();

  data$: Observable<{
    staffCostTotal: number,
    officeFlatRateBasedOnStaffCostTotal: number,
    travelFlatRateBasedOnStaffCostTotal: number,
    isProjectEditable: boolean,
    isStaffCostFlatRateActive: boolean,
    isOfficeAdministrationFlatRateActive: boolean,
    isTravelAndAccommodationFlatRateActive: boolean,
  }>;

  private staffCostTotal$: Observable<number>;
  private officeFlatRateBasedOnStaffCostTotal$: Observable<number>;
  private travelFlatRateBasedOnStaffCostTotal$: Observable<number>;

  constructor(private formService: FormService, private formBuilder: FormBuilder, private multiLanguageInputService: MultiLanguageInputService, private pageStore: ProjectPartnerDetailPageStore) {
  }

  ngOnInit(): void {
    this.formService.init(this.budgetsForm);

    this.pageStore.budgets$.pipe(untilDestroyed(this)).subscribe();

    combineLatest([this.pageStore.budgets$, this.formService.reset$.pipe(startWith(null))]).pipe(
      map(([budgets]) => this.resetForm(budgets)),
      untilDestroyed(this)
    ).subscribe();

    this.staffCostTotal$ = combineLatest([this.pageStore.budgetOptions$, this.budgetsForm.valueChanges]).pipe(
      map(([budgetOptions]) => this.calculateStaffCostsTotal(budgetOptions)),
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
      this.pageStore.budgetOptions$,
      this.pageStore.isProjectEditable$.pipe(startWith(false)),
      this.staffCostTotal$,
      this.officeFlatRateBasedOnStaffCostTotal$,
      this.travelFlatRateBasedOnStaffCostTotal$
    ]).pipe(
      map(([budgetOptions, isProjectEditable, staffCostTotal, officeFlatRateBasedOnStaffCostTotal, travelFlatRateBasedOnStaffCostTotal]: any) => {
        return {
          officeFlatRateBasedOnStaffCostTotal,
          travelFlatRateBasedOnStaffCostTotal,
          staffCostTotal,
          isProjectEditable,
          isStaffCostFlatRateActive: !!budgetOptions.staffCostsFlatRateBasedOnDirectCost,
          isOfficeAdministrationFlatRateActive: !!budgetOptions.officeFlatRateBasedOnStaffCost,
          isTravelAndAccommodationFlatRateActive: !!budgetOptions.travelFlatRateBasedOnStaffCost,
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
    if (!budgetOptions?.staffCostsFlatRateBasedOnDirectCost) {
      return this.getTotalOf(this.staff);
    }
    const travelTotal = budgetOptions.travelFlatRateBasedOnStaffCost ? 0 : this.getTotalOf(this.travel);
    const externalTotal = this.getTotalOf(this.external);
    const equipmentTotal = this.getTotalOf(this.equipment);
    const infrastructureTotal = this.getTotalOf(this.infrastructure);

    return NumberService.truncateNumber(NumberService.product([
      NumberService.divide(budgetOptions.staffCostsFlatRateBasedOnDirectCost, 100),
      NumberService.sum([travelTotal, externalTotal, equipmentTotal, infrastructureTotal])
    ]));

  }

  private calculateOfficeAndAdministrationTotal(officeFlatRateBasedOnStaffCost: number, staffTotal: number): number {
    return NumberService.truncateNumber(NumberService.product([
      NumberService.divide(officeFlatRateBasedOnStaffCost, 100),
      staffTotal
    ]));
  }

  private calculateTravelAndAccommodationTotal(travelFlatRateBasedOnStaffCost: number, staffTotal: number): number {
    return NumberService.truncateNumber(NumberService.product([
      NumberService.divide(travelFlatRateBasedOnStaffCost, 100),
      staffTotal
    ]));
  }

  private formToBudgetTables(): { [key: string]: PartnerBudgetTable } {
    return {
      staff: new PartnerBudgetTable(PartnerBudgetTableType.STAFF, this.getTotalOf(this.staff), this.budgetsForm.get(this.constants.FORM_CONTROL_NAMES.staff)?.value.items.map((item: any) => new PartnerBudgetTableEntry({...item}))),
      travel: new PartnerBudgetTable(PartnerBudgetTableType.TRAVEL, this.getTotalOf(this.travel), this.budgetsForm.get(this.constants.FORM_CONTROL_NAMES.travel)?.value.items.map((item: any) => new PartnerBudgetTableEntry({...item}))),
      external: new PartnerBudgetTable(PartnerBudgetTableType.EXTERNAL, this.getTotalOf(this.external), this.budgetsForm.get(this.constants.FORM_CONTROL_NAMES.external)?.value.items.map((item: any) => new PartnerBudgetTableEntry({...item}))),
      equipment: new PartnerBudgetTable(PartnerBudgetTableType.EQUIPMENT, this.getTotalOf(this.equipment), this.budgetsForm.get(this.constants.FORM_CONTROL_NAMES.equipment)?.value.items.map((item: any) => new PartnerBudgetTableEntry({...item}))),
      infrastructure: new PartnerBudgetTable(PartnerBudgetTableType.INFRASTRUCTURE, this.getTotalOf(this.infrastructure), this.budgetsForm.get(this.constants.FORM_CONTROL_NAMES.infrastructure)?.value.items.map((item: any) => new PartnerBudgetTableEntry({...item})))
    };
  }

  private resetForm(budgets: { [key: string]: PartnerBudgetTable }): void {
    Object.keys(budgets).forEach(key => {
      const tableFormGroup = this.budgetsForm.get(key) as FormGroup;
      const itemsFormArray = tableFormGroup.controls.items as FormArray;
      tableFormGroup.controls.total.setValue(budgets[key].total);
      itemsFormArray.clear();
      budgets[key].entries.forEach(item => {
        itemsFormArray.push(this.formBuilder.group({
          description: this.formBuilder.control(item.description),
          numberOfUnits: this.formBuilder.control(item.numberOfUnits, [Validators.max(this.constants.MAX_VALUE), Validators.min(this.constants.MIN_VALUE)]),
          pricePerUnit: this.formBuilder.control(item.pricePerUnit, [Validators.max(this.constants.MAX_VALUE), Validators.min(this.constants.MIN_VALUE)]),
          rowSum: this.formBuilder.control(item.rowSum, [Validators.max(this.constants.MAX_VALUE), Validators.min(this.constants.MIN_VALUE)]),
        }));
      });
    });
  }

  private creatForm(): FormGroup {
    return this.formBuilder.group({
      staff: this.formBuilder.group({
        items: this.formBuilder.array([], [Validators.maxLength(this.constants.MAX_NUMBER_OF_ITEMS)]),
        total: this.formBuilder.control(0, [Validators.max(this.constants.MAX_VALUE), Validators.min(this.constants.MIN_VALUE)])
      }),
      travel: this.formBuilder.group({
        items: this.formBuilder.array([], [Validators.maxLength(this.constants.MAX_NUMBER_OF_ITEMS)]),
        total: this.formBuilder.control(0, [Validators.max(this.constants.MAX_VALUE), Validators.min(this.constants.MIN_VALUE)])
      }),
      infrastructure: this.formBuilder.group({
        items: this.formBuilder.array([], [Validators.maxLength(this.constants.MAX_NUMBER_OF_ITEMS)]),
        total: this.formBuilder.control(0, [Validators.max(this.constants.MAX_VALUE), Validators.min(this.constants.MIN_VALUE)])
      }),
      equipment: this.formBuilder.group({
        items: this.formBuilder.array([], [Validators.maxLength(this.constants.MAX_NUMBER_OF_ITEMS)]),
        total: this.formBuilder.control(0, [Validators.max(this.constants.MAX_VALUE), Validators.min(this.constants.MIN_VALUE)])
      }),
      external: this.formBuilder.group({
        items: this.formBuilder.array([], [Validators.maxLength(this.constants.MAX_NUMBER_OF_ITEMS)]),
        total: this.formBuilder.control(0, [Validators.max(this.constants.MAX_VALUE), Validators.min(this.constants.MIN_VALUE)])
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

}
