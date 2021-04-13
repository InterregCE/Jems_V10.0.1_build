import {BudgetPeriodDTO, InputTranslation} from '@cat/api';

export class StaffCostsBudgetTableEntry {

  id?: number;
  description?: InputTranslation[] = [];
  numberOfUnits?: number;
  pricePerUnit?: number;
  rowSum?: number;
  new?: boolean;
  unitType?: InputTranslation[] = [];
  unitCostId?: number;
  comment?: InputTranslation[] = [];
  budgetPeriods?: BudgetPeriodDTO[];

  constructor(data: Partial<StaffCostsBudgetTableEntry>) {
    this.id = data.id;
    this.description = data.description;
    this.numberOfUnits = data.numberOfUnits;
    this.pricePerUnit = data.pricePerUnit;
    this.new = data.new;
    this.rowSum = data.rowSum;
    this.unitType = data.unitType;
    this.unitCostId = data.unitCostId;
    this.comment = data.comment;
    this.budgetPeriods = data.budgetPeriods;
  }

}
