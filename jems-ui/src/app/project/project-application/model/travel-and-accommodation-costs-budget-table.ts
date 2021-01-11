import {TravelAndAccommodationCostsBudgetTableEntry} from './travel-and-accommodation-costs-budget-table-entry';

export class TravelAndAccommodationCostsBudgetTable {
  entries: TravelAndAccommodationCostsBudgetTableEntry[] = [];
  total?: number;

  constructor(total: number, entries: TravelAndAccommodationCostsBudgetTableEntry[]) {
    this.entries = entries;
    this.total = total;
  }
}
