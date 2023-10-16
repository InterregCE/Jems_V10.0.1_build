import moment, {LongDateFormatKey} from "moment";
import {LocaleStore} from "@common/services/locale-store.service";

export class LocaleDatePipeUtil {

  static MOMENT_BROWSER_LOCALE = moment.localeData(LocaleStore.browserLocale());
  static DEFAULT_DATE_FORMAT = LocaleDatePipeUtil.MOMENT_BROWSER_LOCALE.longDateFormat('L');
  static DEFAULT_DATETIME_FORMAT = `${LocaleDatePipeUtil.DEFAULT_DATE_FORMAT} ${LocaleDatePipeUtil.MOMENT_BROWSER_LOCALE.longDateFormat('LT')}`;

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
  static transform(value?: Date | string | null, dateFormat?: LongDateFormatKey, timeFormat?: LongDateFormatKey): string {
    if (!value) {
      return '';
    }
    const date = moment(value);
    const formattedDate = dateFormat
      ? date.format(LocaleDatePipeUtil.MOMENT_BROWSER_LOCALE.longDateFormat(dateFormat))
      : date.format(LocaleDatePipeUtil.DEFAULT_DATE_FORMAT);
    if (timeFormat) {
      const formattedTime = date.format(LocaleDatePipeUtil.MOMENT_BROWSER_LOCALE.longDateFormat(timeFormat));
      return `${formattedDate} ${formattedTime}`;
    }
    return formattedDate;
  }

}
