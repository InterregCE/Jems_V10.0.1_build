import {Injectable} from '@angular/core';
import {
  ProjectStore
} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {
  IndicatorOverviewLineDTO,
  ProjectCallSettingsDTO,
  ProjectCoFinancingOverviewDTO,
  ProjectResultService,
  ProjectService
} from '@cat/api';
import {ProjectVersionStore} from '@project/common/services/project-version-store.service';
import {combineLatest, Observable} from 'rxjs';
import {map, switchMap, tap} from 'rxjs/operators';
import {Log} from '@common/utils/log';

@Injectable()
export class ProjectOverviewTablesPageStore {

  indicatorOverviewLines$: Observable<IndicatorOverviewLineDTO[]>;
  projectCoFinancingOverview$: Observable<ProjectCoFinancingOverviewDTO>;
  callMultipleFundsAllowed$: Observable<boolean>;
  isCallSpf$: Observable<boolean>;

  constructor(private projectStore: ProjectStore,
              private projectVersionStore: ProjectVersionStore,
              private projectResultService: ProjectResultService,
              private projectService: ProjectService) {
    this.indicatorOverviewLines$ = this.indicatorOverviewLines();
    this.projectCoFinancingOverview$ = this.projectCoFinancingOverview();
    this.callMultipleFundsAllowed$ = this.callMultipleFundsAllowed();
    this.isCallSpf$ = this.isCallSpf();
  }

  private indicatorOverviewLines(): Observable<IndicatorOverviewLineDTO[]> {
    return combineLatest([
      this.projectStore.projectId$,
      this.projectVersionStore.selectedVersionParam$,
    ]).pipe(
      switchMap(([projectId, version]) =>
        this.projectResultService.getProjectResultIndicatorOverview(projectId, version)
      )
    );
  }

  private projectCoFinancingOverview(): Observable<ProjectCoFinancingOverviewDTO> {
    return combineLatest([
      this.projectStore.projectId$,
      this.projectVersionStore.selectedVersionParam$,
    ]).pipe(
      switchMap(([projectId, version]) =>
        this.projectService.getProjectCoFinancingOverview(projectId, version)
      ),
      tap(coFinancingOverview => Log.info('Fetched the project coFinancing overview', this, coFinancingOverview))
    );
  }

  private callMultipleFundsAllowed(): Observable<boolean> {
    return this.projectStore.projectCallSettings$
      .pipe(
        map(call => call.additionalFundAllowed)
      );
  }

  private isCallSpf(): Observable<boolean> {
    return this.projectStore.projectCallSettings$
      .pipe(
        map(call => call.callType === ProjectCallSettingsDTO.CallTypeEnum.SPF)
      );
  }
}
