import {Injectable} from '@angular/core';
import {
  ProjectPartnerReportService,
  ProjectPartnerReportWorkPackageDTO,
  ProjectPartnerReportWorkPlanService,
  UpdateProjectPartnerReportWorkPackageDTO
} from '@cat/api';
import {combineLatest, Observable, of} from 'rxjs';
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
export class PartnerReportWorkPlanPageStore {

  partnerId$: Observable<string | number | null>;
  partnerWorkPackages$: Observable<ProjectPartnerReportWorkPackageDTO[]>;

  constructor(private routingService: RoutingService,
              private partnerReportPageStore: PartnerReportPageStore,
              private projectPartnerReportService: ProjectPartnerReportService,
              private projectStore: ProjectStore,
              private projectPartnerReportWorkPlanService: ProjectPartnerReportWorkPlanService) {
    this.partnerId$ = this.partnerReportPageStore.partnerId$;
    this.partnerWorkPackages$ = this.getWorkPackages();
  }

  public getWorkPackages(): Observable<ProjectPartnerReportWorkPackageDTO[]> {
    return combineLatest([
      this.partnerId$,
      this.routingService.routeParameterChanges(PartnerReportDetailPageStore.REPORT_DETAIL_PATH, 'reportId'),
    ]).pipe(
      switchMap(([partnerId, reportId]) => this.projectPartnerReportWorkPlanService.getWorkPlan(Number(partnerId), Number(reportId))),
      tap(data => Log.info('Fetched project work packages for report', this, data))
    );
  }

  public saveWorkPackages(workPackages: UpdateProjectPartnerReportWorkPackageDTO[]): Observable<UpdateProjectPartnerReportWorkPackageDTO[]> {
     return combineLatest([
      this.partnerId$,
      this.routingService.routeParameterChanges(PartnerReportDetailPageStore.REPORT_DETAIL_PATH, 'reportId'),
    ]).pipe(
      switchMap(([partnerId, reportId]) =>
        this.projectPartnerReportWorkPlanService.updateWorkPlan(Number(partnerId), Number(reportId), workPackages)),
      tap(data => Log.info('Updated work packages for report', this, data))
    );
  }
}
