import {ChangeDetectionStrategy, Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
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
import {NumberService} from '@common/services/number.service';
import {FormService} from '@common/components/section/form/form.service';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {ProjectPeriodDTO} from '@cat/api';
import {UnitCostsBudgetTable} from '@project/model/budget/unit-costs-budget-table';
import {TableConfig} from '@common/directives/table-config/TableConfig';
import {Alert} from '@common/components/forms/alert';
import {ProgrammeUnitCost} from '@project/model/programmeUnitCost';
import {ProjectPartnerBudgetTabService} from '@project/partner/project-partner-detail-page/project-partner-budget-tab/project-partner-budget-tab.service';
import {APPLICATION_FORM} from '@project/application-form-model';

@UntilDestroy()
@Component({
  selector: 'app-unit-costs-budget-table',
  templateUrl: './unit-costs-budget-table.component.html',
  styleUrls: ['./unit-costs-budget-table.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush

})
export class UnitCostsBudgetTableComponent implements OnInit, OnChanges {
  Alert = Alert;
  constants = ProjectPartnerBudgetConstants;

  @Input()
  editable: boolean;
  @Input()
  unitCostTable: UnitCostsBudgetTable;
  @Input()
  availableUnitCosts: ProgrammeUnitCost[];
  @Input()
  projectPeriods: ProjectPeriodDTO[];

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
        this.setRowTotal(control as FormGroup);
        this.budgetTabService.setOpenForPeriods(this.projectPeriods, control as FormGroup);
      });
      this.budgetTabService.setTotal(this.items, this.total);
      this.warnOpenForPeriods = this.budgetTabService.shouldShowWarningForPeriods(this.projectPeriods, this.items);
    });
  }

  ngOnInit(): void {

    this.formService.reset$.pipe(
      map(() => this.resetUnitCostFormGroup(this.unitCostTable)),
      untilDestroyed(this)
    ).subscribe();

    this.columnsToDisplay = [
      'unitCost',
      ...this.budgetTabService.addIfItsVisible(APPLICATION_FORM.SECTION_B.BUDGET_AND_CO_FINANCING.UNIT_COSTS.DESCRIPTION, ['description']),
      'unitType', 'numberOfUnits', 'pricePerUnit', 'total',
      ...this.budgetTabService.getPeriodTableColumns(this.projectPeriods), 'action',
    ];

    this.tableConfig = [
      {minInRem: 10, maxInRem: 10},
      ...this.budgetTabService.addIfItsVisible(APPLICATION_FORM.SECTION_B.BUDGET_AND_CO_FINANCING.UNIT_COSTS.DESCRIPTION, [{minInRem: 12}]),
      {minInRem: 12}, {minInRem: 5, maxInRem: 5}, {minInRem: 8, maxInRem: 8}, {minInRem: 8},
      ...this.budgetTabService.getPeriodsWidthConfigs(this.projectPeriods),
      {minInRem: 3, maxInRem: 3}
    ];
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.unitCostTable || changes.editable) {
      this.resetUnitCostFormGroup(this.unitCostTable);
    }
  }

  removeItem(index: number): void {
    this.items.removeAt(index);
    this.formService.setDirty(true);
  }

  addNewItem(): void {
    this.items.push(this.formBuilder.group({
      id: null,
      unitCost: [null, Validators.required],
      numberOfUnits: [1, [Validators.max(this.constants.MAX_VALUE), Validators.min(this.constants.MIN_VALUE)]],
      rowSum: [0, [Validators.max(this.constants.MAX_VALUE), Validators.min(this.constants.MIN_VALUE)]],
      new: [true],
      budgetPeriods: this.formBuilder.array([]),
      openForPeriods: [0],
    }));
    this.budgetTabService.addPeriods(this.items, this.projectPeriods);
    this.formService.setDirty(true);
  }

  private resetUnitCostFormGroup(unitCostTable: UnitCostsBudgetTable): void {
    this.total.setValue(unitCostTable.total);
    this.items.clear();
    this.unitCostTable.entries.forEach(item => {
      this.items.push(this.formBuilder.group({
        id: [item.id],
        unitCost: [this.availableUnitCosts.find(it => it.id === item.unitCostId), Validators.required],
        numberOfUnits: [item.numberOfUnits, [Validators.max(this.constants.MAX_VALUE), Validators.min(this.constants.MIN_VALUE)]],
        rowSum: [item.rowSum, [Validators.max(this.constants.MAX_VALUE), Validators.min(this.constants.MIN_VALUE)]],
        budgetPeriods: this.formBuilder.array([]),
        openForPeriods: [0],
      }));
      this.budgetTabService.addPeriods(this.items, this.projectPeriods, item.budgetPeriods);
      this.budgetTabService.setOpenForPeriods(this.projectPeriods, this.items.at(this.items.length - 1) as FormGroup);
      this.warnOpenForPeriods = this.budgetTabService.shouldShowWarningForPeriods(this.projectPeriods, this.items);
    });
    this.formService.resetEditable();
  }

  private setRowTotal(control: FormGroup): void {
    const numberOfUnits = control.get(this.constants.FORM_CONTROL_NAMES.numberOfUnits)?.value || 0;
    const pricePerUnit = control.get(this.constants.FORM_CONTROL_NAMES.unitCost)?.value?.costPerUnit || 0;
    control.get(this.constants.FORM_CONTROL_NAMES.rowSum)?.setValue(NumberService.truncateNumber(NumberService.product([numberOfUnits, pricePerUnit])), {emitEvent: false});
  }

  getUnitCostValue(formGroup: FormGroup): ProgrammeUnitCost | null {
    return formGroup.get(this.constants.FORM_CONTROL_NAMES.unitCost)?.value as ProgrammeUnitCost;
  }
  get table(): FormGroup {
    return this.budgetForm.get(this.constants.FORM_CONTROL_NAMES.unitCost) as FormGroup;
  }

  get items(): FormArray {
    return this.table.get(this.constants.FORM_CONTROL_NAMES.items) as FormArray;
  }

  get total(): FormControl {
    return this.table.get(this.constants.FORM_CONTROL_NAMES.total) as FormControl;
  }

  openForPeriods(rowIndex: number): FormControl {
    return this.items.at(rowIndex).get(this.constants.FORM_CONTROL_NAMES.openForPeriods) as FormControl;
  }

}
