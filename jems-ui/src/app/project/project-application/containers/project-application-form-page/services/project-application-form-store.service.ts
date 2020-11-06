import {Injectable} from '@angular/core';
import {Observable, ReplaySubject} from 'rxjs';
import {OutputProjectDescription, ProjectDescriptionService} from '@cat/api';
import {mergeMap, shareReplay, tap} from 'rxjs/operators';
import {Log} from '../../../../../common/utils/log';

@Injectable()
export class ProjectApplicationFormStore {
  private projectId$ = new ReplaySubject<number>(1);

  projectDescription$ = this.projectId$
    .pipe(
      mergeMap(id => this.projectDescriptionService.getProjectDescription(id)),
      tap(desc => Log.info('Fetched project description', this, desc)),
      shareReplay(1)
    );

  constructor(private projectDescriptionService: ProjectDescriptionService) {
  }

  init(projectId: number): void {
    this.projectId$.next(projectId);
  }

  getProjectDescription(): Observable<OutputProjectDescription> {
    return this.projectDescription$;
  }
}
