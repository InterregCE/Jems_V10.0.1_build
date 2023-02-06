import {Component, EventEmitter, Input, OnChanges, Output, SimpleChanges} from '@angular/core';
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
export class ContractMonitoringCodesOfInterventionTableComponent implements OnChanges {

  @Input()
  formGroup: FormGroup;

  @Input()
  contractMonitoringDimensionCodesDTO: ContractingDimensionCodeDTO[];

  @Input()
  dimensionCodes: {[p: string]: string[]};

  @Input()
  projectBudget: number;

  @Input()
  projectPartnersNuts: {country: string; nuts3Region: string}[];

  @Input()
  editable: boolean;

  @Output()
  changed = new EventEmitter<void>();

  Alert = Alert;

  dimensionsEnum = Object.keys(ContractingDimensionCodeDTO.ProgrammeObjectiveDimensionEnum);

  selectedDimension: string;

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

  get dimensionCodesFormItems(): FormArray {
    return this.formGroup.get('dimensionCodesItems') as FormArray;
  }

  private resetForm(): void {
    this.dimensionCodesFormItems.clear();
    this.contractMonitoringDimensionCodesDTO.forEach((dimensionCodeShare) => {
        const item = this.formBuilder.group({
          id: [dimensionCodeShare.id],
          dimension: [dimensionCodeShare.programmeObjectiveDimension, Validators.required],
          dimensionCode: [dimensionCodeShare.dimensionCode, [this.dimensionCodeAlreadySelected(), Validators.required]],
          projectBudgetAmountShare: [dimensionCodeShare.projectBudgetAmountShare, [Validators.required, this.dimensionCodeAmountValidator()]],
          projectBudgetPercentShare: [{value: NumberService.toLocale(this.calculateDimensionCodePercentShare(dimensionCodeShare.projectBudgetAmountShare)) + '%', disabled: true}]
        });
        this.dimensionCodesFormItems.push(item);
      }
    );
    if (!this.editable) {
      this.dimensionCodesFormItems.disable();
    }
    this.dimensionCodesTableData = [...this.dimensionCodesFormItems.controls];
  }

  addDimensionCodeData(): void {
    const item = this.formBuilder.group({
      id: 0,
      dimension: ['', Validators.required],
      dimensionCode: ['', {validators: [this.dimensionCodeAlreadySelected()], updateOn: 'blur'}],
      projectBudgetAmountShare: ['', [Validators.required, this.dimensionCodeAmountValidator()]],
      projectBudgetPercentShare: ['', Validators.max(100)]
    });
    this.dimensionCodesFormItems.push(item);
    this.dimensionCodesTableData = [...this.dimensionCodesFormItems.controls];
    this.changed.emit();
  }

  getCodesForDimension(dimension: string): string[] {
    switch (dimension) {
      case ContractingDimensionCodeDTO.ProgrammeObjectiveDimensionEnum.Location:
        return [...this.projectPartnersNuts.map(nuts => nuts.nuts3Region ? nuts.nuts3Region : nuts.country).filter(nuts => nuts != null)];
      default:
        return this.dimensionCodes ? this.dimensionCodes[dimension] : [];
    }
  }

  removeItem(controlIndex: number): void {
    this.dimensionCodesFormItems.removeAt(controlIndex);
    this.dimensionCodesTableData = [...this.dimensionCodesFormItems.controls];
    this.changed.emit();
    const projectBudgetAmountShareControl = this.dimensionCodesFormItems.at(controlIndex).get('projectBudgetAmountShare');
    projectBudgetAmountShareControl?.setValidators([Validators.required, this.dimensionCodeAmountValidator()]);
    projectBudgetAmountShareControl?.updateValueAndValidity();
  }

  resetDimensionControl(event: any, controlIndex: number, dimension: string): void {
    if (event.isUserInput) {
      this.dimensionCodesFormItems.at(controlIndex).setValue({
        id: 0,
        dimension: [event.source.value],
        dimensionCode: '',
        projectBudgetAmountShare: '',
        projectBudgetPercentShare: '',
      });
      const dimensionCodeControl = this.dimensionCodesFormItems.at(controlIndex).get('dimensionCode');
      const projectBudgetAmountShareControl = this.dimensionCodesFormItems.at(controlIndex).get('projectBudgetAmountShare');
      projectBudgetAmountShareControl?.setValidators([Validators.required, this.dimensionCodeAmountValidator()]);
      projectBudgetAmountShareControl?.updateValueAndValidity();
      this.selectedDimension = dimension;
      dimensionCodeControl?.setValidators([Validators.required, this.dimensionCodeAlreadySelected(), this.hasDimensionCodesSet()]);
      dimensionCodeControl?.updateValueAndValidity();
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
    return NumberService.roundNumber(
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
      if (parentFormGroup) {
        const otherDimensionCodes = this.dimensionCodesFormItems.controls
          .filter(dimensionControl => dimensionControl.value.dimension === parentFormGroup.value.dimension &&
            dimensionControl.value.dimensionCode !== parentFormGroup.value.dimensionCode);
        currentTotalAmountPerDimension = otherDimensionCodes.reduce((accumulator, amount) => {
          return accumulator + amount.value.projectBudgetAmountShare;
        }, value);
      }
      const dimensionCodeAmountValid = currentTotalAmountPerDimension <= this.projectBudget;
      return !dimensionCodeAmountValid ? {dimensionCodeAmountError: true} : null;
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
      return existingDimensionCodes.length >= 1 ? {dimensionCodeAlreadyInUse: true} : null;
    };
  }

  private hasDimensionCodesSet(): ValidatorFn {
    return (): ValidationErrors | null => {
      return this.getCodesForDimension(this.selectedDimension).length < 1 ? {dimensionCodesNotSet: true} : null;
    };
  }

  private controlIsNew(control: FormGroup): boolean {
    return control.controls.id.value === 0;
  }
}
