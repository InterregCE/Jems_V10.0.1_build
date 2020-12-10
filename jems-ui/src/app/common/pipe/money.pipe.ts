import {Pipe, PipeTransform} from '@angular/core';
import {NumberService} from '../services/number.service';

@Pipe({name: 'asMoney'})
export class MoneyPipe implements PipeTransform {
  constructor(private numberService: NumberService) {
  }

  transform(value: number | null, locale?: string): string | number {
    if (value === null) {
      return '';
    }
    return NumberService.toLocale(value, locale ? locale : this.numberService.getLocale());
  }
}
