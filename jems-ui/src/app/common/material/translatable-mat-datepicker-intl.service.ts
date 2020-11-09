import {Injectable} from '@angular/core';
import {MatDatepickerIntl} from '@angular/material/datepicker';
import {TranslateService} from '@ngx-translate/core';
import {LanguageService} from '../services/language.service';

@Injectable({
  providedIn: 'root'
})
export class TranslatableMatDatepickerIntl extends MatDatepickerIntl {

  constructor(private translateService: TranslateService, private languageService: LanguageService) {
    super();
    this.languageService.systemLanguage$.subscribe(() => {
      this.translateLabels();
    })
  }

  translateLabels(): void {
    this.calendarLabel = this.translateService.instant('common.datepicker.calendar.label');
    this.openCalendarLabel = this.translateService.instant('common.datepicker.open.calendar.label');
    this.prevMonthLabel = this.translateService.instant('common.datepicker.prev.month.label');
    this.nextMonthLabel = this.translateService.instant('common.datepicker.next.month.label');
    this.prevYearLabel = this.translateService.instant('common.datepicker.prev.year.label');
    this.nextYearLabel = this.translateService.instant('common.datepicker.next.year.label');
    this.prevMultiYearLabel = this.translateService.instant('common.datepicker.prv.multi.year.label');
    this.nextMultiYearLabel = this.translateService.instant('common.datepicker.next.multi.year.label');
    this.switchToMonthViewLabel = this.translateService.instant('common.datepicker.switch.to.month.view.label');
    this.switchToMultiYearViewLabel = this.translateService.instant('common.datepicker.switch.to.multi.year.view.label');
    this.changes.next();
  }
}
