import {GeneralBudgetTableEntry} from './general-budget-table-entry';
import {InputTranslation} from '@cat/api';

export class TravelAndAccommodationCostsBudgetTableEntry {

  id?: number;
  description?: InputTranslation[] = [];
  unitType?: InputTranslation[] = [];
  numberOfUnits?: number;
  pricePerUnit?: number;
  rowSum?: number;
  new?: boolean;

  constructor(data: Partial<GeneralBudgetTableEntry>) {
    this.id = data.id;
    this.description = data.description;
    this.unitType = data.unitType;
    this.numberOfUnits = data.numberOfUnits;
    this.pricePerUnit = data.pricePerUnit;
    this.new = data.new;
    this.rowSum = data.rowSum;
  }

}
