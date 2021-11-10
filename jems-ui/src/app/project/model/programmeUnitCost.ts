import {BudgetCostCategoryEnum} from './lump-sums/BudgetCostCategoryEnum';
import {InputTranslation} from '@cat/api';

export class ProgrammeUnitCost {
  id: number;
  name: InputTranslation[];
  description: InputTranslation[];
  type: InputTranslation[];
  costPerUnit: number;
  isOneCostCategory: boolean;
  categories: BudgetCostCategoryEnum[];

  constructor(
    id: number,
    name: InputTranslation[],
    description: InputTranslation[],
    type: InputTranslation[],
    costPerUnit: number,
    isOneCostCategory: boolean,
    categories: BudgetCostCategoryEnum[]
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
