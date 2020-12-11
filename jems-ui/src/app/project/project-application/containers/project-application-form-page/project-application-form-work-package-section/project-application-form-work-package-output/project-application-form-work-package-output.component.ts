import {ChangeDetectionStrategy, Component, EventEmitter, Input} from '@angular/core';
import {combineLatest, merge, Subject} from 'rxjs';
import {HttpErrorResponse} from '@angular/common/http';
import {ActivatedRoute, Router} from '@angular/router';
import {ProjectStore} from '../../../project-application-detail/services/project-store.service';
import {ProjectApplicationFormSidenavService} from '../../services/project-application-form-sidenav.service';
import {BaseComponent} from '@common/components/base-component';
import {WorkPackageOutputUpdateDTO, WorkPackageService, ProgrammeIndicatorService} from '@cat/api';
import {catchError, distinctUntilChanged, filter, map, mergeMap, tap, withLatestFrom} from 'rxjs/operators';
import {Log} from '../../../../../../common/utils/log';
import {ProjectWorkpackageStoreService} from '../../services/project-workpackage-store.service';

@Component({
  selector: 'app-project-application-form-work-package-output',
  templateUrl: './project-application-form-work-package-output.component.html',
  styleUrls: ['./project-application-form-work-package-output.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormWorkPackageOutputComponent extends BaseComponent {

  projectId = this.activatedRoute?.snapshot?.params?.projectId;

  saveError$ = new Subject<HttpErrorResponse | null>();
  saveSuccess$ = new Subject<boolean>();
  updateWorkPackageOutputData$ = new EventEmitter<WorkPackageOutputUpdateDTO[]>();

  private savedWorkPackageOutputData$ = this.workPackageStore.workPackage$
    .pipe(
      filter(workPackage => !!workPackage.id),
      map(workPackage => workPackage.id),
      distinctUntilChanged(),
      mergeMap(id => this.workPackageService.getWorkPackageOutputs(id, this.projectId)),
      tap(data => Log.info('Fetched the workPackage outputs', this, data)),
    );

  private updatedWorkPackageOutputData$ = this.updateWorkPackageOutputData$
    .pipe(
      withLatestFrom(this.workPackageStore.workPackage$),
      mergeMap(([data, workPackage]) => this.workPackageService.updateWorkPackageOutputs(workPackage.id, this.projectId, data)),
      tap(() => this.saveSuccess$.next(true)),
      tap(() => this.saveError$.next(null)),
      tap(saved => Log.info('Updated workPackage outputs:', this, saved)),
      catchError((error: HttpErrorResponse) => {
        this.saveError$.next(error);
        throw error;
      }),
    );

  details$ = combineLatest([
    merge(this.updatedWorkPackageOutputData$, this.savedWorkPackageOutputData$),
    this.projectStore.getProject(),
    this.programmeIndicatorService.getAllIndicatorOutputDetail(),
    this.workPackageStore.workPackage$
  ])
    .pipe(
      map(([workPackageOutputs, project, indicators, workPackage]) => ({
        workPackageOutputs,
        project,
        indicators,
        workPackage
      })),
    );

  constructor(private workPackageService: WorkPackageService,
              private activatedRoute: ActivatedRoute,
              public projectStore: ProjectStore,
              public workPackageStore: ProjectWorkpackageStoreService,
              private projectApplicationFormSidenavService: ProjectApplicationFormSidenavService,
              private router: Router,
              private programmeIndicatorService: ProgrammeIndicatorService) {
    super();
    this.projectStore.init(this.projectId);
  }
}
