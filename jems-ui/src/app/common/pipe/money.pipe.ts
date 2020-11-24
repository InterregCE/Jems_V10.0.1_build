import {Pipe, PipeTransform} from '@angular/core';
import {Numbers} from '../utils/numbers';

@Pipe({name: 'asMoney'})
export class MoneyPipe implements PipeTransform {
  transform(value: number | null, locale?: string): string | number {
    if (value === null) { return ''; }
    return Numbers.toLocale(value, locale ? locale : 'de-DE');
  }
}
