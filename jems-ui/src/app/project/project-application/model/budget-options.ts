export class BudgetOptions {
  officeAndAdministrationOnStaffCostsFlatRate: number | null;
  staffCostsFlatRate: number | null;
  travelAndAccommodationOnStaffCostsFlatRate: number | null;
  otherCostsOnStaffCostsFlatRate: number | null;

  constructor(officeAndAdministrationOnStaffCostsFlatRate: number | null, staffCostsFlatRate: number | null, travelAndAccommodationOnStaffCostsFlatRate: number | null, otherCostsOnStaffCostsFlatRate: number | null) {
    this.officeAndAdministrationOnStaffCostsFlatRate = officeAndAdministrationOnStaffCostsFlatRate;
    this.staffCostsFlatRate = staffCostsFlatRate;
    this.travelAndAccommodationOnStaffCostsFlatRate = travelAndAccommodationOnStaffCostsFlatRate;
    this.otherCostsOnStaffCostsFlatRate = otherCostsOnStaffCostsFlatRate;
  }
}
