import {Injectable} from '@angular/core';
import {PageUserSummaryDTO, UserRoleSummaryDTO, UserSearchRequestDTO, UserService} from '@cat/api';
import {combineLatest, Observable, Subject} from 'rxjs';
import {MatSort} from '@angular/material/sort';
import {map, startWith, switchMap, tap} from 'rxjs/operators';
import {Log} from '@common/utils/log';
import {Tables} from '@common/utils/tables';
import {RoleStore} from '../services/role-store.service';

@Injectable()
export class UserPageStore {
  private filters: UserSearchRequestDTO = {} as UserSearchRequestDTO;

  roles$: Observable<UserRoleSummaryDTO[]>;
  page$: Observable<PageUserSummaryDTO>;

  newPageSize$ = new Subject<number>();
  newPageIndex$ = new Subject<number>();
  newSort$ = new Subject<Partial<MatSort>>();

  constructor(private userService: UserService, private roleStore: RoleStore) {
    this.roles$ = this.roleStore.roles$;
    this.page$ = this.page();
  }

  private page(): Observable<PageUserSummaryDTO> {
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
          this.userService.list(this.filters, pageIndex, pageSize, sort)),
        tap(page => Log.info('Fetched the users:', this, page.content)),
      );
  }

  updateUserList(filters: UserSearchRequestDTO): void {
    this.filters = filters;
    this.newPageIndex$.next(0);
  }
}


