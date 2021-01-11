import {GeneralBudgetTableEntry} from './general-budget-table-entry';

export class GeneralBudgetTable {
  entries: GeneralBudgetTableEntry[] = [];
  total?: number;

  constructor(total: number, entries: GeneralBudgetTableEntry[]) {
    this.entries = entries;
    this.total = total;
  }
}
