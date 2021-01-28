import {BudgetPeriodDTO} from '@cat/api';

export class UnitCostsBudgetTableEntry {

  id?: number;
  unitCostId?: number;
  numberOfUnits?: number;
  rowSum?: number;
  budgetPeriods?: BudgetPeriodDTO[];

  constructor(data: Partial<UnitCostsBudgetTableEntry>, unitCostId: number) {
    this.id = data.id;
    this.unitCostId = unitCostId;
    this.numberOfUnits = data.numberOfUnits;
    this.rowSum = data.rowSum;
    this.budgetPeriods = data.budgetPeriods;
  }

}
