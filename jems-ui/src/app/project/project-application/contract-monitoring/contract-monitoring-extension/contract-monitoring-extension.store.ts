import {Injectable} from '@angular/core';
import {
  ProjectStore
} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {ProjectContractingMonitoringDTO, ProjectContractingMonitoringService, UserRoleCreateDTO} from '@cat/api';
import {switchMap, tap} from 'rxjs/operators';
import {Log} from '@common/utils/log';
import {Observable} from 'rxjs';
import {PermissionService} from '../../../../security/permissions/permission.service';
import PermissionsEnum = UserRoleCreateDTO.PermissionsEnum;

@Injectable({
  providedIn: 'root'
})
export class ContractMonitoringExtensionStore {

  projectId$: Observable<number>;
  projectContractingMonitoring$: Observable<ProjectContractingMonitoringDTO>;
  contractMonitoringViewable$: Observable<boolean>;
  contractMonitoringEditable$: Observable<boolean>;

  constructor(private projectStore: ProjectStore,
              private projectContractingMonitoringService: ProjectContractingMonitoringService,
              private permissionService: PermissionService) {
    this.projectId$ = this.projectStore.projectId$;
    this.projectContractingMonitoring$ = this.projectContractingMonitoring();
    this.contractMonitoringViewable$ = this.permissionService.hasPermission(PermissionsEnum.ProjectContractingView);
    this.contractMonitoringEditable$ = this.permissionService.hasPermission(PermissionsEnum.ProjectSetToContracted);
  }

  save(item: ProjectContractingMonitoringDTO): Observable<ProjectContractingMonitoringDTO> {
    return this.projectStore.projectId$
      .pipe(
        switchMap(projectId => this.projectContractingMonitoringService.updateContractingMonitoring(projectId, item)),
        tap(saved => Log.info('Saved contract monitoring', saved)),
      );
  }

  private projectContractingMonitoring(): Observable<ProjectContractingMonitoringDTO> {
    return this.projectStore.projectId$
      .pipe(
        switchMap(projectId => this.projectContractingMonitoringService.getContractingMonitoring(projectId)),
      );
  }

}
