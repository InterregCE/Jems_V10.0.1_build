import {Injectable} from '@angular/core';
import {combineLatest, merge, Observable, Subject} from 'rxjs';
import {
  ProjectReportSpfContributionClaimDTO, ProjectReportSpfContributionClaimUpdateDTO,
  ProjectReportSPFContributionService,
} from '@cat/api';
import {switchMap, tap} from 'rxjs/operators';
import {
  ProjectStore
} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {
  ProjectReportDetailPageStore
} from '@project/project-application/report/project-report/project-report-detail-page/project-report-detail-page-store.service';
import {Log} from '@common/utils/log';

@Injectable({providedIn: 'root'})
export class ProjectReportSpfContributionsTabStore {

  savedSpfContributions$ = new Subject<ProjectReportSpfContributionClaimDTO[]>();
  spfContributions$: Observable<ProjectReportSpfContributionClaimDTO[]>;

  constructor(
    private projectStore: ProjectStore,
    private projectReportSPFContributionService: ProjectReportSPFContributionService,
    private projectReportDetailPageStore: ProjectReportDetailPageStore,
  ) {
    this.spfContributions$ = this.spfContributions();
  }

  private spfContributions(): Observable<ProjectReportSpfContributionClaimDTO[]> {
    const initialData$ = combineLatest([
      this.projectStore.projectId$,
      this.projectReportDetailPageStore.projectReportId$
    ])
    .pipe(
      switchMap(([projectId, reportId]) => this.projectReportSPFContributionService.getContributionClaims(projectId, reportId)),
    );
    return merge(initialData$, this.savedSpfContributions$);
  }

  updateSpfContributions(spfContributionsUpdate: ProjectReportSpfContributionClaimUpdateDTO[]) {
    return combineLatest([
      this.projectStore.projectId$,
      this.projectReportDetailPageStore.projectReportId$,
    ])
    .pipe(
      switchMap(([projectId, reportId]) =>
        this.projectReportSPFContributionService.updateContributionClaims(projectId, reportId, spfContributionsUpdate)),
      tap(saved => Log.info('Saved spf contributions', saved)),
      tap(data => this.savedSpfContributions$.next(data))
    );
  }
}
