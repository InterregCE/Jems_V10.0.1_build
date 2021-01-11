import {StaffCostsBudgetTable} from './staff-costs-budget-table';
import {GeneralBudgetTable} from './general-budget-table';

export class PartnerBudgetTables {
  staffCosts: StaffCostsBudgetTable;
  travelCosts: GeneralBudgetTable;
  externalCosts: GeneralBudgetTable;
  equipmentCosts: GeneralBudgetTable;
  infrastructureCosts: GeneralBudgetTable;

  constructor(staffCosts: StaffCostsBudgetTable, travelCosts: GeneralBudgetTable, externalCosts: GeneralBudgetTable, equipmentCosts: GeneralBudgetTable, infrastructureCosts: GeneralBudgetTable) {
    this.staffCosts = staffCosts;
    this.travelCosts = travelCosts;
    this.externalCosts = externalCosts;
    this.equipmentCosts = equipmentCosts;
    this.infrastructureCosts = infrastructureCosts;
  }
}
