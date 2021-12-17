import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {OutputProjectEligibilityAssessment, ProjectDetailDTO} from '@cat/api';
import {map} from 'rxjs/operators';
import {ProjectStore} from '../../project-application/containers/project-application-detail/services/project-store.service';

@Injectable()
export class ProjectEligibilityCheckPageStore {

  currentVersionOfProject$: Observable<ProjectDetailDTO>;
  currentVersionOfProjectTitle$: Observable<string>;

  constructor(private projectStore: ProjectStore) {
    this.currentVersionOfProject$ = this.projectStore.currentVersionOfProject$;
    this.currentVersionOfProjectTitle$ = this.projectStore.currentVersionOfProjectTitle$;
  }

  eligibilityAssessment(step: number | undefined): Observable<OutputProjectEligibilityAssessment> {
    return this.projectStore.projectDecisions(step)
      .pipe(
        map(decisions => decisions?.eligibilityAssessment)
      );
  }
}
