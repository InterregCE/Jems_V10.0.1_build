import {Injectable} from '@angular/core';
import {combineLatest, Observable} from 'rxjs';
import {FinancingSourceBreakdownDTO, ProjectReportFinancialOverviewService} from '@cat/api';
import {switchMap, tap} from 'rxjs/operators';
import {Log} from '@common/utils/log';
import {
  ProjectStore
} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {
  ProjectReportDetailPageStore
} from '@project/project-application/report/project-report/project-report-detail-page/project-report-detail-page-store.service';

@Injectable({providedIn: 'root'})
export class ProjectVerificationReportOverviewTabStoreService {

  private projectId$: Observable<number>;
  private reportId$: Observable<number>;
  financingSourceBreakdown$: Observable<FinancingSourceBreakdownDTO>;

  constructor(
    private projectStore: ProjectStore,
    private projectReportDetailStore: ProjectReportDetailPageStore,
    private projectReportFinancialOverviewService: ProjectReportFinancialOverviewService,
  ) {
    this.projectId$ = this.projectStore.projectId$;
    this.reportId$ = this.projectReportDetailStore.projectReportId$;
    this.financingSourceBreakdown$ = this.financingSourceBreakdown();
  }

  private financingSourceBreakdown(): Observable<FinancingSourceBreakdownDTO> {
    return combineLatest([
      this.projectId$,
      this.reportId$,
    ])
      .pipe(
        switchMap(([projectId, reportId]) =>
          this.projectReportFinancialOverviewService.getFinancingSourceBreakdown(projectId, reportId)
        ),
        tap(data => Log.info('Fetched financing source breakdown', this, data)),
      );
  }
}
