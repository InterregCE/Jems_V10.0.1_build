import {MatSort} from '@angular/material/sort';

export class Tables {
  static DEFAULT_PAGE_OPTIONS = [5, 10, 25, 50, 100];
  static DEFAULT_INITIAL_PAGE_INDEX = 0;
  static DEFAULT_INITIAL_PAGE_SIZE = 25;
  static DEFAULT_INITIAL_SORT: Partial<MatSort> = {active: 'id', direction: 'desc'}
  static DEFAULT_DATE_FORMAT = 'y-MM-dd hh:mm:ss';
}
