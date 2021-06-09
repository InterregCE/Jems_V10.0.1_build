import {Injectable} from '@angular/core';
import {
  InputWorkPackageCreate,
  InputWorkPackageUpdate,
  OutputIndicatorSummaryDTO,
  ProjectDetailDTO,
  OutputWorkPackage,
  ProgrammeIndicatorService,
  WorkPackageActivityDTO,
  WorkPackageActivityService,
  WorkPackageInvestmentService,
  WorkPackageOutputDTO,
  WorkPackageOutputService,
  WorkPackageService,
} from '@cat/api';
import {combineLatest, merge, Observable, of, Subject} from 'rxjs';
import {catchError, filter, map, shareReplay, switchMap, tap} from 'rxjs/operators';
import {ProjectApplicationFormSidenavService} from '../../../project-application/containers/project-application-form-page/services/project-application-form-sidenav.service';
import {Log} from '../../../../common/utils/log';
import {RoutingService} from '../../../../common/services/routing.service';
import {ProjectStore} from '../../../project-application/containers/project-application-detail/services/project-store.service';
import {ProjectVersionStore} from '../../../services/project-version-store.service';

@Injectable()
export class ProjectWorkPackagePageStore {
  public static WORK_PACKAGE_DETAIL_PATH = '/applicationFormWorkPackage/detail/';

  private workPackageId: number;
  private projectId: number;

  workPackage$: Observable<OutputWorkPackage>;
  isProjectEditable$: Observable<boolean>;
  project$: Observable<ProjectDetailDTO>;
  activities$: Observable<WorkPackageActivityDTO[]>;
  outputs$: Observable<WorkPackageOutputDTO[]>;
  outputIndicators$: Observable<OutputIndicatorSummaryDTO[]>;



  private savedActivities$ = new Subject<WorkPackageActivityDTO[]>();
  private savedOutputs$ = new Subject<WorkPackageOutputDTO[]>();
  private savedWorkPackage$ = new Subject<OutputWorkPackage>();

  constructor(private workPackageService: WorkPackageService,
              private projectStore: ProjectStore,
              private programmeIndicatorService: ProgrammeIndicatorService,
              private workPackageActivityService: WorkPackageActivityService,
              private workPackageInvestmentService: WorkPackageInvestmentService,
              private workPackageOutputService: WorkPackageOutputService,
              private projectApplicationFormSidenavService: ProjectApplicationFormSidenavService,
              private projectVersionStore: ProjectVersionStore,
              private routingService: RoutingService) {
    this.workPackage$ = this.workPackage();
    this.isProjectEditable$ = this.projectStore.projectEditable$;
    this.project$ = this.projectStore.getProject();
    this.activities$ = this.workPackageActivities();
    this.outputs$ = this.workPackageOutputs();
    this.outputIndicators$ = this.outputIndicators();
  }

  saveWorkPackage(workPackage: InputWorkPackageUpdate): Observable<OutputWorkPackage> {
    return this.workPackageService.updateWorkPackage(this.projectId, workPackage)
      .pipe(
        tap(saved => this.savedWorkPackage$.next(saved)),
        tap(saved => Log.info('Updated workPackage:', this, saved))
      );
  }

  createWorkPackage(workPackage: InputWorkPackageCreate): Observable<OutputWorkPackage> {
    return this.workPackageService.createWorkPackage(this.projectId, workPackage)
      .pipe(
        tap(created => this.savedWorkPackage$.next(created)),
        tap(created => Log.info('Created workPackage:', this, created)),
        tap(() => this.projectApplicationFormSidenavService.refreshPackages(this.projectId)),
      );
  }

  deleteWorkPackageInvestment(investmentId: number): Observable<void> {
    return this.workPackageInvestmentService.deleteWorkPackageInvestment(investmentId, this.projectId, this.workPackageId)
      .pipe(
        tap(deleted => Log.info('Deleted work package investment:', this, deleted)),
        tap(() => this.projectStore.investmentChangeEvent$.next())
      );
  }

  saveWorkPackageActivities(activities: WorkPackageActivityDTO[]): Observable<WorkPackageActivityDTO[]> {
    return this.workPackageActivityService.updateActivities(this.projectId, this.workPackageId, activities)
      .pipe(
        tap(saved => this.savedActivities$.next(saved)),
        tap(saved => Log.info('Saved project activities', saved)),
      );
  }

  saveWorkPackageOutputs(outputs: WorkPackageOutputDTO[]): Observable<WorkPackageOutputDTO[]> {
    return this.workPackageOutputService.updateOutputs(this.projectId, this.workPackageId, outputs)
      .pipe(
        tap(saved => this.savedOutputs$.next(saved)),
        tap(saved => Log.info('Saved project outputs', saved)),
      );
  }

  private workPackage(): Observable<OutputWorkPackage> {
    const initialPackage$ = combineLatest([
      this.routingService.routeParameterChanges(ProjectWorkPackagePageStore.WORK_PACKAGE_DETAIL_PATH, 'workPackageId'),
      this.projectStore.projectId$,
      this.projectVersionStore.currentRouteVersion$,
    ]).pipe(
      tap(([workPackageId, projectId]) => {
        this.workPackageId = Number(workPackageId);
        this.projectId = projectId;
      }),
      filter(([workPackageId, projectId]) => !!projectId),
      switchMap(([workPackageId, projectId, version]) => workPackageId
        ? this.workPackageService.getWorkPackageById(projectId, Number(workPackageId), version)
          .pipe(
            catchError(err => {
              this.routingService.navigate([ProjectStore.PROJECT_DETAIL_PATH, this.projectId]);
              return of({} as OutputWorkPackage);
            })
          )
        : of({} as OutputWorkPackage)
      ),
      tap(workPackage => Log.info('Fetched the programme work package:', this, workPackage))
    );

    return merge(initialPackage$, this.savedWorkPackage$)
      .pipe(
        shareReplay(1)
      );
  }

  private workPackageActivities(): Observable<WorkPackageActivityDTO[]> {
    const initialActivities$ = combineLatest([
      this.workPackage$,
      this.projectStore.projectId$,
      this.projectVersionStore.currentRouteVersion$
    ])
      .pipe(
        filter(([workPackage]) => !!workPackage?.id),
        switchMap(([workPackage, projectId, version]) =>
          this.workPackageActivityService.getActivities(projectId, workPackage.id, version)
        ),
        tap(activities => Log.info('Fetched project activities', activities)),
      );

    return merge(this.savedActivities$, initialActivities$)
      .pipe(
        shareReplay(1)
      );
  }

  private workPackageOutputs(): Observable<WorkPackageOutputDTO[]> {
    const initialOutputs$ = combineLatest([
      this.workPackage$,
      this.projectStore.projectId$,
      this.projectVersionStore.currentRouteVersion$
    ])
      .pipe(
        filter(([workPackage]) => !!workPackage?.id),
        switchMap(([workPackage, projectId, version]) =>
          this.workPackageOutputService.getOutputs(projectId, workPackage.id, version)
        ),
        tap(outputs => Log.info('Fetched project outputs', outputs)),
      );

    return merge(this.savedOutputs$, initialOutputs$)
      .pipe(
        shareReplay(1)
      );
  }

  private outputIndicators(): Observable<OutputIndicatorSummaryDTO[]> {
    return this.projectStore.getProject()
      .pipe(
        map(project => project?.projectData?.specificObjective?.programmeObjectivePolicy),
        switchMap(programmeObjectivePolicy => programmeObjectivePolicy ? this.programmeIndicatorService.getOutputIndicatorSummariesForSpecificObjective(programmeObjectivePolicy) : of([])),
        tap(outputs => Log.info('Fetched programme output indicators', outputs)),
      );
  }
}
