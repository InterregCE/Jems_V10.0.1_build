import {ChangeDetectionStrategy, Component} from '@angular/core';
import {combineLatest} from 'rxjs';
import {ProjectStore} from '../../project-application-detail/services/project-store.service';
import {ProjectApplicationFormStore} from '../services/project-application-form-store.service';
import {ActivatedRoute} from '@angular/router';
import {ProjectApplicationFormSidenavService} from '../services/project-application-form-sidenav.service';
import {map, mergeMap, tap} from 'rxjs/operators';
import {Log} from '../../../../../common/utils/log';
import {
  CallService,
  InputProjectData,
  OutputCallProgrammePriority,
  OutputProgrammePrioritySimple,
  ProjectService
} from '@cat/api';

@Component({
  selector: 'app-project-application-form-identification-page',
  templateUrl: './project-application-form-identification-page.component.html',
  styleUrls: ['./project-application-form-identification-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormIdentificationPageComponent {
  projectId = this.activatedRoute?.snapshot?.params?.projectId;

  private callObjectives$ = this.projectStore.getProject()
    .pipe(
      mergeMap(project => this.callService.getCallObjectives(project.callSettings.callId)),
      tap(objectives => Log.info('Fetched objectives', this, objectives)),
      map(objectives => ({
        priorities: objectives
          .sort((a, b) => {
            const orderBool = a.code.toLocaleLowerCase() > b.code.toLocaleLowerCase();
            return orderBool ? 1 : -1;
          })
          .map(objective => ({ title: objective.title, code: objective.code }) as OutputProgrammePrioritySimple),
        objectivesWithPolicies: this.getObjectivesWithPolicies(objectives)
      }))
    );

  details$ = combineLatest([
    this.projectStore.getProject(),
    this.callObjectives$,
  ])
    .pipe(
      map(
        ([project, callObjectives]) => ({project, callObjectives})
      )
    );

  constructor(public projectStore: ProjectStore,
              private projectApplicationFormStore: ProjectApplicationFormStore,
              private projectService: ProjectService,
              private activatedRoute: ActivatedRoute,
              private projectApplicationFormSidenavService: ProjectApplicationFormSidenavService,
              private callService: CallService) {
    this.projectApplicationFormStore.init(this.projectId);
  }

  private getObjectivesWithPolicies(objectives: OutputCallProgrammePriority[]): { [key: string]: InputProjectData.SpecificObjectiveEnum[] } {
    const objectivesWithPolicies: any = {};
    objectives.forEach(objective =>
      objectivesWithPolicies[objective.code] =
        objective.programmePriorityPolicies.map(priority => priority.programmeObjectivePolicy));
    return objectivesWithPolicies;
  }

}
