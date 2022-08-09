import {Injectable} from '@angular/core';
import {
  ProjectPartnerReportProcurementService,
  ProjectPartnerReportService,
  PageProjectPartnerReportProcurementSummaryDTO, IdNamePairDTO,
} from '@cat/api';
import {BehaviorSubject, combineLatest, Observable, Subject} from 'rxjs';
import {map, startWith, switchMap, tap} from 'rxjs/operators';
import {RoutingService} from '@common/services/routing.service';
import {Log} from '@common/utils/log';
import {
  ProjectStore
} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {PartnerReportPageStore} from '@project/project-application/report/partner-report-page-store.service';
import {
  PartnerReportDetailPageStore
} from '@project/project-application/report/partner-report-detail-page/partner-report-detail-page-store.service';
import {Tables} from '@common/utils/tables';
import {MatSort} from '@angular/material/sort';

@Injectable({providedIn: 'root'})
export class PartnerReportProcurementsPageStore {

  partnerId$: Observable<string | number | null>;
  page$: Observable<PageProjectPartnerReportProcurementSummaryDTO>;

  newPageSize$ = new BehaviorSubject<number>(Tables.DEFAULT_INITIAL_PAGE_SIZE);
  newPageIndex$ = new BehaviorSubject<number>(Tables.DEFAULT_INITIAL_PAGE_INDEX);
  newSort$ = new Subject<Partial<MatSort>>();

  refreshProcurements$ = new BehaviorSubject<void>(undefined);

  constructor(private routingService: RoutingService,
              private partnerReportPageStore: PartnerReportPageStore,
              private projectPartnerReportService: ProjectPartnerReportService,
              private projectStore: ProjectStore,
              private projectPartnerProcurementService: ProjectPartnerReportProcurementService) {
    this.partnerId$ = this.partnerId();
    this.page$ = this.getProcurements();
  }

  private partnerId(): Observable<number | string | null> {
    return this.routingService.routeParameterChanges(PartnerReportPageStore.PARTNER_REPORT_DETAIL_PATH, 'partnerId');
  }

  public getProcurements(): Observable<PageProjectPartnerReportProcurementSummaryDTO> {
    return combineLatest([
      this.partnerId$.pipe(map(id => Number(id))),
      this.routingService.routeParameterChanges(PartnerReportDetailPageStore.REPORT_DETAIL_PATH, 'reportId')
        .pipe(map(id => Number(id))),
      this.newPageIndex$,
      this.newPageSize$,
      this.newSort$.pipe(
        startWith(({ active: undefined, direction: undefined }) as Partial<MatSort>),
        map((sort: Partial<MatSort>) => sort?.direction ? [`${sort.active},${sort.direction}`] : ['reportEntity.id,desc', 'id,desc']),
      ),
      this.refreshProcurements$,
    ]).pipe(
      switchMap(([partnerId, reportId, page, size, sort]) =>
        this.projectPartnerProcurementService.getProcurement(partnerId, reportId, page, size, sort)),
      tap(data => Log.info('Fetched project procurements for report', this, data))
    );
  }

  deleteProcurement(procurementId: number): Observable<any> {
    return combineLatest([
      this.partnerId$.pipe(map(id => Number(id))),
      this.routingService.routeParameterChanges(PartnerReportDetailPageStore.REPORT_DETAIL_PATH, 'reportId')
        .pipe(map(id => Number(id))),
    ]).pipe(
      switchMap(([partnerId, reportId]) =>
        this.projectPartnerProcurementService.deleteProcurement(partnerId, procurementId, reportId)
      ),
      tap(() => this.refreshProcurements$.next(undefined)),
    );
  }

  public getProcurementList(): Observable<IdNamePairDTO[]> {
    return combineLatest([
      this.partnerId$,
      this.routingService.routeParameterChanges(PartnerReportDetailPageStore.REPORT_DETAIL_PATH, 'reportId'),
    ]).pipe(
      switchMap(([partnerId, reportId]) =>
        this.projectPartnerProcurementService.getProcurementSelectorList(Number(partnerId), Number(reportId))),
      tap(data => Log.info('Fetched project procurements list for report expeditures', this, data))
    );
  }

}
