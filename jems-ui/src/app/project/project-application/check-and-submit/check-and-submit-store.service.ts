import {Injectable} from '@angular/core';
import {BehaviorSubject, Observable} from 'rxjs';
import {ProjectStatusService} from '@cat/api';
import {map, shareReplay, tap} from 'rxjs/operators';
import {Log} from '@common/utils/log';
import {ProjectVersionStore} from '@project/common/services/project-version-store.service';
import {PreConditionCheckResult} from '@project/model/plugin/PreConditionCheckResult';
import {ProjectStore} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {PermissionService} from '../../../security/permissions/permission.service';

@Injectable({
  providedIn: 'root'
})
export class CheckAndSubmitStore {

  private preConditionCheckResult = new BehaviorSubject<PreConditionCheckResult | null>(null);
  preConditionCheckResult$ = this.preConditionCheckResult.asObservable();

  constructor(private projectStore: ProjectStore,
              private projectVersionStore: ProjectVersionStore,
              private permissionService: PermissionService,
              private projectStatusService: ProjectStatusService) {
    this.preConditionCheckResult.next(null);
  }

  preConditionCheck(projectId: number): Observable<PreConditionCheckResult> {
    this.preConditionCheckResult.next(null);
    return this.projectStatusService.preConditionCheck(projectId)
      .pipe(
        map(resultDTO => PreConditionCheckResult.newInstance(resultDTO)),
        tap(result => this.preConditionCheckResult.next(result)),
        tap(() => Log.info('execute pre condition check', projectId)),
        shareReplay(1)
      );
  }

  submitApplication(projectId: number): Observable<string> {
    return this.projectStatusService.submitApplication(projectId)
      .pipe(
        tap(() => this.projectStore.projectStatusChanged$.next()),
        tap(() => this.preConditionCheckResult.next(null)),
        tap(() => this.projectVersionStore.versionChanged$.next()),
        tap(status => Log.info('Changed status for project', projectId, status))
      );
  }

}
