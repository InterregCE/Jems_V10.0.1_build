import {FlatRateSetting} from './flat-rate-setting';

export class CallFlatRateSetting {
  staffCostBasedOnDirectCost: FlatRateSetting | null;
  officeBasedOnStaffCost: FlatRateSetting | null;
  officeBasedOnDirectCost: FlatRateSetting | null;
  travelBasedOnStaffCost: FlatRateSetting | null;
  otherBasedOnStaffCost: FlatRateSetting | null;

  constructor(staffCostBasedOnDirectCost: FlatRateSetting | null, officeBasedOnStaffCost: FlatRateSetting | null, officeBasedOnDirectCost: FlatRateSetting | null, travelBasedOnStaffCost: FlatRateSetting | null, otherBasedOnStaffCost: FlatRateSetting | null) {
    this.staffCostBasedOnDirectCost = staffCostBasedOnDirectCost;
    this.officeBasedOnStaffCost = officeBasedOnStaffCost;
    this.officeBasedOnDirectCost = officeBasedOnDirectCost;
    this.travelBasedOnStaffCost = travelBasedOnStaffCost;
    this.otherBasedOnStaffCost = otherBasedOnStaffCost;
  }
}

