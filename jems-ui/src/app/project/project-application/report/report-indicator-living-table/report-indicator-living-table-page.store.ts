import {Injectable} from '@angular/core';
import {ProjectOverviewResultLivingTableApiService, ProjectReportResultIndicatorLivingTableDTO} from '@cat/api';
import {combineLatest, Observable} from 'rxjs';
import {filter, switchMap, tap} from 'rxjs/operators';
import {Log} from '@common/utils/log';
import {UntilDestroy} from '@ngneat/until-destroy';
import {
  ProjectStore
} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {RoutingService} from '@common/services/routing.service';

@UntilDestroy()
@Injectable({
  providedIn: 'root'
})
export class ReportIndicatorLivingTablePageStore {
  resultIndicatorOverview$: Observable<ProjectReportResultIndicatorLivingTableDTO[]>;

  constructor(private routingService: RoutingService,
              private projectReportOverviewService: ProjectOverviewResultLivingTableApiService,
              private projectStore: ProjectStore) {
    this.resultIndicatorOverview$ = this.resultIndicatorOverview();
  }

  public resultIndicatorOverview(): Observable<ProjectReportResultIndicatorLivingTableDTO[]> {
    return combineLatest([
      this.projectStore.projectId$,
    ]).pipe(
      filter(([projectId]) => !!projectId),
      switchMap(([projectId]) => this.projectReportOverviewService.getResultOverview(Number(projectId))),
      tap(data => Log.info('Loaded ResultIndicator Living table', this, data)),
    );
  }

}
