import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {ProjectStatusService} from '@cat/api';
import {shareReplay, tap} from 'rxjs/operators';
import {Log} from '@common/utils/log';
import {ProjectStore} from '@project/project-application/containers/project-application-detail/services/project-store.service';

@Injectable({
  providedIn: 'root'
})
export class ContractMonitoringStore {


  constructor(private projectStore: ProjectStore,
              private projectStatusService: ProjectStatusService) {
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
