import {ChangeDetectionStrategy, Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {ProjectPartnerBudgetConstants} from '../project-partner-budget.constants';
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
import {map, startWith} from 'rxjs/operators';
import {Observable} from 'rxjs';
import {NumberService} from '../../../../../../common/services/number.service';
import {FormService} from '@common/components/section/form/form.service';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {GeneralBudgetTable} from '../../../../../model/budget/general-budget-table';
import {InvestmentSummary} from '../../../../../work-package/work-package-detail-page/workPackageInvestment';
import {BudgetPeriodDTO, ProjectPeriodDTO} from '@cat/api';
import {Alert} from '@common/components/forms/alert';
import {TableConfig} from '../../../../../../common/directives/table-config/TableConfig';
import {ProgrammeUnitCost} from '../../../../../model/programmeUnitCost';
import {MatSelectChange} from '@angular/material/select/select';

@UntilDestroy()
@Component({
  selector: 'app-general-budget-table',
  templateUrl: './general-budget-table.component.html',
  styleUrls: ['./general-budget-table.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class GeneralBudgetTableComponent implements OnInit, OnChanges {
  Alert = Alert;
  constants = ProjectPartnerBudgetConstants;

  @Input()
  editable = true;
  @Input()
  tableName: string;
  @Input()
  budgetTable: GeneralBudgetTable;
  @Input()
  investmentSummaries: InvestmentSummary[];
  @Input()
  projectPeriods: ProjectPeriodDTO[];
  @Input()
  availableUnitCosts: ProgrammeUnitCost[];

  budgetForm: FormGroup;
  dataSource: MatTableDataSource<AbstractControl>;
  numberOfItems$: Observable<number>;
  warnOpenForPeriods = false;
  columnsToDisplay: string[];
  tableConfig: TableConfig[];

  constructor(private formService: FormService, private controlContainer: ControlContainer, private formBuilder: FormBuilder) {
    this.budgetForm = this.controlContainer.control as FormGroup;
  }

  ngOnInit(): void {

    this.dataSource = new MatTableDataSource<AbstractControl>(this.items.controls);
    this.numberOfItems$ = this.items.valueChanges.pipe(startWith(0), map(() => this.items.length));

    this.items.valueChanges.pipe(untilDestroyed(this)).subscribe(() => {
      this.dataSource.data = this.items.controls;
      this.items.controls.forEach(control => {
        this.setRowTotal(control as FormGroup);
        this.setOpenForPeriods(control as FormGroup);
      });
      this.setTableTotal();
      this.setOpenForPeriodsWarning();
    });

    this.formService.reset$.pipe(
      map(() => this.resetTableFormGroup(this.budgetTable)),
      untilDestroyed(this)
    ).subscribe();

    const periodColumns = this.projectPeriods?.length
      ? [...this.projectPeriods?.map(period => 'period' + period.number), 'openForPeriods'] : [];
    this.columnsToDisplay = [
      'description', 'awardProcedures', 'investment', 'unitType',
      'numberOfUnits', 'pricePerUnit', 'total', ...periodColumns, 'action',
    ];

    const periodWidths = this.projectPeriods?.length
      ? [...this.projectPeriods?.map(() => ({minInRem: 8})), {minInRem: 8}] : [];
    this.tableConfig = [
      {minInRem: 12}, {minInRem: 12}, {minInRem: 5}, {minInRem: 12}, {minInRem: 5},
      {minInRem: 8}, {minInRem: 8}, ...periodWidths, {minInRem: 3, maxInRem: 3}
    ];

    if (this.availableUnitCosts.length > 0) {
      this.columnsToDisplay.unshift('unitCost');
      this.tableConfig.unshift({minInRem: 10});
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.budgetTable || changes.editable) {
      this.resetTableFormGroup(this.budgetTable);
    }
  }

  onUnitCostChange(change: MatSelectChange, control: FormGroup, rowIndex: number): void {
    const selectedUnitCost = change.value as ProgrammeUnitCost;
    control.patchValue(
      {
        description: [],
        unitType: [],
        awardProcedures: [],
        investmentId: null,
        numberOfUnits: 1,
        pricePerUnit: selectedUnitCost?.costPerUnit || 0,
        openForPeriods: 0,
      }
    );
    this.periods(rowIndex).controls.forEach(periodControl => {
      periodControl.get(this.constants.FORM_CONTROL_NAMES.amount)?.setValue(0);
    });
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
      unitCost: [null],
      awardProcedures: [[]],
      investmentId: [null],
      numberOfUnits: [1, [Validators.max(this.constants.MAX_VALUE), Validators.min(this.constants.MIN_VALUE)]],
      pricePerUnit: [0, [Validators.max(this.constants.MAX_VALUE), Validators.min(this.constants.MIN_VALUE)]],
      rowSum: [0, [Validators.max(this.constants.MAX_VALUE), Validators.min(this.constants.MIN_VALUE)]],
      budgetPeriods: this.formBuilder.array([]),
      openForPeriods: [0],
    }));
    this.addPeriods(this.items.length - 1);
    this.formService.setDirty(true);
  }

  private resetTableFormGroup(commonBudgetTable: GeneralBudgetTable): void {
    this.total.setValue(commonBudgetTable.total);
    this.items.clear();
    this.budgetTable.entries.forEach(item => {
      this.items.push(this.formBuilder.group({
        id: [item.id],
        description: [item.description],
        unitType: [item.unitType],
        unitCost: [this.availableUnitCosts.find(it => it.id === item.unitCostId) || null],
        awardProcedures: [item.awardProcedures],
        investmentId: [item.investmentId],
        numberOfUnits: [item.numberOfUnits, [Validators.max(this.constants.MAX_VALUE), Validators.min(this.constants.MIN_VALUE)]],
        pricePerUnit: [item.pricePerUnit, [Validators.max(this.constants.MAX_VALUE), Validators.min(this.constants.MIN_VALUE)]],
        rowSum: [item.rowSum, [Validators.max(this.constants.MAX_VALUE), Validators.min(this.constants.MIN_VALUE)]],
        budgetPeriods: this.formBuilder.array([]),
        openForPeriods: [0],
      }));
      this.addPeriods(this.items.length - 1, item.budgetPeriods);
    });
    this.formService.resetEditable();
  }

  private setTableTotal(): void {
    let total = 0;
    this.items.controls.forEach(control => {
      total = NumberService.sum([control.get(this.constants.FORM_CONTROL_NAMES.rowSum)?.value || 0, total]);
    });
    this.total.setValue(NumberService.truncateNumber(total));
  }

  private setRowTotal(control: FormGroup): void {
    const numberOfUnits = control.get(this.constants.FORM_CONTROL_NAMES.numberOfUnits)?.value || 0;
    const pricePerUnit = control.get(this.constants.FORM_CONTROL_NAMES.pricePerUnit)?.value || 0;
    control.get(this.constants.FORM_CONTROL_NAMES.rowSum)?.setValue(NumberService.truncateNumber(NumberService.product([numberOfUnits, pricePerUnit])), {emitEvent: false});
  }

  get table(): FormGroup {
    return this.budgetForm.get(this.tableName) as FormGroup;
  }

  getUnitCost(formGroup: FormGroup): FormControl {
    return formGroup.get(this.constants.FORM_CONTROL_NAMES.unitCost) as FormControl;
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

  periods(rowIndex: number): FormArray {
    return this.items.at(rowIndex).get(this.constants.FORM_CONTROL_NAMES.budgetPeriods) as FormArray;
  }

  periodTotal(periodIndex: number): number {
    let total = 0;
    this.items.controls.forEach(control => {
      const periods = control.get(this.constants.FORM_CONTROL_NAMES.budgetPeriods) as FormArray;
      const periodAmount = periods?.at(periodIndex - 1)?.get(this.constants.FORM_CONTROL_NAMES.amount)?.value;
      total = NumberService.sum([periodAmount || 0, total]);
    });
    return total;
  }

  private addPeriods(rowIndex: number, budgetPeriods?: BudgetPeriodDTO[]): void {
    if (!this.projectPeriods?.length) {
      return;
    }
    this.projectPeriods.forEach(projectPeriod => {
      const budgetPeriod = budgetPeriods?.find(period => period.number === projectPeriod.number);
      this.periods(rowIndex).push(this.formBuilder.group({
        amount: this.formBuilder.control(
          budgetPeriod?.amount || 0,
          [Validators.max(this.constants.MAX_VALUE), Validators.min(this.constants.MIN_VALUE)]
        ),
        number: this.formBuilder.control(projectPeriod.number)
      }));
    });
  }

  private setOpenForPeriodsWarning(): void {
    if (!this.projectPeriods?.length) {
      return;
    }
    this.warnOpenForPeriods = this.items.controls.some(
      control => control.get(this.constants.FORM_CONTROL_NAMES.openForPeriods)?.value !== 0
    );
  }

  private setOpenForPeriods(control: FormGroup): void {
    let periodsSum = 0;
    (control.get(this.constants.FORM_CONTROL_NAMES.budgetPeriods) as FormArray).controls.forEach(period => {
      periodsSum = NumberService.sum([period.get(this.constants.FORM_CONTROL_NAMES.amount)?.value || 0, periodsSum]);
    });
    const rowSum = control.get(this.constants.FORM_CONTROL_NAMES.rowSum)?.value || 0;
    control.get(this.constants.FORM_CONTROL_NAMES.openForPeriods)?.setValue(
      NumberService.minus(rowSum, periodsSum), {emitEvent: false}
    );
  }

}
