import {FlatRateSetting} from './flat-rate-setting';
import { FlatRateDTO } from '@cat/api';

export class CallFlatRateSetting {
  staffCostFlatRateSetup: FlatRateSetting | null;
  officeAndAdministrationOnStaffCostsFlatRateSetup: FlatRateSetting | null;
  officeAndAdministrationOnDirectCostsFlatRateSetup: FlatRateSetting | null;
  travelAndAccommodationOnStaffCostsFlatRateSetup: FlatRateSetting | null;
  otherCostsOnStaffCostsFlatRateSetup: FlatRateSetting | null;

  constructor(staffCostFlatRateSetup: FlatRateDTO | null, officeAndAdministrationOnStaffCostsFlatRateSetup: FlatRateDTO | null, officeAndAdministrationOnDirectCostsFlatRateSetup: FlatRateDTO | null, travelAndAccommodationOnStaffCostsFlatRateSetup: FlatRateDTO | null, otherCostsOnStaffCostsFlatRateSetup: FlatRateDTO | null) {
    this.staffCostFlatRateSetup = staffCostFlatRateSetup ? {rate: staffCostFlatRateSetup.rate, adjustable: staffCostFlatRateSetup.adjustable} : null;
    this.officeAndAdministrationOnStaffCostsFlatRateSetup = officeAndAdministrationOnStaffCostsFlatRateSetup ? {rate: officeAndAdministrationOnStaffCostsFlatRateSetup.rate, adjustable: officeAndAdministrationOnStaffCostsFlatRateSetup.adjustable} : null;
    this.officeAndAdministrationOnDirectCostsFlatRateSetup = officeAndAdministrationOnDirectCostsFlatRateSetup ? {rate: officeAndAdministrationOnDirectCostsFlatRateSetup.rate, adjustable: officeAndAdministrationOnDirectCostsFlatRateSetup.adjustable} : null;
    this.travelAndAccommodationOnStaffCostsFlatRateSetup = travelAndAccommodationOnStaffCostsFlatRateSetup ? {rate: travelAndAccommodationOnStaffCostsFlatRateSetup.rate, adjustable: travelAndAccommodationOnStaffCostsFlatRateSetup.adjustable} : null;
    this.otherCostsOnStaffCostsFlatRateSetup = otherCostsOnStaffCostsFlatRateSetup ? {rate: otherCostsOnStaffCostsFlatRateSetup.rate, adjustable: otherCostsOnStaffCostsFlatRateSetup.adjustable} : null;
  }
}

