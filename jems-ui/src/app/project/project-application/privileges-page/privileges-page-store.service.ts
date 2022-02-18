import {ProjectStore} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {combineLatest, merge, Observable, Subject} from 'rxjs';
import {
  PartnerUserCollaboratorDTO, ProjectPartnerSummaryDTO, ProjectPartnerUserCollaboratorService, ProjectStatusDTO,
  ProjectUserCollaboratorDTO,
  ProjectUserCollaboratorService, UserRoleDTO
} from '@cat/api';
import {filter, map, startWith, switchMap, take, tap} from 'rxjs/operators';
import {Log} from '@common/utils/log';

import {Injectable} from '@angular/core';
import {PermissionService} from '../../../security/permissions/permission.service';
import PermissionsEnum = UserRoleDTO.PermissionsEnum;
import { ProjectVersionStore } from '@project/common/services/project-version-store.service';
import {
  ProjectPartnerStore
} from '@project/project-application/containers/project-application-form-page/services/project-partner-store.service';
import StatusEnum = ProjectStatusDTO.StatusEnum;

@Injectable()
export class PrivilegesPageStore {

  projectCollaborators$: Observable<ProjectUserCollaboratorDTO[]>;
  partnerCollaborators$: Observable<Map<ProjectPartnerSummaryDTO, PartnerUserCollaboratorDTO[]>>;
  projectTitle$: Observable<string>;
  projectCollaboratorsEditable$: Observable<boolean>;
  partnerTeamsVisible$: Observable<boolean>;

  private partnerSummariesOfLastApprovedVersion$: Observable<ProjectPartnerSummaryDTO[]>;
  private savedProjectCollaborators$ = new Subject<ProjectUserCollaboratorDTO[]>();
  private savedPartnerProjectCollaborators$ = new Subject<[number, PartnerUserCollaboratorDTO[]]>();


  constructor(private projectStore: ProjectStore,
              private projectUserCollaboratorService: ProjectUserCollaboratorService,
              private partnerUserCollaboratorService: ProjectPartnerUserCollaboratorService,
              private permissionService: PermissionService,
              private partnerStore: ProjectPartnerStore,
              private projectVersionStore: ProjectVersionStore) {
    this.partnerSummariesOfLastApprovedVersion$ = this.partnerSummariesOfLastApprovedVersion();
    this.projectCollaborators$ = this.projectCollaborators();
    this.partnerCollaborators$ = this.partnerCollaborators();
    this.projectTitle$ = this.projectStore.projectTitle$;
    this.projectCollaboratorsEditable$ = this.projectCollaboratorsEditable();
    this.partnerTeamsVisible$ = this.partnerTeamsVisible();
  }

  saveProjectCollaborators(collaborators: ProjectUserCollaboratorDTO[]): Observable<ProjectUserCollaboratorDTO[]> {
    return this.projectStore.projectId$
      .pipe(
        take(1),
        switchMap(projectId => this.projectUserCollaboratorService.updateAssignedUserCollaborators(
          projectId,
          collaborators.map(collaborator => ({...collaborator, userEmail: collaborator.userEmail?.trim()}))
        )),
        tap(saved => this.savedProjectCollaborators$.next(saved)),
        tap(saved => Log.info('Updated project collaborators', this, saved))
      );
  }

  saveProjectPartnerCollaborators(partnerId: number, collaborators: PartnerUserCollaboratorDTO[]): Observable<PartnerUserCollaboratorDTO[]> {
    return this.projectStore.projectId$
      .pipe(
        take(1),
        switchMap(projectId => this.partnerUserCollaboratorService.updatePartnerUserCollaborators(
          partnerId,
          projectId,
          collaborators.map(collaborator => ({...collaborator, userEmail: collaborator.userEmail?.trim()}))
        )),
        tap(saved => this.savedPartnerProjectCollaborators$.next([partnerId, saved])),
        tap(saved => Log.info('Updated project partner collaborators', this, saved))
      );
  }

  private projectCollaborators(): Observable<ProjectUserCollaboratorDTO[]> {
    const initialCollaborators$ = combineLatest([this.projectStore.projectId$, this.projectStore.collaboratorLevel$])
      .pipe(
        switchMap(([projectId, level]) => this.projectUserCollaboratorService.listAssignedUserCollaborators(projectId)),
        tap(collaborators => Log.info('Fetched project collaborators', this, collaborators))
      );
    return merge(initialCollaborators$, this.savedProjectCollaborators$);
  }

  private partnerCollaborators(): Observable<Map<ProjectPartnerSummaryDTO, PartnerUserCollaboratorDTO[]>> {
    const allPartnerCollaborators$ = this.projectStore.projectId$
      .pipe(
        switchMap(projectId => this.partnerUserCollaboratorService.listAllPartnerCollaborators(projectId)),
        tap(collaborators => Log.info('Fetched project partner collaborators', this, collaborators))
      );

    return combineLatest([
      this.partnerSummariesOfLastApprovedVersion$,
      allPartnerCollaborators$,
      this.savedPartnerProjectCollaborators$.pipe(startWith([])),
      this.projectStore.collaboratorLevel$,
    ])
      .pipe(
        map(([partners, allCollaborators, savedCollaborators, level]) =>
          this.getPartnerTeams(partners, allCollaborators, savedCollaborators, level)
        ),
      );
  }

  private getPartnerTeams(partners: ProjectPartnerSummaryDTO[],
                          allCollaborators: PartnerUserCollaboratorDTO[],
                          savedCollaborators: [number, PartnerUserCollaboratorDTO[]] | any,
                          level: ProjectUserCollaboratorDTO.LevelEnum): Map<ProjectPartnerSummaryDTO, PartnerUserCollaboratorDTO[]> {
    const teams = new Map<ProjectPartnerSummaryDTO, PartnerUserCollaboratorDTO[]>();
    partners.forEach(partner => {
      const collaborators = savedCollaborators.length && savedCollaborators[0] === partner.id
        ? savedCollaborators[1]
        : allCollaborators.filter(partnerCollaborator => partnerCollaborator.partnerId === partner.id);
      teams.set(partner, collaborators.length || level === ProjectUserCollaboratorDTO.LevelEnum.MANAGE
        ? collaborators : null as any);
    });
    return teams;
  }

  private projectCollaboratorsEditable(): Observable<boolean> {
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

  private partnerSummariesOfLastApprovedVersion(): Observable<ProjectPartnerSummaryDTO[]> {
    return this.projectVersionStore.lastApprovedOrContractedVersion$
      .pipe(
        map(lastApprovedVersion => lastApprovedVersion?.version),
        filter(version => !!version),
        switchMap(version => this.partnerStore.partnerSummariesFromVersion(version)),
      );
  }

  private partnerTeamsVisible(): Observable<boolean> {
    return this.projectStore.currentVersionOfProjectStatus$
      .pipe(
        map(status => [
          StatusEnum.APPROVED,
          StatusEnum.MODIFICATIONPRECONTRACTING,
          StatusEnum.MODIFICATIONPRECONTRACTINGSUBMITTED,
          StatusEnum.INMODIFICATION,
          StatusEnum.MODIFICATIONSUBMITTED,
          StatusEnum.CONTRACTED
        ].includes(status.status)),
      );
  }
}
