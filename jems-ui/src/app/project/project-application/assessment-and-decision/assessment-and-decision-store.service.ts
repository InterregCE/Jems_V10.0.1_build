import {Injectable} from '@angular/core';
import {combineLatest, Observable, of} from 'rxjs';
import {ProjectStatusService, UserRoleCreateDTO} from '@cat/api';
import {switchMap, tap} from 'rxjs/operators';
import {Log} from '@common/utils/log';
import {ProjectVersionStore} from '@project/common/services/project-version-store.service';
import {ProjectStore} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {PermissionService} from '../../../security/permissions/permission.service';
import PermissionsEnum = UserRoleCreateDTO.PermissionsEnum;

@Injectable({
  providedIn: 'root'
})
export class AssessmentAndDecisionStore {

  revertToStatus$: Observable<string | null>;

  constructor(private projectStore: ProjectStore,
              private projectVersionStore: ProjectVersionStore,
              private permissionService: PermissionService,
              private projectStatusService: ProjectStatusService) {
    this.revertToStatus$ = this.revertToStatus();
  }

  returnApplicationToApplicant(projectId: number): Observable<string> {
    return this.projectStatusService.returnApplicationToApplicant(projectId)
      .pipe(
        tap(() => this.projectStore.projectStatusChanged$.next()),
        tap(status => Log.info('Changed status for project', projectId, status))
      );
  }

  returnApplicationToApplicantForConditions(projectId: number): Observable<string> {
    return this.projectStatusService.handBackToApplicant(projectId)
      .pipe(
        tap(() => this.projectStore.projectStatusChanged$.next()),
        tap(status => Log.info('Changed status for project', projectId, status))
      );
  }

  returnApplicationToDraft(projectId: number): Observable<string> {
    return this.projectStatusService.startSecondStep(projectId)
      .pipe(
        tap(() => this.projectStore.projectStatusChanged$.next()),
        tap(status => Log.info('Changed status for project', projectId, status))
      );
  }

  revertApplicationDecision(projectId: number): Observable<string> {
    return this.projectStatusService.revertApplicationDecision(projectId)
      .pipe(
        tap(() => this.projectStore.projectStatusChanged$.next()),
        tap(status => Log.info('Changed status for project', projectId, status))
      );
  }

  private revertToStatus(): Observable<string | null> {
    return combineLatest([this.projectStore.currentVersionOfProject$, this.permissionService.permissionsChanged()])
      .pipe(
        switchMap(([project, perms]) =>
          perms.includes(PermissionsEnum.ProjectStatusDecisionRevert)
            ? this.projectStatusService.findPossibleDecisionRevertStatus(project.id)
            : of(null)
        ),
        tap(status => Log.info('Fetched revert status', status))
      );
  }
}
