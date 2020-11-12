export class BudgetOptions {
  officeAdministrationFlatRate: number;
  staffCostsFlatRate: number;

  constructor(officeAdministrationFlatRate: number, staffCostsFlatRate: number) {
    this.officeAdministrationFlatRate = officeAdministrationFlatRate;
    this.staffCostsFlatRate = staffCostsFlatRate;
  }
}
