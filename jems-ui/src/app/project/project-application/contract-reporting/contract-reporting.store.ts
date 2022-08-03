import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {ProjectDetailFormDTO} from '@cat/api';
import {
  ProjectStore
} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {switchMap} from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class ContractReportingStore {
  projectId$: Observable<number>;
  projectForm$: Observable<ProjectDetailFormDTO>;

  constructor(private projectStore: ProjectStore) {
    this.projectId$ = this.projectStore.projectId$;
    this.projectForm$ = this.projectStore.projectForm$;
  }

}
