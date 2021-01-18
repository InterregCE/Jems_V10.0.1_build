import {Injectable} from '@angular/core';
import {
  IndicatorOutputDto,
  InputWorkPackageCreate,
  InputWorkPackageUpdate,
  OutputProject,
  OutputWorkPackage,
  ProgrammeIndicatorService,
  WorkPackageActivityDTO,
  WorkPackageActivityService,
  WorkPackageInvestmentService,
  InvestmentSummaryDTO,
  WorkPackageOutputDTO,
  WorkPackageOutputService,
  WorkPackageOutputUpdateDTO,
  WorkPackageService,
} from '@cat/api';
import {combineLatest, merge, Observable, ReplaySubject, Subject} from 'rxjs';
import {filter, map, shareReplay, startWith, switchMap, tap} from 'rxjs/operators';
import {Log} from '../../../common/utils/log';
import {ProjectApplicationFormSidenavService} from '../../project-application/containers/project-application-form-page/services/project-application-form-sidenav.service';
import {ProjectStore} from '../../project-application/containers/project-application-detail/services/project-store.service';
import {InvestmentSummary} from './workPackageInvestment';

@Injectable()
export class ProjectWorkPackagePageStore {

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
              private projectApplicationFormSidenavService: ProjectApplicationFormSidenavService) {
    this.isProjectEditable$ = this.projectStore.projectEditable$;
    this.project$ = this.projectStore.getProject();
    this.activities$ = this.workPackageActivities();
    this.outputs$ = this.workPackageOutputs();
    this.outputIndicators$ = this.outputIndicators();

    this.projectInvestmentSummaries$ = combineLatest([this.project$, this.investmentChangeEvent$.pipe(startWith(null))]).pipe(
      switchMap(([project]) => this.workPackageInvestmentService.getProjectInvestmentSummaries(project.id)),
      map((investmentSummeryDTOs: InvestmentSummaryDTO[]) => investmentSummeryDTOs.map(it => new InvestmentSummary(it.id, it.investmentNumber, it.workPackageId))),
      shareReplay(1)
    );
  }

  init(workPackageId: number | string | null, projectId: number): void {
    if (workPackageId && Number(workPackageId) === this.workPackageId) {
      return;
    }
    this.workPackageId = Number(workPackageId);
    this.projectId = projectId;
    if (!this.workPackageId || !this.projectId) {
      this.workPackage$.next({});
      return;
    }
    this.projectStore.init(this.projectId);
    this.workPackageService.getWorkPackageById(this.workPackageId)
      .pipe(
        tap(workPackage => Log.info('Fetched project work package:', this, workPackage)),
        tap(workPackage => this.workPackage$.next(workPackage)),
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
    return this.programmeIndicatorService.getAllIndicatorOutputDetail()
      .pipe(
        tap(results => Log.info('Fetched programme output indicators', results)),
      );
  }
}
