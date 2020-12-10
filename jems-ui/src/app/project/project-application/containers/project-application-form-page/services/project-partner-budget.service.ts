import {Injectable} from '@angular/core';
import {isNotNullOrUndefined} from 'codelyzer/util/isNotNullOrUndefined';
import {isNumeric} from 'rxjs/internal-compatibility';
import {NumberService} from '../../../../../common/services/number.service';

@Injectable()
export class ProjectPartnerBudget {

  static toNumber(value: string): number {
    const valueNumber = NumberService.toDecimal(value);
    if (valueNumber > 999999999999999) {
      return NaN;
    }
    return NumberService.truncateNumber(valueNumber);
  }

  static validNumber(nr: number): boolean {
    return isNotNullOrUndefined(nr) && isNumeric(nr) && nr <= 999999999999999;
  }

  static computeTotal(numberOfUnits?: number,
                      pricePerUnit?: number): number {
    const total = NumberService.product([numberOfUnits || 0, pricePerUnit || 0]);
    return NumberService.truncateNumber(total);
  }

}
