import { InputCallFlatRateSetup } from '@cat/api';

export class BudgetOption {
  currentValue: number;
  callValue: number;
  fixed: boolean;
  isDefault: boolean;
  key: InputCallFlatRateSetup.TypeEnum;
  constructor(value: number, callValue: number, fixed: boolean, isDefault: boolean, key: InputCallFlatRateSetup.TypeEnum) {
    this.currentValue = value;
    this.fixed = fixed;
    this.isDefault = isDefault;
    this.key = key;
  }
}
