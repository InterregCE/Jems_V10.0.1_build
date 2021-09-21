import {Injectable} from '@angular/core';
import {combineLatest, Observable, ReplaySubject} from 'rxjs';
import {OutputProjectDescription, ProjectDescriptionService} from '@cat/api';
import {mergeMap, shareReplay, tap} from 'rxjs/operators';
import {Log} from '@common/utils/log';
import {ProjectVersionStore} from '@project/common/services/project-version-store.service';

@Injectable()
export class ProjectApplicationFormStore {
  private projectId$ = new ReplaySubject<number>(1);

  projectDescription$ = combineLatest([this.projectId$, this.projectVersionStore.currentRouteVersion$])
    .pipe(
      mergeMap(([id, version]) => this.projectDescriptionService.getProjectDescription(id, version)),
      tap(desc => Log.info('Fetched project description', this, desc)),
      shareReplay(1)
    );

  constructor(private projectDescriptionService: ProjectDescriptionService,
              private projectVersionStore: ProjectVersionStore) {
  }

  init(projectId: number): void {
    this.projectId$.next(projectId);
  }

  getProjectDescription(): Observable<OutputProjectDescription> {
    return this.projectDescription$;
  }
}
