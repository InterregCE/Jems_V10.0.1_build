import {InputWorkPackageCreate, OutputWorkPackage, OutputWorkPackageSimple, WorkPackageService} from '@cat/api';
import {Injectable} from '@angular/core';
import {combineLatest, Observable, Subject} from 'rxjs';
import {filter, startWith, switchMap, take, tap} from 'rxjs/operators';
import {Log} from '@common/utils/log';
import {ProjectStore} from '../../project-application/containers/project-application-detail/services/project-store.service';
import {ProjectVersionStore} from '../../common/services/project-version-store.service';

@Injectable()
export class ProjectWorkPackagePageStore {

  workPackages$: Observable<OutputWorkPackageSimple[]>;
  projectEditable$: Observable<boolean>;
  projectTitle$: Observable<string>;

  private refreshPackages$ = new Subject<void>();

  constructor(private projectStore: ProjectStore,
              private projectVersionStore: ProjectVersionStore,
              private workPackageService: WorkPackageService) {
    this.workPackages$ = this.workPackages();
    this.projectEditable$ = this.projectStore.projectEditable$;
    this.projectTitle$ = this.projectStore.projectTitle$;
  }

  deleteWorkPackage(workPackageId: number): Observable<void> {
    return this.projectStore.projectId$.pipe(
        take(1),
        switchMap(projectId => this.workPackageService.deleteWorkPackage(projectId, workPackageId)),
        tap(() => this.refreshPackages$.next()),
        tap(() => Log.info('Deleted work package: ', this, workPackageId))
      );
  }

  createEmptyWorkPackage(projectId: number): Observable<OutputWorkPackage> {
    return this.workPackageService.createWorkPackage(projectId, {} as InputWorkPackageCreate)
      .pipe(
        tap(created => Log.info('Created workPackage:', this, created)),
      );
  }


  private workPackages(): Observable<OutputWorkPackageSimple[]> {
    return combineLatest([
      this.projectStore.projectId$,
      this.projectVersionStore.selectedVersionParam$,
      this.refreshPackages$.pipe(startWith(null))
    ]).pipe(
      filter(([projectId, version]) => !!projectId),
      switchMap(([projectId, version]) => this.workPackageService.getWorkPackagesByProjectId(Number(projectId), version)),
      tap(packages => Log.info('Fetched the work packages:', this, packages)),
    );
  }
}
