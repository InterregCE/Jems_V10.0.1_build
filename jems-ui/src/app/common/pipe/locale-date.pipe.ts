import {Pipe, PipeTransform} from '@angular/core';
import moment, {LongDateFormatKey} from 'moment/moment';
import {LocaleStore} from '../services/locale-store.service';

@Pipe({name: 'localeDate'})
export class LocaleDatePipe implements PipeTransform {

  static MOMENT_BROWSER_LOCALE = moment.localeData(LocaleStore.browserLocale());
  static DEFAULT_DATE_FORMAT = LocaleDatePipe.MOMENT_BROWSER_LOCALE.longDateFormat('L');
  static DEFAULT_DATETIME_FORMAT = `${LocaleDatePipe.DEFAULT_DATE_FORMAT} ${LocaleDatePipe.MOMENT_BROWSER_LOCALE.longDateFormat('LT')}`;

  /**
   * Formats the given date/string as a localized date. The format is a tuple consisting of
   * the date format and the time format. For the 'en' locale the formats look like this:
   * LT : 'HH:mm',
   * LTS : 'HH:mm:ss',
   * L : 'DD/MM/YYYY',
   * LL : 'D MMMM YYYY',
   * LLL : 'D MMMM YYYY HH:mm',
   * LLLL : 'dddd, D MMMM YYYY HH:mm'
   */
  transform(value?: Date | string | null, dateFormat?: LongDateFormatKey, timeFormat?: LongDateFormatKey): string {
    if (!value) {
      return '';
    }
    const date = moment(value);
    const formattedDate = dateFormat
      ? date.format(LocaleDatePipe.MOMENT_BROWSER_LOCALE.longDateFormat(dateFormat))
      : date.format(LocaleDatePipe.DEFAULT_DATE_FORMAT);
    if (timeFormat) {
      const formattedTime = date.format(LocaleDatePipe.MOMENT_BROWSER_LOCALE.longDateFormat(timeFormat));
      return `${formattedDate} ${formattedTime}`;
    }
    return formattedDate;
  }
}
