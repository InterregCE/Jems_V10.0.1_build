export class BudgetOptions {
  officeFlatRateBasedOnStaffCost: number | null;
  staffCostsFlatRateBasedOnDirectCost: number | null;
  travelFlatRateBasedOnStaffCost: number | null;

  constructor(officeFlatRateBasedOnStaffCost: number | null, staffCostsFlatRateBasedOnDirectCost: number | null, travelFlatRateBasedOnStaffCost: number | null) {
    this.officeFlatRateBasedOnStaffCost = officeFlatRateBasedOnStaffCost;
    this.staffCostsFlatRateBasedOnDirectCost = staffCostsFlatRateBasedOnDirectCost;
    this.travelFlatRateBasedOnStaffCost = travelFlatRateBasedOnStaffCost;
  }
}
