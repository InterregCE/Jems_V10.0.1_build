import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {ProjectStatusService} from '@cat/api';
import {map, shareReplay, tap} from 'rxjs/operators';
import {Log} from '@common/utils/log';
import {PreConditionCheckResult} from '@project/model/plugin/PreConditionCheckResult';
import {ProjectStore} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {PermissionService} from '../../../security/permissions/permission.service';

@Injectable({
  providedIn: 'root'
})
export class CheckAndSubmitStore {


  constructor(private projectStore: ProjectStore,
              private permissionService: PermissionService,
              private projectStatusService: ProjectStatusService) {
  }

  preConditionCheck(projectId: number): Observable<PreConditionCheckResult> {
    return this.projectStatusService.preConditionCheck(projectId)
      .pipe(
        map(resultDTO => PreConditionCheckResult.newInstance(resultDTO)),
        tap(() => Log.info('execute pre condition check', projectId)),
        shareReplay(1)
      );
  }

  submitApplication(projectId: number): Observable<string> {
    return this.projectStatusService.submitApplication(projectId)
      .pipe(
        tap(() => this.projectStore.projectStatusChanged$.next()),
        tap(status => Log.info('Changed status for project', projectId, status))
      );
  }
}
