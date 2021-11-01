import {Injectable} from '@angular/core';
import {PageProjectUserDTO, ProjectUserService} from '@cat/api';
import {BehaviorSubject, combineLatest, Observable, Subject} from 'rxjs';
import {MatSort} from '@angular/material/sort';
import {map, startWith, switchMap, tap} from 'rxjs/operators';
import {Tables} from '../../utils/tables';
import {Log} from '../../utils/log';

@Injectable()
export class ProjectApplicationListUserAssignmentsStore {

  page$: Observable<PageProjectUserDTO>;

  newPageSize$ = new BehaviorSubject<number>(Tables.DEFAULT_INITIAL_PAGE_SIZE);
  newPageIndex$ = new BehaviorSubject<number>(Tables.DEFAULT_INITIAL_PAGE_INDEX);
  newSort$ = new Subject<Partial<MatSort>>();

  constructor(
    private projectUserService: ProjectUserService,
  ) {
    this.page$ = this.page();
  }

  private page(): Observable<PageProjectUserDTO> {
    return combineLatest([
      this.newPageIndex$,
      this.newPageSize$,
      this.newSort$.pipe(
        startWith(Tables.DEFAULT_INITIAL_SORT),
        map(sort => sort?.direction ? sort : Tables.DEFAULT_INITIAL_SORT),
        map(sort => `${sort.active},${sort.direction}`)
      ),
    ]).pipe(
      switchMap(([pageIndex, pageSize, sort]) => this.projectUserService.listProjectsWithAssignedUsers(pageIndex, pageSize, sort)),
      tap(page => Log.info('Fetched project-user assignments:', this, page.content)),
    );
  }

  refresh(): void {
    this.newPageIndex$.next(this.newPageIndex$.value)
  }
}
