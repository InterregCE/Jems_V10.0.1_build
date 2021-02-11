import {Injectable} from '@angular/core';
import {
  IndicatorOutputDto,
  InputWorkPackageCreate,
  InputWorkPackageUpdate,
  InvestmentSummaryDTO,
  OutputProject,
  OutputWorkPackage,
  ProgrammeIndicatorService,
  WorkPackageActivityDTO,
  WorkPackageActivityService,
  WorkPackageInvestmentService,
  WorkPackageOutputDTO,
  WorkPackageOutputService,
  WorkPackageOutputUpdateDTO,
  WorkPackageService,
} from '@cat/api';
import {combineLatest, merge, Observable, of, ReplaySubject, Subject} from 'rxjs';
import {filter, map, shareReplay, startWith, switchMap, tap} from 'rxjs/operators';
import {Log} from '../../../common/utils/log';
import {ProjectApplicationFormSidenavService} from '../../project-application/containers/project-application-form-page/services/project-application-form-sidenav.service';
import {ProjectStore} from '../../project-application/containers/project-application-detail/services/project-store.service';
import {InvestmentSummary} from './workPackageInvestment';
import {RoutingService} from '../../../common/services/routing.service';

@Injectable()
export class ProjectWorkPackagePageStore {
  public static WORK_PACKAGE_DETAIL_PATH = '/applicationFormWorkPackage/detail/';

  private workPackageId: number;
  private projectId: number;

  workPackage$ = new ReplaySubject<OutputWorkPackage | any>(1);
  projectInvestmentSummaries$: Observable<InvestmentSummary[]>;
  isProjectEditable$: Observable<boolean>;
  project$: Observable<OutputProject>;
  activities$: Observable<WorkPackageActivityDTO[]>;
  outputs$: Observable<WorkPackageOutputDTO[]>;
  outputIndicators$: Observable<IndicatorOutputDto[]>;

  investmentChangeEvent$ = new Subject<void>();

  private savedActivities$ = new Subject<WorkPackageActivityDTO[]>();
  private savedOutputs$ = new Subject<WorkPackageOutputDTO[]>();

  constructor(private workPackageService: WorkPackageService,
              private projectStore: ProjectStore,
              private programmeIndicatorService: ProgrammeIndicatorService,
              private workPackageActivityService: WorkPackageActivityService,
              private workPackageInvestmentService: WorkPackageInvestmentService,
              private workPackageOutputService: WorkPackageOutputService,
              private projectApplicationFormSidenavService: ProjectApplicationFormSidenavService,
              private routingService: RoutingService) {
    this.isProjectEditable$ = this.projectStore.projectEditable$;
    this.project$ = this.projectStore.getProject();
    this.activities$ = this.workPackageActivities();
    this.outputs$ = this.workPackageOutputs();
    this.outputIndicators$ = this.outputIndicators();

    this.projectInvestmentSummaries$ = combineLatest([this.project$, this.investmentChangeEvent$.pipe(startWith(null))]).pipe(
      switchMap(([project]) => this.workPackageInvestmentService.getProjectInvestmentSummaries(project.id)),
      map((investmentSummeryDTOs: InvestmentSummaryDTO[]) => investmentSummeryDTOs.map(it => new InvestmentSummary(it.id, it.investmentNumber, it.workPackageNumber))),
      shareReplay(1)
    );

    combineLatest([
      this.routingService.routeParameterChanges(ProjectWorkPackagePageStore.WORK_PACKAGE_DETAIL_PATH, 'workPackageId'),
      this.projectStore.projectId$
    ]).pipe(
      tap(([workPackageId, projectId]) => {
        this.workPackageId = Number(workPackageId);
        this.projectId = projectId;
      }),
      switchMap(([workPackageId, projectId]) =>
        workPackageId && projectId ? this.workPackageService.getWorkPackageById(Number(workPackageId)) : of({})
      ),
      tap(workPackage => this.workPackage$.next(workPackage)),
      tap(workPackage => Log.info('Fetched the programme work package:', this, workPackage)),
    ).subscribe();
  }

  saveWorkPackage(workPackage: InputWorkPackageUpdate): Observable<OutputWorkPackage> {
    return this.workPackageService.updateWorkPackage(workPackage)
      .pipe(
        tap(saved => this.workPackage$.next(saved)),
        tap(saved => Log.info('Updated workPackage:', this, saved))
      );
  }

  createWorkPackage(workPackage: InputWorkPackageCreate): Observable<OutputWorkPackage> {
    return this.workPackageService.createWorkPackage(this.projectId, workPackage)
      .pipe(
        tap(created => this.workPackage$.next(created)),
        tap(created => Log.info('Created workPackage:', this, created)),
        tap(() => this.projectApplicationFormSidenavService.refreshPackages(this.projectId)),
      );
  }

  deleteWorkPackageInvestment(investmentId: number): Observable<void> {
    return this.workPackageInvestmentService.deleteWorkPackageInvestment(investmentId, this.workPackageId)
      .pipe(
        tap(deleted => Log.info('Deleted work package investment:', this, deleted)),
        tap(() => this.investmentChangeEvent$.next())
      );
  }

  saveWorkPackageActivities(activities: WorkPackageActivityDTO[]): Observable<WorkPackageActivityDTO[]> {
    return this.workPackageActivityService.updateActivities(this.workPackageId, activities)
      .pipe(
        tap(saved => this.savedActivities$.next(saved)),
        tap(saved => Log.info('Saved project activities', saved)),
      );
  }

  private workPackageActivities(): Observable<WorkPackageActivityDTO[]> {
    const initialActivities$ = this.workPackage$
      .pipe(
        filter(workPackage => workPackage && workPackage.id),
        switchMap(workPackage => this.workPackageActivityService.getActivities(workPackage.id)),
        tap(activities => Log.info('Fetched project activities', activities)),
      );

    return merge(this.savedActivities$, initialActivities$)
      .pipe(
        shareReplay(1)
      );
  }

  saveWorkPackageOutputs(outputs: WorkPackageOutputUpdateDTO[]): Observable<WorkPackageOutputDTO[]> {
    return this.workPackageOutputService.updateWorkPackageOutputs(this.workPackageId, outputs)
      .pipe(
        tap(saved => this.savedOutputs$.next(saved)),
        tap(saved => Log.info('Saved project outputs', saved)),
      );
  }

  private workPackageOutputs(): Observable<WorkPackageOutputDTO[]> {
    const initialOutputs$ = this.workPackage$
      .pipe(
        filter(workPackage => workPackage && workPackage.id),
        switchMap(workPackage => this.workPackageOutputService.getWorkPackageOutputs(workPackage.id)),
        tap(outputs => Log.info('Fetched project outputs', outputs)),
      );

    return merge(this.savedOutputs$, initialOutputs$)
      .pipe(
        shareReplay(1)
      );
  }

  private outputIndicators(): Observable<IndicatorOutputDto[]> {
    return this.projectStore.getProject()
      .pipe(
        map(project => project?.projectData.specificObjective.code),
        switchMap(code => this.programmeIndicatorService.getOutputIndicatorsForSpecificObjective(code)),
        tap(outputs => Log.info('Fetched programme output indicators', outputs)),
      );
  }
}
