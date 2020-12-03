import {ChangeDetectionStrategy, Component, Input, OnInit} from '@angular/core';
import {Tools} from '../../../../common/utils/tools';
import {FlatRateSetupDTO, OutputCall} from '@cat/api';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {FormService} from '@common/components/section/form/form.service';
import {CallStore} from '../../../services/call-store.service';
import {catchError, take, tap} from 'rxjs/operators';
import { CALL_FLAT_RATE_FORM_ERRORS, FLAT_RATE_MAX_VALUES } from './call-flat-rates.constants';

@Component({
  selector: 'app-call-flat-rates',
  templateUrl: './call-flat-rates.component.html',
  styleUrls: ['./call-flat-rates.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CallFlatRatesComponent implements OnInit {

  tools = Tools;
  FORM_ERRORS = CALL_FLAT_RATE_FORM_ERRORS;
  FLAT_RATE_MAX_VALUES = FLAT_RATE_MAX_VALUES;

  @Input()
  call: OutputCall;

  published = false;

  callFlatRateForm: FormGroup;


  constructor(private formBuilder: FormBuilder,
              private formService: FormService,
              private callStore: CallStore) {
  }

  ngOnInit(): void {
    this.initForm(this.call.flatRates);
    this.formService.init(this.callFlatRateForm);
    this.formService.setCreation(!this.call?.id);
    this.published = this.call?.status === OutputCall.StatusEnum.PUBLISHED;
    this.formService.setEditable(!this.published);
  }

  onSubmit(): void {
    const callFlatRates: FlatRateSetupDTO = {
      staffCostFlatRateSetup: this.isStaffCostFlatRateActive.value ? this.staffCostFlatRateSetup.value : null,
      officeOnStaffFlatRateSetup: this.isOfficeOnStaffFlatRateActive.value ? this.officeOnStaffFlatRateSetup.value : null,
      officeOnOtherFlatRateSetup: this.isOfficeOnOtherFlatRateActive.value ? this.officeOnOtherFlatRateSetup.value : null,
      travelOnStaffFlatRateSetup: this.isTravelOnStaffFlatRateActive.value ? this.travelOnStaffFlatRateSetup.value : null,
      otherOnStaffFlatRateSetup: this.isOtherOnStaffFlatRateActive.value ? this.otherOnStaffFlatRateSetup.value : null
    } as FlatRateSetupDTO;
    this.callStore.saveFlatRates(callFlatRates)
      .pipe(
        take(1),
        tap(() => this.formService.setSuccess('call.detail.flat.rate.updated.success')),
        catchError(err => this.formService.setError(err))
      ).subscribe();
  }


  toggleFlatRate(checkboxFormControl: FormControl, rateFormControl: FormControl, typeFormControl: FormControl, defaultValue: number, checked: boolean): void {
    checkboxFormControl
      .patchValue(checked ? checkboxFormControl.value : null);
    if (checked) {
      typeFormControl.enable();
      rateFormControl.setValue(defaultValue);
      typeFormControl.setValue(FLAT_RATE_MAX_VALUES.IS_ADJUSTABLE);
    } else {
      typeFormControl.disable();
      rateFormControl.setValue(null);
      typeFormControl.setValue(null);
    }
  }

  toggleFlatRateType(formControl: FormControl, $event: boolean): void {
    formControl.setValue($event);
    this.callFlatRateForm.markAsDirty();
  }

  initForm(flatRateSetup: FlatRateSetupDTO): void {
    this.callFlatRateForm = this.formBuilder.group({
      isStaffCostFlatRateActive: [!!flatRateSetup.staffCostFlatRateSetup],
      staffCostFlatRateSetup: this.formBuilder.group({
        rate: [flatRateSetup.staffCostFlatRateSetup ? flatRateSetup.staffCostFlatRateSetup.rate : null, [Validators.max(FLAT_RATE_MAX_VALUES.STAFF_COST), Validators.min(1)]],
        isAdjustable: [flatRateSetup.staffCostFlatRateSetup?.isAdjustable]
      }),

      isOfficeOnStaffFlatRateActive: [!!flatRateSetup.officeOnStaffFlatRateSetup],
      officeOnStaffFlatRateSetup: this.formBuilder.group({
        rate: [flatRateSetup.officeOnStaffFlatRateSetup ? flatRateSetup.officeOnStaffFlatRateSetup.rate : null, [Validators.max(FLAT_RATE_MAX_VALUES.OFFICE_ON_STAFF), Validators.min(1)]],
        isAdjustable: [flatRateSetup.officeOnStaffFlatRateSetup?.isAdjustable]
      }),

      isOfficeOnOtherFlatRateActive: [!!flatRateSetup.officeOnOtherFlatRateSetup],
      officeOnOtherFlatRateSetup: this.formBuilder.group({
        rate: [flatRateSetup.officeOnOtherFlatRateSetup ? flatRateSetup.officeOnOtherFlatRateSetup.rate : null, [Validators.max(FLAT_RATE_MAX_VALUES.OFFICE_ON_OTHER), Validators.min(1)]],
        isAdjustable: [flatRateSetup.officeOnOtherFlatRateSetup?.isAdjustable]
      }),

      isTravelOnStaffFlatRateActive: [!!flatRateSetup.travelOnStaffFlatRateSetup],
      travelOnStaffFlatRateSetup: this.formBuilder.group({
        rate: [flatRateSetup.travelOnStaffFlatRateSetup ? flatRateSetup.travelOnStaffFlatRateSetup.rate : null, [Validators.max(FLAT_RATE_MAX_VALUES.TRAVEL_ON_STAFF), Validators.min(1)]],
        isAdjustable: [flatRateSetup.travelOnStaffFlatRateSetup?.isAdjustable]
      }),

      isOtherOnStaffFlatRateActive: [!!flatRateSetup.otherOnStaffFlatRateSetup],
      otherOnStaffFlatRateSetup: this.formBuilder.group({
        rate: [flatRateSetup.otherOnStaffFlatRateSetup ? flatRateSetup.otherOnStaffFlatRateSetup.rate : null, [Validators.max(FLAT_RATE_MAX_VALUES.OTHER_ON_STAFF), Validators.min(1)]],
        isAdjustable: [flatRateSetup.otherOnStaffFlatRateSetup?.isAdjustable]
      })
    });
    this.formService.init(this.callFlatRateForm);
  }


  get isStaffCostFlatRateActive(): FormControl {
    return this.callFlatRateForm.get('isStaffCostFlatRateActive') as FormControl;
  }

  get staffCostFlatRateSetup(): FormGroup {
    return this.callFlatRateForm.get('staffCostFlatRateSetup') as FormGroup;
  }

  get staffCostFlatRate(): FormControl {
    return this.staffCostFlatRateSetup.get('rate') as FormControl;
  }

  get staffCostFlatRateType(): FormControl {
    return this.staffCostFlatRateSetup.get('isAdjustable') as FormControl;
  }

  get isOfficeOnStaffFlatRateActive(): FormControl {
    return this.callFlatRateForm.get('isOfficeOnStaffFlatRateActive') as FormControl;
  }

  get officeOnStaffFlatRateSetup(): FormGroup {
    return this.callFlatRateForm.get('officeOnStaffFlatRateSetup') as FormGroup;
  }

  get officeOnStaffFlatRate(): FormControl {
    return this.officeOnStaffFlatRateSetup.get('rate') as FormControl;
  }

  get officeOnStaffFlatRateType(): FormControl {
    return this.officeOnStaffFlatRateSetup.get('isAdjustable') as FormControl;
  }

  get isOfficeOnOtherFlatRateActive(): FormControl {
    return this.callFlatRateForm.get('isOfficeOnOtherFlatRateActive') as FormControl;
  }

  get officeOnOtherFlatRateSetup(): FormGroup {
    return this.callFlatRateForm.get('officeOnOtherFlatRateSetup') as FormGroup;
  }

  get officeOnOtherFlatRate(): FormControl {
    return this.officeOnOtherFlatRateSetup.get('rate') as FormControl;
  }

  get officeOnOtherFlatRateType(): FormControl {
    return this.officeOnOtherFlatRateSetup.get('isAdjustable') as FormControl;
  }

  get isTravelOnStaffFlatRateActive(): FormControl {
    return this.callFlatRateForm.get('isTravelOnStaffFlatRateActive') as FormControl;
  }

  get travelOnStaffFlatRateSetup(): FormGroup {
    return this.callFlatRateForm.get('travelOnStaffFlatRateSetup') as FormGroup;
  }

  get travelOnStaffFlatRate(): FormControl {
    return this.travelOnStaffFlatRateSetup.get('rate') as FormControl;
  }

  get travelOnStaffFlatRateType(): FormControl {
    return this.travelOnStaffFlatRateSetup.get('isAdjustable') as FormControl;
  }

  get isOtherOnStaffFlatRateActive(): FormControl {
    return this.callFlatRateForm.get('isOtherOnStaffFlatRateActive') as FormControl;
  }

  get otherOnStaffFlatRateSetup(): FormGroup {
    return this.callFlatRateForm.get('otherOnStaffFlatRateSetup') as FormGroup;
  }

  get otherOnStaffFlatRateType(): FormControl {
    return this.otherOnStaffFlatRateSetup.get('isAdjustable') as FormControl;
  }

  get otherOnStaffFlatRate(): FormControl {
    return this.otherOnStaffFlatRateSetup.get('rate') as FormControl;
  }
}
