import {Pipe, PipeTransform} from '@angular/core';
import {LongDateFormatKey} from 'moment/moment';
import {LocaleDatePipeUtil} from '@common/utils/locale-date-pipe-util';

@Pipe({name: 'localeDate'})
export class LocaleDatePipe implements PipeTransform {
  transform(value?: Date | string | null, dateFormat?: LongDateFormatKey, timeFormat?: LongDateFormatKey): string {
    return LocaleDatePipeUtil.transform(value, dateFormat, timeFormat);
  }
}
