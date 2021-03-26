import {Pipe, PipeTransform} from '@angular/core';
import {NumberService} from '../services/number.service';
import {LocaleStore} from '../services/locale-store.service';

@Pipe({name: 'asMoney'})
export class MoneyPipe implements PipeTransform {
  constructor(private numberService: NumberService,
              private localeStore: LocaleStore) {
  }

  transform(value: number | null, locale?: string): string | number {
    if (value === null) {
      return '';
    }
    return NumberService.toLocale(value, locale ? locale : this.localeStore.userLocale());
  }
}
