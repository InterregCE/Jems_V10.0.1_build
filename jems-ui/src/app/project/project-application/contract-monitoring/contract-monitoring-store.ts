import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {ProjectStatusService, UserRoleCreateDTO} from '@cat/api';
import {shareReplay, tap} from 'rxjs/operators';
import {Log} from '@common/utils/log';
import {ProjectStore} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {PermissionService} from '../../../security/permissions/permission.service';
import PermissionsEnum = UserRoleCreateDTO.PermissionsEnum;

@Injectable({
  providedIn: 'root'
})
export class ContractMonitoringStore {

  canSetToContracted$: Observable<boolean>;
  constructor(private projectStore: ProjectStore,
              private projectStatusService: ProjectStatusService,
              private permissionService: PermissionService) {
    this.canSetToContracted$ = this.permissionService.hasPermission(PermissionsEnum.ProjectSetToContracted);
  }

  setToContracted(projectId: number): Observable<string> {
    return this.projectStatusService.setToContracted(projectId)
      .pipe(
        tap(() => this.projectStore.projectStatusChanged$.next()),
        tap(status => Log.info('Changed status for project', projectId, status)),
        shareReplay(1)
      );
  }

}
