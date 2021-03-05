import {Pipe, PipeTransform} from '@angular/core';
import {NumberService} from '../services/number.service';

@Pipe({name: 'percentage'})
export class PercentagePipe implements PipeTransform {
  transform(value: number | null, total: number, fractionLength: number = 0): number {
    if (total === 0 || value === 0 || value === null) {
      return 0;
    }
    return NumberService.truncateNumber(NumberService.divide(NumberService.product([value, 100]), total), fractionLength);
  }
}
