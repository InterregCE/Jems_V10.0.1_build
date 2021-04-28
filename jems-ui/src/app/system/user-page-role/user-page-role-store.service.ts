import {Injectable} from '@angular/core';
import {combineLatest, Observable, Subject} from 'rxjs';
import {PageUserRoleSummaryDTO, UserRoleService} from '@cat/api';
import {MatSort} from '@angular/material/sort';
import {map, startWith, switchMap, tap} from 'rxjs/operators';
import {Tables} from '../../common/utils/tables';
import {Log} from '../../common/utils/log';

@Injectable()
export class UserPageRoleStore {

  page$: Observable<PageUserRoleSummaryDTO>;

  newPageSize$ = new Subject<number>();
  newPageIndex$ = new Subject<number>();
  newSort$ = new Subject<Partial<MatSort>>();

  constructor(private roleService: UserRoleService) {
    this.page$ = this.page();
  }

  private page(): Observable<PageUserRoleSummaryDTO> {
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
          this.roleService.list(pageIndex, pageSize, sort)),
        tap(page => Log.info('Fetched user roles:', this, page.content)),
      );
  }
}
