import {Injectable} from '@angular/core';
import {
  PageProjectPartnerReportSummaryDTO,
  ProjectPartnerReportService,
  ProjectPartnerReportSummaryDTO,
  ProjectPartnerSummaryDTO,
  ProjectPartnerUserCollaboratorService
} from '@cat/api';
import {BehaviorSubject, combineLatest, Observable, Subject} from 'rxjs';
import {filter, map, shareReplay, startWith, switchMap, tap} from 'rxjs/operators';
import {RoutingService} from '@common/services/routing.service';
import {ProjectPartnerStore} from '@project/project-application/containers/project-application-form-page/services/project-partner-store.service';
import {Log} from '@common/utils/log';
import {Tables} from '@common/utils/tables';

@Injectable({providedIn: 'root'})
export class PartnerReportPageStore {
  public static PARTNER_REPORT_DETAIL_PATH = '/reporting/';

  partnerReports$: Observable<ProjectPartnerReportSummaryDTO[]>;
  partnerSummary$: Observable<ProjectPartnerSummaryDTO>;
  partnerReportLevel$: Observable<string>;
  partnerId$: Observable<string | number | null>;

  newPageSize$ = new BehaviorSubject<number>(Tables.DEFAULT_INITIAL_PAGE_SIZE);
  newPageIndex$ = new BehaviorSubject<number>(Tables.DEFAULT_INITIAL_PAGE_INDEX);

  private refreshReports$ = new Subject<void>();

  constructor(private routingService: RoutingService,
              private partnerProjectStore: ProjectPartnerStore,
              private projectPartnerReportService: ProjectPartnerReportService,
              private projectPartnerUserCollaboratorService: ProjectPartnerUserCollaboratorService) {
    this.partnerId$ = this.partnerId();
    this.partnerReports$ = this.partnerReports();
    this.partnerSummary$ = this.partnerSummary();
    this.partnerReportLevel$ = this.partnerReportLevel();
  }

  createPartnerReport(): Observable<ProjectPartnerReportSummaryDTO> {
    return this.partnerId$
      .pipe(
        switchMap(partnerId => this.projectPartnerReportService.createProjectPartnerReport(partnerId as any)),
        tap(() => this.refreshReports$.next()),
        tap(created => Log.info('Created partnerReport:', this, created)),
      );
  }

  private partnerReports(): Observable<ProjectPartnerReportSummaryDTO[]> {
    return combineLatest([
      this.partnerId$,
      this.partnerProjectStore.lastContractedVersionASObservable(),
      this.newPageIndex$,
      this.newPageSize$,
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

  private partnerSummary(): Observable<ProjectPartnerSummaryDTO> {
    return combineLatest([
      this.partnerId$,
      this.partnerProjectStore.partnerReportSummaries$
    ]).pipe(
      filter(([partnerId, partnerSummaries]) => !!partnerId),
      map(([partnerId, partnerSummaries]) =>
        partnerSummaries.find(value => value.id === Number(partnerId)) || {} as any
    ));
  }

  private partnerReportLevel(): Observable<string> {
    return this.partnerId$
      .pipe(
        filter((partnerId) => !!partnerId),
        switchMap((partnerId) => this.projectPartnerUserCollaboratorService.checkMyPartnerLevel(Number(partnerId))),
        map((level: string) => level),
        shareReplay(1)
      );
  }

  private partnerId():Observable<number | string | null> {
    return this.routingService.routeParameterChanges(PartnerReportPageStore.PARTNER_REPORT_DETAIL_PATH, 'partnerId');
  }
}
