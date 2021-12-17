import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {OutputProjectQualityAssessment, ProjectDetailDTO} from '@cat/api';
import {map} from 'rxjs/operators';
import {ProjectStore} from '../../project-application/containers/project-application-detail/services/project-store.service';

@Injectable()
export class ProjectQualityCheckPageStore {

  currentVersionOfProject$: Observable<ProjectDetailDTO>;
  currentVersionOfProjectTitle$: Observable<string>;

  constructor(private projectStore: ProjectStore) {
    this.currentVersionOfProject$ = this.projectStore.currentVersionOfProject$;
    this.currentVersionOfProjectTitle$ = this.projectStore.currentVersionOfProjectTitle$;
  }

  qualityAssessment(step: number | undefined): Observable<OutputProjectQualityAssessment> {
    return this.projectStore.projectDecisions(step)
      .pipe(
        map(decisions => decisions?.qualityAssessment)
      );
  }

}
