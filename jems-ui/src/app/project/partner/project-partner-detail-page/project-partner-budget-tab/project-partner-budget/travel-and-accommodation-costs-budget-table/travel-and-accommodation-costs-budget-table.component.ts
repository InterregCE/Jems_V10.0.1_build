import {Component, Input, OnChanges, OnDestroy, OnInit, SimpleChanges} from '@angular/core';
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
import {map, startWith} from 'rxjs/operators';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {TravelAndAccommodationCostsBudgetTable} from '@project/model/budget/travel-and-accommodation-costs-budget-table';
import {ProjectPeriodDTO} from '@cat/api';
import {TableConfig} from '@common/directives/table-config/TableConfig';
import {Alert} from '@common/components/forms/alert';
import {ProgrammeUnitCost} from '@project/model/programmeUnitCost';
import {MatSelectChange} from '@angular/material/select/select';
import {ProjectPartnerBudgetTabService} from '@project/partner/project-partner-detail-page/project-partner-budget-tab/project-partner-budget-tab.service';
import {APPLICATION_FORM} from '@project/application-form-model';

@UntilDestroy()
@Component({
  selector: 'app-travel-and-accommodation-costs-budget-table',
  templateUrl: './travel-and-accommodation-costs-budget-table.component.html',
  styleUrls: ['./travel-and-accommodation-costs-budget-table.component.scss']
})
export class TravelAndAccommodationCostsBudgetTableComponent implements OnInit, OnChanges, OnDestroy {
  Alert = Alert;
  constants = ProjectPartnerBudgetConstants;

  @Input()
  editable: boolean;
  @Input()
  travelAndAccommodationTable: TravelAndAccommodationCostsBudgetTable;
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

  ngOnInit(): void {
    this.formService.reset$.pipe(
      map(() => this.resetTravelFormGroup(this.travelAndAccommodationTable)),
      untilDestroyed(this)
    ).subscribe();

    this.columnsToDisplay = [
      ...this.budgetTabService.addIfItsVisible(APPLICATION_FORM.SECTION_B.BUDGET_AND_CO_FINANCING.TRAVEL_AND_ACCOMMODATION.DESCRIPTION, ['description']),
      ...this.budgetTabService.addIfItsVisible(APPLICATION_FORM.SECTION_B.BUDGET_AND_CO_FINANCING.TRAVEL_AND_ACCOMMODATION.UNIT_TYPE_AND_NUMBER_OF_UNITS, ['unitType', 'numberOfUnits']),
      'pricePerUnit', 'total',
      ...this.budgetTabService.getPeriodTableColumns(this.projectPeriods),
      'action'
    ];

    this.tableConfig = [
      ...this.budgetTabService.addIfItsVisible(APPLICATION_FORM.SECTION_B.BUDGET_AND_CO_FINANCING.TRAVEL_AND_ACCOMMODATION.DESCRIPTION, [{minInRem: 12}]),
      ...this.budgetTabService.addIfItsVisible(APPLICATION_FORM.SECTION_B.BUDGET_AND_CO_FINANCING.TRAVEL_AND_ACCOMMODATION.UNIT_TYPE_AND_NUMBER_OF_UNITS, [{minInRem: 12}, {minInRem: 5, maxInRem: 5}]),
      {minInRem: 8, maxInRem: 8}, {minInRem: 8},
      ...this.budgetTabService.getPeriodsWidthConfigs(this.projectPeriods),
      {minInRem: 3, maxInRem: 3}
    ];

    if (this.availableUnitCosts.length > 0) {
      this.columnsToDisplay.unshift('unitCost');
      this.tableConfig.unshift({minInRem: 10, maxInRem: 10});
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.travelAndAccommodationTable || changes.editable) {
      this.resetTravelFormGroup(this.travelAndAccommodationTable);
    }
  }

  onUnitCostChange(change: MatSelectChange, control: FormGroup, rowIndex: number): void {
    const selectedUnitCost = change.value as ProgrammeUnitCost;
    control.patchValue(
      {
        description: [],
        unitType: [],
        numberOfUnits: 1,
        pricePerUnit: selectedUnitCost?.costPerUnit || 0,
        openForPeriods: 0,
      }
    );
    this.budgetTabService.getPeriodsFormArray(this.items, rowIndex).controls.forEach(periodControl => {
      periodControl.get(this.constants.FORM_CONTROL_NAMES.amount)?.setValue(0);
    });
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
      unitCost: [null],
      numberOfUnits: [1, [Validators.max(this.constants.MAX_VALUE), Validators.min(this.constants.MIN_VALUE)]],
      pricePerUnit: [0, [Validators.max(this.constants.MAX_VALUE), Validators.min(this.constants.MIN_VALUE)]],
      rowSum: [0, [Validators.max(this.constants.MAX_VALUE), Validators.min(this.constants.MIN_VALUE)]],
      new: [true],
      budgetPeriods: this.formBuilder.array([]),
      openForPeriods: [0],
    }));
    this.budgetTabService.addPeriods(this.items, this.projectPeriods);
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
        unitCost: [this.availableUnitCosts.find(it => it.id === item.unitCostId) || null],
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

  getUnitCost(formGroup: FormGroup): FormControl {
    return formGroup.get(this.constants.FORM_CONTROL_NAMES.unitCost) as FormControl;
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

}
