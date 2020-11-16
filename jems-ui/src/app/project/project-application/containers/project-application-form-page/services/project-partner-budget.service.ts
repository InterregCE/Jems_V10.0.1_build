import {Injectable} from '@angular/core';
import {Numbers} from '../../../../../common/utils/numbers';
import {isNotNullOrUndefined} from 'codelyzer/util/isNotNullOrUndefined';
import {isNumeric} from 'rxjs/internal-compatibility';

@Injectable()
export class ProjectPartnerBudget {

  static toNumber(value: string): number {
    const valueNumber = Numbers.toDecimal(value);
    if (valueNumber > 999999999999999) {
      return NaN;
    }
    return Numbers.truncateNumber(valueNumber);
  }

  static validNumber(nr: number): boolean {
    return isNotNullOrUndefined(nr) && isNumeric(nr) && nr <= 999999999999999;
  }

  static computeTotal(numberOfUnits?: number,
                      pricePerUnit?: number): number {
    const total = Numbers.product([numberOfUnits || 0, pricePerUnit || 0]);
    return Numbers.truncateNumber(total);
  }

}
