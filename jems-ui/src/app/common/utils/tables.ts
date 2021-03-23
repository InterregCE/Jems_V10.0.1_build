import {MatSort} from '@angular/material/sort';

export class Tables {
  static DEFAULT_PAGE_OPTIONS = [5, 10, 25, 50, 100];
  static DEFAULT_INITIAL_PAGE_INDEX = 0;
  static DEFAULT_INITIAL_PAGE_SIZE = 25;
  static DEFAULT_INITIAL_SORT: Partial<MatSort> = {active: 'id', direction: 'desc'};
  static DEFAULT_DATE_FORMAT = 'DD.MM.YYYY HH:mm';
  static DEFAULT_DATE_FORMAT_WITH_SECONDS = 'DD.MM.YYYY HH:mm:ss';
  static DEFAULT_DATE_FORMAT_NO_TIME = 'dd.MM.yyyy';

  static getNextId(entries: any[]): number {
    const next = Math.max(...entries.map(entry => entry.id)) + 1;
    return next < 0 ? 0 : next;
  }
}
