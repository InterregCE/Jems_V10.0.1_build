import {Pipe, PipeTransform} from '@angular/core';
import {LocaleDatePipeUtil} from '@common/utils/locale-date-pipe-util';

@Pipe({name: 'dateFormatInfo'})
export class DateFormatInfoPipe implements PipeTransform {

  transform(label: string, withTime = false): string {
    if (withTime) {
      return `${label} (${LocaleDatePipeUtil.DEFAULT_DATETIME_FORMAT})`;
    }
    return `${label} (${LocaleDatePipeUtil.DEFAULT_DATE_FORMAT})`;
  }
}
