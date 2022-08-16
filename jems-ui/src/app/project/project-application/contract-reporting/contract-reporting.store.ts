import {Injectable} from '@angular/core';
import {combineLatest, merge, Observable, Subject} from 'rxjs';
import {
  ProjectContractingReportingScheduleDTO,
  ProjectContractingReportingService,
  ProjectDetailFormDTO,
  ProjectUserCollaboratorDTO,
  UserRoleCreateDTO
} from '@cat/api';
import {
  ProjectStore
} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {map, shareReplay, switchMap, tap} from 'rxjs/operators';
import {Log} from '@common/utils/log';
import {PermissionService} from '../../../security/permissions/permission.service';
import PermissionsEnum = UserRoleCreateDTO.PermissionsEnum;
import LevelEnum = ProjectUserCollaboratorDTO.LevelEnum;


@Injectable({
  providedIn: 'root'
})
export class ContractReportingStore {
  projectId$: Observable<number>;
  projectForm$: Observable<ProjectDetailFormDTO>;
  contractReportingDeadlines$: Observable<ProjectContractingReportingScheduleDTO[]>;
  userCanViewDeadlines$: Observable<boolean>;
  userCanEditDeadlines$: Observable<boolean>;
  savedData$ = new Subject<ProjectContractingReportingScheduleDTO[]>();

  constructor(private projectStore: ProjectStore,
              private projectContractingReportingService: ProjectContractingReportingService,
              private permissionService: PermissionService) {
    this.projectId$ = this.projectStore.projectId$;
    this.projectForm$ = this.projectStore.projectForm$;
    this.contractReportingDeadlines$ = this.contractReportingDeadlines();
    this.userCanViewDeadlines$ = this.userCanViewDeadlines();
    this.userCanEditDeadlines$ = this.userCanEditDeadlines();
  }

  save(contractReportingDeadlines: ProjectContractingReportingScheduleDTO[]): Observable<ProjectContractingReportingScheduleDTO[]> {
    return this.projectStore.projectId$
      .pipe(
        switchMap(projectId => this.projectContractingReportingService.updateReportingSchedule(projectId, contractReportingDeadlines)),
        tap(saved => Log.info('Saved contract reporting', saved)),
        tap(data => this.savedData$.next(data))
      );
  }

  private contractReportingDeadlines(): Observable<ProjectContractingReportingScheduleDTO[]> {
    const initialData$ = this.projectStore.projectId$
      .pipe(
        switchMap(projectId => this.projectContractingReportingService.getReportingSchedule(projectId)),
      );
    return merge(initialData$, this.savedData$)
      .pipe(
        shareReplay(1)
      );
  }

  private userCanViewDeadlines(): Observable<boolean> {
    return combineLatest([
      this.permissionService.hasPermission(PermissionsEnum.ProjectContractingReportingView),
      this.permissionService.hasPermission(PermissionsEnum.ProjectContractingReportingEdit),
      this.permissionService.hasPermission(PermissionsEnum.ProjectCreatorContractingReportingView),
      this.permissionService.hasPermission(PermissionsEnum.ProjectCreatorContractingReportingEdit)
    ])
      .pipe(
        map(([canView, canEdit,canCreatorView, canCreatorEdit]) => canView || canEdit || canCreatorView || canCreatorEdit)
      );
  }

  private userCanEditDeadlines(): Observable<boolean> {
    return combineLatest([
      this.permissionService.hasPermission(PermissionsEnum.ProjectContractingReportingEdit),
      this.permissionService.hasPermission(PermissionsEnum.ProjectCreatorContractingReportingEdit),
      this.projectStore.collaboratorLevel$
    ])
      .pipe(
        map(([canEdit, canCreatorEdit, level]) => canEdit || (canCreatorEdit && (level === LevelEnum.EDIT || level === LevelEnum.MANAGE)))
      );
  }


}
