import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {
  OutputProjectQualityAssessment,
  ProjectAssessmentQualityDTO,
  ProjectDetailDTO,
  ProjectStatusService
} from '@cat/api';
import {map, switchMap, take, tap} from 'rxjs/operators';
import {ProjectStore} from '../../project-application/containers/project-application-detail/services/project-store.service';
import {Log} from '@common/utils/log';
import {RoutingService} from '@common/services/routing.service';

@Injectable()
export class ProjectQualityCheckPageStore {

  currentVersionOfProject$: Observable<ProjectDetailDTO>;
  currentVersionOfProjectTitle$: Observable<string>;

  constructor(private projectStore: ProjectStore,
              private projectStatusService: ProjectStatusService,
              private routingService: RoutingService) {
    this.currentVersionOfProject$ = this.projectStore.currentVersionOfProject$;
    this.currentVersionOfProjectTitle$ = this.projectStore.currentVersionOfProjectTitle$;
  }

  setQualityAssessment(assessment: ProjectAssessmentQualityDTO): Observable<ProjectDetailDTO> {
    return this.projectStore.projectId$
      .pipe(
        take(1),
        switchMap(id=> this.projectStatusService.setQualityAssessment(id, assessment)),
        tap(() => this.projectStore.projectStatusChanged$.next()),
        tap(saved => Log.info('Updated project quality assessment:', this, saved)),
        tap(saved => this.routingService.navigate(['app', 'project', 'detail', saved.id, 'assessmentAndDecision']))
    );
  }

  qualityAssessment(step: number | undefined): Observable<OutputProjectQualityAssessment> {
    return this.projectStore.projectDecisions(step)
      .pipe(
        map(decisions => decisions?.qualityAssessment)
      );
  }

}
