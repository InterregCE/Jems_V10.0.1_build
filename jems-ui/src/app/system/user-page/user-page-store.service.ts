import {Injectable} from '@angular/core';
import {PageOutputUserWithRole, UserService} from '@cat/api';
import {combineLatest, Observable, Subject} from 'rxjs';
import {MatSort} from '@angular/material/sort';
import {map, startWith, switchMap, tap} from 'rxjs/operators';
import {Log} from '../../common/utils/log';
import {Tables} from '../../common/utils/tables';

@Injectable()
export class UserPageStore {

  page$: Observable<PageOutputUserWithRole>;

  newPageSize$ = new Subject<number>();
  newPageIndex$ = new Subject<number>();
  newSort$ = new Subject<Partial<MatSort>>();

  constructor(private userService: UserService) {
    this.page$ = this.page();
  }

  private page(): Observable<PageOutputUserWithRole> {
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
          this.userService.list(pageIndex, pageSize, sort)),
        tap(page => Log.info('Fetched the users:', this, page.content)),
      );
  }
}


