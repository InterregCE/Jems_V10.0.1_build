import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  Input,
  OnChanges,
  OnInit,
  SimpleChanges
} from '@angular/core';
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
import {FormService} from '@common/components/section/form/form.service';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {GeneralBudgetTable} from '@project/model/budget/general-budget-table';
import {InvestmentSummary} from '@project/work-package/project-work-package-page/work-package-detail-page/workPackageInvestment';
import {ProjectPeriodDTO} from '@cat/api';
import {Alert} from '@common/components/forms/alert';
import {TableConfig} from '@common/directives/table-config/TableConfig';
import {ProgrammeUnitCost} from '@project/model/programmeUnitCost';
import {MatSelectChange} from '@angular/material/select/select';
import {ProjectPartnerBudgetTabService} from '@project/partner/project-partner-detail-page/project-partner-budget-tab/project-partner-budget-tab.service';
import {APPLICATION_FORM, ApplicationFormModel} from '@project/common/application-form-model';
import {AllowedBudgetCategory} from '@project/model/allowed-budget-category';

const FIELD_KEYS =
  {
    DESCRIPTION: 'DESCRIPTION',
    COMMENTS: 'COMMENTS',
    AWARD_PROCEDURE: 'AWARD_PROCEDURE',
    INVESTMENT: 'INVESTMENT',
    UNIT_TYPE_AND_NUMBER_OF_UNITS: 'UNIT_TYPE_AND_NUMBER_OF_UNITS',
    PRICE_PER_UNIT: 'PRICE_PER_UNIT'
  };

@UntilDestroy()
@Component({
  selector: 'jems-general-budget-table',
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
  @Input()
  allowedBudgetCategory: AllowedBudgetCategory;

  budgetForm: FormGroup;
  dataSource: MatTableDataSource<AbstractControl>;
  numberOfItems$: Observable<number>;
  warnOpenForPeriods = false;
  columnsToDisplay: string[];
  tableConfig: TableConfig[];

  constructor(private formService: FormService, private controlContainer: ControlContainer, private formBuilder: FormBuilder, private budgetTabService: ProjectPartnerBudgetTabService, private changeDetectorRef: ChangeDetectorRef) {
    this.budgetForm = this.controlContainer.control as FormGroup;
  }

  get table(): FormGroup {
    return this.budgetForm.get(this.tableName) as FormGroup;
  }

  get items(): FormArray {
    return this.table.get(this.constants.FORM_CONTROL_NAMES.items) as FormArray;
  }

  get total(): FormControl {
    return this.table.get(this.constants.FORM_CONTROL_NAMES.total) as FormControl;
  }

  ngOnInit(): void {

    this.formService.reset$.pipe(
      map(() => this.resetTableFormGroup(this.budgetTable)),
      untilDestroyed(this)
    ).subscribe();

    this.setTableConfig();
  }

  setTableConfig() {
    this.columnsToDisplay = [
      ...this.budgetTabService.addIfItsVisible(this.getFieldId(FIELD_KEYS.DESCRIPTION), ['description']),
      ...this.budgetTabService.addIfItsVisible(this.getFieldId(FIELD_KEYS.COMMENTS), ['comments']),
      ...this.budgetTabService.addIfItsVisible(this.getFieldId(FIELD_KEYS.AWARD_PROCEDURE), ['awardProcedures']),
      ...this.budgetTabService.addIfItsVisible(this.getFieldId(FIELD_KEYS.INVESTMENT), ['investment']),
      ...this.budgetTabService.addIfItsVisible(this.getFieldId(FIELD_KEYS.UNIT_TYPE_AND_NUMBER_OF_UNITS), ['unitType', 'numberOfUnits']),
      'pricePerUnit',
      'total',
      ...this.budgetTabService.getPeriodTableColumns(this.projectPeriods),
      ...this.editable ? ['action'] : []
    ];

    this.tableConfig = [
      ...this.budgetTabService.addIfItsVisible(this.getFieldId(FIELD_KEYS.DESCRIPTION), [{minInRem: 12}]),
      ...this.budgetTabService.addIfItsVisible(this.getFieldId(FIELD_KEYS.COMMENTS), [{minInRem: 12}]),
      ...this.budgetTabService.addIfItsVisible(this.getFieldId(FIELD_KEYS.AWARD_PROCEDURE), [{minInRem: 12}]),
      ...this.budgetTabService.addIfItsVisible(this.getFieldId(FIELD_KEYS.INVESTMENT), [{minInRem: 5, maxInRem: 5}]),
      ...this.budgetTabService.addIfItsVisible(this.getFieldId(FIELD_KEYS.UNIT_TYPE_AND_NUMBER_OF_UNITS), [{minInRem: 9}, {
        minInRem: 6,
        maxInRem: 6
      }]),
      {minInRem: 8, maxInRem: 8},
      {minInRem: 9}, // totals column
      ...this.budgetTabService.getPeriodsWidthConfigs(this.projectPeriods),
      ...this.editable ? [{minInRem: 3, maxInRem: 3}] : []
    ];

    if (this.availableUnitCosts.length > 0) {
      this.columnsToDisplay.unshift('unitCost');
      this.tableConfig.unshift({minInRem: 10, maxInRem: 10});
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.budgetTable?.isFirstChange()) {
      this.dataSource = new MatTableDataSource<AbstractControl>(this.items.controls);
      this.numberOfItems$ = this.items.valueChanges.pipe(startWith(0), map(() => this.items.length));
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
    if (changes.budgetTable || changes.editable) {
      this.resetTableFormGroup(this.budgetTable);
    }
  }

  onUnitCostChange(change: MatSelectChange, control: FormGroup, rowIndex: number): void {
    const selectedUnitCost = change.value as ProgrammeUnitCost;
    control.patchValue(
      {
        description: [],
        comments: [],
        unitType: [],
        awardProcedures: [],
        investmentId: null,
        numberOfUnits: 1,
        pricePerUnit: selectedUnitCost?.costPerUnit || 0,
        openForPeriods: 0,
      }
    );
    this.budgetTabService.getPeriodsFormArray(this.items, rowIndex).controls.forEach(periodControl => {
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
      comments: [[]],
      unitType: [[]],
      unitCost: [null, [this.constants.requiredUnitCost(this.allowedBudgetCategory)]],
      awardProcedures: [[]],
      investmentId: [null],
      numberOfUnits: [1, [Validators.max(this.constants.MAX_VALUE), Validators.min(this.constants.MIN_VALUE)]],
      pricePerUnit: [0, [Validators.max(this.constants.MAX_VALUE), Validators.min(this.constants.MIN_VALUE)]],
      rowSum: [0, [Validators.max(this.constants.MAX_VALUE), Validators.min(this.constants.MIN_VALUE)]],
      budgetPeriods: this.formBuilder.array([]),
      openForPeriods: [0],
    }));
    this.budgetTabService.addPeriods(this.items, this.projectPeriods);
    this.formService.setDirty(true);
    setTimeout(() => this.changeDetectorRef.detectChanges());
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

  private resetTableFormGroup(commonBudgetTable: GeneralBudgetTable): void {
    this.total.setValue(commonBudgetTable.total);
    this.items.clear();
    this.budgetTable.entries.forEach(item => {
      this.items.push(this.formBuilder.group({
        id: [item.id],
        description: [item.description],
        comments: [item.comments],
        unitType: [item.unitType],
        unitCost: [this.availableUnitCosts.find(it => it.id === item.unitCostId) || null, [this.constants.requiredUnitCost(this.allowedBudgetCategory)]],
        awardProcedures: [item.awardProcedures],
        investmentId: [item.investmentId],
        numberOfUnits: [item.numberOfUnits, [Validators.max(this.constants.MAX_VALUE), Validators.min(this.constants.MIN_VALUE)]],
        pricePerUnit: [item.pricePerUnit, [Validators.max(this.constants.MAX_VALUE), Validators.min(this.constants.MIN_VALUE)]],
        rowSum: [item.rowSum, [Validators.max(this.constants.MAX_VALUE), Validators.min(this.constants.MIN_VALUE)]],
        budgetPeriods: this.formBuilder.array([]),
        openForPeriods: [0],
      }));
      this.budgetTabService.addPeriods(this.items, this.projectPeriods, item.budgetPeriods);
    });
    this.formService.resetEditable();
    setTimeout(() => this.changeDetectorRef.detectChanges());
  }

  private getFieldId(key: string): string {
    let context: ApplicationFormModel | null = null;
    switch (this.tableName) {
      case ProjectPartnerBudgetConstants.FORM_CONTROL_NAMES.external:
        context = APPLICATION_FORM.SECTION_B.BUDGET_AND_CO_FINANCING.EXTERNAL_EXPERTISE;
        break;
      case ProjectPartnerBudgetConstants.FORM_CONTROL_NAMES.equipment:
        context = APPLICATION_FORM.SECTION_B.BUDGET_AND_CO_FINANCING.EQUIPMENT;
        break;
      case ProjectPartnerBudgetConstants.FORM_CONTROL_NAMES.infrastructure:
        context = APPLICATION_FORM.SECTION_B.BUDGET_AND_CO_FINANCING.INFRASTRUCTURE_AND_WORKS;
        break;
    }
    return context ? context[key] as string : '';
  }
}
