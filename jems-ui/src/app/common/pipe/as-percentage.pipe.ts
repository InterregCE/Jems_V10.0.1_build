import {Pipe, PipeTransform} from '@angular/core';
import {NumberService} from '../services/number.service';
import {LocaleStore} from '../services/locale-store.service';

@Pipe({name: 'asPercentage'})
export class AsPercentagePipe implements PipeTransform {

  constructor(
    private localeStore: LocaleStore,
  ) {
  }

  transform(value: number | null, locale?: string, numberFormatOptions?: any): string | number {
    if (value === null) {
      return '';
    }

    const numberOrEmpty = NumberService.toLocale(value, locale ? locale : this.localeStore.userLocale(), numberFormatOptions)
    return numberOrEmpty ? `${numberOrEmpty}\u00A0%` : numberOrEmpty;
  }

}
