import {Injectable} from '@angular/core';
import {
  ProjectPartnerReportContributionService,
  ProjectPartnerReportContributionWrapperDTO,
  ProjectPartnerReportService, ProjectReportFileMetadataDTO,
  UpdateProjectPartnerReportContributionDataDTO,
} from '@cat/api';
import {BehaviorSubject, combineLatest, merge, Observable, Subject} from 'rxjs';
import {map, switchMap, take, tap} from 'rxjs/operators';
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
export class PartnerReportContributionStore {

  partnerId$: Observable<number>;
  partnerContribution$: Observable<ProjectPartnerReportContributionWrapperDTO>;

  refreshContributions$ = new BehaviorSubject<void>(undefined);
  private savedContribution$ = new Subject<ProjectPartnerReportContributionWrapperDTO>();

  constructor(
    private routingService: RoutingService,
    private partnerReportPageStore: PartnerReportPageStore,
    private projectPartnerReportService: ProjectPartnerReportService,
    private projectStore: ProjectStore,
    private projectPartnerReportContributionService: ProjectPartnerReportContributionService,
  ) {
    this.partnerId$ = this.partnerReportPageStore.partnerId$.pipe(map(partnerId => Number(partnerId)));
    this.partnerContribution$ = this.getContribution();
  }

  public getContribution(): Observable<ProjectPartnerReportContributionWrapperDTO> {
    const initialContribution$ = combineLatest([
      this.partnerId$,
      this.routingService
        .routeParameterChanges(PartnerReportDetailPageStore.REPORT_DETAIL_PATH, 'reportId')
        .pipe(map(reportId => Number(reportId))),
      this.refreshContributions$
    ]).pipe(
      switchMap(([partnerId, reportId, _ ]) =>
        this.projectPartnerReportContributionService.getContribution(partnerId, reportId)
      ),
      tap(data => Log.info('Fetched contribution for partner report', this, data)),
    );

    return merge(initialContribution$, this.savedContribution$);
  }

  public saveContribution(contribution: UpdateProjectPartnerReportContributionDataDTO): Observable<ProjectPartnerReportContributionWrapperDTO> {
    return combineLatest([
      this.partnerId$,
      this.routingService
        .routeParameterChanges(PartnerReportDetailPageStore.REPORT_DETAIL_PATH, 'reportId')
        .pipe(map(reportId => Number(reportId))),
    ]).pipe(
      switchMap(([partnerId, reportId]) =>
        this.projectPartnerReportContributionService.updateContribution(partnerId, reportId, contribution)),
      tap(saved => this.savedContribution$.next(saved)),
      tap(data => Log.info('Updated contribution for partner report', this, data))
    );
  }

  uploadFile(file: File, contributionId: number): Observable<ProjectReportFileMetadataDTO> {
    return combineLatest([
      this.partnerId$.pipe(map(id => Number(id))),
      this.routingService.routeParameterChanges(PartnerReportDetailPageStore.REPORT_DETAIL_PATH, 'reportId')
        .pipe(map(id => Number(id))),
    ]).pipe(
      take(1),
      switchMap(([partnerId, reportId]) => this.projectPartnerReportContributionService
        .uploadFileToContributionForm(file, contributionId, partnerId, reportId)
      ),
    );
  }

}
