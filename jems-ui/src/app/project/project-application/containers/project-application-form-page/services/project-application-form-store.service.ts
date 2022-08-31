import {Injectable} from '@angular/core';
import {combineLatest, merge, Observable, of, ReplaySubject, Subject} from 'rxjs';
import {
  InputProjectOverallObjective,
  InputProjectPartnership,
  InputProjectRelevance,
  OutputProjectDescription, OutputProjectLongTermPlans, OutputProjectManagement,
  ProjectDescriptionService
} from '@cat/api';
import {map, mergeMap, shareReplay, tap} from 'rxjs/operators';
import {Log} from '@common/utils/log';
import {ProjectVersionStore} from '@project/common/services/project-version-store.service';

@Injectable()
export class ProjectApplicationFormStore {
  private projectId$ = new ReplaySubject<number>(1);

  projectRelevance$: Observable<InputProjectRelevance>;
  projectPartnership$: Observable<InputProjectPartnership>;
  projectManagement$: Observable<OutputProjectManagement>;
  projectLongTermPlans$: Observable<OutputProjectLongTermPlans>;
  projectOverallObjective$: Observable<InputProjectOverallObjective>;

  savedProjectRelevance$ = new Subject<InputProjectRelevance>();
  savedProjectPartnership$ = new Subject<InputProjectPartnership>();
  savedProjectManagement$ = new Subject<OutputProjectManagement>();
  savedProjectLongTermPlans$ = new Subject<OutputProjectLongTermPlans>();
  savedProjectOverallObjective$ = new Subject<InputProjectOverallObjective>();

  projectDescription$ = combineLatest([this.projectId$, this.projectVersionStore.selectedVersionParam$])
    .pipe(
      mergeMap(([id, version]) => this.projectDescriptionService.getProjectDescription(id, version)),
      tap(desc => Log.info('Fetched project description', this, desc)),
      shareReplay(1)
    );

  constructor(private projectDescriptionService: ProjectDescriptionService,
              private projectVersionStore: ProjectVersionStore) {
    this.projectRelevance$ = this.projectRelevance();
    this.projectPartnership$ = this.projectPartnership();
    this.projectManagement$ = this.projectManagement();
    this.projectLongTermPlans$ = this.projectLongTermPlans();
    this.projectOverallObjective$ = this.projectOverallObjective();
  }

  init(projectId: number): void {
    this.projectId$.next(projectId);
  }

  getProjectDescription(): Observable<OutputProjectDescription> {
    return this.projectDescription$;
  }

  private projectRelevance(): Observable<InputProjectRelevance> {
    const initialProjectRelevance$ = this.projectDescription$
      .pipe(
        map(project => project.projectRelevance)
      );
    return merge(initialProjectRelevance$, this.savedProjectRelevance$)
      .pipe(
        shareReplay(1)
      );
  }

  private projectPartnership(): Observable<InputProjectPartnership> {
    const initialProjectPartnership$ = this.projectDescription$
      .pipe(
        map(project => project.projectPartnership)
      );
    return merge(initialProjectPartnership$, this.savedProjectPartnership$)
      .pipe(
        shareReplay(1)
      );
  }

  private projectManagement(): Observable<OutputProjectManagement> {
    const initialProjectManagement$ = this.projectDescription$
      .pipe(
        map(project => project.projectManagement)
      );
    return merge(initialProjectManagement$, this.savedProjectManagement$)
      .pipe(
        shareReplay(1)
      );
  }

  private projectLongTermPlans(): Observable<OutputProjectLongTermPlans> {
    const initialProjectLongTermPlans$ = this.projectDescription$
      .pipe(
        map(project => project.projectLongTermPlans)
      );
    return merge(initialProjectLongTermPlans$, this.savedProjectLongTermPlans$)
      .pipe(
        shareReplay(1)
      );
  }

  private projectOverallObjective(): Observable<InputProjectOverallObjective> {
    const initialProjectOverallObjective$ = this.projectDescription$
      .pipe(
        map(project => project.projectOverallObjective)
      );
    return merge(initialProjectOverallObjective$, this.savedProjectOverallObjective$)
      .pipe(
        shareReplay(1)
      );
  }
}
