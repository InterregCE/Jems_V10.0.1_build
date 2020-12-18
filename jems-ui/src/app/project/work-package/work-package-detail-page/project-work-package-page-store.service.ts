import {Injectable} from '@angular/core';
import {
  InputWorkPackageCreate,
  InputWorkPackageUpdate,
  OutputProject,
  OutputWorkPackage,
  ProgrammeIndicatorService,
  WorkPackageActivityService,
  WorkPackageService
} from '@cat/api';
import {merge, Observable, ReplaySubject, Subject} from 'rxjs';
import {shareReplay, switchMap, tap} from 'rxjs/operators';
import {Log} from '../../../common/utils/log';
import {ProjectApplicationFormSidenavService} from '../../project-application/containers/project-application-form-page/services/project-application-form-sidenav.service';
import {ProjectStore} from '../../project-application/containers/project-application-detail/services/project-store.service';
import {WorkPackageActivityDTO} from 'build/generated-sources/openapi/model/workPackageActivityDTO';

@Injectable()
export class ProjectWorkPackagePageStore {

  private workPackageId: number;
  private projectId: number;

  totalAmountChanged$ = new Subject<boolean>();
  workPackage$ = new ReplaySubject<OutputWorkPackage | any>(1);
  isProjectEditable$: Observable<boolean>;
  project$: Observable<OutputProject>;
  activities$: Observable<WorkPackageActivityDTO[]>;

  private savedActivities$ = new Subject<WorkPackageActivityDTO[]>();

  constructor(private workPackageService: WorkPackageService,
              private projectStore: ProjectStore,
              private programmeIndicatorService: ProgrammeIndicatorService,
              private workPackageActivityService: WorkPackageActivityService,
              private projectApplicationFormSidenavService: ProjectApplicationFormSidenavService) {
    this.isProjectEditable$ = this.projectStore.projectEditable$;
    this.project$ = this.projectStore.getProject();
    this.activities$ = this.workPackageActivities();
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
