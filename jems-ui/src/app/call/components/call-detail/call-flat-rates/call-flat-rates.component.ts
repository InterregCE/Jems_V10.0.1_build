import {ChangeDetectionStrategy, Component, Input, OnInit} from '@angular/core';
import {Tools} from '../../../../common/utils/tools';
import {InputCallFlatRateSetup, OutputCall} from '@cat/api';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {MatDialog} from '@angular/material/dialog';
import {SelectionModel} from '@angular/cdk/collections';
import {FormService} from '@common/components/section/form/form.service';
import {CallStore} from '../../../services/call-store.service';
import {catchError, take, tap} from 'rxjs/operators';

@Component({
  selector: 'app-call-flat-rates',
  templateUrl: './call-flat-rates.component.html',
  styleUrls: ['./call-flat-rates.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CallFlatRatesComponent implements OnInit {
  static ID = 'CallFlatRatesComponent';
  tools = Tools;
  CallFlatRatesComponent = CallFlatRatesComponent;
  InputCallFlatRateSetup = InputCallFlatRateSetup;
  FIXED = 'Fixed';
  UPTO = 'UpTo';
  STAFF_COST = 20;
  OFFICE_ON_STAFF = 15;
  OFFICE_ON_OTHER = 25;
  TRAVEL_ON_STAFF = 15;
  OTHER_ON_STAFF = 40;

  @Input()
  call: OutputCall;

  published = false;
  selection = new SelectionModel<string>(true, []);
  selectedStaffDirectCost: string;
  selectedOfficeAdminDirectStaffCost: string;
  selectedOfficeAdministrationDirectCost: string;
  selectedTravelAccommodationDirectStaffCost: string;
  selectedOtherCosts: string;

  callFlatRateForm = this.formBuilder.group({
    staffDirectCostPercent: ['', Validators.compose([Validators.max(this.STAFF_COST), Validators.min(1)])],
    officeAdminDirectStaffCostPercent: ['', Validators.compose([Validators.max(this.OFFICE_ON_STAFF), Validators.min(1)])],
    officeAdministrationDirectCostPercent: ['', Validators.compose([Validators.max(this.OFFICE_ON_OTHER), Validators.min(1)])],
    travelAccommodationDirectStaffCostPercent: ['', Validators.compose([Validators.max(this.TRAVEL_ON_STAFF), Validators.min(1)])],
    otherCostsPercent: ['', Validators.compose([Validators.max(this.OTHER_ON_STAFF), Validators.min(1)])],
  });

  staffDirectCostPercentErrors = {
    max: 'call.detail.flat.rate.staff.direct.cost',
    min: 'call.detail.flat.rate.staff.direct.cost'
  };
  officeAdminDirectStaffCostPercentErrors = {
    max: 'call.detail.flat.rate.office.admin.direct.staff.cost',
    min: 'call.detail.flat.rate.office.admin.direct.staff.cost'
  };
  officeAdministrationDirectCostPercentErrors = {
    max: 'call.detail.flat.rate.office.admin.direct.cost',
    min: 'call.detail.flat.rate.office.admin.direct.cost'
  };
  travelAccommodationDirectStaffCostPercentErrors = {
    max: 'call.detail.flat.rate.travel.accommodation.direct.staff.cost',
    min: 'call.detail.flat.rate.travel.accommodation.direct.staff.cost'
  };
  otherCostsPercentErrors = {
    max: 'call.detail.flat.rate.other.cost',
    min: 'call.detail.flat.rate.other.cost'
  };

  constructor(private formBuilder: FormBuilder,
              private dialog: MatDialog,
              private formService: FormService,
              private callStore: CallStore) {
  }

  ngOnInit(): void {
    this.formService.init(this.callFlatRateForm);
    this.formService.setCreation(!this.call?.id);
    this.published = this.call?.status === OutputCall.StatusEnum.PUBLISHED;
    this.formService.setEditable(!this.published);
    this.resetForm();
  }

  onSubmit(): void {
    const callFlatRates: InputCallFlatRateSetup[] = [];
    this.deselectAndResetEmpty();
    if (this.selection.isSelected(InputCallFlatRateSetup.TypeEnum.StaffCost)) {
      callFlatRates.push({
          type: InputCallFlatRateSetup.TypeEnum.StaffCost,
          rate: this.callFlatRateForm.controls?.staffDirectCostPercent?.value,
          isAdjustable: this.selectedStaffDirectCost !== this.FIXED
        }
      );
    }
    if (this.selection.isSelected(InputCallFlatRateSetup.TypeEnum.OfficeOnStaff)) {
      callFlatRates.push({
          type: InputCallFlatRateSetup.TypeEnum.OfficeOnStaff,
          rate: this.callFlatRateForm.controls?.officeAdminDirectStaffCostPercent?.value,
          isAdjustable: this.selectedOfficeAdminDirectStaffCost !== this.FIXED
        }
      );
    }
    if (this.selection.isSelected(InputCallFlatRateSetup.TypeEnum.OfficeOnOther)) {
      callFlatRates.push({
          type: InputCallFlatRateSetup.TypeEnum.OfficeOnOther,
          rate: this.callFlatRateForm.controls?.officeAdministrationDirectCostPercent?.value,
          isAdjustable: this.selectedOfficeAdministrationDirectCost !== this.FIXED
        }
      );
    }
    if (this.selection.isSelected(InputCallFlatRateSetup.TypeEnum.TravelOnStaff)) {
      callFlatRates.push({
          type: InputCallFlatRateSetup.TypeEnum.TravelOnStaff,
          rate: this.callFlatRateForm.controls?.travelAccommodationDirectStaffCostPercent?.value,
          isAdjustable: this.selectedTravelAccommodationDirectStaffCost !== this.FIXED
        }
      );
    }
    if (this.selection.isSelected(InputCallFlatRateSetup.TypeEnum.OtherOnStaff)) {
      callFlatRates.push({
          type: InputCallFlatRateSetup.TypeEnum.OtherOnStaff,
          rate: this.callFlatRateForm.controls?.otherCostsPercent?.value,
          isAdjustable: this.selectedOtherCosts !== this.FIXED
        }
      );
    }

    this.callStore.saveFlatRates(callFlatRates)
      .pipe(
        take(1),
        tap(() => this.formService.setSuccess('call.detail.flat.rate.updated.success')),
        catchError(err => this.formService.setError(err))
      ).subscribe();
  }

  formChanged(): void {
    this.formService.setDirty(true);
  }

  resetForm(): void {
    this.selection.clear();
    this.resetToggles(this.FIXED);
    this.resetFields();
    this.call.flatRates.forEach(flatRate => {
      this.selection.toggle(flatRate.type);
      this.setFieldValueDependingOnKey(flatRate.type, flatRate.rate.toString(), false);
      this.setToggleValueDependingOnKey(flatRate.type, flatRate.isAdjustable ? this.UPTO : this.FIXED);
    });
  }

  changeSelection(key: string): void {
    this.selection.toggle(key);
    if (this.selection.isSelected(key)) {
      this.setFieldValueDependingOnKey(key, null, true);
    } else {
      this.setFieldValueDependingOnKey(key, null, false);
      this.setToggleValueDependingOnKey(key, this.FIXED);
    }
    this.formChanged();
  }

  toggleSelectionChanged($event: string, key: string): void {
    this.setToggleValueDependingOnKey(key, $event);
    this.formChanged();
  }

  private setFieldValueDependingOnKey(key: string, value: string | null, useMaxValue: boolean): void {
    if (key === InputCallFlatRateSetup.TypeEnum.StaffCost) {
      useMaxValue ?
        this.callFlatRateForm.controls?.staffDirectCostPercent?.setValue(this.STAFF_COST) :
        this.callFlatRateForm.controls?.staffDirectCostPercent?.setValue(value);
    }
    if (key === InputCallFlatRateSetup.TypeEnum.OfficeOnStaff) {
      useMaxValue ?
        this.callFlatRateForm.controls?.officeAdminDirectStaffCostPercent?.setValue(this.OFFICE_ON_STAFF) :
        this.callFlatRateForm.controls?.officeAdminDirectStaffCostPercent?.setValue(value);
    }
    if (key === InputCallFlatRateSetup.TypeEnum.OfficeOnOther) {
      useMaxValue ?
        this.callFlatRateForm.controls?.officeAdministrationDirectCostPercent?.setValue(this.OFFICE_ON_OTHER) :
        this.callFlatRateForm.controls?.officeAdministrationDirectCostPercent?.setValue(value);
    }
    if (key === InputCallFlatRateSetup.TypeEnum.TravelOnStaff) {
      useMaxValue ?
        this.callFlatRateForm.controls?.travelAccommodationDirectStaffCostPercent?.setValue(this.TRAVEL_ON_STAFF) :
        this.callFlatRateForm.controls?.travelAccommodationDirectStaffCostPercent?.setValue(value);
    }
    if (key === InputCallFlatRateSetup.TypeEnum.OtherOnStaff) {
      useMaxValue ?
        this.callFlatRateForm.controls?.otherCostsPercent?.setValue(this.OTHER_ON_STAFF) :
        this.callFlatRateForm.controls?.otherCostsPercent?.setValue(value);
    }
  }

  private setToggleValueDependingOnKey(key: string, value: string): void {
    if (key === InputCallFlatRateSetup.TypeEnum.StaffCost) {
      this.selectedStaffDirectCost = value;
    }
    if (key === InputCallFlatRateSetup.TypeEnum.OfficeOnStaff) {
      this.selectedOfficeAdminDirectStaffCost = value;
    }
    if (key === InputCallFlatRateSetup.TypeEnum.OfficeOnOther) {
      this.selectedOfficeAdministrationDirectCost = value;
    }
    if (key === InputCallFlatRateSetup.TypeEnum.TravelOnStaff) {
      this.selectedTravelAccommodationDirectStaffCost = value;
    }
    if (key === InputCallFlatRateSetup.TypeEnum.OtherOnStaff) {
      this.selectedOtherCosts = value;
    }
  }

  private resetToggles(value: string): void {
    this.selectedStaffDirectCost = value;
    this.selectedOfficeAdminDirectStaffCost = value;
    this.selectedOfficeAdministrationDirectCost = value;
    this.selectedTravelAccommodationDirectStaffCost = value;
    this.selectedOtherCosts = value;
  }

  private resetFields(): void {
    this.callFlatRateForm.controls?.staffDirectCostPercent?.setValue(null);
    this.callFlatRateForm.controls?.officeAdminDirectStaffCostPercent?.setValue(null);
    this.callFlatRateForm.controls?.officeAdministrationDirectCostPercent?.setValue(null);
    this.callFlatRateForm.controls?.travelAccommodationDirectStaffCostPercent?.setValue(null);
    this.callFlatRateForm.controls?.otherCostsPercent?.setValue(null);
  }

  private deselectAndResetEmpty(): void {
    if (!this.callFlatRateForm.controls?.staffDirectCostPercent?.value) {
      this.selection.deselect(InputCallFlatRateSetup.TypeEnum.StaffCost);
      this.selectedStaffDirectCost = this.FIXED;
    }
    if (!this.callFlatRateForm.controls?.officeAdminDirectStaffCostPercent?.value) {
      this.selection.deselect(InputCallFlatRateSetup.TypeEnum.OfficeOnStaff);
      this.selectedOfficeAdminDirectStaffCost = this.FIXED;
    }
    if (!this.callFlatRateForm.controls?.officeAdministrationDirectCostPercent?.value) {
      this.selection.deselect(InputCallFlatRateSetup.TypeEnum.OfficeOnOther);
      this.selectedOfficeAdministrationDirectCost = this.FIXED;
    }
    if (!this.callFlatRateForm.controls?.travelAccommodationDirectStaffCostPercent?.value) {
      this.selection.deselect(InputCallFlatRateSetup.TypeEnum.TravelOnStaff);
      this.selectedTravelAccommodationDirectStaffCost = this.FIXED;
    }
    if (!this.callFlatRateForm.controls?.otherCostsPercent?.value) {
      this.selection.deselect(InputCallFlatRateSetup.TypeEnum.OtherOnStaff);
      this.selectedOtherCosts = this.FIXED;
    }
  }
}
