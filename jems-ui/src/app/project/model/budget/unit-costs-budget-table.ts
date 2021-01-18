import {UnitCostsBudgetTableEntry} from './unit-costs-budget-table-entry';

export class UnitCostsBudgetTable {
  entries: UnitCostsBudgetTableEntry[] = [];
  total?: number;

  constructor(total: number, entries: UnitCostsBudgetTableEntry[]) {
    this.entries = entries;
    this.total = total;
  }
}
