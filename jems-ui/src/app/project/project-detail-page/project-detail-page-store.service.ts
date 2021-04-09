import {Injectable} from '@angular/core';
import {ProjectStore} from '../project-application/containers/project-application-detail/services/project-store.service';
import {combineLatest, from, Observable} from 'rxjs';
import {ProjectDetailDTO, ProjectStatusDTO, ProjectStatusService} from '@cat/api';
import {Permission} from '../../security/permissions/permission';
import {map, switchMap, tap} from 'rxjs/operators';
import {PermissionService} from '../../security/permissions/permission.service';
import {Log} from '../../common/utils/log';

@Injectable()
export class ProjectDetailPageStore {

  project$: Observable<ProjectDetailDTO>;
  assessmentFilesVisible$: Observable<boolean>;
  revertToStatus$: Observable<string>;

  constructor(private projectStore: ProjectStore,
              private permissionService: PermissionService,
              private projectStatusService: ProjectStatusService) {
    this.project$ = this.projectStore.project$;
    this.assessmentFilesVisible$ = this.assessmentFilesVisible();
    this.revertToStatus$ = this.revertToStatus();
  }

  private assessmentFilesVisible(): Observable<boolean> {
    return combineLatest([
      this.projectStore.project$,
      this.permissionService.permissionsChanged()
    ])
      .pipe(
        map(([project, permissions]) =>
          permissions[0] !== Permission.APPLICANT_USER
          && project.projectStatus.status !== ProjectStatusDTO.StatusEnum.DRAFT
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

  revertApplicationDecision(projectId: number): Observable<string> {
    return this.projectStatusService.revertApplicationDecision(projectId)
      .pipe(
        tap(status => this.projectStore.projectStatusChanged$.next()),
        tap(status => Log.info('Changed status for project', projectId, status))
      );
  }

  private revertToStatus(): Observable<string> {
    return from(this.project$)
      .pipe(
        switchMap(project => this.projectStatusService.findPossibleDecisionRevertStatus(project.id)),
        tap(status => Log.info('Fetched revert status', status))
      );
  }
}
