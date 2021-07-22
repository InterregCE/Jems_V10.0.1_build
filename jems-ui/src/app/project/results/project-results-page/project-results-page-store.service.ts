import {Injectable} from '@angular/core';
import {
  ProjectPeriodDTO,
  ProgrammeIndicatorService,
  ProjectResultDTO,
  ProjectResultService, ResultIndicatorSummaryDTO
} from '@cat/api';
import {combineLatest, merge, Observable, of, Subject} from 'rxjs';
import {map, shareReplay, switchMap, tap} from 'rxjs/operators';
import {Log} from '@common/utils/log';
import {ProjectStore} from '../../project-application/containers/project-application-detail/services/project-store.service';
import {take} from 'rxjs/internal/operators';
import {ProjectVersionStore} from '@project/services/project-version-store.service';

@Injectable()
export class ProjectResultsPageStore {

  isProjectEditable$: Observable<boolean>;
  results$: Observable<ProjectResultDTO[]>;
  resultIndicators$: Observable<ResultIndicatorSummaryDTO[]>;
  periods$: Observable<ProjectPeriodDTO[]>;
  projectId$: Observable<number>;
  projectTitle$: Observable<string>;

  private savedResults$ = new Subject<ProjectResultDTO[]>();

  constructor(private projectResultService: ProjectResultService,
              private projectStore: ProjectStore,
              private programmeIndicatorService: ProgrammeIndicatorService,
              private projectVersionStore: ProjectVersionStore) {
    this.isProjectEditable$ = this.projectStore.projectEditable$;
    this.results$ = this.results();
    this.resultIndicators$ = this.resultIndicators();
    this.periods$ = this.periods();
    this.projectTitle$ = this.projectStore.projectTitle$;
    this.projectId$ = this.projectStore.projectId$;
  }

  saveResults(results: ProjectResultDTO[]): Observable<ProjectResultDTO[]> {
    return this.projectStore.projectId$
      .pipe(
        take(1),
        switchMap(projectId => this.projectResultService.updateProjectResults(projectId, results)),
        tap(saved => this.savedResults$.next(saved)),
        tap(saved => Log.info('Saved project results', saved)),
      );
  }

  private results(): Observable<ProjectResultDTO[]> {
    const initialResults$ = combineLatest([
      this.projectStore.projectId$,
      this.projectVersionStore.currentRouteVersion$
    ]).pipe(
      switchMap(([projectId, version]) => this.projectResultService.getProjectResults(projectId, version)),
      tap(results => Log.info('Fetched project results', results)),
    );

    return merge(this.savedResults$, initialResults$)
      .pipe(
        shareReplay(1)
      );
  }

  private resultIndicators(): Observable<ResultIndicatorSummaryDTO[]> {
    return this.projectStore.projectForm$
      .pipe(
        map(projectForm => projectForm?.specificObjective?.programmeObjectivePolicy),
        switchMap(programmeObjectivePolicy => programmeObjectivePolicy ? this.programmeIndicatorService.getResultIndicatorSummariesForSpecificObjective(programmeObjectivePolicy) : of([])),
        tap(results => Log.info('Fetched programme result indicators', results)),
      );
  }

  private periods(): Observable<ProjectPeriodDTO[]> {
    return this.projectStore.projectForm$
      .pipe(
        map(projectForm => projectForm.periods),
      );
  }
}
