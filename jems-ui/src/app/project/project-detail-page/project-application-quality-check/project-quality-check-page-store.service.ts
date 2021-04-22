import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {OutputProjectQualityAssessment, ProjectDetailDTO} from '@cat/api';
import {map} from 'rxjs/operators';
import {ProjectStore} from '../../project-application/containers/project-application-detail/services/project-store.service';

@Injectable()
export class ProjectQualityCheckPageStore {

  project$: Observable<ProjectDetailDTO>;
  qualityAssessment$: Observable<OutputProjectQualityAssessment>;

  constructor(private projectStore: ProjectStore) {
    this.project$ = this.projectStore.project$;
    this.qualityAssessment$ = this.projectStore.projectDecisions$
      .pipe(
        map(decisions => decisions?.qualityAssessment)
      );
  }
}
