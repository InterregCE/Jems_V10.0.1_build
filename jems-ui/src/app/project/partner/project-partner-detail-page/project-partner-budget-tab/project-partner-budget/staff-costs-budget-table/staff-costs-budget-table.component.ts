import {ChangeDetectionStrategy, Component, Input, OnChanges, OnDestroy, OnInit, SimpleChanges} from '@angular/core';
import {
  AbstractControl,
  ControlContainer,
  FormArray,
  FormBuilder,
  FormControl,
  FormGroup,
  Validators
} from '@angular/forms';
import {MatTableDataSource} from '@angular/material/table';
import {ProjectPartnerBudgetConstants} from '../project-partner-budget.constants';
import {Observable} from 'rxjs';
import {map, startWith} from 'rxjs/operators';
import {FormService} from '@common/components/section/form/form.service';
import {StaffCostsBudgetTable} from '@project/model/budget/staff-costs-budget-table';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {ProjectPeriodDTO} from '@cat/api';
import {Alert} from '@common/components/forms/alert';
import {TableConfig} from '@common/directives/table-config/TableConfig';
import {ProgrammeUnitCost} from '@project/model/programmeUnitCost';
import {MatSelectChange} from '@angular/material/select/select';
import {APPLICATION_FORM} from '@project/common/application-form-model';
import {ProjectPartnerBudgetTabService} from '@project/partner/project-partner-detail-page/project-partner-budget-tab/project-partner-budget-tab.service';
import {AllowedBudgetCategory} from '@project/model/allowed-budget-category';

@UntilDestroy()
@Component({
  selector: 'app-staff-costs-budget-table',
  templateUrl: './staff-costs-budget-table.component.html',
  styleUrls: ['./staff-costs-budget-table.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class StaffCostsBudgetTableComponent implements OnInit, OnChanges, OnDestroy {
  Alert = Alert;
  constants = ProjectPartnerBudgetConstants;
  APPLICATION_FORM = APPLICATION_FORM;

  @Input()
  editable: boolean;
  @Input()
  staffCostTable: StaffCostsBudgetTable;
  @Input()
  projectPeriods: ProjectPeriodDTO[];
  @Input()
  availableUnitCosts: ProgrammeUnitCost[];
  @Input()
  allowedBudgetCategory: AllowedBudgetCategory;

  budgetForm: FormGroup;
  dataSource: MatTableDataSource<AbstractControl>;
  numberOfItems$: Observable<number>;
  warnOpenForPeriods = false;
  columnsToDisplay: string[];
  tableConfig: TableConfig[];

  constructor(private formService: FormService, private controlContainer: ControlContainer, private formBuilder: FormBuilder, private budgetTabService: ProjectPartnerBudgetTabService) {
    this.budgetForm = this.controlContainer.control as FormGroup;
    this.dataSource = new MatTableDataSource<AbstractControl>(this.items.controls);
    this.numberOfItems$ = this.items.valueChanges.pipe(startWith(null), map(() => this.items.length));
    this.items.valueChanges.pipe(untilDestroyed(this)).subscribe(() => {
      this.dataSource.data = this.items.controls;
      this.items.controls.forEach(control => {
        this.budgetTabService.setRowSum(control as FormGroup);
        this.budgetTabService.setOpenForPeriods(this.projectPeriods, control as FormGroup);
      });
      this.budgetTabService.setTotal(this.items, this.total);
      this.warnOpenForPeriods = this.budgetTabService.shouldShowWarningForPeriods(this.projectPeriods, this.items);
    });
  }

  get staff(): FormGroup {
    return this.budgetForm.get(this.constants.FORM_CONTROL_NAMES.staff) as FormGroup;
  }

  get items(): FormArray {
    return this.staff.get(this.constants.FORM_CONTROL_NAMES.items) as FormArray;
  }

  get total(): FormControl {
    return this.staff.get(this.constants.FORM_CONTROL_NAMES.total) as FormControl;
  }

  onUnitCostChange(change: MatSelectChange, control: FormGroup, rowIndex: number): void {
    const selectedUnitCost = change.value as ProgrammeUnitCost;
    control.patchValue(
      {
        description: [],
        unitType: selectedUnitCost?.type || [],
        comments: [],
        numberOfUnits: 1,
        pricePerUnit: selectedUnitCost?.costPerUnit || 0,
        openForPeriods: 0,
      }
    );

    this.budgetTabService.getPeriodsFormArray(this.items, rowIndex).controls.forEach(periodControl => {
      periodControl.get(this.constants.FORM_CONTROL_NAMES.amount)?.setValue(0);
    });
  }

  ngOnInit(): void {

    this.formService.reset$.pipe(
      map(() => this.resetStaffFormGroup(this.staffCostTable)),
      untilDestroyed(this)
    ).subscribe();

    this.columnsToDisplay = [
      ...this.budgetTabService.addIfItsVisible(APPLICATION_FORM.SECTION_B.BUDGET_AND_CO_FINANCING.STAFF_COST.STAFF_FUNCTION, ['description']),
      ...this.budgetTabService.addIfItsVisible(APPLICATION_FORM.SECTION_B.BUDGET_AND_CO_FINANCING.STAFF_COST.COMMENTS, ['comments']),
      ...this.budgetTabService.addIfItsVisible(APPLICATION_FORM.SECTION_B.BUDGET_AND_CO_FINANCING.STAFF_COST.UNIT_TYPE_AND_NUMBER_OF_UNITS, ['unitType', 'numberOfUnits']),
      'pricePerUnit', 'total',
      ...this.budgetTabService.getPeriodTableColumns(this.projectPeriods),
      'action'
    ];

    this.tableConfig = [
      ...this.budgetTabService.addIfItsVisible(APPLICATION_FORM.SECTION_B.BUDGET_AND_CO_FINANCING.STAFF_COST.STAFF_FUNCTION, [{minInRem: 12}]),
      ...this.budgetTabService.addIfItsVisible(APPLICATION_FORM.SECTION_B.BUDGET_AND_CO_FINANCING.STAFF_COST.COMMENTS, [{minInRem: 12}]),
      ...this.budgetTabService.addIfItsVisible(APPLICATION_FORM.SECTION_B.BUDGET_AND_CO_FINANCING.STAFF_COST.UNIT_TYPE_AND_NUMBER_OF_UNITS, [{minInRem: 12}, {
        minInRem: 5,
        maxInRem: 5
      }]),
      {minInRem: 8, maxInRem: 8}, {minInRem: 8},
      ...this.budgetTabService.getPeriodsWidthConfigs(this.projectPeriods), {minInRem: 3, maxInRem: 3}
    ];

    if (this.availableUnitCosts.length > 0) {
      this.columnsToDisplay.unshift('unitCost');
      this.tableConfig.unshift({minInRem: 10, maxInRem: 10});
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.staffCostTable || changes.editable) {
      this.resetStaffFormGroup(this.staffCostTable);
    }
  }

  ngOnDestroy(): void {
    this.items.clear();
  }

  removeItem(index: number): void {
    this.items.removeAt(index);
    this.formService.setDirty(true);
  }

  addNewItem(): void {
    this.items.push(this.formBuilder.group({
      id: null,
      description: [[]],
      unitType: [[]],
      unitCost: [null, [this.constants.requiredUnitCost(this.allowedBudgetCategory)]],
      comments: [[]],
      numberOfUnits: [1, [Validators.max(this.constants.MAX_VALUE), Validators.min(this.constants.MIN_VALUE)]],
      pricePerUnit: [0, [Validators.max(this.constants.MAX_VALUE), Validators.min(this.constants.MIN_VALUE)]],
      rowSum: [0, [Validators.max(this.constants.MAX_VALUE), Validators.min(this.constants.MIN_VALUE)]],
      new: [true],
      budgetPeriods: this.formBuilder.array([]),
      openForPeriods: [0],
    }));
    this.formService.setDirty(true);
    this.budgetTabService.addPeriods(this.items, this.projectPeriods);
  }

  getUnitCost(formGroup: FormGroup): FormControl {
    return formGroup.get(this.constants.FORM_CONTROL_NAMES.unitCost) as FormControl;
  }

  openForPeriods(rowIndex: number): FormControl {
    return this.items.at(rowIndex).get(this.constants.FORM_CONTROL_NAMES.openForPeriods) as FormControl;
  }

  fieldEnabled(control: FormGroup): boolean {
    if (this.allowedBudgetCategory.unitCostsOnly()) {
      return !!this.getUnitCost(control)?.value;
    }
    return true;
  }

  private resetStaffFormGroup(staffTable: StaffCostsBudgetTable): void {
    this.total.setValue(staffTable.total, {emitEvent: false});
    this.items.clear();
    staffTable.entries.forEach(item => {
      this.items.push(this.formBuilder.group({
        id: [item.id],
        description: [item.description],
        unitType: [item.unitType],
        unitCost: [this.availableUnitCosts.find(it => it.id === item.unitCostId) || null, [this.constants.requiredUnitCost(this.allowedBudgetCategory)]],
        comments: [item.comments],
        numberOfUnits: [item.numberOfUnits, [Validators.max(this.constants.MAX_VALUE), Validators.min(this.constants.MIN_VALUE)]],
        pricePerUnit: [item.pricePerUnit, [Validators.max(this.constants.MAX_VALUE), Validators.min(this.constants.MIN_VALUE)]],
        rowSum: [item.rowSum, [Validators.max(this.constants.MAX_VALUE), Validators.min(this.constants.MIN_VALUE)]],
        budgetPeriods: this.formBuilder.array([]),
        openForPeriods: [0],
      }));
      this.budgetTabService.addPeriods(this.items, this.projectPeriods, item.budgetPeriods);
    });
    this.formService.resetEditable();
  }

}
