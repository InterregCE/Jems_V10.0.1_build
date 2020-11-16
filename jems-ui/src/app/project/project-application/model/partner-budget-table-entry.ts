export interface PartnerBudgetTableEntry {
  id?: number;
  numberOfUnits?: number;
  pricePerUnit?: number;
  total?: number;
  new?: boolean;

  validNumberOfUnits: boolean;
  validPricePerUnit: boolean;
  validDescription: boolean;
  validTotal: boolean;

  valid(): boolean;

}
