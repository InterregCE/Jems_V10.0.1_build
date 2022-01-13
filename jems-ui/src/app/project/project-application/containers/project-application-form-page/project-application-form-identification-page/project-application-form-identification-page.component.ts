import {ChangeDetectionStrategy, Component} from '@angular/core';
import {combineLatest} from 'rxjs';
import {ProjectStore} from '../../project-application-detail/services/project-store.service';
import {ProjectApplicationFormStore} from '../services/project-application-form-store.service';
import {ActivatedRoute} from '@angular/router';
import {map, mergeMap, tap} from 'rxjs/operators';
import {Log} from '@common/utils/log';
import {
  CallService,
  OutputProgrammePrioritySimple,
  ProgrammePriorityDTO,
  ProgrammeSpecificObjectiveDTO,
  ProjectPartnerService,
  ProjectService
} from '@cat/api';
import {ProjectPartnerStore} from '@project/project-application/containers/project-application-form-page/services/project-partner-store.service';

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
    this.partnerStore.leadPartner$,
  ])
    .pipe(
      map(
        ([project, projectForm, callObjectives, leadPartner]) => ({project, projectForm, callObjectives, leadPartner})
      )
    );

  constructor(public projectStore: ProjectStore,
              private projectApplicationFormStore: ProjectApplicationFormStore,
              private projectService: ProjectService,
              private activatedRoute: ActivatedRoute,
              private partnerStore: ProjectPartnerStore,
              private partnerService: ProjectPartnerService,
              private callService: CallService) {
    this.projectApplicationFormStore.init(this.projectId);
  }

  private getObjectivesWithPolicies(objectives: ProgrammePriorityDTO[]): { [key: string]: ProgrammeSpecificObjectiveDTO[] } {
    const objectivesWithPolicies: { [key: string]: ProgrammeSpecificObjectiveDTO[] } = {};
    objectives.forEach(objective =>
      objectivesWithPolicies[objective.code] =
        objective.specificObjectives.map(priority => priority));
    return objectivesWithPolicies;
  }

}
