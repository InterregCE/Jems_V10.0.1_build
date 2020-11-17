import { InputCallFlatRateSetup } from '@cat/api';

export class BudgetOption {
  value: number;
  fixed: boolean;
  isDefault: boolean;
  key: InputCallFlatRateSetup.TypeEnum;
  constructor(value: number, fixed: boolean, isDefault: boolean, key: InputCallFlatRateSetup.TypeEnum) {
    this.value = value;
    this.fixed = fixed;
    this.isDefault = isDefault;
    this.key = key;
  }
}
