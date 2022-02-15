import {BudgetPeriodDTO, InputTranslation} from '@cat/api';

export class SpfPartnerBudgetTableEntry {

  id?: number;
  description?: InputTranslation[] = [];
  numberOfUnits?: number;
  pricePerUnit?: number;
  rowSum?: number;
  unitType?: InputTranslation[] = [];
  unitCostId?: number;
  comments?: InputTranslation[] = [];
  budgetPeriods?: BudgetPeriodDTO[];

  constructor(data: Partial<SpfPartnerBudgetTableEntry>) {
    this.id = data.id;
    this.description = data.description;
    this.numberOfUnits = data.numberOfUnits;
    this.pricePerUnit = data.pricePerUnit;
    this.rowSum = data.rowSum;
    this.unitType = data.unitType;
    this.unitCostId = data.unitCostId;
    this.comments = data.comments;
    this.budgetPeriods = data.budgetPeriods;
  }
}
