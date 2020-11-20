import {Injectable} from '@angular/core';
import {
  InputWorkPackageCreate,
  InputWorkPackageUpdate,
  OutputWorkPackage,
  WorkPackageService
} from '@cat/api';
import {Observable, ReplaySubject, Subject} from 'rxjs';
import {
  tap
} from 'rxjs/operators';
import {Log} from '../../../../../common/utils/log';
import {ProjectApplicationFormSidenavService} from './project-application-form-sidenav.service';

@Injectable()
export class ProjectWorkpackageStoreService {

  private workPackageId: number;
  private projectId: number;

  totalAmountChanged$ = new Subject<boolean>();
  workPackage$ = new ReplaySubject<OutputWorkPackage | any>(1);

  constructor(private workPackageService: WorkPackageService,
              private projectApplicationFormSidenavService: ProjectApplicationFormSidenavService) {
  }

  init(workPackageId: number | string | null, projectId: number): void {
    if (workPackageId === this.workPackageId) {
      return;
    }
    this.workPackageId = Number(workPackageId);
    this.projectId = projectId;
    if (!this.workPackageId || !this.projectId) {
      this.workPackage$.next({});
      return;
    }
    this.workPackageService.getWorkPackageById(this.workPackageId, this.projectId)
      .pipe(
        tap(workPackage => Log.info('Fetched project work package:', this, workPackage)),
        tap(workPackage => this.workPackage$.next(workPackage)),
      ).subscribe();
  }

  saveWorkPackage(workPackage: InputWorkPackageUpdate): Observable<OutputWorkPackage> {
    return this.workPackageService.updateWorkPackage(this.projectId, workPackage)
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
}
