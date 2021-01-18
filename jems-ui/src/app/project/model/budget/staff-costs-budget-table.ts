import {StaffCostsBudgetTableEntry} from './staff-costs-budget-table-entry';

export class StaffCostsBudgetTable {
  entries: StaffCostsBudgetTableEntry[] = [];
  total?: number;

  constructor(total: number, entries: StaffCostsBudgetTableEntry[]) {
    this.entries = entries;
    this.total = total;
  }
}
