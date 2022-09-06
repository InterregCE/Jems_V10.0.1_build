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
  isProjectUnitCost: boolean;

  constructor(
    id: number,
    name: InputTranslation[],
    description: InputTranslation[],
    type: InputTranslation[],
    costPerUnit: number,
    isOneCostCategory: boolean,
    categories: BudgetCostCategoryEnum[],
    isProjectUnitCost: boolean
  ) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.type = type;
    this.costPerUnit = costPerUnit;
    this.isOneCostCategory = isOneCostCategory;
    this.categories = categories;
    this.isProjectUnitCost = isProjectUnitCost;
  }
}
