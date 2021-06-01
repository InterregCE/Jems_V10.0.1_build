import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {OutputProjectEligibilityAssessment, ProjectDetailDTO} from '@cat/api';
import {map} from 'rxjs/operators';
import {ProjectStore} from '../../project-application/containers/project-application-detail/services/project-store.service';

@Injectable()
export class ProjectEligibilityCheckPageStore {

  project$: Observable<ProjectDetailDTO>;

  constructor(private projectStore: ProjectStore) {
    this.project$ = this.projectStore.project$;
  }

  eligibilityAssessment(step: number | undefined): Observable<OutputProjectEligibilityAssessment> {
    return this.projectStore.projectDecisions(step)
      .pipe(
        map(decisions => decisions?.eligibilityAssessment)
      );
  }
}
