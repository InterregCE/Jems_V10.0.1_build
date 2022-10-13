import {AfterViewInit, Component, EventEmitter, Input, OnChanges, Output, SimpleChanges} from '@angular/core';
import {
  AbstractControl,
  FormArray,
  FormBuilder,
  FormGroup,
  ValidationErrors,
  ValidatorFn,
  Validators
} from '@angular/forms';
import {ContractingDimensionCodeDTO} from '@cat/api';
import {NumberService} from '@common/services/number.service';
import {Alert} from '@common/components/forms/alert';

@Component({
  selector: 'jems-contract-monitoring-codes-of-intervention-table',
  templateUrl: './contract-monitoring-codes-of-intervention-table.component.html',
  styleUrls: ['./contract-monitoring-codes-of-intervention-table.component.scss']
})
export class ContractMonitoringCodesOfInterventionTableComponent implements OnChanges, AfterViewInit {

  @Input()
  formGroup: FormGroup;

  @Input()
  contractMonitoringDimensionCodesDTO: ContractingDimensionCodeDTO[];

  @Input()
  dimensionCodes: {[p: string]: string[]};

  @Input()
  projectBudget: number;

  @Input()
  editable: boolean;

  @Output()
  changed = new EventEmitter<void>();

  Alert = Alert;

  dimensionsEnum = Object.keys(ContractingDimensionCodeDTO.ProgrammeObjectiveDimensionEnum);

  dimensionCodesTableData: AbstractControl[] = [];
  dimensionCodesColumnsToDisplay = [
    'dimension',
    'dimensionCode',
    'projectBudgetAmountShare',
    'projectBudgetPercentShare',
    'delete'
  ];

  constructor(private formBuilder: FormBuilder) {}

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.contractMonitoringDimensionCodesDTO) {
      this.resetForm();
    }
  }

  ngAfterViewInit(): void {
    this.dimensionCodesFormItems.controls.forEach(control =>
      (control as FormGroup).controls.dimensionCode.addValidators(this.dimensionCodeAlreadySelected()));
  }


  get dimensionCodesFormItems(): FormArray {
    return this.formGroup.get('dimensionCodesItems') as FormArray;
  }

  private resetForm(): void {
    this.dimensionCodesFormItems.clear();
    this.contractMonitoringDimensionCodesDTO.forEach((dimensionCodeShare) => {
        const item = this.formBuilder.group({
          id: [dimensionCodeShare.id],
          dimension: [dimensionCodeShare.programmeObjectiveDimension],
          dimensionCode: [dimensionCodeShare.dimensionCode, this.dimensionCodeAlreadySelected()],
          projectBudgetAmountShare: [dimensionCodeShare.projectBudgetAmountShare, this.dimensionCodeAmountValidator()],
          projectBudgetPercentShare: [{value: this.calculateDimensionCodePercentShare(dimensionCodeShare.projectBudgetAmountShare) +'%',  disabled:true}],
        });
        this.dimensionCodesFormItems.push(item);
      }
    );
    this.dimensionCodesTableData = [... this.dimensionCodesFormItems.controls];
  }

  addDimensionCodeData(): void {
    const item = this.formBuilder.group({
      id: 0,
      dimension: [''],
      dimensionCode: ['', {validators: [this.dimensionCodeAlreadySelected()],  updateOn: 'blur'}],
      projectBudgetAmountShare: ['', [this.dimensionCodeAmountValidator()]],
      projectBudgetPercentShare: ['', Validators.max(100)]
    });
    this.dimensionCodesFormItems.push(item);
    this.dimensionCodesTableData = [...this.dimensionCodesFormItems.controls];
    this.changed.emit();
  }

  getCodesForDimension(dimension: ContractingDimensionCodeDTO.ProgrammeObjectiveDimensionEnum): string[] {
    return this.dimensionCodes ? this.dimensionCodes[dimension] : [];
  }

  removeItem(controlIndex: number): void {
    this.dimensionCodesFormItems.removeAt(controlIndex);
    this.dimensionCodesTableData = [... this.dimensionCodesFormItems.controls];
    this.changed.emit();
  }

  resetDimensionControl(event: any, controlIndex: number): void {
    if (event.isUserInput) {
      this.dimensionCodesFormItems.at(controlIndex).setValue({
        id: 0,
        dimension: [event.source.value],
        dimensionCode: ['', {validators: [this.dimensionCodeAlreadySelected()],  updateOn: 'blur'}],
        projectBudgetAmountShare: ['', this.dimensionCodeAmountValidator()],
        projectBudgetPercentShare: [''],
      });
    }
  }

  setDimensionCodeBudgetPercentShare(event: any, index: number): void {
    const selectedDimensionControl = this.dimensionCodesFormItems.at(index).value;
    const newDimensionCodeAmount = selectedDimensionControl.projectBudgetAmountShare;
    this.dimensionCodesFormItems.at(index).patchValue({
      projectBudgetPercentShare: [this.calculateDimensionCodePercentShare(newDimensionCodeAmount) + '%'],
    });
  }

  private calculateDimensionCodePercentShare(dimensionCodeAmountShare: number): number {
    return NumberService.truncateNumber(
      NumberService.divide(NumberService.product([dimensionCodeAmountShare, 100]), this.projectBudget), 2);
  }

  private dimensionCodeAmountValidator(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {

      const value = control.value;

      if (typeof value !== 'number' || !value) {
        return null;
      }
      const parentFormGroup = (control.parent as FormGroup);

      let currentTotalAmountPerDimension = 0;
      if (parentFormGroup){
        const otherDimensionCodes = this.dimensionCodesFormItems.controls
          .filter(dimensionControl => dimensionControl.value.dimension === parentFormGroup.value.dimension &&
            dimensionControl.value.dimensionCode !== parentFormGroup.value.dimensionCode);
        currentTotalAmountPerDimension = otherDimensionCodes.reduce((accumulator, amount) => {
          return accumulator + amount.value.projectBudgetAmountShare;
        }, value);
      }
      const dimensionCodeAmountValid = currentTotalAmountPerDimension <= this.projectBudget;
      return !dimensionCodeAmountValid ? {dimensionCodeAmountError: true}: null;
    };
  }


  private dimensionCodeAlreadySelected(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const value = control.value;
      if (!value) {
        return null;
      }
      const parentFormGroup = (control.parent as FormGroup);
      let existingDimensionCodes: AbstractControl[] = [];
      if (parentFormGroup) {
        const dimensionCodesControls = this.dimensionCodesFormItems.controls
          .filter(otherControl => {
            return otherControl.value.dimension === parentFormGroup.value.dimension &&
              value === otherControl.value.dimensionCode;
          });

        if (this.controlIsNew(parentFormGroup)) {
          existingDimensionCodes.push(...dimensionCodesControls);
        } else {
          existingDimensionCodes = dimensionCodesControls.filter(existingControl => existingControl.value.id !== parentFormGroup.controls.id.value);
        }
      }
      return existingDimensionCodes.length >= 1 ? {dimensionCodeAmountAlreadyExists: true} : null;
    };
  }

  private controlIsNew(control: FormGroup): boolean {
    return control.controls.id.value === 0;
  }}
