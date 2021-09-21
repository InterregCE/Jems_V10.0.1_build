import {Injectable} from '@angular/core';
import {ProjectStore} from '../../project-application/containers/project-application-detail/services/project-store.service';
import {Observable} from 'rxjs';
import {ApplicationActionInfoDTO, ProjectDetailDTO, ProjectStatusDTO, ProjectStatusService} from '@cat/api';
import {map, tap} from 'rxjs/operators';
import {Log} from '@common/utils/log';

@Injectable()
export class ProjectEligibilityDecisionStore {

  project$: Observable<ProjectDetailDTO>;

  constructor(private projectStore: ProjectStore,
              private projectStatusService: ProjectStatusService) {
    this.project$ = this.projectStore.project$;
  }

  eligibilityDecision(step: number | undefined): Observable<ProjectStatusDTO> {
    return this.projectStore.projectDecisions(step)
      .pipe(
        map(decisions => decisions?.eligibilityDecision)
      );
  }

  setApplicationAsEligible(projectId: number, info: ApplicationActionInfoDTO): Observable<string> {
    return this.projectStatusService.setApplicationAsEligible(projectId, info)
      .pipe(
        tap(status => this.projectStore.projectStatusChanged$.next()),
        tap(status => Log.info('Changed status for project', projectId, status))
      );
  }

  setApplicationAsIneligible(projectId: number, info: ApplicationActionInfoDTO): Observable<string> {
    return this.projectStatusService.setApplicationAsIneligible(projectId, info)
      .pipe(
        tap(status => this.projectStore.projectStatusChanged$.next()),
        tap(status => Log.info('Changed status for project', projectId, status))
      );
  }
}
