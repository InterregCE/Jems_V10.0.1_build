import {Injectable} from '@angular/core';
import {combineLatest, Observable} from 'rxjs';
import {
  ProjectReportVerificationClarificationDTO,
  ProjectReportVerificationConclusionDTO,
  ProjectReportVerificationService
} from '@cat/api';
import {RoutingService} from '@common/services/routing.service';
import {switchMap} from 'rxjs/operators';
import {
  ProjectReportDetailPageStore
} from '@project/project-application/report/project-report/project-report-detail-page/project-report-detail-page-store.service';

@Injectable({providedIn: 'root'})
export class ProjectVerificationReportFinalizeStore {

  conclusions$: Observable<ProjectReportVerificationConclusionDTO>;
  clarifications$: Observable<ProjectReportVerificationClarificationDTO[]>;

  constructor(
    public routingService: RoutingService,
    public projectReportDetailPageStore: ProjectReportDetailPageStore,
    private service: ProjectReportVerificationService
  ) {
    this.conclusions$ = this.getConclusions();
    this.clarifications$ = this.getClarifications();
  }

  private getConclusions(): Observable<ProjectReportVerificationConclusionDTO> {
    return combineLatest([
      this.projectReportDetailPageStore.projectStore.projectId$,
      this.projectReportDetailPageStore.projectReportId$,
    ]).pipe(
      switchMap(([projectId, reportId]) =>
        this.service.getReportVerificationConclusion(projectId, reportId)
      )
    );
  }

  private getClarifications(): Observable<ProjectReportVerificationClarificationDTO[]> {
    return combineLatest([
      this.projectReportDetailPageStore.projectStore.projectId$,
      this.projectReportDetailPageStore.projectReportId$,
    ]).pipe(
      switchMap(([partnerId, reportId]) => {
          return this.service.getReportVerificationClarificationRequests(partnerId, reportId);
        }
      )
    );
  }
}
