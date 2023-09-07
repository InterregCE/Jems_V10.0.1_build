import {Injectable} from '@angular/core';
import {MatSort} from '@angular/material/sort';
import {combineLatest, Observable, Subject} from 'rxjs';
import {PageProjectReportSummaryDTO, ProjectReportService} from '@cat/api';
import {Tables} from '@common/utils/tables';
import {map, startWith, switchMap, tap} from 'rxjs/operators';
import {Log} from '@common/utils/log';

@Injectable()
export class ProjectReportListStoreService {

  static DEFAULT_SORT: Partial<MatSort> = {active: 'firstSubmission', direction: 'desc'};

  projectReportListPage$: Observable<PageProjectReportSummaryDTO>;
  newPageSize$ = new Subject<number>();
  newPageIndex$ = new Subject<number>();
  newSort$ = new Subject<Partial<MatSort>>();

  constructor(private projectReportService: ProjectReportService) {
    this.projectReportListPage$ = this.projectReportListPage();
  }

  private projectReportListPage(): Observable<PageProjectReportSummaryDTO> {
    const defaultPageSize = Tables.DEFAULT_PAGE_OPTIONS.find(el => el === 10) ?? Tables.DEFAULT_INITIAL_PAGE_SIZE;

    return combineLatest([
      this.newPageIndex$.pipe(startWith(Tables.DEFAULT_INITIAL_PAGE_INDEX)),
      this.newPageSize$.pipe(startWith(defaultPageSize)),
      this.newSort$.pipe(
        startWith(ProjectReportListStoreService.DEFAULT_SORT),
        map(sort => sort?.direction ? sort : ProjectReportListStoreService.DEFAULT_SORT),
        map(sort => `${sort.active},${sort.direction}`)
      )
    ])
      .pipe(
        switchMap(([pageIndex, pageSize, sort]) =>
          this.projectReportService.getMyProjectReports(pageIndex, pageSize, sort)),
        tap(page => Log.info('Fetched my project reports:', this, page.content)),
      );
  }
}
