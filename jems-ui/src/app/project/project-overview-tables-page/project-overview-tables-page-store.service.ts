import {Injectable} from '@angular/core';
import {ProjectStore} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {IndicatorOverviewLineDTO, ProjectCoFinancingOverviewDTO, ProjectResultService, ProjectService} from '@cat/api';
import {ProjectVersionStore} from '@project/common/services/project-version-store.service';
import {combineLatest, Observable} from 'rxjs';
import {map, switchMap, tap} from 'rxjs/operators';
import {Log} from '@common/utils/log';

@Injectable()
export class ProjectOverviewTablesPageStore {

  indicatorOverviewLines$: Observable<IndicatorOverviewLineDTO[]>;
  projectCoFinancingOverview$: Observable<ProjectCoFinancingOverviewDTO>;
  callMultipleFundsAllowed$: Observable<boolean>;

  constructor(private projectStore: ProjectStore,
              private projectVersionStore: ProjectVersionStore,
              private projectResultService: ProjectResultService,
              private projectService: ProjectService) {
    this.indicatorOverviewLines$ = this.indicatorOverviewLines();
    this.projectCoFinancingOverview$ = this.projectCoFinancingOverview();
    this.callMultipleFundsAllowed$ = this.callMultipleFundsAllowed();
  }

  private indicatorOverviewLines(): Observable<IndicatorOverviewLineDTO[]> {
    return combineLatest([
      this.projectStore.projectId$,
      this.projectVersionStore.currentRouteVersion$,
    ]).pipe(
      switchMap(([projectId, version]) =>
        this.projectResultService.getProjectResultIndicatorOverview(projectId, version)
      )
    );
  }

  private projectCoFinancingOverview(): Observable<ProjectCoFinancingOverviewDTO> {
    return combineLatest([
      this.projectStore.projectId$,
      this.projectVersionStore.currentRouteVersion$,
    ]).pipe(
      switchMap(([projectId, version]) =>
        this.projectService.getProjectCoFinancingOverview(projectId, version)
      ),
      tap(coFinancingOverview => Log.info('Fetched the project coFinancing overview', this, coFinancingOverview))
    );
  }

  private callMultipleFundsAllowed(): Observable<boolean> {
    return this.projectStore.projectCall$
      .pipe(
        map(call => call.multipleFundsAllowed)
      );
  }
}
