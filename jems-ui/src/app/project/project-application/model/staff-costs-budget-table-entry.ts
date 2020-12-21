import {InputTranslation} from '@cat/api';

export class StaffCostsBudgetTableEntry {

  id?: number;
  description?: InputTranslation[] = [];
  numberOfUnits?: number;
  pricePerUnit?: number;
  rowSum?: number;
  new?: boolean;
  typeOfStaff?: string;
  unitType?: string;
  comments?: string;

  constructor(data: Partial<StaffCostsBudgetTableEntry>) {
    this.id = data.id;
    this.description = data.description;
    this.numberOfUnits = data.numberOfUnits;
    this.pricePerUnit = data.pricePerUnit;
    this.new = data.new;
    this.rowSum = data.rowSum;
    this.typeOfStaff = data.typeOfStaff;
    this.unitType = data.unitType;
    this.comments = data.comments;
  }

}
