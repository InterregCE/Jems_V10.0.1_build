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
import {Tables} from '../../../../../../common/utils/tables';
import {NumberService} from '../../../../../../common/services/number.service';
import {TravelAndAccommodationCostsBudgetTable} from '../../../../../project-application/model/travel-and-accommodation-costs-budget-table';

@UntilDestroy()
@Component({
  selector: 'app-travel-and-accommodation-costs-budget-table',
  templateUrl: './travel-and-accommodation-costs-budget-table.component.html',
  styleUrls: ['./travel-and-accommodation-costs-budget-table.component.scss']
})
export class TravelAndAccommodationCostsBudgetTableComponent implements OnInit, OnChanges {

  constants = ProjectPartnerBudgetConstants;

  columnsToDisplay = ['description', 'unitType', 'numberOfUnits', 'pricePerUnit', 'total', 'action'];

  @Input()
  editable: boolean;
  @Input()
  travelAndAccommodationTable: TravelAndAccommodationCostsBudgetTable;

  budgetForm: FormGroup;
  dataSource: MatTableDataSource<AbstractControl>;
  numberOfItems$: Observable<number>;

  constructor(private formService: FormService, private controlContainer: ControlContainer, private formBuilder: FormBuilder, private multiLanguageInputService: MultiLanguageInputService) {
    this.budgetForm = this.controlContainer.control as FormGroup;
  }

  ngOnInit(): void {
    this.dataSource = new MatTableDataSource<AbstractControl>(this.items.controls);
    this.numberOfItems$ = this.items.valueChanges.pipe(startWith(null), map(() => this.items.length));
    this.items.valueChanges.subscribe(() => {
      this.dataSource.data = this.items.controls;
      this.items.controls.forEach(control => {
        this.setRowSum(control as FormGroup);
      });
      this.setTotal();
    });

    this.formService.reset$.pipe(
      map(() => this.resetTravelFormGroup(this.travelAndAccommodationTable)),
      untilDestroyed(this)
    ).subscribe();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.travelAndAccommodationTable) {
      this.resetTravelFormGroup(this.travelAndAccommodationTable);
    }
  }

  removeItem(index: number): void {
    this.items.removeAt(index);
    this.formService.setDirty(true);
  }

  addNewItem(): void {
    this.items.push(this.formBuilder.group({
      id: Tables.getNextId(this.items.controls),
      description: this.formBuilder.control(this.multiLanguageInputService.multiLanguageFormFieldDefaultValue()),
      unitType: this.formBuilder.control(this.multiLanguageInputService.multiLanguageFormFieldDefaultValue()),
      numberOfUnits: this.formBuilder.control(1, [Validators.max(this.constants.MAX_VALUE), Validators.min(this.constants.MIN_VALUE)]),
      pricePerUnit: this.formBuilder.control(0, [Validators.max(this.constants.MAX_VALUE), Validators.min(this.constants.MIN_VALUE)]),
      rowSum: this.formBuilder.control(0, [Validators.max(this.constants.MAX_VALUE), Validators.min(this.constants.MIN_VALUE)]),
      new: true
    }));
    this.formService.setDirty(true);
  }

  private resetTravelFormGroup(travelTable: TravelAndAccommodationCostsBudgetTable): void {
    this.total.setValue(travelTable.total);
    this.items.clear();
    travelTable.entries.forEach(item => {
      this.items.push(this.formBuilder.group({
        description: this.formBuilder.control(item.description),
        unitType: this.formBuilder.control(item.unitType),
        numberOfUnits: this.formBuilder.control(item.numberOfUnits, [Validators.max(this.constants.MAX_VALUE), Validators.min(this.constants.MIN_VALUE)]),
        pricePerUnit: this.formBuilder.control(item.pricePerUnit, [Validators.max(this.constants.MAX_VALUE), Validators.min(this.constants.MIN_VALUE)]),
        rowSum: this.formBuilder.control(item.rowSum, [Validators.max(this.constants.MAX_VALUE), Validators.min(this.constants.MIN_VALUE)]),
      }));
    });
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

}
