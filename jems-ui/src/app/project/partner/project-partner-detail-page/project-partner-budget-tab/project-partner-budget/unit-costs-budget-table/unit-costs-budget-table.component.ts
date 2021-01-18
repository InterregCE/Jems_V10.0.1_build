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
import {NumberService} from '../../../../../../common/services/number.service';
import {FormService} from '@common/components/section/form/form.service';
import {MultiLanguageInputService} from '../../../../../../common/services/multi-language-input.service';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {ProgrammeUnitCostDTO} from '@cat/api';
import {UnitCostsBudgetTable} from '../../../../../model/budget/unit-costs-budget-table';

@UntilDestroy()
@Component({
  selector: 'app-unit-costs-budget-table',
  templateUrl: './unit-costs-budget-table.component.html',
  styleUrls: ['./unit-costs-budget-table.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush

})
export class UnitCostsBudgetTableComponent implements OnInit, OnChanges {

  constants = ProjectPartnerBudgetConstants;
  columnsToDisplay = ['unitCost', 'description', 'unitType', 'numberOfUnits', 'pricePerUnit', 'total', 'action'];

  @Input()
  editable: boolean;
  @Input()
  unitCostTable: UnitCostsBudgetTable;
  @Input()
  availableUnitCosts: ProgrammeUnitCostDTO[];

  budgetForm: FormGroup;
  dataSource: MatTableDataSource<AbstractControl>;
  numberOfItems$: Observable<number>;

  constructor(private formService: FormService, private controlContainer: ControlContainer, private formBuilder: FormBuilder, private multiLanguageInputService: MultiLanguageInputService) {
    this.budgetForm = this.controlContainer.control as FormGroup;
  }

  ngOnInit(): void {

    this.dataSource = new MatTableDataSource<AbstractControl>(this.items.controls);
    this.numberOfItems$ = this.items.valueChanges.pipe(startWith(null), map(() => this.items.length));

    this.items.valueChanges.pipe(untilDestroyed(this)).subscribe(() => {
      this.dataSource.data = this.items.controls;
      this.items.controls.forEach(control => {
        this.setRowTotal(control as FormGroup);
      });
      this.setTableTotal();
    });

    this.formService.reset$.pipe(
      map(() => this.resetUnitCostFormGroup(this.unitCostTable)),
      untilDestroyed(this)
    ).subscribe();
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
      new: [true]
    }));
    this.formService.setDirty(true);
  }

  private resetUnitCostFormGroup(unitCostTable: UnitCostsBudgetTable): void {
    this.total.setValue(unitCostTable.total);
    this.items.clear();
    console.log(unitCostTable.entries);
    this.unitCostTable.entries.forEach(item => {
      this.items.push(this.formBuilder.group({
        id: [item.id],
        unitCost: [this.availableUnitCosts.find(it => it.id === item.unitCostId), Validators.required],
        numberOfUnits: [item.numberOfUnits, [Validators.max(this.constants.MAX_VALUE), Validators.min(this.constants.MIN_VALUE)]],
        rowSum: [item.rowSum, [Validators.max(this.constants.MAX_VALUE), Validators.min(this.constants.MIN_VALUE)]],
      }));
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
    const pricePerUnit = control.get(this.constants.FORM_CONTROL_NAMES.unitCost)?.value?.costPerUnit || 0;
    control.get(this.constants.FORM_CONTROL_NAMES.rowSum)?.setValue(NumberService.truncateNumber(NumberService.product([numberOfUnits, pricePerUnit])), {emitEvent: false});
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

}
