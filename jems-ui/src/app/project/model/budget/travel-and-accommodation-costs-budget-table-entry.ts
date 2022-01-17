import {GeneralBudgetTableEntry} from './general-budget-table-entry';
import {BudgetPeriodDTO, InputTranslation} from '@cat/api';

export class TravelAndAccommodationCostsBudgetTableEntry {

  id?: number;
  description?: InputTranslation[] = [];
  comments?: InputTranslation[] = [];
  unitType?: InputTranslation[] = [];
  unitCostId?: number;
  numberOfUnits?: number;
  pricePerUnit?: number;
  rowSum?: number;
  new?: boolean;
  budgetPeriods?: BudgetPeriodDTO[];

  constructor(data: Partial<GeneralBudgetTableEntry>) {
    this.id = data.id;
    this.description = data.description;
    this.comments = data.comments;
    this.unitType = data.unitType;
    this.unitCostId = data.unitCostId;
    this.numberOfUnits = data.numberOfUnits;
    this.pricePerUnit = data.pricePerUnit;
    this.new = data.new;
    this.rowSum = data.rowSum;
    this.budgetPeriods = data.budgetPeriods;
  }

}
