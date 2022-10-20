import {Injectable} from '@angular/core';
import {ProjectStore} from '../../project-application/containers/project-application-detail/services/project-store.service';
import {combineLatest, Observable} from 'rxjs';
import {ProjectPeriodDTO, ProjectResultDTO, ProjectResultService, WorkPackageService} from '@cat/api';
import {filter, map, switchMap, tap} from 'rxjs/operators';
import {Log} from '@common/utils/log';
import {ProjectVersionStore} from '@project/common/services/project-version-store.service';
import {RoutingService} from '@common/services/routing.service';

@Injectable()
export class ProjectTimeplanPageStore {

  projectId$: Observable<number>;
  projectTitle$: Observable<string>;
  workPackages$: Observable<any>;
  periods$: Observable<ProjectPeriodDTO[]>;
  projectResults$: Observable<ProjectResultDTO[]>;

  constructor(private projectStore: ProjectStore,
              private workPackageService: WorkPackageService,
              private projectResultService: ProjectResultService,
              private projectVersionStore: ProjectVersionStore,
              private router: RoutingService) {
    this.projectId$ = this.projectStore.projectId$;
    this.projectTitle$ = this.projectStore.projectTitle$;
    this.workPackages$ = this.workPackages();
    this.periods$ = this.periods();
    this.projectResults$ = this.projectResults();
  }

  private workPackages(): Observable<any> {
    return combineLatest([this.projectId$, this.getVersion()])
      .pipe(
        filter(([id]) => !!id),
        switchMap(([id, version]) => this.workPackageService.getWorkPackagesForTimePlanByProjectId(id, version)),
        tap(workPackages => Log.info('Fetching work packages for timeplan', this, workPackages))
      );
  }

  private periods(): Observable<ProjectPeriodDTO[]> {
    return this.projectStore.projectForm$
      .pipe(
        map(projectForm => projectForm?.periods)
      );
  }

  private projectResults(): Observable<any> {
    return combineLatest([this.projectId$, this.getVersion()])
      .pipe(
        filter(([id]) => !!id),
        switchMap(([id, version]) => this.projectResultService.getProjectResults(id, version)),
        tap(projectResults => Log.info('Fetching project results for timeplan', this, projectResults))
      );
  }

  private getVersion(): Observable<string | undefined> {
    const isFromReportingPage = this.router.url.includes('contractReporting');
    if (isFromReportingPage) {
      return this.projectVersionStore.lastApprovedOrContractedVersion$
        .pipe(
          map(lastApprovedVersion => lastApprovedVersion?.version)
        );
    } else {
      return this.projectVersionStore.selectedVersionParam$;
    }
  }
}
