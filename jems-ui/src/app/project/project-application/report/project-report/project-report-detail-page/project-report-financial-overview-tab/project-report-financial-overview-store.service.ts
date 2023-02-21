import {Injectable} from '@angular/core';
import {combineLatest, Observable} from 'rxjs';
import {
  CallFundRateDTO,
  CallService,
  CertificateCoFinancingBreakdownDTO, ProjectReportFinancialOverviewService,
} from '@cat/api';

import {map, switchMap, tap} from 'rxjs/operators';
import {Log} from '@common/utils/log';
import {ProjectStore} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {
  ProjectReportDetailPageStore
} from '@project/project-application/report/project-report/project-report-detail-page/project-report-detail-page-store.service';

@Injectable({providedIn: 'root'})
export class ProjectReportFinancialOverviewStoreService {

  perCoFinancing$: Observable<CertificateCoFinancingBreakdownDTO>;
  callFunds$: Observable<CallFundRateDTO[]>;

  constructor(
    private financialOverviewService: ProjectReportFinancialOverviewService,
    private projectReportDetailPageStore: ProjectReportDetailPageStore,
    private projectStore: ProjectStore,
    private callService: CallService
  ) {
    this.perCoFinancing$ = this.perCoFinancing();
    this.callFunds$ = this.callFunds();
  }

  private perCoFinancing(): Observable<CertificateCoFinancingBreakdownDTO> {
    return combineLatest([
      this.projectStore.projectId$,
      this.projectReportDetailPageStore.projectReportId$,
    ])
      .pipe(
        switchMap(([projectId, reportId]) =>
          this.financialOverviewService.getCoFinancingBreakdown(projectId, reportId)
        ),
        tap(data => Log.info('Fetched overview breakdown per co-financing', this, data)),
      );
  }

  private callFunds(): Observable<CallFundRateDTO[]> {
    return combineLatest([
      this.projectStore.projectCall$,
    ])
      .pipe(
        map(([call]) => call.callId),
        switchMap(callId => this.callService.getCallById(callId)),
        map(call => call.funds),
        tap(data => Log.info('Fetched call funds for financial overview', this, data)),
      );
  }
}

