import {Injectable} from '@angular/core';
import {
  PageProjectPartnerReportSummaryDTO,
  ProjectPartnerReportDTO,
  ProjectPartnerReportService,
  ProjectPartnerReportSummaryDTO,
  ProjectPartnerSummaryDTO,
  ProjectPartnerUserCollaboratorService
} from '@cat/api';
import {combineLatest, merge, Observable, of, Subject} from 'rxjs';
import {catchError, filter, map, shareReplay, startWith, switchMap, tap} from 'rxjs/operators';
import {RoutingService} from '@common/services/routing.service';
import {ProjectPartnerStore} from '@project/project-application/containers/project-application-form-page/services/project-partner-store.service';
import {Log} from '@common/utils/log';
import {ProjectPaths} from '@project/common/project-util';
import {
  ProjectStore
} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {Tables} from '@common/utils/tables';

@Injectable({
  providedIn: 'root'
})
export class ProjectPartnerReportPageStore {
  public static REPORT_DETAIL_PATH = '/reports/';

  partnerReports$: Observable<ProjectPartnerReportSummaryDTO[]>;
  partnerReportSummary$: Observable<ProjectPartnerSummaryDTO>;
  partnerReport$: Observable<ProjectPartnerReportDTO>;
  partnerReportLevel$: Observable<string>;

  newPageSize$ = new Subject<number>();
  newPageIndex$ = new Subject<number>();

  private refreshReports$ = new Subject<void>();
  private updatedReport$ = new Subject<ProjectPartnerReportDTO>();

  constructor(private routingService: RoutingService,
              private partnerProjectStore: ProjectPartnerStore,
              private projectPartnerReportService: ProjectPartnerReportService,
              private projectPartnerUserCollaboratorService: ProjectPartnerUserCollaboratorService,
              private projectStore: ProjectStore,) {
    this.partnerReports$ = this.partnerReports();
    this.partnerReportSummary$ = this.partnerReportSummary();
    this.partnerReport$ = this.partnerReport();
    this.partnerReportLevel$ = this.partnerReportLevel();
  }

  private partnerReports(): Observable<ProjectPartnerReportSummaryDTO[]> {
    return combineLatest([
      this.routingService.routeParameterChanges(ProjectPartnerStore.PARTNER_REPORT_DETAIL_PATH, 'partnerId'),
      this.partnerProjectStore.lastContractedVersionASObservable(),
      this.newPageIndex$.pipe(startWith(Tables.DEFAULT_INITIAL_PAGE_INDEX)),
      this.newPageSize$.pipe(startWith(Tables.DEFAULT_INITIAL_PAGE_SIZE)),
      this.refreshReports$.pipe(startWith(null)),
    ])
      .pipe(
        filter(([partnerId, lastContractedVersion, pageIndex, pageSize]) => !!partnerId),
        switchMap(([partnerId, lastContractedVersion, pageIndex, pageSize]) =>
          this.projectPartnerReportService.getProjectPartnerReports(Number(partnerId), pageIndex, pageSize, `number,desc`)),
        map((data: PageProjectPartnerReportSummaryDTO) => data.content),
        tap((data: ProjectPartnerReportSummaryDTO[]) => Log.info('Fetched partner reports for partner:', this, data))
      );
  }

  private partnerReportSummary(): Observable<ProjectPartnerSummaryDTO> {
    return combineLatest([
      this.routingService.routeParameterChanges(ProjectPartnerStore.PARTNER_REPORT_DETAIL_PATH, 'partnerId'),
      this.partnerProjectStore.partnerReportSummaries$
    ]).pipe(
      filter(([partnerId, partnerSummaries]) => !!partnerId),
      map(([partnerId, partnerSummaries]) =>
        this.findPartnerSummary(partnerId, partnerSummaries)
    ));
  }

  createPartnerReport(partnerId: number): Observable<ProjectPartnerReportSummaryDTO> {
    return this.projectPartnerReportService.createProjectPartnerReport(partnerId)
      .pipe(
        tap(() => this.refreshReports$.next()),
        tap(created => Log.info('Created partnerReport:', this, created)),
      );
  }

  private partnerReport(): Observable<ProjectPartnerReportDTO> {
    const initialReport$ = combineLatest([
      this.routingService.routeParameterChanges(ProjectPartnerStore.PARTNER_REPORT_DETAIL_PATH, 'partnerId'),
      this.routingService.routeParameterChanges(ProjectPartnerReportPageStore.REPORT_DETAIL_PATH, 'reportId'),
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
      tap(partner => Log.info('Fetched the programme partner:', this, partner)),
    );

    return merge(initialReport$, this.updatedReport$)
      .pipe(
        shareReplay(1)
      );
  }

  private findPartnerSummary(partnerId: string | number | null, partnerSummaries: ProjectPartnerSummaryDTO[]): ProjectPartnerSummaryDTO
  {
    const found = partnerSummaries.find(value => value.id === Number(partnerId));
    return found ? found : {} as ProjectPartnerSummaryDTO;
  }

  private partnerReportLevel(): Observable<string> {
    return this.routingService.routeParameterChanges(ProjectPartnerStore.PARTNER_REPORT_DETAIL_PATH, 'partnerId')
      .pipe(
        filter((partnerId) => !!partnerId),
        switchMap((partnerId) => this.projectPartnerUserCollaboratorService.checkMyPartnerLevel(Number(partnerId))),
        map((level: string) => level),
        shareReplay(1)
      );
  }
}
