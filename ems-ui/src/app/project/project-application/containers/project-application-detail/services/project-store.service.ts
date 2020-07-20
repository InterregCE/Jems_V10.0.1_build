import {Injectable} from '@angular/core';
import {merge, Observable, ReplaySubject, Subject} from 'rxjs';
import {
  InputProjectEligibilityAssessment,
  InputProjectQualityAssessment,
  InputProjectStatus,
  OutputProject,
  OutputProjectStatus,
  ProjectService,
  ProjectStatusService
} from '@cat/api';
import {distinctUntilChanged, flatMap, shareReplay, tap, withLatestFrom} from 'rxjs/operators';
import {Log} from '../../../../../common/utils/log';
import {Router} from '@angular/router';

/**
 * Stores project related information.
 */
@Injectable()
export class ProjectStore {
  private projectId$ = new ReplaySubject<number>(1);
  private newStatus$ = new Subject<InputProjectStatus>();
  private newEligibilityAssessment$ = new Subject<InputProjectEligibilityAssessment>();
  private newQualityAssessment$ = new Subject<InputProjectQualityAssessment>();

  private projectById$ = this.projectId$
    .pipe(
      distinctUntilChanged(),
      flatMap(id => this.projectService.getProjectById(id)),
      tap(project => Log.info('Fetched project:', this, project))
    );

  private changedStatus$ = this.newStatus$
    .pipe(
      withLatestFrom(this.projectId$),
      flatMap(([newStatus, id]) =>
        this.projectStatusService.setProjectStatus(id, newStatus)),
      tap(saved => this.projectStatus$.next(saved.projectStatus.status)),
      tap(saved => Log.info('Updated project status status:', this, saved)),
      tap(saved => this.router.navigate(['project', saved.id]))
    );

  private changedEligibilityAssessment$ = this.newEligibilityAssessment$
    .pipe(
      withLatestFrom(this.projectId$),
      flatMap(([assessment, id]) => this.projectStatusService.setEligibilityAssessment(id, assessment)),
      tap(saved => Log.info('Updated project eligibility assessment:', this, saved)),
      tap(saved => this.router.navigate(['project', saved.id]))
    );

  private changedQualityAssessment$ = this.newQualityAssessment$
    .pipe(
      withLatestFrom(this.projectId$),
      flatMap(([assessment, id]) => this.projectStatusService.setQualityAssessment(id, assessment)),
      tap(saved => Log.info('Updated project uality assessment:', this, saved)),
      tap(saved => this.router.navigate(['project', saved.id]))
    )

  private projectStatus$ = new ReplaySubject<OutputProjectStatus.StatusEnum>(1);
  private project$ =
    merge(
      this.projectById$,
      this.changedStatus$,
      this.changedEligibilityAssessment$,
      this.changedQualityAssessment$,
    )
      .pipe(
        shareReplay(1)
      );


  constructor(private projectService: ProjectService,
              private projectStatusService: ProjectStatusService,
              private router: Router) {
  }

  init(projectId: number) {
    this.projectId$.next(projectId);
  }

  getProject(): Observable<OutputProject> {
    return this.project$;
  }

  getStatus(): Observable<OutputProjectStatus.StatusEnum> {
    return this.projectStatus$.asObservable();
  }

  changeStatus(newStatus: InputProjectStatus) {
    this.newStatus$.next(newStatus)
  }

  setEligibilityAssessment(assessment: InputProjectEligibilityAssessment): void {
    this.newEligibilityAssessment$.next(assessment);
  }

  setQualityAssessment(assessment: InputProjectQualityAssessment): void {
    this.newQualityAssessment$.next(assessment);
  }
}
