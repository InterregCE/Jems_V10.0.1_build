import {PartnerBudgetTableEntry} from './partner-budget-table-entry';
import {InputBudget} from '@cat/api';
import {Numbers} from '../../../common/utils/numbers';
import {PartnerBudgetTableType} from './partner-budget-table-type';

export class PartnerBudgetTable {
  type: PartnerBudgetTableType;
  entries: PartnerBudgetTableEntry[] = [];
  total?: number;

  constructor(type: PartnerBudgetTableType, rawEntries: InputBudget[]) {
    this.type = type;
    this.entries = rawEntries.map(entry => new PartnerBudgetTableEntry(entry));
    this.computeTotal();
  }

  valid(): boolean {
    return this.entries.every(entry => entry.valid());
  }

  computeTotal(): void {
    const sum = this.entries.reduce((a, b) => a + (b.total || 0), 0);
    this.total = Numbers.truncateNumber(sum);
  }
}
