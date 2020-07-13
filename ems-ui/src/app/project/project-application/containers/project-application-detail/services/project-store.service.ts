import {Injectable} from '@angular/core';
import {combineLatest, merge, Observable, ReplaySubject, Subject} from 'rxjs';
import {InputProjectStatus, OutputProject, OutputProjectStatus, ProjectService, ProjectStatusService} from '@cat/api';
import {flatMap, shareReplay, tap} from 'rxjs/operators';
import {Log} from '../../../../../common/utils/log';

/**
 * Stores project related information.
 * Because his injectable is tied to an entity id it is meant to be
 * provided in a detail container rather than a module.
 */
@Injectable()
export class ProjectStore {
  private projectId$ = new ReplaySubject<number>(1);
  private newStatus$ = new Subject<InputProjectStatus.StatusEnum>();

  private projectById$ = this.projectId$
    .pipe(
      flatMap(id => this.projectService.getProjectById(id)),
      tap(project => Log.info('Fetched project:', this, project))
    );
  private changedStatus$ = combineLatest([
    this.projectId$,
    this.newStatus$
  ])
    .pipe(
      flatMap(([id, newStatus]) =>
        this.projectStatusService.setProjectStatus(id, {note: '', status: newStatus})),
      tap(saved => Log.info('Updated project status status:', this, saved)),
    );
  private projectStatus$ = new ReplaySubject<OutputProjectStatus.StatusEnum>(1);
  private project$ =
    merge(
      this.projectById$,
      this.changedStatus$
    )
      .pipe(
        tap(saved => this.projectStatus$.next(saved.projectStatus.status)),
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

  changeStatus(newStatus: InputProjectStatus.StatusEnum) {
    this.newStatus$.next(newStatus)
  }

  constructor(private projectService: ProjectService,
              private projectStatusService: ProjectStatusService) {
  }
}
