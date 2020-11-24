import {Pipe, PipeTransform} from '@angular/core';
import {Numbers} from '../utils/numbers';

@Pipe({name: 'percentage'})
export class PercentagePipe implements PipeTransform {
  transform(value: number | null, total: number): string | number {
    if (total === 0 || value === 0 || value === null) {
      return '0 %';
    }
    return `${Numbers.truncateNumber(Numbers.divide(Numbers.product([value, 100]), total))} %`;
  }
}
