import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {OutputProjectEligibilityAssessment, ProjectDetailDTO} from '@cat/api';
import {map} from 'rxjs/operators';
import {ProjectStore} from '../../project-application/containers/project-application-detail/services/project-store.service';

@Injectable()
export class ProjectEligibilityCheckPageStore {

  project$: Observable<ProjectDetailDTO>;
  eligibilityAssessment$: Observable<OutputProjectEligibilityAssessment>;

  constructor(private projectStore: ProjectStore) {
    this.project$ = this.projectStore.project$;
    this.eligibilityAssessment$ = this.projectStore.projectDecisions$
      .pipe(
        map(decisions => decisions?.eligibilityAssessment)
      );
  }
}
