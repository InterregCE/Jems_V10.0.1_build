import {LumpSumPhaseEnum} from './LumpSumPhaseEnum';
import {BudgetCostCategoryEnum} from './BudgetCostCategoryEnum';
import {InputTranslation} from '@cat/api';

export class ProgrammeLumpSum {
  id: number;
  name: Array<InputTranslation>;
  description: Array<InputTranslation>;
  cost: number;
  isSplittingAllowed: boolean;
  phase: LumpSumPhaseEnum | null;
  categories: Array<BudgetCostCategoryEnum>;

  constructor(
    id: number,
    name: Array<InputTranslation>,
    description: Array<InputTranslation>,
    cost: number,
    isSplittingAllowed: boolean,
    phase: LumpSumPhaseEnum | null,
    categories: Array<BudgetCostCategoryEnum>
  ) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.cost = cost;
    this.isSplittingAllowed = isSplittingAllowed;
    this.phase = phase;
    this.categories = categories;
  }
}
