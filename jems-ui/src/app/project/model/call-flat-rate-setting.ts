import {FlatRateSetting} from './flat-rate-setting';
import { FlatRateDTO } from '@cat/api';

export class CallFlatRateSetting {
  staffCostFlatRateSetup: FlatRateSetting | null;
  officeAndAdministrationOnStaffCostsFlatRateSetup: FlatRateSetting | null;
  officeAndAdministrationOnDirectCostsFlatRateSetup: FlatRateSetting | null;
  travelAndAccommodationOnStaffCostsFlatRateSetup: FlatRateSetting | null;
  otherCostsOnStaffCostsFlatRateSetup: FlatRateSetting | null;

  constructor(staffCostFlatRateSetup: FlatRateDTO | null, officeAndAdministrationOnStaffCostsFlatRateSetup: FlatRateDTO | null, officeAndAdministrationOnDirectCostsFlatRateSetup: FlatRateDTO | null, travelAndAccommodationOnStaffCostsFlatRateSetup: FlatRateDTO | null, otherCostsOnStaffCostsFlatRateSetup: FlatRateDTO | null) {
    this.staffCostFlatRateSetup = staffCostFlatRateSetup ? {rate: staffCostFlatRateSetup.rate, isAdjustable: staffCostFlatRateSetup.adjustable} : null;
    this.officeAndAdministrationOnStaffCostsFlatRateSetup = officeAndAdministrationOnStaffCostsFlatRateSetup ? {rate: officeAndAdministrationOnStaffCostsFlatRateSetup.rate, isAdjustable: officeAndAdministrationOnStaffCostsFlatRateSetup.adjustable} : null;
    this.officeAndAdministrationOnDirectCostsFlatRateSetup = officeAndAdministrationOnDirectCostsFlatRateSetup ? {rate: officeAndAdministrationOnDirectCostsFlatRateSetup.rate, isAdjustable: officeAndAdministrationOnDirectCostsFlatRateSetup.adjustable} : null;
    this.travelAndAccommodationOnStaffCostsFlatRateSetup = travelAndAccommodationOnStaffCostsFlatRateSetup ? {rate: travelAndAccommodationOnStaffCostsFlatRateSetup.rate, isAdjustable: travelAndAccommodationOnStaffCostsFlatRateSetup.adjustable} : null;
    this.otherCostsOnStaffCostsFlatRateSetup = otherCostsOnStaffCostsFlatRateSetup ? {rate: otherCostsOnStaffCostsFlatRateSetup.rate, isAdjustable: otherCostsOnStaffCostsFlatRateSetup.adjustable} : null;
  }
}

