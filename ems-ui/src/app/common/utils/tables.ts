import {MatSort} from '@angular/material/sort';
import {PageEvent} from '@angular/material/paginator';

export class Tables {

  static DEFAULT_PAGE_OPTIONS = [5, 10, 25, 50, 100];
  static DEFAULT_INITIAL_PAGE: PageEvent = {pageIndex: 0, pageSize: 25, length: 0};
  static DEFAULT_INITIAL_SORT: Partial<MatSort> = {active: 'id', direction: 'desc'}
}
