import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  Input,
  OnChanges,
  OnDestroy,
  OnInit,
  SimpleChanges
} from '@angular/core';
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
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {ProjectPeriodDTO} from '@cat/api';
import {Alert} from '@common/components/forms/alert';
import {TableConfig} from '@common/directives/table-config/TableConfig';
import {APPLICATION_FORM} from '@project/common/application-form-model';
import {ProjectPartnerBudgetTabService} from '@project/partner/project-partner-detail-page/project-partner-budget-tab/project-partner-budget-tab.service';
import {SpfPartnerBudgetTable} from '@project/model/budget/spf-partner-budget-table';

@UntilDestroy()
@Component({
  selector: 'jems-small-project-fund-table',
  templateUrl: './small-project-fund-table.component.html',
  styleUrls: ['./small-project-fund-table.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SmallProjectFundTableComponent implements OnInit, OnChanges, OnDestroy {
  Alert = Alert;
  constants = ProjectPartnerBudgetConstants;
  APPLICATION_FORM = APPLICATION_FORM;

  @Input()
  editable: boolean;
  @Input()
  spfCostTable: SpfPartnerBudgetTable;
  @Input()
  projectPeriods: ProjectPeriodDTO[];

  spfBudgetForm: FormGroup;
  dataSource: MatTableDataSource<AbstractControl>;
  numberOfItems$: Observable<number>;
  warnOpenForPeriods = false;
  columnsToDisplay: string[];
  tableConfig: TableConfig[];

  constructor(private formService: FormService, private controlContainer: ControlContainer, private formBuilder: FormBuilder, private budgetTabService: ProjectPartnerBudgetTabService, private changeDetectorRef: ChangeDetectorRef) {
    this.spfBudgetForm = this.controlContainer.control as FormGroup;
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

  get spf(): FormGroup {
    return this.spfBudgetForm.get(this.constants.SPF_FORM_CONTROL_NAMES.spf) as FormGroup;
  }

  get items(): FormArray {
    return this.spf.get(this.constants.SPF_FORM_CONTROL_NAMES.items) as FormArray;
  }

  get total(): FormControl {
    return this.spf.get(this.constants.SPF_FORM_CONTROL_NAMES.total) as FormControl;
  }

  ngOnInit(): void {
    this.formService.reset$.pipe(
      map(() => this.resetSpfFormGroup(this.spfCostTable)),
      untilDestroyed(this)
    ).subscribe();

   this.setTableConfig();
  }

  setTableConfig() {
    this.columnsToDisplay = [
      ...this.budgetTabService.addIfItsVisible(APPLICATION_FORM.SECTION_B.BUDGET_AND_CO_FINANCING.SPF_COST.DESCRIPTION, ['description']),
      ...this.budgetTabService.addIfItsVisible(APPLICATION_FORM.SECTION_B.BUDGET_AND_CO_FINANCING.SPF_COST.COMMENTS, ['comments']),
      ...this.budgetTabService.addIfItsVisible(APPLICATION_FORM.SECTION_B.BUDGET_AND_CO_FINANCING.SPF_COST.UNIT_TYPE_AND_NUMBER_OF_UNITS, ['unitType', 'numberOfUnits']),
      ...this.budgetTabService.addIfItsVisible(APPLICATION_FORM.SECTION_B.BUDGET_AND_CO_FINANCING.SPF_COST.PRICE_PER_UNIT, ['pricePerUnit']),
      'total',
      ...this.budgetTabService.getPeriodTableColumns(this.projectPeriods),
      ...this.editable ? ['action'] : []
    ];

    this.tableConfig = [
      ...this.budgetTabService.addIfItsVisible(APPLICATION_FORM.SECTION_B.BUDGET_AND_CO_FINANCING.SPF_COST.DESCRIPTION, [{minInRem: 12}]),
      ...this.budgetTabService.addIfItsVisible(APPLICATION_FORM.SECTION_B.BUDGET_AND_CO_FINANCING.SPF_COST.COMMENTS, [{minInRem: 12}]),
      ...this.budgetTabService.addIfItsVisible(APPLICATION_FORM.SECTION_B.BUDGET_AND_CO_FINANCING.SPF_COST.UNIT_TYPE_AND_NUMBER_OF_UNITS, [{minInRem: 12}]),
      ...this.budgetTabService.addIfItsVisible(APPLICATION_FORM.SECTION_B.BUDGET_AND_CO_FINANCING.SPF_COST.PRICE_PER_UNIT, [{minInRem: 5, maxInRem:5}]),
      {minInRem: 8, maxInRem: 8}, {minInRem: 8},
      ...this.budgetTabService.getPeriodsWidthConfigs(this.projectPeriods),
      ...this.editable ? [{minInRem: 3, maxInRem: 3}] : []
    ];
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.spfCostTable || changes.editable) {
      this.resetSpfFormGroup(this.spfCostTable);
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
    setTimeout(() => this.changeDetectorRef.detectChanges());
  }

  openForPeriods(rowIndex: number): FormControl {
    return this.items.at(rowIndex).get(this.constants.SPF_FORM_CONTROL_NAMES.openForPeriods) as FormControl;
  }

  private resetSpfFormGroup(spfTable: SpfPartnerBudgetTable): void {
    this.total.setValue(spfTable.total, {emitEvent: false});
    this.items.clear();
    spfTable.entries.forEach(item => {
      this.items.push(this.formBuilder.group({
        id: [item.id],
        description: [item.description],
        unitType: [item.unitType],
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
    setTimeout(() => this.changeDetectorRef.detectChanges());
  }
}
