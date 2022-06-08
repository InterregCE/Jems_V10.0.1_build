import {Pipe, PipeTransform} from '@angular/core';
import {LocaleDatePipe} from './locale-date.pipe';

@Pipe({name: 'dateFormatInfo'})
export class DateFormatInfoPipe implements PipeTransform {

  transform(label: string, withTime = false): string {
    if (withTime) {
      return `${label} (${LocaleDatePipe.DEFAULT_DATETIME_FORMAT})`;
    }
    return `${label} (${LocaleDatePipe.DEFAULT_DATE_FORMAT})`;
  }
}
