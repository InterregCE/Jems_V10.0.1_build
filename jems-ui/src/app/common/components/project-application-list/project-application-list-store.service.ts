import {Injectable} from '@angular/core';
import {PageOutputProjectSimple, ProjectService} from '@cat/api';
import {combineLatest, Observable, Subject} from 'rxjs';
import {MatSort} from '@angular/material/sort';
import {map, startWith, switchMap, tap} from 'rxjs/operators';
import {Tables} from '../../utils/tables';
import {Log} from '../../utils/log';

@Injectable()
export class ProjectApplicationListStore {

  page$: Observable<PageOutputProjectSimple>;

  newPageSize$ = new Subject<number>();
  newPageIndex$ = new Subject<number>();
  newSort$ = new Subject<Partial<MatSort>>();

  constructor(private projectService: ProjectService) {
    this.page$ = this.page();
  }

  private page(): Observable<PageOutputProjectSimple> {
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
          this.projectService.getProjects(pageIndex, pageSize, sort)),
        tap(page => Log.info('Fetched the projects:', this, page.content)),
      );
  }
}


