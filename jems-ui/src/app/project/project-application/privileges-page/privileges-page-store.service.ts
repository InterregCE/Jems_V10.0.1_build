import {ProjectStore} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {combineLatest, merge, Observable, Subject} from 'rxjs';
import {
  PartnerUserCollaboratorDTO, ProjectPartnerUserCollaboratorService,
  ProjectUserCollaboratorDTO,
  ProjectUserCollaboratorService, UserRoleDTO
} from '@cat/api';
import {filter, map, switchMap, take, tap} from 'rxjs/operators';
import {Log} from '@common/utils/log';

import {Injectable} from '@angular/core';
import {PermissionService} from '../../../security/permissions/permission.service';
import PermissionsEnum = UserRoleDTO.PermissionsEnum;

@Injectable()
export class PrivilegesPageStore {

  projectCollaborators$: Observable<ProjectUserCollaboratorDTO[]>;
  partnerCollaborators$: Observable<PartnerUserCollaboratorDTO[]>;
  projectTitle$: Observable<string>;
  projectCollaboratorsEditable$: Observable<boolean>;

  private savedProjectCollaborators = new Subject<ProjectUserCollaboratorDTO[]>();
  private savedPartnerProjectCollaborators = new Subject<PartnerUserCollaboratorDTO[]>();


  constructor(private projectStore: ProjectStore,
              private projectUserCollaboratorService: ProjectUserCollaboratorService,
              private partnerUserCollaboratorService: ProjectPartnerUserCollaboratorService,
              private permissionService: PermissionService) {
    this.projectCollaborators$ = this.projectCollaborators();
    this.partnerCollaborators$ = this.partnerCollaborators();
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

  saveProjectPartnerCollaborators(partnerId: number, collaborators: PartnerUserCollaboratorDTO[]): Observable<PartnerUserCollaboratorDTO[]> {
    return this.projectStore.projectId$
      .pipe(
        take(1),
        switchMap(projectId => this.partnerUserCollaboratorService.updatePartnerUserCollaborators(partnerId, projectId, collaborators)),
        tap(saved => Log.info('Updated project partner collaborators', this, saved))
      );
  }

  private projectCollaborators(): Observable<ProjectUserCollaboratorDTO[]> {
    const initialCollaborators$ = combineLatest([this.projectStore.projectId$, this.projectStore.collaboratorLevel$])
      .pipe(
        filter(([projectId, level]) => level === ProjectUserCollaboratorDTO.LevelEnum.MANAGE),
        switchMap(([projectId, level]) => this.projectUserCollaboratorService.listAssignedUserCollaborators(projectId)),
        tap(collaborators => Log.info('Fetched project collaborators', this, collaborators))
      );
    return merge(initialCollaborators$, this.savedProjectCollaborators);
  }

  private partnerCollaborators(): Observable<PartnerUserCollaboratorDTO[]> {
    const initialPartnerCollaborators$ = this.projectStore.projectId$
      .pipe(
        switchMap(projectId => this.partnerUserCollaboratorService.listAllPartnerCollaborators(projectId)),
        tap(collaborators => Log.info('Fetched project partner collaborators', this, collaborators))
      );
    return merge(initialPartnerCollaborators$, this.savedPartnerProjectCollaborators);
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
