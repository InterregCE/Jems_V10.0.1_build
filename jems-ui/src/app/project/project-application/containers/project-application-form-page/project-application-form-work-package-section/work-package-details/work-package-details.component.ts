import {ChangeDetectionStrategy, Component, EventEmitter, OnInit} from '@angular/core';
import {BaseComponent} from '@common/components/base-component';
import {ActivatedRoute, Router} from '@angular/router';
import {InputWorkPackageCreate, InputWorkPackageUpdate, OutputProjectStatus, WorkPackageService} from '@cat/api'
import {combineLatest, merge, of, Subject} from 'rxjs';
import {catchError, flatMap, map, tap} from 'rxjs/operators';
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
  workPackageId = this.activatedRoute?.snapshot?.params?.workPackageId;

  saveError$ = new Subject<I18nValidationError | null>();
  saveSuccess$ = new Subject<boolean>();
  updateWorkPackageData$ = new EventEmitter<InputWorkPackageUpdate>()
  createWorkPackageData$ = new EventEmitter<InputWorkPackageCreate>()

  constructor(private workPackageService: WorkPackageService,
              private activatedRoute: ActivatedRoute,
              private projectStore: ProjectStore,
              private projectApplicationFormSidenavService: ProjectApplicationFormSidenavService,
              private router: Router) {
    super();
  }

  private updatedWorkPackageData$ = this.updateWorkPackageData$
    .pipe(
      flatMap((data) => this.workPackageService.updateWorkPackage(this.projectId, data)),
      tap(() => this.saveSuccess$.next(true)),
      tap(() => this.saveError$.next(null)),
      tap(saved => Log.info('Updated work package data:', this, saved)),
      catchError((error: HttpErrorResponse) => {
        this.saveError$.next(error.error);
        throw error;
      })
    );

  private createdWorkPackageData$ = this.createWorkPackageData$
    .pipe(
      flatMap((data) => this.workPackageService.createWorkPackage(this.projectId, data)),
      tap(() => this.saveSuccess$.next(true)),
      tap(() => this.saveError$.next(null)),
      tap(saved => Log.info('Created work package data:', this, saved)),
      tap(() => this.projectApplicationFormSidenavService.refreshPackages()),
      catchError((error: HttpErrorResponse) => {
        this.saveError$.next(error.error);
        throw error;
      })
    );

  public workPackageDetails$ = merge(
    this.workPackageId
      ? this.workPackageService.getWorkPackageById(this.workPackageId, this.projectId)
      : of({}),
    this.updatedWorkPackageData$,
    this.createdWorkPackageData$
  )

  details$ = combineLatest([
    this.workPackageDetails$,
    this.projectStore.getProject()
  ])
    .pipe(
      tap(([workPackage, project]) =>
        this.projectApplicationFormSidenavService.setAcronym(project.acronym)
      ),
      map(
        ([workPackage, project]) => ({
          workPackage,
          editable: project.projectStatus.status === OutputProjectStatus.StatusEnum.DRAFT
            || project.projectStatus.status === OutputProjectStatus.StatusEnum.RETURNEDTOAPPLICANT
        })
      )
    )

  ngOnInit(): void {
    this.projectStore.init(this.projectId);
    this.projectApplicationFormSidenavService.init(this.destroyed$, this.projectId);
  }

  redirectToWorkPackageOverview(): void {
    this.router.navigate(['app', 'project', 'detail', this.projectId, 'applicationForm']);
  }
}
