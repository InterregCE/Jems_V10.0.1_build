import {PartnerBudgetTableEntry} from './partner-budget-table-entry';
import {InputBudget} from '@cat/api';
import {Numbers} from '../../../common/utils/numbers';

export class PartnerBudgetTable {
  entries: PartnerBudgetTableEntry[] = [];
  total?: number;

  constructor(rawEntries: InputBudget[]) {
    this.entries = rawEntries.map(entry => new PartnerBudgetTableEntry(entry));
    this.computeTotal();
  }

  valid(): boolean {
    return this.entries.every(entry => entry.valid());
  }

  computeTotal(): void {
    const sum = this.entries.reduce((a, b) => a + (b.total || 0), 0);
    this.total = Numbers.floorNumber(sum);
  }
}
