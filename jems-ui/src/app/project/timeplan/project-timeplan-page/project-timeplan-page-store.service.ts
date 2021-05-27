import {Injectable} from '@angular/core';
import {ProjectStore} from '../../project-application/containers/project-application-detail/services/project-store.service';
import {Observable} from 'rxjs';
import {ProjectPeriodDTO, ProjectResultDTO, ProjectResultService, WorkPackageService} from '@cat/api';
import {map, switchMap, tap} from 'rxjs/operators';
import {filter} from 'rxjs/internal/operators';
import {Log} from '../../../common/utils/log';

@Injectable()
export class ProjectTimeplanPageStore {

  projectId$: Observable<number>;
  projectTitle$: Observable<string>;
  workPackages$: Observable<any>;
  periods$: Observable<ProjectPeriodDTO[]>;
  projectResults$: Observable<ProjectResultDTO[]>;

  constructor(private projectStore: ProjectStore,
              private workPackageService: WorkPackageService,
              private projectResultService: ProjectResultService) {
    this.projectId$ = this.projectStore.projectId$;
    this.projectTitle$ = this.projectStore.projectTitle$;
    this.workPackages$ = this.workPackages();
    this.periods$ = this.periods();
    this.projectResults$ = this.projectResults();
  }

  private workPackages(): Observable<any> {
    return this.projectId$
      .pipe(
        filter(id => !!id),
        switchMap(id => this.workPackageService.getWorkPackagesForTimePlanByProjectId(id)),
        map(page => page.content),
        tap(workPackages => Log.info('Fetching work packages for timeplan', this, workPackages))
      );
  }

  private periods(): Observable<ProjectPeriodDTO[]> {
    return this.projectStore.getProject()
      .pipe(
        map(project => project?.periods)
      );
  }

  private projectResults(): Observable<any> {
    return this.projectId$
      .pipe(
        filter(id => !!id),
        switchMap(id => this.projectResultService.getProjectResults(id)),
        tap(projectResults => Log.info('Fetching project results for timeplan', this, projectResults))
      );
  }
}
