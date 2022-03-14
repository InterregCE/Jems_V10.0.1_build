import {BudgetCostCategoryEnum} from './lump-sums/BudgetCostCategoryEnum';

export class AllowedBudgetCategory {
  realCostsEnabled: boolean;
  unitCostsEnabled: boolean;

  constructor(realCostsEnabled: boolean, unitCostsEnabled: boolean) {
    this.realCostsEnabled = realCostsEnabled;
    this.unitCostsEnabled = unitCostsEnabled;
  }

  realOrUnitCosts(): boolean {
    return this.realCostsEnabled || this.unitCostsEnabled;
  }

  unitCostsOnly(): boolean {
    return !this.realCostsEnabled && this.unitCostsEnabled;
  }

  realCosts(): boolean {
    return this.realCostsEnabled;
  }
}

export class AllowedBudgetCategories {
  categories: Map<BudgetCostCategoryEnum, AllowedBudgetCategory>;

  constructor(entries: any) {
    this.categories = new Map<BudgetCostCategoryEnum, AllowedBudgetCategory>(entries);
  }

  get staff(): AllowedBudgetCategory {
    return this.categories.get(BudgetCostCategoryEnum.STAFF_COSTS) as any;
  }

  get travel(): AllowedBudgetCategory {
    return this.categories.get(BudgetCostCategoryEnum.TRAVEL_AND_ACCOMMODATION_COSTS) as any;
  }

  get external(): AllowedBudgetCategory {
    return this.categories.get(BudgetCostCategoryEnum.EXTERNAL_COSTS) as any;
  }

  get equipment(): AllowedBudgetCategory {
    return this.categories.get(BudgetCostCategoryEnum.EQUIPMENT_COSTS) as any;
  }

  get infrastructure(): AllowedBudgetCategory {
    return this.categories.get(BudgetCostCategoryEnum.INFRASTRUCTURE_COSTS) as any;
  }
}
