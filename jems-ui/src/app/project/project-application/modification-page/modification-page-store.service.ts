import { Injectable } from '@angular/core';
import {ProjectStore} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {combineLatest, Observable, of, Subject} from 'rxjs';
import {ApplicationActionInfoDTO, ProjectStatusDTO, ProjectStatusService, UserRoleCreateDTO} from '@cat/api';
import {map, startWith, switchMap, take, tap} from 'rxjs/operators';
import {Log} from '@common/utils/log';
import {PermissionService} from '../../../security/permissions/permission.service';
import PermissionsEnum = UserRoleCreateDTO.PermissionsEnum;

@Injectable()
export class ModificationPageStore {

  modificationDecisions$: Observable<ProjectStatusDTO[]>;
  currentStatus$: Observable<ProjectStatusDTO.StatusEnum>;
  projectTitle$: Observable<string>;
  currentVersionIsLatest$: Observable<boolean>;
  hasOpenPermission$: Observable<boolean>;

  private modificationSubmitted$ = new Subject<void>();

  constructor(private projectStore: ProjectStore,
              private projectStatusService: ProjectStatusService,
              private permissionService: PermissionService) {
    this.modificationDecisions$ = this.modificationDecisions();
    this.projectTitle$ = this.projectStore.projectTitle$;
    this.currentVersionIsLatest$ = this.projectStore.currentVersionIsLatest$;
    this.currentStatus$ = this.projectStore.projectStatus$
      .pipe(
        map(status => status.status),
      );
    this.hasOpenPermission$ = this.permissionService.hasPermission(PermissionsEnum.ProjectOpenModification);
  }

  startModification(): Observable<string> {
    return this.projectStore.projectId$
      .pipe(
        take(1),
        switchMap(projectId => this.projectStatusService.startModification(projectId)),
        tap(() => this.projectStore.projectStatusChanged$.next()),
        tap(status => Log.info('Changed status for project', this, status))
      );
  }

  handBackToApplicant(): Observable<string> {
    return this.projectStore.projectId$
      .pipe(
        take(1),
        switchMap(projectId => this.projectStatusService.handBackToApplicant(projectId)),
        tap(() => this.projectStore.projectStatusChanged$.next()),
        tap(status => Log.info('Changed status for project', this, status))
      );
  }

  approveApplication(info: ApplicationActionInfoDTO): Observable<string> {
    return this.projectStore.projectId$
      .pipe(
        take(1),
        switchMap(projectId => this.projectStatusService.approveApplication(projectId, info)),
        tap(() => this.projectStore.projectStatusChanged$.next()),
        tap(() => this.modificationSubmitted$.next()),
        tap(status => Log.info('Changed status for project', this, status))
      );
  }

  private modificationDecisions(): Observable<ProjectStatusDTO[]> {
    return combineLatest([
      this.permissionService.hasPermission(PermissionsEnum.ProjectModificationView),
      this.projectStore.projectId$,
      this.modificationSubmitted$.pipe(startWith(null))
    ]).pipe(
      switchMap(([hasOpenPermission, projectId]) =>
        hasOpenPermission ? this.projectStatusService.getModificationDecisions(projectId) : of([])
      ),
      tap(decisions => Log.info('Fetched project modification decisions', this, decisions))
    );
  }
}
