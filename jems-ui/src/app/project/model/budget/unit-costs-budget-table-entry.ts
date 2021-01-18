export class UnitCostsBudgetTableEntry {

  id?: number;
  unitCostId?: number;
  numberOfUnits?: number;
  rowSum?: number;

  constructor(data: Partial<UnitCostsBudgetTableEntry>, unitCostId: number) {
    this.id = data.id;
    this.unitCostId = unitCostId;
    this.numberOfUnits = data.numberOfUnits;
    this.rowSum = data.rowSum;
  }

}
