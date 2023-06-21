import {Injectable} from '@angular/core';
import {
  ProjectStore
} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {
  ProjectContractingMonitoringDTO,
  ProjectContractingMonitoringService,
  UserRoleCreateDTO
} from '@cat/api';
import {map, switchMap, tap} from 'rxjs/operators';
import {Log} from '@common/utils/log';
import {Observable, Subject, merge, combineLatest} from 'rxjs';
import { PermissionService } from 'src/app/security/permissions/permission.service';
import PermissionsEnum = UserRoleCreateDTO.PermissionsEnum;
import {ProjectVersionStore} from '@project/common/services/project-version-store.service';

@Injectable({
  providedIn: 'root'
})
export class ContractMonitoringExtensionStore {

  projectId$: Observable<number>;
  projectContractingMonitoring$: Observable<ProjectContractingMonitoringDTO>;
  projectContractingMonitoringBudget$: Observable<number>;
  contractMonitoringViewable$: Observable<boolean>;
  contractMonitoringEditable$: Observable<boolean>;

  savedProjectContractingMonitoring$ = new Subject<ProjectContractingMonitoringDTO>();

  constructor(private projectStore: ProjectStore,
              private projectContractingMonitoringService: ProjectContractingMonitoringService,
              private permissionService: PermissionService,
              private projectVersionStore: ProjectVersionStore) {

    this.projectId$ = this.projectStore.projectId$;
    this.projectContractingMonitoring$ = this.projectContractingMonitoring();
    this.projectContractingMonitoringBudget$ = this.getProjectBudget();
    this.contractMonitoringViewable$ = this.permissionService.hasPermission(PermissionsEnum.ProjectContractingView);
    this.contractMonitoringEditable$ = this.permissionService.hasPermission(PermissionsEnum.ProjectSetToContracted);
  }

  save(item: ProjectContractingMonitoringDTO): Observable<ProjectContractingMonitoringDTO> {
    return this.projectStore.projectId$
      .pipe(
        switchMap(projectId => this.projectContractingMonitoringService.updateContractingMonitoring(projectId, item)),
        // trigger new project fetch, because for ProjectContractedOnDate
        tap(() => this.projectStore.projectStatusChanged$.next()),
        tap(saved => Log.info('Saved contract monitoring', saved)),
      );
  }

  private projectContractingMonitoring(): Observable<ProjectContractingMonitoringDTO> {
    const initialProjectContractMonitoring$ = this.projectStore.projectId$
      .pipe(
        switchMap(projectId => this.projectContractingMonitoringService.getContractingMonitoring(projectId)),
      );
    return merge(initialProjectContractMonitoring$, this.savedProjectContractingMonitoring$);
  }

  private getProjectBudget(): Observable<number> {
    return combineLatest([
      this.projectId$,
      this.projectVersionStore.lastApprovedOrContractedVersion$,
    ]).pipe(
      switchMap(([projectId, version]) =>
        this.projectContractingMonitoringService.getContractingMonitoringProjectBudget(projectId, version?.version)
      ),
      map(data => data ),
    );
  }

}
