import {PartnerBudgetTableEntry} from './partner-budget-table-entry';
import {PartnerBudgetTableType} from './partner-budget-table-type';

export class PartnerBudgetTable {
  type: PartnerBudgetTableType;
  entries: PartnerBudgetTableEntry[] = [];
  total?: number;

  constructor(type: PartnerBudgetTableType, total: number, entries: PartnerBudgetTableEntry[]) {
    this.type = type;
    this.entries = entries;
    this.total = total;
  }
}
