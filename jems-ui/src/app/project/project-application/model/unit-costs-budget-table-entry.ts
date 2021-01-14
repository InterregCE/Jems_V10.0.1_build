export class UnitCostsBudgetTableEntry {

  id?: number;
  unitCostId?: number;
  description?: string;
  unitType?: string;
  numberOfUnits?: number;
  pricePerUnit?: number;
  rowSum?: number;
  new?: boolean;

  constructor(data: Partial<UnitCostsBudgetTableEntry>) {
    this.id = data.id;
    this.unitCostId = data.unitCostId;
    this.description = data.description;
    this.unitType = data.unitType;
    this.numberOfUnits = data.numberOfUnits;
    this.pricePerUnit = data.pricePerUnit;
    this.new = data.new;
    this.rowSum = data.rowSum;
  }

}
