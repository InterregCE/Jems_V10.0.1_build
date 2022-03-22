import {Injectable} from '@angular/core';
import {
  IdNamePairDTO,
  ProjectPartnerReportProcurementDTO,
  ProjectPartnerReportProcurementService,
  ProjectPartnerReportService,
  UpdateProjectPartnerReportProcurementDTO
} from '@cat/api';
import {combineLatest, merge, Observable, Subject} from 'rxjs';
import {switchMap, tap} from 'rxjs/operators';
import {RoutingService} from '@common/services/routing.service';
import {Log} from '@common/utils/log';
import {
  ProjectStore
} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {PartnerReportPageStore} from '@project/project-application/report/partner-report-page-store.service';
import {
  PartnerReportDetailPageStore
} from '@project/project-application/report/partner-report-detail-page/partner-report-detail-page-store.service';

@Injectable({providedIn: 'root'})
export class PartnerReportProcurementsPageStore {

  partnerId$: Observable<string | number | null>;
  procurements$: Observable<ProjectPartnerReportProcurementDTO[]>;

  private savedProcurements$ = new Subject<ProjectPartnerReportProcurementDTO[]>();

  constructor(private routingService: RoutingService,
              private partnerReportPageStore: PartnerReportPageStore,
              private projectPartnerReportService: ProjectPartnerReportService,
              private projectStore: ProjectStore,
              private projectPartnerProcurementService: ProjectPartnerReportProcurementService) {
    this.partnerId$ = this.partnerReportPageStore.partnerId$;
    this.procurements$ = this.getProcurements();
  }

  public getProcurements(): Observable<ProjectPartnerReportProcurementDTO[]> {
    const initialProcurements$ = combineLatest([
      this.partnerId$,
      this.routingService.routeParameterChanges(PartnerReportDetailPageStore.REPORT_DETAIL_PATH, 'reportId'),
    ]).pipe(
      switchMap(([partnerId, reportId]) => this.projectPartnerProcurementService.getProcurement(Number(partnerId), Number(reportId))),
      tap(data => Log.info('Fetched project procurements for report', this, data))
    );

    return merge(initialProcurements$, this.savedProcurements$);
  }

  public saveProcurements(procurements: UpdateProjectPartnerReportProcurementDTO[]): Observable<UpdateProjectPartnerReportProcurementDTO[]> {
    return combineLatest([
      this.partnerId$,
      this.routingService.routeParameterChanges(PartnerReportDetailPageStore.REPORT_DETAIL_PATH, 'reportId'),
    ]).pipe(
      switchMap(([partnerId, reportId]) =>
        this.projectPartnerProcurementService.updateProcurement(Number(partnerId), Number(reportId), procurements)),
      tap(saved => this.savedProcurements$.next(saved)),
      tap(data => Log.info('Updated procurements', this, data))
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
