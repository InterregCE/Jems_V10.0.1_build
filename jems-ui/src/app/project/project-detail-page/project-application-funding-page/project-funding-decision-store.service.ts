import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {ApplicationActionInfoDTO, ProjectDetailDTO, ProjectStatusDTO, ProjectStatusService} from '@cat/api';
import {map, tap} from 'rxjs/operators';
import {ProjectStore} from '../../project-application/containers/project-application-detail/services/project-store.service';
import {Log} from '@common/utils/log';

@Injectable()
export class ProjectFundingDecisionStore {
  private static readonly LOG_INFO_CHANGE_STATUS_PROJECT = 'Changed status for project';

  currentVersionOfProject$: Observable<ProjectDetailDTO>;
  currentVersionOfProjectTitle$: Observable<string>;

  constructor(private projectStore: ProjectStore,
              private projectStatusService: ProjectStatusService) {
    this.currentVersionOfProject$ = this.projectStore.currentVersionOfProject$;
    this.currentVersionOfProjectTitle$ = this.projectStore.currentVersionOfProjectTitle$;
  }

  finalFundingDecision(step: number | undefined): Observable<ProjectStatusDTO> {
    return this.projectStore.projectDecisions(step)
      .pipe(
        map(decisions => decisions?.finalFundingDecision)
      );
  }

  preFundingDecision(step: number | undefined): Observable<ProjectStatusDTO> {
    return this.projectStore.projectDecisions(step)
      .pipe(
        map(decisions => decisions?.preFundingDecision)
      );
  }

  eligibilityDecisionDate(step: number | undefined): Observable<string> {
    return this.projectStore.projectDecisions(step)
      .pipe(
        map(decisions => decisions.eligibilityDecision.decisionDate)
      );
  }

  approveApplication(projectId: number, info: ApplicationActionInfoDTO): Observable<string> {
    return this.projectStatusService.approveApplication(projectId, info)
      .pipe(
        tap(status => this.projectStore.projectStatusChanged$.next()),
        tap(status => Log.info(ProjectFundingDecisionStore.LOG_INFO_CHANGE_STATUS_PROJECT, projectId, status))
      );
  }

  approveApplicationWithCondition(projectId: number, info: ApplicationActionInfoDTO): Observable<string> {
    return this.projectStatusService.approveApplicationWithCondition(projectId, info)
      .pipe(
        tap(status => this.projectStore.projectStatusChanged$.next()),
        tap(status => Log.info(ProjectFundingDecisionStore.LOG_INFO_CHANGE_STATUS_PROJECT, projectId, status))
      );
  }

  refuseApplication(projectId: number, info: ApplicationActionInfoDTO): Observable<string> {
    return this.projectStatusService.refuseApplication(projectId, info)
      .pipe(
        tap(status => this.projectStore.projectStatusChanged$.next()),
        tap(status => Log.info(ProjectFundingDecisionStore.LOG_INFO_CHANGE_STATUS_PROJECT, projectId, status))
      );
  }
}
