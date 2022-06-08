import { Pipe, PipeTransform } from '@angular/core';
import {TranslateService} from '@ngx-translate/core';
import {Observable, of} from 'rxjs';

@Pipe({
  name: 'secondsToTimePipe'
})
export class SecondsToTimePipePipe implements PipeTransform {

  constructor(private translateService: TranslateService) {
  }

  transform(seconds: number | null): Observable<string> {
      if(seconds == null) {
        return  of('');
      }
      if(seconds > 86400) {
        return this.translateService.get('common.more.than.a.day');
      }
      else {return of(new Date(seconds * 1000).toISOString().substr(11, 8));}
  }

}
