import {Injectable} from '@angular/core';
import {combineLatest, merge, Observable, Subject} from 'rxjs';
import {
  ProjectContractingMonitoringService,
  ProjectContractingReportingScheduleDTO,
  ProjectContractingReportingService, ProjectPeriodDTO,
  ProjectUserCollaboratorDTO,
  UserRoleCreateDTO
} from '@cat/api';
import {
  ProjectStore
} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {map, shareReplay, switchMap, tap} from 'rxjs/operators';
import {Log} from '@common/utils/log';
import {PermissionService} from '../../../../security/permissions/permission.service';
import PermissionsEnum = UserRoleCreateDTO.PermissionsEnum;
import LevelEnum = ProjectUserCollaboratorDTO.LevelEnum;


@Injectable({
  providedIn: 'root'
})
export class ContractReportingStore {
  projectId$: Observable<number>;
  contractReportingDeadlines$: Observable<ProjectContractingReportingScheduleDTO[]>;
  userCanViewDeadlines$: Observable<boolean>;
  userCanEditDeadlines$: Observable<boolean>;
  userCanViewTimeplan$: Observable<boolean>;
  savedData$ = new Subject<ProjectContractingReportingScheduleDTO[]>();
  availablePeriods$: Observable<ProjectPeriodDTO[]>;
  contractingMonitoringStartDate$: Observable<string>;

  constructor(private projectStore: ProjectStore,
              private projectContractingReportingService: ProjectContractingReportingService,
              private projectContractingMonitoringService: ProjectContractingMonitoringService,
              private permissionService: PermissionService) {
    this.projectId$ = this.projectStore.projectId$;
    this.contractReportingDeadlines$ = this.contractReportingDeadlines();
    this.userCanViewDeadlines$ = this.userCanViewDeadlines();
    this.userCanEditDeadlines$ = this.userCanEditDeadlines();
    this.userCanViewTimeplan$ = this.userCanViewTimeplan();
    this.availablePeriods$ = this.contractReportingAvailablePeriods();
    this.contractingMonitoringStartDate$ = this.contractingMonitoringStartDate();
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
    return merge(initialData$, this.savedData$);
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

  private userCanViewTimeplan(): Observable<boolean> {
    return combineLatest([
      this.permissionService.hasPermission(PermissionsEnum.ProjectFormRetrieve),
      this.projectStore.userIsProjectOwner$
    ])
      .pipe(
        map(([canView, isOwner]) => canView || isOwner)
      );
  }

  private contractReportingAvailablePeriods(): Observable<ProjectPeriodDTO[]> {
    return this.projectStore.projectId$
      .pipe(
        switchMap(projectId => this.projectContractingMonitoringService.getContractingMonitoringPeriods(projectId)),
        map(periods => [
          ...periods,
          { number: 255, start: 0, end: 0, startDate: '', endDate: '' },
        ]),
      );
  }

  private contractingMonitoringStartDate(): Observable<string> {
    return this.projectStore.projectId$
      .pipe(
        switchMap(projectId => this.projectContractingMonitoringService.getContractingMonitoringStartDate(projectId)),
        map(data => data.startDate)
      );
  }

}
