import {Injectable} from '@angular/core';
import {
  InputWorkPackageCreate,
  InputWorkPackageUpdate,
  OutputProject,
  OutputWorkPackage,
  ProgrammeIndicatorService,
  WorkPackageActivityService,
  WorkPackageInvestmentDTO,
  WorkPackageInvestmentService,
  WorkPackageService,
} from '@cat/api';
import {combineLatest, merge, Observable, ReplaySubject, Subject} from 'rxjs';
import {shareReplay, startWith, switchMap, tap} from 'rxjs/operators';
import {Log} from '../../../common/utils/log';
import {ProjectApplicationFormSidenavService} from '../../project-application/containers/project-application-form-page/services/project-application-form-sidenav.service';
import {ProjectStore} from '../../project-application/containers/project-application-detail/services/project-store.service';
import { WorkPackageActivityDTO } from '@cat/api';

@Injectable()
export class ProjectWorkPackagePageStore {

  private workPackageId: number;
  private projectId: number;

  totalAmountChanged$ = new Subject<boolean>();
  workPackage$ = new ReplaySubject<OutputWorkPackage | any>(1);
  workPackageInvestment$ = new ReplaySubject<WorkPackageInvestmentDTO | any>(1);
  workPackageInvestmentIdsOfProject$: Observable<number[]>;
  isProjectEditable$: Observable<boolean>;
  project$: Observable<OutputProject>;
  activities$: Observable<WorkPackageActivityDTO[]>;

  private savedActivities$ = new Subject<WorkPackageActivityDTO[]>();

  constructor(private workPackageService: WorkPackageService,
              private projectStore: ProjectStore,
              private programmeIndicatorService: ProgrammeIndicatorService,
              private workPackageActivityService: WorkPackageActivityService,
              private workPackageInvestmentService: WorkPackageInvestmentService,
              private projectApplicationFormSidenavService: ProjectApplicationFormSidenavService) {
    this.isProjectEditable$ = this.projectStore.projectEditable$;
    this.project$ = this.projectStore.getProject();
    this.activities$ = this.workPackageActivities();

    this.workPackageInvestmentIdsOfProject$ = combineLatest([this.project$, this.workPackageInvestment$.pipe(startWith(null))]).pipe(
      switchMap(([project]) => this.workPackageInvestmentService.getWorkPackageInvestmentIdsOfProject(project.id)),
      shareReplay(1)
    );
  }

  init(workPackageId: number | string | null, projectId: number): void {
    if (workPackageId && workPackageId === this.workPackageId) {
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

  getWorkPackageInvestmentById(projectId: number, investmentId: number): Observable<WorkPackageInvestmentDTO> {
    return this.workPackageInvestmentService.getWorkPackageInvestment(investmentId)
      .pipe(
        tap(workPackageInvestment => Log.info('Fetched work package investment:', this, workPackageInvestment)),
        tap(workPackageInvestment => this.workPackageInvestment$.next(workPackageInvestment)),
      );
  }

  createWorkPackageInvestment(workPackageId: number, projectId: number, workPackageInvestment: WorkPackageInvestmentDTO): Observable<number> {
    return this.workPackageInvestmentService.addWorkPackageInvestment(workPackageId, workPackageInvestment)
      .pipe(
        tap(created => Log.info('Created work package investment:', this, created)),
        tap(created => this.workPackageInvestment$.next(created)),
        tap(() => this.projectApplicationFormSidenavService.refreshPackages(projectId)),
      );
  }

  updateWorkPackageInvestment(workPackageId: number, projectId: number, workPackageInvestment: WorkPackageInvestmentDTO): Observable<number> {
    return this.workPackageInvestmentService.updateWorkPackageInvestment(workPackageId, workPackageInvestment)
      .pipe(
        tap(updated => Log.info('Updated work package investment:', this, updated)),
        tap(updated => this.workPackageInvestment$.next(updated)),
        tap(() => this.projectApplicationFormSidenavService.refreshPackages(projectId)),
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
        switchMap(workPackage => this.workPackageActivityService.getActivities(workPackage.id)),
        tap(activities => Log.info('Fetched project activities', activities)),
      );

    return merge(this.savedActivities$, initialActivities$)
      .pipe(
        shareReplay(1)
      );
  }
}
