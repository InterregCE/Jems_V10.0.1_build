import {Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
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
import {Observable} from 'rxjs';
import {FormService} from '@common/components/section/form/form.service';
import {MultiLanguageInputService} from '../../../../../../common/services/multi-language-input.service';
import {map, startWith} from 'rxjs/operators';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {NumberService} from '../../../../../../common/services/number.service';
import {TravelAndAccommodationCostsBudgetTable} from '../../../../../model/budget/travel-and-accommodation-costs-budget-table';
import {BudgetPeriodDTO, OutputProjectPeriod} from '@cat/api';
import {WidthConfig} from '../../../../../../common/directives/table-config/WidthConfig';
import {Alert} from '@common/components/forms/alert';

@UntilDestroy()
@Component({
  selector: 'app-travel-and-accommodation-costs-budget-table',
  templateUrl: './travel-and-accommodation-costs-budget-table.component.html',
  styleUrls: ['./travel-and-accommodation-costs-budget-table.component.scss']
})
export class TravelAndAccommodationCostsBudgetTableComponent implements OnInit, OnChanges {
  Alert = Alert;
  constants = ProjectPartnerBudgetConstants;

  @Input()
  editable: boolean;
  @Input()
  travelAndAccommodationTable: TravelAndAccommodationCostsBudgetTable;
  @Input()
  projectPeriods: OutputProjectPeriod[];

  budgetForm: FormGroup;
  dataSource: MatTableDataSource<AbstractControl>;
  numberOfItems$: Observable<number>;
  warnOpenForPeriods = false;
  columnsToDisplay: string[];
  widthConfig: WidthConfig[];

  constructor(private formService: FormService, private controlContainer: ControlContainer, private formBuilder: FormBuilder, private multiLanguageInputService: MultiLanguageInputService) {
    this.budgetForm = this.controlContainer.control as FormGroup;
  }

  ngOnInit(): void {
    this.dataSource = new MatTableDataSource<AbstractControl>(this.items.controls);
    this.numberOfItems$ = this.items.valueChanges.pipe(startWith(null), map(() => this.items.length));
    this.items.valueChanges.pipe(untilDestroyed(this)).subscribe(() => {
      this.dataSource.data = this.items.controls;
      this.items.controls.forEach(control => {
        this.setRowSum(control as FormGroup);
        this.setOpenForPeriods(control as FormGroup);
      });
      this.setTotal();
      this.setOpenForPeriodsWarning();
    });

    this.formService.reset$.pipe(
      map(() => this.resetTravelFormGroup(this.travelAndAccommodationTable)),
      untilDestroyed(this)
    ).subscribe();

    this.columnsToDisplay = [
      'description', 'unitType', 'numberOfUnits', 'pricePerUnit', 'total',
      ...this.projectPeriods?.map(period => 'period' + period.number),
      'openForPeriods', 'action',
    ];

    this.widthConfig = [
      {minInRem: 12}, {minInRem: 12}, {minInRem: 5}, {minInRem: 12}, {minInRem: 5},
      ...this.projectPeriods?.map(period => ({minInRem: 8})),
      {minInRem: 8}, {minInRem: 3}
    ];
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.travelAndAccommodationTable || changes.editable) {
      this.resetTravelFormGroup(this.travelAndAccommodationTable);
    }
  }

  removeItem(index: number): void {
    this.items.removeAt(index);
    this.formService.setDirty(true);
  }

  addNewItem(): void {
    this.items.push(this.formBuilder.group({
      id: null,
      description: [this.multiLanguageInputService.multiLanguageFormFieldDefaultValue()],
      unitType: [this.multiLanguageInputService.multiLanguageFormFieldDefaultValue()],
      numberOfUnits: [1, [Validators.max(this.constants.MAX_VALUE), Validators.min(this.constants.MIN_VALUE)]],
      pricePerUnit: [0, [Validators.max(this.constants.MAX_VALUE), Validators.min(this.constants.MIN_VALUE)]],
      rowSum: [0, [Validators.max(this.constants.MAX_VALUE), Validators.min(this.constants.MIN_VALUE)]],
      new: [true],
      budgetPeriods: this.formBuilder.array([]),
      openForPeriods: [0],
    }));
    this.addPeriods(this.items.length - 1);
    this.formService.setDirty(true);
  }

  private resetTravelFormGroup(travelTable: TravelAndAccommodationCostsBudgetTable): void {
    this.total.setValue(travelTable.total);
    this.items.clear();
    travelTable.entries.forEach(item => {
      this.items.push(this.formBuilder.group({
        id: [item.id],
        description: [item.description],
        unitType: [item.unitType],
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

  private setTotal(): void {
    let total = 0;
    this.items.controls.forEach(control => {
      total = NumberService.sum([control.get(this.constants.FORM_CONTROL_NAMES.rowSum)?.value || 0, total]);
    });
    this.total.setValue(NumberService.truncateNumber(total));
  }

  private setRowSum(control: FormGroup): void {
    const numberOfUnits = control.get(this.constants.FORM_CONTROL_NAMES.numberOfUnits)?.value || 0;
    const pricePerUnit = control.get(this.constants.FORM_CONTROL_NAMES.pricePerUnit)?.value || 0;
    control.get(this.constants.FORM_CONTROL_NAMES.rowSum)?.setValue(NumberService.truncateNumber(NumberService.product([numberOfUnits, pricePerUnit])), {emitEvent: false});
  }

  get travel(): FormGroup {
    return this.budgetForm.get(this.constants.FORM_CONTROL_NAMES.travel) as FormGroup;
  }

  get items(): FormArray {
    return this.travel.get(this.constants.FORM_CONTROL_NAMES.items) as FormArray;
  }

  get total(): FormControl {
    return this.travel.get(this.constants.FORM_CONTROL_NAMES.total) as FormControl;
  }

  openForPeriods(rowIndex: number): FormControl {
    return this.items.at(rowIndex).get(this.constants.FORM_CONTROL_NAMES.openForPeriods) as FormControl;
  }

  periods(rowIndex: number): FormArray {
    return this.items.at(rowIndex).get(this.constants.FORM_CONTROL_NAMES.budgetPeriods) as FormArray;
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
