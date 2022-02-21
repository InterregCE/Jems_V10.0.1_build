import {Injectable} from '@angular/core';
import {
  ProjectPartnerReportDTO,
  ProjectPartnerReportService,
  ProjectPartnerSummaryDTO
} from '@cat/api';
import {combineLatest, merge, Observable, of, Subject} from 'rxjs';
import {catchError, shareReplay, switchMap, tap} from 'rxjs/operators';
import {RoutingService} from '@common/services/routing.service';
import {Log} from '@common/utils/log';
import {ProjectPaths} from '@project/common/project-util';
import {
  ProjectStore
} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {PartnerReportPageStore} from '@project/project-application/report/partner-report-page-store.service';

@Injectable({providedIn: 'root'})
export class PartnerReportDetailPageStore {
  public static REPORT_DETAIL_PATH = '/reports/';

  partnerSummary$: Observable<ProjectPartnerSummaryDTO>;
  partnerReport$: Observable<ProjectPartnerReportDTO>;
  partnerId$: Observable<string | number | null>;

  newPageSize$ = new Subject<number>();
  newPageIndex$ = new Subject<number>();

  private updatedReport$ = new Subject<ProjectPartnerReportDTO>();

  constructor(private routingService: RoutingService,
              private partnerReportPageStore: PartnerReportPageStore,
              private projectPartnerReportService: ProjectPartnerReportService,
              private projectStore: ProjectStore,) {
    this.partnerId$ = this.partnerReportPageStore.partnerId$;
    this.partnerSummary$ = this.partnerReportPageStore.partnerSummary$;
    this.partnerReport$ = this.partnerReport();
  }

  private partnerReport(): Observable<ProjectPartnerReportDTO> {
    const initialReport$ = combineLatest([
      this.partnerId$,
      this.routingService.routeParameterChanges(PartnerReportDetailPageStore.REPORT_DETAIL_PATH, 'reportId'),
      this.projectStore.projectId$
    ]).pipe(
      switchMap(([partnerId, reportId, projectId]) => !!partnerId && !!projectId && !!reportId
        ? this.projectPartnerReportService.getProjectPartnerReport(Number(partnerId), Number(reportId))
          .pipe(
            catchError(() => {
              this.routingService.navigate([ProjectPaths.PROJECT_DETAIL_PATH, projectId, 'reporting']);
              return of({} as ProjectPartnerReportDTO);
            })
          )
        : of({} as ProjectPartnerReportDTO)
      ),
      tap(partner => Log.info('Fetched the partner report:', this, partner)),
    );

    return merge(initialReport$, this.updatedReport$)
      .pipe(
        shareReplay(1)
      );
  }
}
