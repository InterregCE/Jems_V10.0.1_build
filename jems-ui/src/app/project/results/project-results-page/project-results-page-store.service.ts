import {Injectable} from '@angular/core';
import {IndicatorResultDto, OutputProjectPeriod, ProgrammeIndicatorService, ProjectResultDTO, ProjectResultService} from '@cat/api';
import {merge, Observable, Subject} from 'rxjs';
import {map, shareReplay, switchMap, tap} from 'rxjs/operators';
import {Log} from '../../../common/utils/log';
import {ProjectStore} from '../../project-application/containers/project-application-detail/services/project-store.service';

@Injectable()
export class ProjectResultsPageStore {
  private projectId: number;

  isProjectEditable$: Observable<boolean>;
  results$: Observable<ProjectResultDTO[]>;
  resultIndicators$: Observable<IndicatorResultDto[]>;
  periods$: Observable<OutputProjectPeriod[]>;
  projectAcronym$: Observable<string>;

  private savedResults$ = new Subject<ProjectResultDTO[]>();

  constructor(private projectResultService: ProjectResultService,
              private projectStore: ProjectStore,
              private programmeIndicatorService: ProgrammeIndicatorService) {
    this.isProjectEditable$ = this.projectStore.projectEditable$;
    this.results$ = this.results();
    this.resultIndicators$ = this.resultIndicators();
    this.periods$ = this.periods();
    this.projectAcronym$ = this.projectStore.getAcronym();
  }

  init(projectId: number): void {
    this.projectStore.init(projectId);
  }

  saveResults(results: ProjectResultDTO[]): Observable<ProjectResultDTO[]> {
    return this.projectResultService.updateProjectResults(this.projectId, results)
      .pipe(
        tap(saved => this.savedResults$.next(saved)),
        tap(saved => Log.info('Saved project results', saved)),
      );
  }

  private results(): Observable<ProjectResultDTO[]> {
    const initialResults$ = this.projectStore.getProject()
      .pipe(
        tap(project => this.projectId = project.id),
        switchMap(project => this.projectResultService.getProjectResults(this.projectId)),
        tap(results => Log.info('Fetched project results', results)),
      );

    return merge(this.savedResults$, initialResults$)
      .pipe(
        shareReplay(1)
      );
  }

  private resultIndicators(): Observable<IndicatorResultDto[]> {
    return this.projectStore.getProject()
      .pipe(
        map(project => project?.projectData.specificObjective.code),
        switchMap(code => this.programmeIndicatorService.getAllIndicatorResultDetailForSpecificObjective(code)),
        tap(results => Log.info('Fetched programme result indicators', results)),
      );
  }

  private periods(): Observable<OutputProjectPeriod[]> {
    return this.projectStore.getProject()
      .pipe(
        map(project => project.periods),
      );
  }
}
