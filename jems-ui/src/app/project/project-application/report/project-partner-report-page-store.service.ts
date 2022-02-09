import {Injectable} from '@angular/core';
import {
  PageProjectPartnerReportSummaryDTO,
  ProjectPartnerReportService, ProjectPartnerReportSummaryDTO
} from '@cat/api';
import {combineLatest, Observable} from 'rxjs';
import {map, switchMap, tap} from 'rxjs/operators';
import {RoutingService} from '@common/services/routing.service';
import {ProjectPartnerStore} from '@project/project-application/containers/project-application-form-page/services/project-partner-store.service';
import {Log} from '@common/utils/log';

@Injectable({
  providedIn: 'root'
})
export class ProjectPartnerReportPageStore {

  partnerReports$: Observable<ProjectPartnerReportSummaryDTO[]>;
  partnerReportSummary$: Observable<any>;

  constructor(private routingService: RoutingService,
              private partnerProjectStore: ProjectPartnerStore,
              private projectPartnerReportService: ProjectPartnerReportService) {
    this.partnerReports$ = this.partnerReports();
    this.partnerReportSummary$ = this.partnerReportSummary();
  }

  private partnerReports(): Observable<ProjectPartnerReportSummaryDTO[]> {
    return combineLatest([
      this.routingService.routeParameterChanges(ProjectPartnerStore.PARTNER_REPORT_DETAIL_PATH, 'partnerId'),
      this.partnerProjectStore.lastContractedVersionASObservable()
    ])
      .pipe(
        switchMap(([partnerId, lastContractedVersion]) =>
          // todo for Vlad MP2-2332
          this.projectPartnerReportService.getProjectPartnerReports(Number(partnerId), 0, 25, `number,desc`)),
        map((data: PageProjectPartnerReportSummaryDTO) => data.content),
        tap((data: ProjectPartnerReportSummaryDTO[]) => Log.info('Fetched partner reports for partner:', this, data))
      );
  }

  private partnerReportSummary(): Observable<any> {
    return combineLatest([
      this.routingService.routeParameterChanges(ProjectPartnerStore.PARTNER_REPORT_DETAIL_PATH, 'partnerId'),
      this.partnerProjectStore.partnerReportSummaries$
    ]).pipe(map(([partnerId, partnerSummaries]) => {
      return partnerSummaries.find(value => value.id === Number(partnerId));
    }));
  }
}
