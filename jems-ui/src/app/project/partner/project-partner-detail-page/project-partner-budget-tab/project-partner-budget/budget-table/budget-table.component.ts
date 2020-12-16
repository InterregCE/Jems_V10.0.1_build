import {ChangeDetectionStrategy, Component, Input, OnInit} from '@angular/core';
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
import {Tables} from '../../../../../../common/utils/tables';
import {FormService} from '@common/components/section/form/form.service';
import {MultiLanguageInputService} from '../../../../../../common/services/multi-language-input.service';

@Component({
  selector: 'app-budget-table',
  templateUrl: './budget-table.component.html',
  styleUrls: ['./budget-table.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class BudgetTableComponent implements OnInit {

  constants = ProjectPartnerBudgetConstants;
  columnsToDisplay = ['description', 'numberOfUnits', 'pricePerUnit', 'total', 'action'];

  @Input()
  editable = true;
  @Input()
  tableName: string;

  budgetForm: FormGroup;
  dataSource: MatTableDataSource<AbstractControl>;
  isAddNewItemDisabled$: Observable<boolean>;

  constructor(private formService: FormService, private controlContainer: ControlContainer, private formBuilder: FormBuilder, private multiLanguageInputService: MultiLanguageInputService) {
    this.budgetForm = this.controlContainer.control as FormGroup;
  }

  ngOnInit(): void {

    this.dataSource = new MatTableDataSource<AbstractControl>(this.items.controls);
    this.isAddNewItemDisabled$ = this.items.valueChanges.pipe(startWith(null), map(() => this.items.length >= this.constants.MAX_NUMBER_OF_ITEMS));

    this.items.valueChanges.subscribe(() => {
      this.dataSource.data = this.items.controls;
      this.items.controls.forEach(control => {
        this.setRowTotal(control as FormGroup);
      });
      this.setTableTotal();
    });
  }

  removeItem(index: number): void {
    this.items.removeAt(index);
    this.formService.setDirty(true);
  }

  addNewItem(): void {
    this.items.push(this.formBuilder.group({
      id: Tables.getNextId(this.items.controls),
      description: this.formBuilder.control(this.multiLanguageInputService.multiLanguageFormFieldDefaultValue()),
      numberOfUnits: this.formBuilder.control(1, [Validators.max(this.constants.MAX_VALUE), Validators.min(this.constants.MIN_VALUE)]),
      pricePerUnit: this.formBuilder.control(0, [Validators.max(this.constants.MAX_VALUE), Validators.min(this.constants.MIN_VALUE)]),
      rowSum: this.formBuilder.control(0, [Validators.max(this.constants.MAX_VALUE), Validators.min(this.constants.MIN_VALUE)]),
      new: true
    }));
    this.formService.setDirty(true);
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

  get items(): FormArray {
    return this.table.get(this.constants.FORM_CONTROL_NAMES.items) as FormArray;
  }

  get total(): FormControl {
    return this.table.get(this.constants.FORM_CONTROL_NAMES.total) as FormControl;
  }

}
