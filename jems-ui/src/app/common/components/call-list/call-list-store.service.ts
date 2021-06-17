import {Injectable} from '@angular/core';
import {CallService, PageCallDTO} from '@cat/api';
import {combineLatest, Observable, Subject} from 'rxjs';
import {MatSort} from '@angular/material/sort';
import {map, startWith, switchMap, tap} from 'rxjs/operators';
import {Tables} from '../../utils/tables';
import {Log} from '../../utils/log';

@Injectable()
export class CallListStore {

  callPage$: Observable<PageCallDTO>;
  publishedCallPage$: Observable<PageCallDTO>;

  newPageSize$ = new Subject<number>();
  newPageIndex$ = new Subject<number>();
  newSort$ = new Subject<Partial<MatSort>>();

  constructor(private callService: CallService) {
    this.callPage$ = this.callPage();
    this.publishedCallPage$ = this.publishedCallPage();
  }

  private callPage(): Observable<PageCallDTO> {
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

  private publishedCallPage(): Observable<PageCallDTO> {
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
          this.callService.getPublishedCalls(pageIndex, pageSize, sort)
        ),
        tap(page => Log.info('Fetched the calls:', this, page.content)),
      );
  }
}


