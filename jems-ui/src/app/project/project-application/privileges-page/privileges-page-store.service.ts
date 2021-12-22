import {ProjectStore} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {combineLatest, merge, Observable, Subject} from 'rxjs';
import {
  ProjectUserCollaboratorDTO,
  ProjectUserCollaboratorService, UserRoleDTO
} from '@cat/api';
import {map, switchMap, take, tap} from 'rxjs/operators';
import {Log} from '@common/utils/log';

import {Injectable} from '@angular/core';
import {PermissionService} from '../../../security/permissions/permission.service';
import PermissionsEnum = UserRoleDTO.PermissionsEnum;

@Injectable()
export class PrivilegesPageStore {

  projectCollaborators$: Observable<ProjectUserCollaboratorDTO[]>;
  projectTitle$: Observable<string>;
  projectCollaboratorsEditable$: Observable<boolean>;

  private savedProjectCollaborators = new Subject<ProjectUserCollaboratorDTO[]>();


  constructor(private projectStore: ProjectStore,
              private projectUserCollaboratorService: ProjectUserCollaboratorService,
              private permissionService: PermissionService) {
    this.projectCollaborators$ = this.projectCollaborators();
    this.projectTitle$ = this.projectStore.projectTitle$;
    this.projectCollaboratorsEditable$ = this.projectCollaboratorsEditable();
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

  projectCollaboratorsEditable(): Observable<boolean> {
    return combineLatest([
      this.projectStore.collaboratorLevel$,
      this.permissionService.hasPermission(PermissionsEnum.ProjectCreatorCollaboratorsUpdate),
      // we expect, that if user can open this page, he is assigned to the project already, or he has ProjectRetrieve global permission
      this.permissionService.hasPermission(PermissionsEnum.ProjectMonitorCollaboratorsUpdate),
    ]).pipe(
      map(([collaboratorLevel, canUpdateProjectCreatorCollaborators, canUpdateProjectMonitorCollaborators]) =>
        (collaboratorLevel === ProjectUserCollaboratorDTO.LevelEnum.MANAGE && canUpdateProjectCreatorCollaborators) || canUpdateProjectMonitorCollaborators
      )
    );
  }

}
