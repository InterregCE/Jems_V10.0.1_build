import {FlatRateSetting} from './flat-rate-setting';

export class CallFlatRateSetting {
  staffCostFlatRateSetup: FlatRateSetting | null;
  officeAndAdministrationOnStaffCostsFlatRateSetup: FlatRateSetting | null;
  officeAndAdministrationOnDirectCostsFlatRateSetup: FlatRateSetting | null;
  travelAndAccommodationOnStaffCostsFlatRateSetup: FlatRateSetting | null;
  otherCostsOnStaffCostsFlatRateSetup: FlatRateSetting | null;

  constructor(staffCostFlatRateSetup: FlatRateSetting | null, officeAndAdministrationOnStaffCostsFlatRateSetup: FlatRateSetting | null, officeAndAdministrationOnDirectCostsFlatRateSetup: FlatRateSetting | null, travelAndAccommodationOnStaffCostsFlatRateSetup: FlatRateSetting | null, otherCostsOnStaffCostsFlatRateSetup: FlatRateSetting | null) {
    this.staffCostFlatRateSetup = staffCostFlatRateSetup;
    this.officeAndAdministrationOnStaffCostsFlatRateSetup = officeAndAdministrationOnStaffCostsFlatRateSetup;
    this.officeAndAdministrationOnDirectCostsFlatRateSetup = officeAndAdministrationOnDirectCostsFlatRateSetup;
    this.travelAndAccommodationOnStaffCostsFlatRateSetup = travelAndAccommodationOnStaffCostsFlatRateSetup;
    this.otherCostsOnStaffCostsFlatRateSetup = otherCostsOnStaffCostsFlatRateSetup;
  }
}

