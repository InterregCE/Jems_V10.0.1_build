import {ChangeDetectionStrategy, Component} from '@angular/core';
import {CallDetailDTO, FlatRateDTO, FlatRateSetupDTO} from '@cat/api';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {FormService} from '@common/components/section/form/form.service';
import {catchError, map, take, tap} from 'rxjs/operators';
import {CallFlatRatesConstants} from './call-flat-rates.constants';
import {Tools} from '@common/utils/tools';
import {CallStore} from '../../services/call-store.service';
import {combineLatest, Observable} from 'rxjs';
import {ActivatedRoute} from '@angular/router';

@Component({
  selector: 'jems-call-flat-rates',
  templateUrl: './call-flat-rates.component.html',
  styleUrls: ['./call-flat-rates.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CallFlatRatesComponent {
  tools = Tools;
  constants = CallFlatRatesConstants;

  callId = this.activatedRoute?.snapshot?.params?.callId;
  callFlatRateForm: FormGroup;

  data$: Observable<{
    call: CallDetailDTO;
    callIsEditable: boolean;
    callIsPublished: boolean;
  }>;

  constructor(private callStore: CallStore,
              private formBuilder: FormBuilder,
              private formService: FormService,
              private activatedRoute: ActivatedRoute) {
    this.data$ = combineLatest([
      this.callStore.call$,
      this.callStore.callIsEditable$,
      this.callStore.callIsPublished$,
    ])
      .pipe(
        map(([call, callIsEditable, callIsPublished]) => ({call, callIsEditable, callIsPublished})),
        tap(data => this.resetForm(data.call.flatRates))
      );
  }

  onSubmit(): void {
    const callFlatRates: FlatRateSetupDTO = {
      staffCostFlatRateSetup: this.isStaffCostFlatRateActive.value ? this.staffCostFlatRateSetup.value : null,
      officeAndAdministrationOnStaffCostsFlatRateSetup: this.isOfficeOnStaffFlatRateActive.value ? this.officeOnStaffFlatRateSetup.value : null,
      officeAndAdministrationOnDirectCostsFlatRateSetup: this.isOfficeOnOtherFlatRateActive.value ? this.officeOnOtherFlatRateSetup.value : null,
      travelAndAccommodationOnStaffCostsFlatRateSetup: this.isTravelOnStaffFlatRateActive.value ? this.travelOnStaffFlatRateSetup.value : null,
      otherCostsOnStaffCostsFlatRateSetup: this.isOtherOnStaffFlatRateActive.value ? this.otherOnStaffFlatRateSetup.value : null
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
      typeFormControl.setValue(this.constants.FLAT_RATE_DEFAULT_VALUES.IS_ADJUSTABLE);
    } else {
      typeFormControl.disable();
      rateFormControl.setValue(this.constants.FLAT_RATE_MIN_VALUES);
      typeFormControl.setValue(null);
    }
  }

  toggleFlatRateType(formControl: FormControl, $event: boolean): void {
    formControl.setValue($event);
    this.callFlatRateForm.markAsDirty();
  }

  resetForm(flatRateSetup: FlatRateSetupDTO): void {
    this.callFlatRateForm = this.formBuilder.group({
      isStaffCostFlatRateActive: [!!flatRateSetup.staffCostFlatRateSetup],
      staffCostFlatRateSetup: this.formBuilder.group({
        rate: [flatRateSetup.staffCostFlatRateSetup?.rate, [Validators.max(this.constants.FLAT_RATE_MAX_VALUE), Validators.min(this.constants.FLAT_RATE_MIN_VALUES)]],
        adjustable: [flatRateSetup.staffCostFlatRateSetup?.adjustable]
      }),

      isOfficeOnStaffFlatRateActive: [!!flatRateSetup.officeAndAdministrationOnStaffCostsFlatRateSetup],
      officeOnStaffFlatRateSetup: this.formBuilder.group({
        rate: [flatRateSetup.officeAndAdministrationOnStaffCostsFlatRateSetup?.rate, [Validators.max(this.constants.FLAT_RATE_MAX_VALUE), Validators.min(this.constants.FLAT_RATE_MIN_VALUES)]],
        adjustable: [flatRateSetup.officeAndAdministrationOnStaffCostsFlatRateSetup?.adjustable]
      }),

      isOfficeOnOtherFlatRateActive: [!!flatRateSetup.officeAndAdministrationOnDirectCostsFlatRateSetup],
      officeOnOtherFlatRateSetup: this.formBuilder.group({
        rate: [flatRateSetup.officeAndAdministrationOnDirectCostsFlatRateSetup?.rate, [Validators.max(this.constants.FLAT_RATE_MAX_VALUE), Validators.min(this.constants.FLAT_RATE_MIN_VALUES)]],
        adjustable: [flatRateSetup.officeAndAdministrationOnDirectCostsFlatRateSetup?.adjustable]
      }),

      isTravelOnStaffFlatRateActive: [!!flatRateSetup.travelAndAccommodationOnStaffCostsFlatRateSetup],
      travelOnStaffFlatRateSetup: this.formBuilder.group({
        rate: [flatRateSetup.travelAndAccommodationOnStaffCostsFlatRateSetup?.rate, [Validators.max(this.constants.FLAT_RATE_MAX_VALUE), Validators.min(this.constants.FLAT_RATE_MIN_VALUES)]],
        adjustable: [flatRateSetup.travelAndAccommodationOnStaffCostsFlatRateSetup?.adjustable]
      }),

      isOtherOnStaffFlatRateActive: [!!flatRateSetup.otherCostsOnStaffCostsFlatRateSetup],
      otherOnStaffFlatRateSetup: this.formBuilder.group({
        rate: [flatRateSetup.otherCostsOnStaffCostsFlatRateSetup?.rate, [Validators.max(this.constants.FLAT_RATE_MAX_VALUE), Validators.min(this.constants.FLAT_RATE_MIN_VALUES)]],
        adjustable: [flatRateSetup.otherCostsOnStaffCostsFlatRateSetup?.adjustable]
      })
    });
    this.formService.init(this.callFlatRateForm);
    this.formService.setCreation(!this.callId);
  }

  disabled(flatRate: FlatRateDTO, data: {callIsEditable: boolean; callIsPublished: boolean}): boolean {
    return !data.callIsEditable || (data.callIsPublished && !!flatRate);
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
    return this.staffCostFlatRateSetup.get('adjustable') as FormControl;
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
    return this.officeOnStaffFlatRateSetup.get('adjustable') as FormControl;
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
    return this.officeOnOtherFlatRateSetup.get('adjustable') as FormControl;
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
    return this.travelOnStaffFlatRateSetup.get('adjustable') as FormControl;
  }

  get isOtherOnStaffFlatRateActive(): FormControl {
    return this.callFlatRateForm.get('isOtherOnStaffFlatRateActive') as FormControl;
  }

  get otherOnStaffFlatRateSetup(): FormGroup {
    return this.callFlatRateForm.get('otherOnStaffFlatRateSetup') as FormGroup;
  }

  get otherOnStaffFlatRateType(): FormControl {
    return this.otherOnStaffFlatRateSetup.get('adjustable') as FormControl;
  }

  get otherOnStaffFlatRate(): FormControl {
    return this.otherOnStaffFlatRateSetup.get('rate') as FormControl;
  }
}
