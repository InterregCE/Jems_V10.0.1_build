import {BudgetCostCategoryEnum} from './lump-sums/BudgetCostCategoryEnum';

export class ProgrammeUnitCost {
  id: number;
  name: string;
  description: string;
  type: string;
  costPerUnit: number;
  isOneCostCategory: boolean;
  categories: Array<BudgetCostCategoryEnum>;

  constructor(id: number, name: string, description: string, type: string, costPerUnit: number, isOneCostCategory: boolean, categories: Array<BudgetCostCategoryEnum>) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.type = type;
    this.costPerUnit = costPerUnit;
    this.isOneCostCategory = isOneCostCategory;
    this.categories = categories;
  }
}
