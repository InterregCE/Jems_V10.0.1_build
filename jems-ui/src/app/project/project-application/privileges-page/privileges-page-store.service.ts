import {ProjectStore} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {merge, Observable, Subject} from 'rxjs';
import {
  ProjectUserCollaboratorDTO,
  ProjectUserCollaboratorService
} from '@cat/api';
import {switchMap, take, tap} from 'rxjs/operators';
import {Log} from '@common/utils/log';

import {Injectable} from '@angular/core';

@Injectable()
export class PrivilegesPageStore {

  projectCollaborators$: Observable<ProjectUserCollaboratorDTO[]>;
  projectTitle$: Observable<string>;

  private savedProjectCollaborators = new Subject<ProjectUserCollaboratorDTO[]>();


  constructor(private projectStore: ProjectStore,
              private projectUserCollaboratorService: ProjectUserCollaboratorService) {
    this.projectCollaborators$ = this.projectCollaborators();
    this.projectTitle$ = this.projectStore.projectTitle$;
  }

  saveProjectCollaborators(collaborators: ProjectUserCollaboratorDTO[]): Observable<ProjectUserCollaboratorDTO[]> {
    return this.projectStore.projectId$
      .pipe(
        take(1),
        switchMap(projectId => this.projectUserCollaboratorService.updateAssignedUserCollaborators(projectId, collaborators)),
        tap(saved => Log.info('Updated project collaborators', this, saved))
      );
  }

  private projectCollaborators(): Observable<ProjectUserCollaboratorDTO[]> {
    const initialCollaborators$ = this.projectStore.projectId$
      .pipe(
        switchMap(projectId => this.projectUserCollaboratorService.listAssignedUserCollaborators(projectId)),
        tap(collaborators => Log.info('Fetched project collaborators', this, collaborators))
      );
    return merge(initialCollaborators$, this.savedProjectCollaborators);
  }
}
