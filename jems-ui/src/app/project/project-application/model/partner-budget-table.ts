import {PartnerBudgetTableEntry} from './partner-budget-table-entry';
import {InputBudget} from '@cat/api';
import {Numbers} from '../../../common/utils/numbers';
import {PartnerBudgetTableType} from './partner-budget-table-type';
import {PartnerBudgetStaffCostTableEntry} from './partner-budget-staffcost-table-entry';
import {PartnerBudgetTravelTableEntry} from './partner-budget-travel-table-entry';
import {PartnerBudgetGeneralTableEntry} from './partner-budget-general-table-entry';
import {LanguageService} from '../../../common/services/language.service';

export class PartnerBudgetTable {
  type: PartnerBudgetTableType;
  entries: PartnerBudgetTableEntry[];
  total?: number;

  constructor(type: PartnerBudgetTableType,
              rawEntries: InputBudget[],
              public languageService: LanguageService) {
    this.type = type;
    if (type === PartnerBudgetTableType.STAFF) {
      this.entries = rawEntries.map(entry => new PartnerBudgetStaffCostTableEntry(entry, languageService));
    } else if (type === PartnerBudgetTableType.TRAVEL) {
      this.entries = rawEntries.map(entry => new PartnerBudgetTravelTableEntry(entry, languageService));
    } else {
      this.entries = rawEntries.map(entry => new PartnerBudgetGeneralTableEntry(entry, languageService));
    }
    this.computeTotal();
  }

  valid(): boolean {
    return this.entries.every(entry => entry.valid());
  }

  computeTotal(): void {
    const sum = Numbers.sum(this.entries.map(entry => entry.total || 0));
    this.total = Numbers.truncateNumber(sum);
  }
}
