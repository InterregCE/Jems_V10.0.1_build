import {Pipe, PipeTransform} from '@angular/core';
import {Numbers} from '../utils/numbers';

@Pipe({name: 'asMoney'})
export class MoneyPipe implements PipeTransform {
  transform(value: number, locale?: string): string | number {
    return Numbers.toLocale(value, locale ? locale : 'de-DE');
  }
}
