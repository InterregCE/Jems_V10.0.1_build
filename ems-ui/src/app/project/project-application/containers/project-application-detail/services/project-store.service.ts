import {Injectable} from '@angular/core';
import {merge, Observable, ReplaySubject, Subject} from 'rxjs';
import {InputProjectStatus, OutputProject, OutputProjectStatus, ProjectService, ProjectStatusService} from '@cat/api';
import {distinct, flatMap, shareReplay, tap, withLatestFrom} from 'rxjs/operators';
import {Log} from '../../../../../common/utils/log';

/**
 * Stores project related information.
 */
@Injectable()
export class ProjectStore {
  private projectId$ = new ReplaySubject<number>(1);
  private newStatus$ = new Subject<InputProjectStatus>();

  private projectById$ = this.projectId$
    .pipe(
      distinct(),
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
    );

  private projectStatus$ = new ReplaySubject<OutputProjectStatus.StatusEnum>(1);
  private project$ =
    merge(
      this.projectById$,
      this.changedStatus$
    )
      .pipe(
        shareReplay(1)
      );

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

  constructor(private projectService: ProjectService,
              private projectStatusService: ProjectStatusService) {
  }
}
