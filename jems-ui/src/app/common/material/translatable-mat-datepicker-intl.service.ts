import {Injectable} from '@angular/core';
import {MatDatepickerIntl} from '@angular/material/datepicker';
import {TranslateService} from '@ngx-translate/core';

@Injectable({
  providedIn: 'root'
})
export class TranslatableMatDatepickerIntl extends MatDatepickerIntl {

  constructor(private translateService: TranslateService) {
    super();
    this.translateService.stream([
        'common.datepicker.calendar.label',
        'common.datepicker.open.calendar.label',
        'common.datepicker.prev.month.label',
        'common.datepicker.next.month.label',
        'common.datepicker.prev.year.label',
        'common.datepicker.next.year.label',
        'common.datepicker.prv.multi.year.label',
        'common.datepicker.next.multi.year.label',
        'common.datepicker.switch.to.month.view.label',
        'common.datepicker.switch.to.multi.year.view.label'
      ]
    ).subscribe((value) => {
        this.calendarLabel = value['common.datepicker.calendar.label'];
        this.openCalendarLabel = value['common.datepicker.open.calendar.label'];
        this.prevMonthLabel = value['common.datepicker.prev.month.label'];
        this.nextMonthLabel = value['common.datepicker.next.month.label'];
        this.prevYearLabel = value['common.datepicker.prev.year.label'];
        this.nextYearLabel = value['common.datepicker.next.year.label'];
        this.prevMultiYearLabel = value['common.datepicker.prv.multi.year.label'];
        this.nextMultiYearLabel = value['common.datepicker.next.multi.year.label'];
        this.switchToMonthViewLabel = value['common.datepicker.switch.to.month.view.label'];
        this.switchToMultiYearViewLabel = value['common.datepicker.switch.to.multi.year.view.label'];
        this.changes.next();
      }
    );
  }
}
