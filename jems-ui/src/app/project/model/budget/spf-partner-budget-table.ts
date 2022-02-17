import {SpfPartnerBudgetTableEntry} from '@project/model/budget/spf-partner-budget-table-entry';

export class SpfPartnerBudgetTable {
  entries: SpfPartnerBudgetTableEntry[] = [];
  total?: number;

  constructor(total: number, entries: SpfPartnerBudgetTableEntry[]) {
    this.entries = entries;
    this.total = total;
  }
}
