import {Numbers} from '../../../common/utils/numbers';
import {isNotNullOrUndefined} from 'codelyzer/util/isNotNullOrUndefined';
import {isNumeric} from 'rxjs/internal-compatibility';
import {MultiLanguageInput} from '@common/components/forms/multi-language/multi-language-input';

export class PartnerBudgetTableEntry {

  id?: number;
  description?: MultiLanguageInput;
  numberOfUnits?: number;
  pricePerUnit?: number;
  total?: number;
  new?: boolean;

  validNumberOfUnits = true;
  validPricePerUnit = true;
  validDescription = true;
  validTotal = true;

  static validDescription = (description: string) => !description || description.length < 250;

  constructor(data: Partial<PartnerBudgetTableEntry>) {
    this.id = data.id;
    this.description = data.description;
    this.numberOfUnits = data.numberOfUnits;
    this.pricePerUnit = data.pricePerUnit;
    this.new = data.new;
    this.computeTotal();
  }

  private static toNumber(value: string): number {
    const valueNumber = Numbers.toDecimal(value);
    if (valueNumber > 999_999_999) {
      return NaN;
    }
    return Numbers.truncateNumber(valueNumber);
  }

  private static validNumber(nr: number): boolean {
    return isNotNullOrUndefined(nr) && isNumeric(nr) && nr <= 999_999_999;
  }

  setNumberOfUnits(newValue: string): void {
    const newNumberOfUnits = PartnerBudgetTableEntry.toNumber(newValue);
    this.validNumberOfUnits = PartnerBudgetTableEntry.validNumber(newNumberOfUnits);
    this.numberOfUnits = isNaN(newNumberOfUnits as any) ? NaN : newNumberOfUnits;
    this.computeTotal();
  }

  setPricePerUnit(newValue: string): void {
    const newPricePerUnit = PartnerBudgetTableEntry.toNumber(newValue);
    this.validPricePerUnit = PartnerBudgetTableEntry.validNumber(newPricePerUnit);
    this.pricePerUnit = isNaN(newPricePerUnit as any) ? NaN : newPricePerUnit;
    this.computeTotal();
  }

  computeTotal(): void {
    const total = Numbers.product([this.numberOfUnits || 0, this.pricePerUnit || 0]);
    this.total = Numbers.truncateNumber(total);
    this.validTotal = PartnerBudgetTableEntry.validNumber(this.total);
  }

  valid(): boolean {
    return this.validDescription && this.validNumberOfUnits && this.validPricePerUnit && this.validTotal || false;
  }
}
