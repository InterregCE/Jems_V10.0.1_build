import {Injectable} from '@angular/core';
import {ProjectStore} from '../project-application/containers/project-application-detail/services/project-store.service';
import {combineLatest, Observable, of} from 'rxjs';
import {ProjectDetailDTO, ProjectStatusService} from '@cat/api';
import {Permission} from '../../security/permissions/permission';
import {map, switchMap, tap} from 'rxjs/operators';
import {PermissionService} from '../../security/permissions/permission.service';
import {Log} from '../../common/utils/log';
import {ProjectUtil} from '../project-util';
import {SecurityService} from '../../security/security.service';

@Injectable()
export class ProjectDetailPageStore {

  project$: Observable<ProjectDetailDTO>;
  assessmentFilesVisible$: Observable<boolean>;
  revertToStatus$: Observable<string | null>;
  callHasTwoSteps$: Observable<boolean>;
  isThisUserOwner$: Observable<boolean>;

  constructor(private projectStore: ProjectStore,
              private permissionService: PermissionService,
              private projectStatusService: ProjectStatusService,
              private securityService: SecurityService) {
    this.project$ = this.projectStore.project$;
    this.assessmentFilesVisible$ = this.assessmentFilesVisible();
    this.revertToStatus$ = this.revertToStatus();
    this.callHasTwoSteps$ = this.projectStore.callHasTwoSteps$;
    this.isThisUserOwner$ = this.isThisUserOwner();
  }

  private assessmentFilesVisible(): Observable<boolean> {
    return combineLatest([
      this.projectStore.project$,
      this.permissionService.permissionsChanged()
    ])
      .pipe(
        map(([project, permissions]) =>
          permissions[0] !== Permission.APPLICANT_USER && !ProjectUtil.isDraft(project)
        )
      );

  }

  submitApplication(projectId: number): Observable<string> {
    return this.projectStatusService.submitApplication(projectId)
      .pipe(
        tap(status => this.projectStore.projectStatusChanged$.next()),
        tap(status => Log.info('Changed status for project', projectId, status))
      );
  }

  returnApplicationToApplicant(projectId: number): Observable<string> {
    return this.projectStatusService.returnApplicationToApplicant(projectId)
      .pipe(
        tap(status => this.projectStore.projectStatusChanged$.next()),
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
        tap(status => this.projectStore.projectStatusChanged$.next()),
        tap(status => Log.info('Changed status for project', projectId, status))
      );
  }

  private revertToStatus(): Observable<string | null> {
    return combineLatest([this.project$, this.permissionService.permissionsChanged()])
      .pipe(
        switchMap(([project, perms]) =>
          perms.includes(Permission.ADMINISTRATOR)
            ? this.projectStatusService.findPossibleDecisionRevertStatus(project.id)
            : of(null)
        ),
        tap(status => Log.info('Fetched revert status', status))
      );
  }

  private isThisUserOwner(): Observable<boolean> {
    return combineLatest([this.project$, this.securityService.currentUser])
      .pipe(
        map(([project, currentUser]) => project?.applicant?.id === currentUser?.id)
      );
  }
}
