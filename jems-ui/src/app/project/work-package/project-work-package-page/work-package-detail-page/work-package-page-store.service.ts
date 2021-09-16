import {Injectable} from '@angular/core';
import {
  InputWorkPackageUpdate,
  OutputIndicatorSummaryDTO,
  OutputWorkPackage,
  ProgrammeIndicatorService,
  ProjectDetailFormDTO,
  WorkPackageActivityDTO,
  WorkPackageActivityService,
  WorkPackageInvestmentService,
  WorkPackageOutputDTO,
  WorkPackageOutputService,
  WorkPackageService, WorkPackageInvestmentDTO,
} from '@cat/api';
import {combineLatest, merge, Observable, of, Subject} from 'rxjs';
import {catchError, filter, map, mergeMap, shareReplay, startWith, switchMap, tap} from 'rxjs/operators';
import {ProjectApplicationFormSidenavService} from '../../../project-application/containers/project-application-form-page/services/project-application-form-sidenav.service';
import {Log} from '@common/utils/log';
import {RoutingService} from '@common/services/routing.service';
import {ProjectStore} from '../../../project-application/containers/project-application-detail/services/project-store.service';
import {ProjectVersionStore} from '../../../common/services/project-version-store.service';
import {ProjectPaths} from '@project/common/project-util';

@Injectable()
export class WorkPackagePageStore {
  public static WORK_PACKAGE_DETAIL_PATH = '/applicationFormWorkPackage/';

  private workPackageId: number;
  private projectId: number;

  workPackage$: Observable<OutputWorkPackage>;
  isProjectEditable$: Observable<boolean>;
  projectForm$: Observable<ProjectDetailFormDTO>;
  activities$: Observable<WorkPackageActivityDTO[]>;
  outputs$: Observable<WorkPackageOutputDTO[]>;
  outputIndicators$: Observable<OutputIndicatorSummaryDTO[]>;
  investments$: Observable<WorkPackageInvestmentDTO[]>;

  investmentsChanged$ = new Subject<void>();

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
    this.projectForm$ = this.projectStore.projectForm$;
    this.activities$ = this.workPackageActivities();
    this.outputs$ = this.workPackageOutputs();
    this.outputIndicators$ = this.outputIndicators();
    this.investments$ = this.investments();
  }

  saveWorkPackage(workPackage: InputWorkPackageUpdate): Observable<OutputWorkPackage> {
    return this.workPackageService.updateWorkPackage(this.projectId, workPackage)
      .pipe(
        tap(saved => this.savedWorkPackage$.next(saved)),
        tap(saved => Log.info('Updated workPackage:', this, saved))
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
      this.routingService.routeParameterChanges(WorkPackagePageStore.WORK_PACKAGE_DETAIL_PATH, 'workPackageId'),
      this.projectStore.projectId$,
      this.projectVersionStore.currentRouteVersion$,
    ]).pipe(
      tap(([workPackageId, projectId]) => {
        this.workPackageId = Number(workPackageId);
        this.projectId = projectId;
      }),
      filter(([, projectId]) => !!projectId),
      switchMap(([workPackageId, projectId, version]) => workPackageId
        ? this.workPackageService.getWorkPackageById(projectId, Number(workPackageId), version)
          .pipe(
            catchError(() => {
              this.routingService.navigate([ProjectPaths.PROJECT_DETAIL_PATH, this.projectId]);
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
    return this.projectStore.projectForm$
      .pipe(
        map(projectForm => projectForm?.specificObjective?.programmeObjectivePolicy),
        switchMap(programmeObjectivePolicy => programmeObjectivePolicy ? this.programmeIndicatorService.getOutputIndicatorSummariesForSpecificObjective(programmeObjectivePolicy) : of([])),
        tap(outputs => Log.info('Fetched programme output indicators', outputs)),
      );
  }

  private investments(): Observable<WorkPackageInvestmentDTO[]> {
    return combineLatest([
        this.projectStore.projectId$,
        this.projectVersionStore.currentRouteVersion$,
        this.workPackage$,
        this.investmentsChanged$.pipe(startWith(null))
      ])
        .pipe(
          filter(([projectId, version, workPackage]) => !!workPackage.id && !!projectId),
          mergeMap(([projectId, version, workPackage]) =>
            this.workPackageInvestmentService.getWorkPackageInvestments(projectId, workPackage.id, version)),
          tap(investments => Log.info('Fetched the work package investments:', this, investments)),
        );
  }
}
