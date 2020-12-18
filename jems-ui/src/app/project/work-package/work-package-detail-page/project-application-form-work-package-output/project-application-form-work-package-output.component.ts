import {ChangeDetectionStrategy, Component, EventEmitter, Input} from '@angular/core';
import {combineLatest, merge, Subject} from 'rxjs';
import {HttpErrorResponse} from '@angular/common/http';
import {ActivatedRoute, Router} from '@angular/router';
import {ProjectApplicationFormSidenavService} from '../../../project-application/containers/project-application-form-page/services/project-application-form-sidenav.service';
import {WorkPackageOutputUpdateDTO, WorkPackageOutputService, ProgrammeIndicatorService} from '@cat/api';
import {catchError, distinctUntilChanged, filter, map, mergeMap, tap, withLatestFrom} from 'rxjs/operators';
import {Log} from '../../../../common/utils/log';
import {ProjectWorkPackagePageStore} from '../project-work-package-page-store.service';

@Component({
  selector: 'app-project-application-form-work-package-output',
  templateUrl: './project-application-form-work-package-output.component.html',
  styleUrls: ['./project-application-form-work-package-output.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormWorkPackageOutputComponent {

  projectId = this.activatedRoute?.snapshot?.params?.projectId;

  saveError$ = new Subject<HttpErrorResponse | null>();
  saveSuccess$ = new Subject<boolean>();
  updateWorkPackageOutputData$ = new EventEmitter<WorkPackageOutputUpdateDTO[]>();

  private savedWorkPackageOutputData$ = this.workPackageStore.workPackage$
    .pipe(
      filter(workPackage => !!workPackage.id),
      map(workPackage => workPackage.id),
      distinctUntilChanged(),
      mergeMap(id => this.workPackageOutputService.getWorkPackageOutputs(id)),
      tap(data => Log.info('Fetched the workPackage outputs', this, data)),
    );

  private updatedWorkPackageOutputData$ = this.updateWorkPackageOutputData$
    .pipe(
      withLatestFrom(this.workPackageStore.workPackage$),
      mergeMap(([data, workPackage]) => this.workPackageOutputService.updateWorkPackageOutputs(workPackage.id, data)),
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
    this.workPackageStore.project$,
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

  constructor(private workPackageOutputService: WorkPackageOutputService,
              private activatedRoute: ActivatedRoute,
              public workPackageStore: ProjectWorkPackagePageStore,
              private projectApplicationFormSidenavService: ProjectApplicationFormSidenavService,
              private router: Router,
              private programmeIndicatorService: ProgrammeIndicatorService) {
  }
}
