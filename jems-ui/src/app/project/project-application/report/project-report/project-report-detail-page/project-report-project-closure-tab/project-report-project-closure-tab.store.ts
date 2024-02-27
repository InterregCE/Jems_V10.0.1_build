import {Injectable} from '@angular/core';
import {combineLatest, merge, Observable, Subject} from 'rxjs';
import {
  ProjectReportProjectClosureDTO,
  ProjectReportProjectClosureService,
  ProjectReportService
} from '@cat/api';
import {RoutingService} from '@common/services/routing.service';
import {
  ProjectStore
} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {
  ProjectReportDetailPageStore
} from '@project/project-application/report/project-report/project-report-detail-page/project-report-detail-page-store.service';
import {switchMap, tap} from 'rxjs/operators';
import {Log} from '@common/utils/log';

@Injectable({providedIn: 'root'})
export class ProjectReportProjectClosureTabStore {
  savedProjectClosure$ = new Subject<ProjectReportProjectClosureDTO>();
  projectClosure$: Observable<ProjectReportProjectClosureDTO>;

  constructor(private routingService: RoutingService,
              private projectStore: ProjectStore,
              private projectReportDetailPageStore: ProjectReportDetailPageStore,
              private projectReportService: ProjectReportService,
              private projectReportProjectClosureService: ProjectReportProjectClosureService,
  ) {
    this.projectClosure$ = this.projectClosure();
  }

  private projectClosure(): Observable<ProjectReportProjectClosureDTO> {
    const initialData$ = combineLatest([
      this.projectStore.projectId$,
      this.projectReportDetailPageStore.projectReportId$
    ])
      .pipe(
        switchMap(([projectId, reportId]) => this.projectReportProjectClosureService.getProjectClosure(projectId, reportId)),
      );

    return merge(initialData$, this.savedProjectClosure$);
  }

  updateProjectClosure(projectClosure: ProjectReportProjectClosureDTO) {
    return combineLatest([
      this.projectStore.projectId$,
      this.projectReportDetailPageStore.projectReportId$,
    ])
      .pipe(
        switchMap(([projectId, reportId]) =>
          this.projectReportProjectClosureService.updateProjectClosure(projectId, reportId, projectClosure)),
        tap(saved => Log.info('Saved project closure', saved)),
        tap(data => this.savedProjectClosure$.next(data))
      );
  }
}
