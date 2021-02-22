import {BudgetCostCategoryEnum} from './lump-sums/BudgetCostCategoryEnum';
import {InputTranslation} from '@cat/api';

export class ProgrammeUnitCost {
  id: number;
  name: Array<InputTranslation>;
  description: Array<InputTranslation>;
  type: Array<InputTranslation>;
  costPerUnit: number;
  isOneCostCategory: boolean;
  categories: Array<BudgetCostCategoryEnum>;

  constructor(
    id: number,
    name: Array<InputTranslation>,
    description: Array<InputTranslation>,
    type: Array<InputTranslation>,
    costPerUnit: number,
    isOneCostCategory: boolean,
    categories: Array<BudgetCostCategoryEnum>
  ) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.type = type;
    this.costPerUnit = costPerUnit;
    this.isOneCostCategory = isOneCostCategory;
    this.categories = categories;
  }
}
