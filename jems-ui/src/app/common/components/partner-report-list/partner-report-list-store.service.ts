import {Injectable} from '@angular/core';
import {MatSort} from '@angular/material/sort';
import {combineLatest, Observable, Subject} from 'rxjs';
import {PageProjectPartnerReportSummaryDTO, ProjectPartnerReportService} from '@cat/api';
import {Tables} from '@common/utils/tables';
import {map, startWith, switchMap, tap} from 'rxjs/operators';
import {Log} from '@common/utils/log';

@Injectable()
export class PartnerReportListStoreService {

  static DEFAULT_SORT: Partial<MatSort> = {active: 'firstSubmission', direction: 'desc'};

  partnerReportListPage$: Observable<PageProjectPartnerReportSummaryDTO>;
  newPageSize$ = new Subject<number>();
  newPageIndex$ = new Subject<number>();
  newSort$ = new Subject<Partial<MatSort>>();

  constructor(private projectPartnerReportService: ProjectPartnerReportService) {
    this.partnerReportListPage$ = this.partnerReportListPage();
  }

  private partnerReportListPage(): Observable<PageProjectPartnerReportSummaryDTO> {
    const defaultPageSize = Tables.DEFAULT_PAGE_OPTIONS.find(el => el === 10) ?? Tables.DEFAULT_INITIAL_PAGE_SIZE;

    return combineLatest([
      this.newPageIndex$.pipe(startWith(Tables.DEFAULT_INITIAL_PAGE_INDEX)),
      this.newPageSize$.pipe(startWith(defaultPageSize)),
      this.newSort$.pipe(
        startWith(PartnerReportListStoreService.DEFAULT_SORT),
        map(sort => sort?.direction ? sort : PartnerReportListStoreService.DEFAULT_SORT),
        map(sort => `${sort.active},${sort.direction}`)
      )
    ])
      .pipe(
        switchMap(([pageIndex, pageSize, sort]) =>
          this.projectPartnerReportService.getMyProjectPartnerReports(pageIndex, pageSize, sort)),
        tap(page => Log.info('Fetched my project partner reports:', this, page.content)),
      );
  }
}
