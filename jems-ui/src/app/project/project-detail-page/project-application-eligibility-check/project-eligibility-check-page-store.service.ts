import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {
  OutputProjectEligibilityAssessment,
  ProjectAssessmentEligibilityDTO,
  ProjectDetailDTO,
  ProjectStatusService
} from '@cat/api';
import {map, switchMap, take, tap} from 'rxjs/operators';
import {ProjectStore} from '../../project-application/containers/project-application-detail/services/project-store.service';
import {RoutingService} from '@common/services/routing.service';
import {Log} from '@common/utils/log';

@Injectable()
export class ProjectEligibilityCheckPageStore {

  currentVersionOfProject$: Observable<ProjectDetailDTO>;
  currentVersionOfProjectTitle$: Observable<string>;

  constructor(private projectStore: ProjectStore,
              private projectStatusService: ProjectStatusService,
              private routingService: RoutingService) {
    this.currentVersionOfProject$ = this.projectStore.currentVersionOfProject$;
    this.currentVersionOfProjectTitle$ = this.projectStore.currentVersionOfProjectTitle$;
  }

  eligibilityAssessment(step: number | undefined): Observable<OutputProjectEligibilityAssessment> {
    return this.projectStore.projectDecisions(step)
      .pipe(
        map(decisions => decisions?.eligibilityAssessment)
      );
  }

  setEligibilityAssessment(assessment: ProjectAssessmentEligibilityDTO): Observable<ProjectDetailDTO> {
    return this.projectStore.projectId$
      .pipe(
        take(1),
        switchMap(id => this.projectStatusService.setEligibilityAssessment(id, assessment)),
        tap(() => this.projectStore.projectStatusChanged$.next()),
        tap(saved => Log.info('Updated project eligibility assessment:', this, saved)),
        tap(saved => this.routingService.navigate(['app', 'project', 'detail', saved.id, 'assessmentAndDecision']))
      );
  }

}
