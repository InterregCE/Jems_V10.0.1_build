import {Injectable} from '@angular/core';
import {CallService, PageOutputCallList} from '@cat/api';
import {combineLatest, Observable, Subject} from 'rxjs';
import {MatSort} from '@angular/material/sort';
import {map, startWith, switchMap, tap} from 'rxjs/operators';
import {Tables} from '../../utils/tables';
import {Log} from '../../utils/log';

@Injectable()
export class CallListStore {

  page$: Observable<PageOutputCallList>;

  newPageSize$ = new Subject<number>();
  newPageIndex$ = new Subject<number>();
  newSort$ = new Subject<Partial<MatSort>>();

  constructor(private callService: CallService) {
    this.page$ = this.page();
  }

  private page(): Observable<PageOutputCallList> {
    return combineLatest([
      this.newPageIndex$.pipe(startWith(Tables.DEFAULT_INITIAL_PAGE_INDEX)),
      this.newPageSize$.pipe(startWith(Tables.DEFAULT_INITIAL_PAGE_SIZE)),
      this.newSort$.pipe(
        startWith(Tables.DEFAULT_INITIAL_SORT),
        map(sort => sort?.direction ? sort : Tables.DEFAULT_INITIAL_SORT),
        map(sort => `${sort.active},${sort.direction}`)
      )
    ])
      .pipe(
        switchMap(([pageIndex, pageSize, sort]) =>
          this.callService.getCalls(pageIndex, pageSize, sort)),
        tap(page => Log.info('Fetched the projects:', this, page.content)),
      );
  }
}


