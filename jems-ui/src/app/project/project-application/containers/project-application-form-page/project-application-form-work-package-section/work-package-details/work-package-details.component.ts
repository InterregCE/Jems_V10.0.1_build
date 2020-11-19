import {ChangeDetectionStrategy, Component, EventEmitter, OnInit} from '@angular/core';
import {BaseComponent} from '@common/components/base-component';
import {ActivatedRoute, Router} from '@angular/router';
import {InputWorkPackageCreate, InputWorkPackageUpdate, WorkPackageService} from '@cat/api';
import {merge, of, ReplaySubject, Subject} from 'rxjs';
import {catchError, distinctUntilChanged, map, mergeMap, takeUntil, tap} from 'rxjs/operators';
import {Log} from '../../../../../../common/utils/log';
import {HttpErrorResponse} from '@angular/common/http';
import {I18nValidationError} from '@common/validation/i18n-validation-error';
import {ProjectStore} from '../../../project-application-detail/services/project-store.service';
import {ProjectApplicationFormSidenavService} from '../../services/project-application-form-sidenav.service';

@Component({
  selector: 'app-work-package-details',
  templateUrl: './work-package-details.component.html',
  styleUrls: ['./work-package-details.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class WorkPackageDetailsComponent extends BaseComponent implements OnInit {
  projectId = this.activatedRoute?.snapshot?.params?.projectId;
  workPackageId$ = new ReplaySubject<number>(1);

  saveError$ = new Subject<HttpErrorResponse | null>();
  saveSuccess$ = new Subject<boolean>();
  updateWorkPackageData$ = new EventEmitter<InputWorkPackageUpdate>();
  createWorkPackageData$ = new EventEmitter<InputWorkPackageCreate>();

  constructor(private workPackageService: WorkPackageService,
              private activatedRoute: ActivatedRoute,
              public projectStore: ProjectStore,
              private projectApplicationFormSidenavService: ProjectApplicationFormSidenavService,
              private router: Router) {
    super();
  }

  private updatedWorkPackageData$ = this.updateWorkPackageData$
    .pipe(
      mergeMap((data) => this.workPackageService.updateWorkPackage(this.projectId, data)),
      tap(() => this.saveSuccess$.next(true)),
      tap(() => this.saveError$.next(null)),
      tap(saved => Log.info('Updated work package data:', this, saved)),
      catchError((error: HttpErrorResponse) => {
        this.saveError$.next(error);
        throw error;
      })
    );

  private createdWorkPackageData$ = this.createWorkPackageData$
    .pipe(
      mergeMap((data) => this.workPackageService.createWorkPackage(this.projectId, data)),
      tap(() => this.saveSuccess$.next(true)),
      tap(() => this.saveError$.next(null)),
      tap(saved => Log.info('Created work package data:', this, saved)),
      tap(() => this.projectApplicationFormSidenavService.refreshPackages(this.projectId)),
      tap(() => this.redirectToWorkPackageOverview()),
      catchError((error: HttpErrorResponse) => {
        this.saveError$.next(error);
        throw error;
      })
    );

  private workPackageById$ = this.workPackageId$
    .pipe(
      mergeMap(id => id ? this.workPackageService.getWorkPackageById(id, this.projectId) : of({}))
    );

  public workPackageDetails$ = merge(
    this.workPackageById$,
    this.updatedWorkPackageData$,
    this.createdWorkPackageData$
  );

  ngOnInit(): void {
    this.projectStore.init(this.projectId);

    this.activatedRoute.params.pipe(
      takeUntil(this.destroyed$),
      map(params => params.workPackageId),
      distinctUntilChanged(),
      tap(id => this.workPackageId$.next(Number(id))),
    ).subscribe();
  }

  redirectToWorkPackageOverview(): void {
    this.router.navigate(['app', 'project', 'detail', this.projectId, 'applicationFormWorkPackage']);
  }
}
