import {ChangeDetectionStrategy, Component} from '@angular/core';
import {combineLatest, Subject} from 'rxjs';
import {map, mergeMap, tap} from 'rxjs/operators';
import {Log} from '@common/utils/log';
import {CallService, InputProjectRelevance, ProjectDescriptionService} from '@cat/api';
import {ProjectApplicationFormStore} from '../services/project-application-form-store.service';
import {ProjectStore} from '../../project-application-detail/services/project-store.service';
import {ActivatedRoute} from '@angular/router';

@Component({
  selector: 'jems-project-application-form-project-relevance-and-context-section',
  templateUrl: './project-application-form-project-relevance-and-context-section.component.html',
  styleUrls: ['./project-application-form-project-relevance-and-context-section.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormProjectRelevanceAndContextSectionComponent {
  projectId = this.activatedRoute?.snapshot?.params?.projectId;
  projectRelevance$ = this.projectApplicationFormStore.projectDescription$
    .pipe(
      map(project => project.projectRelevance)
    );

  private callStrategies$ = this.projectStore.project$
    .pipe(
      mergeMap(project => this.callService.getCallById(project.callSettings.callId)),
      tap(call => Log.info('Fetched strategies from call', this, call.strategies)),
      map(call => call.strategies)
    );


  details$ = combineLatest([
    this.projectRelevance$,
    this.projectStore.project$,
    this.callStrategies$
  ])
    .pipe(
      map(([relevance, project, callStrategies]) => ({
        relevance,
        project,
        callStrategies
      })),
    );

  constructor(public projectStore: ProjectStore,
              private activatedRoute: ActivatedRoute,
              private callService: CallService,
              private projectDescriptionService: ProjectDescriptionService,
              private projectApplicationFormStore: ProjectApplicationFormStore) {
    this.projectApplicationFormStore.init(this.projectId);
  }
}
