import {Injectable} from '@angular/core';
import {combineLatest, Observable} from 'rxjs';
import {
  FinancingSourceBreakdownDTO,
  ProjectReportVerificationOverviewService,
  VerificationWorkOverviewDTO
} from '@cat/api';
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
  verificationWorkOverview$: Observable<VerificationWorkOverviewDTO>;

  constructor(
    private projectStore: ProjectStore,
    private projectReportDetailStore: ProjectReportDetailPageStore,
    private projectReportVerificationOverviewService: ProjectReportVerificationOverviewService,
  ) {
    this.projectId$ = this.projectStore.projectId$;
    this.reportId$ = this.projectReportDetailStore.projectReportId$;
    this.financingSourceBreakdown$ = this.financingSourceBreakdown();
    this.verificationWorkOverview$ = this.verificationWorkOverview();
  }

  private financingSourceBreakdown(): Observable<FinancingSourceBreakdownDTO> {
    return combineLatest([
      this.projectId$,
      this.reportId$,
    ])
      .pipe(
        switchMap(([projectId, reportId]) =>
          this.projectReportVerificationOverviewService.getFinancingSourceBreakdown(projectId, reportId)
        ),
        tap(data => Log.info('Fetched financing source breakdown', this, data)),
      );
  }

  private verificationWorkOverview(): Observable<VerificationWorkOverviewDTO> {
    return combineLatest([
      this.projectId$,
      this.reportId$,
    ])
      .pipe(
        switchMap(([projectId, reportId]) =>
          this.projectReportVerificationOverviewService.getDeductionBreakdown(projectId, reportId)
        ),
        tap(data => Log.info('Fetched verification work overview', this, data)),
      );
  }
}
