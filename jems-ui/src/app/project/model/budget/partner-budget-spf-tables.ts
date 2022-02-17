import {SpfPartnerBudgetTable} from '@project/model/budget/spf-partner-budget-table';

export class PartnerBudgetSpfTables {
  spfCosts: SpfPartnerBudgetTable;

  constructor(spfCosts: SpfPartnerBudgetTable) {
    this.spfCosts = spfCosts;
  }
}
