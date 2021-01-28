import {BudgetPeriodDTO, InputTranslation} from '@cat/api';

export class GeneralBudgetTableEntry {

  id?: number;
  description?: InputTranslation[] = [];
  unitType?: InputTranslation[] = [];
  awardProcedures?: InputTranslation[] = [];
  investmentId?: number;
  numberOfUnits?: number;
  pricePerUnit?: number;
  rowSum?: number;
  new?: boolean;
  budgetPeriods?: BudgetPeriodDTO[];

  constructor(data: Partial<GeneralBudgetTableEntry>) {
    this.id = data.id;
    this.description = data.description;
    this.numberOfUnits = data.numberOfUnits;
    this.pricePerUnit = data.pricePerUnit;
    this.unitType = data.unitType;
    this.awardProcedures = data.awardProcedures;
    this.investmentId = data.investmentId;
    this.new = data.new;
    this.rowSum = data.rowSum;
    this.budgetPeriods = data.budgetPeriods;
  }

}
