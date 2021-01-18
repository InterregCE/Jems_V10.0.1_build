import {StaffCostsBudgetTable} from './staff-costs-budget-table';
import {GeneralBudgetTable} from './general-budget-table';
import {UnitCostsBudgetTable} from './unit-costs-budget-table';

export class PartnerBudgetTables {
  staffCosts: StaffCostsBudgetTable;
  travelCosts: GeneralBudgetTable;
  externalCosts: GeneralBudgetTable;
  equipmentCosts: GeneralBudgetTable;
  infrastructureCosts: GeneralBudgetTable;
  unitCosts: UnitCostsBudgetTable;

  constructor(staffCosts: StaffCostsBudgetTable, travelCosts: GeneralBudgetTable, externalCosts: GeneralBudgetTable, equipmentCosts: GeneralBudgetTable, infrastructureCosts: GeneralBudgetTable, unitCosts: UnitCostsBudgetTable) {
    this.staffCosts = staffCosts;
    this.travelCosts = travelCosts;
    this.externalCosts = externalCosts;
    this.equipmentCosts = equipmentCosts;
    this.infrastructureCosts = infrastructureCosts;
    this.unitCosts = unitCosts;
  }
}
