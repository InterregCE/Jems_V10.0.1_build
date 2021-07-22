import {ChangeDetectionStrategy, Component} from '@angular/core';
import {combineLatest} from 'rxjs';
import {ProjectStore} from '../../project-application-detail/services/project-store.service';
import {ProjectApplicationFormStore} from '../services/project-application-form-store.service';
import {ActivatedRoute} from '@angular/router';
import {map, mergeMap, tap} from 'rxjs/operators';
import {Log} from '@common/utils/log';
import {
  CallService,
  InputProjectData,
  OutputProgrammePrioritySimple,
  ProgrammePriorityDTO,
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

  private callObjectives$ = this.projectStore.project$
    .pipe(
      mergeMap(project => this.callService.getCallById(project.callSettings.callId)),
      map(call => call.objectives),
      tap(objectives => Log.info('Fetched objectives', this, objectives)),
      map(objectives => ({
        priorities: objectives
          .sort((a, b) => {
            const orderBool = a.code.toLocaleLowerCase() > b.code.toLocaleLowerCase();
            return orderBool ? 1 : -1;
          })
          .map(objective => ({title: objective.title, code: objective.code}) as OutputProgrammePrioritySimple),
        objectivesWithPolicies: this.getObjectivesWithPolicies(objectives)
      }))
    );

  details$ = combineLatest([
    this.projectStore.project$,
    this.projectStore.projectForm$,
    this.callObjectives$,
  ])
    .pipe(
      map(
        ([project, projectForm, callObjectives]) => ({project, projectForm, callObjectives})
      )
    );

  constructor(public projectStore: ProjectStore,
              private projectApplicationFormStore: ProjectApplicationFormStore,
              private projectService: ProjectService,
              private activatedRoute: ActivatedRoute,
              private callService: CallService) {
    this.projectApplicationFormStore.init(this.projectId);
  }

  private getObjectivesWithPolicies(objectives: ProgrammePriorityDTO[]): { [key: string]: InputProjectData.SpecificObjectiveEnum[] } {
    const objectivesWithPolicies: any = {};
    objectives.forEach(objective =>
      objectivesWithPolicies[objective.code] =
        objective.specificObjectives.map(priority => priority.programmeObjectivePolicy));
    return objectivesWithPolicies;
  }

}
