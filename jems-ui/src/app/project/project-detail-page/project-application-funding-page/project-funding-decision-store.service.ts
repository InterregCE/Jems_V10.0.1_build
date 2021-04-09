import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {ApplicationActionInfoDTO, ProjectDetailDTO, ProjectStatusService} from '@cat/api';
import {tap} from 'rxjs/operators';
import {ProjectStore} from '../../project-application/containers/project-application-detail/services/project-store.service';
import {Log} from '../../../common/utils/log';

@Injectable()
export class ProjectFundingDecisionStore {

  project$: Observable<ProjectDetailDTO>;

  constructor(private projectStore: ProjectStore,
              private projectStatusService: ProjectStatusService) {
    this.project$ = this.projectStore.project$;
  }

  approveApplication(projectId: number, info: ApplicationActionInfoDTO): Observable<string> {
    return this.projectStatusService.approveApplication(projectId, info)
      .pipe(
        tap(status => this.projectStore.projectStatusChanged$.next()),
        tap(status => Log.info('Changed status for project', projectId, status))
      );
  }

  approveApplicationWithCondition(projectId: number, info: ApplicationActionInfoDTO): Observable<string> {
    return this.projectStatusService.approveApplicationWithCondition(projectId, info)
      .pipe(
        tap(status => this.projectStore.projectStatusChanged$.next()),
        tap(status => Log.info('Changed status for project', projectId, status))
      );
  }

  refuseApplication(projectId: number, info: ApplicationActionInfoDTO): Observable<string> {
    return this.projectStatusService.refuseApplication(projectId, info)
      .pipe(
        tap(status => this.projectStore.projectStatusChanged$.next()),
        tap(status => Log.info('Changed status for project', projectId, status))
      );
  }
}
