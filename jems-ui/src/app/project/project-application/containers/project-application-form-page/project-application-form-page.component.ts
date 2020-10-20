import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {catchError, mergeMap, map, takeUntil, tap} from 'rxjs/operators';
import {ProjectStore} from '../project-application-detail/services/project-store.service';
import {ActivatedRoute} from '@angular/router';
import {
  CallService,
  InputProjectData,
  OutputCallProgrammePriority,
  OutputProject,
  OutputProjectStatus,
  ProjectService
} from '@cat/api';
import {BaseComponent} from '@common/components/base-component';
import {combineLatest, merge, Subject} from 'rxjs';
import {Log} from '../../../../common/utils/log';
import {I18nValidationError} from '@common/validation/i18n-validation-error';
import {HttpErrorResponse} from '@angular/common/http';
import {Permission} from 'src/app/security/permissions/permission';
import {ProjectApplicationFormSidenavService} from './services/project-application-form-sidenav.service';
import {ProjectApplicationFormStore} from './services/project-application-form-store.service';

@Component({
  selector: 'app-project-application-form-page',
  templateUrl: './project-application-form-page.component.html',
  styleUrls: ['./project-application-form-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormPageComponent extends BaseComponent implements OnInit {
  Permission = Permission;
  OutputProjectStatus = OutputProjectStatus;
  projectId = this.activatedRoute?.snapshot?.params?.projectId;

  saveError$ = new Subject<I18nValidationError | null>();
  saveSuccess$ = new Subject<boolean>();
  updateProjectData$ = new Subject<InputProjectData>();

  constructor(private projectStore: ProjectStore,
              private projectApplicationFormStore: ProjectApplicationFormStore,
              private projectService: ProjectService,
              private activatedRoute: ActivatedRoute,
              private projectApplicationFormSidenavService: ProjectApplicationFormSidenavService,
              private callService: CallService) {
    super();
  }


  private fetchObjectives$ = new Subject<OutputProject>()
  private fetchStrategies$ = new Subject<OutputProject>()
  private updatedProjectData$ = this.updateProjectData$
    .pipe(
      mergeMap((data) => this.projectService.updateProjectData(this.projectId, data)),
      tap(() => this.saveSuccess$.next(true)),
      tap(() => this.saveError$.next(null)),
      tap(saved => Log.info('Updated project data:', this, saved)),
      catchError((error: HttpErrorResponse) => {
        this.saveError$.next(error.error);
        throw error;
      })
    );

  private callObjectives$ = this.fetchObjectives$
    .pipe(
      mergeMap(project => this.callService.getCallObjectives(project.call.id)),
      tap(objectives => Log.info('Fetched objectives', this, objectives)),
      map(objectives => ({
        priorities: objectives
          .sort((a, b) => {
            const orderBool = a.code.toLocaleLowerCase() > b.code.toLocaleLowerCase();
            return orderBool ? 1 : -1;
          })
          .map(objective => objective.code + ' - ' + objective.title),
        objectivesWithPolicies: this.getObjectivesWithPolicies(objectives)
      }))
    );

  private callStrategies$ = this.fetchStrategies$
    .pipe(
      mergeMap(project => this.callService.getCallById(project.call.id)),
      tap(call => Log.info('Fetched strategies from call', this, call.strategies)),
      map(call => ({
        strategies: call.strategies
      }))
    );

  private projectDetails$ = merge(
    this.projectStore.getProject(),
    this.updatedProjectData$,
  )
    .pipe(
      takeUntil(this.destroyed$),
      tap(project => this.projectApplicationFormSidenavService.setAcronym(project.acronym)),
      tap(project => this.fetchObjectives$.next(project)),
      tap(project => this.fetchStrategies$.next(project)),
      map(project => ({
        project,
        editable: project.projectStatus.status === OutputProjectStatus.StatusEnum.DRAFT
          || project.projectStatus.status === OutputProjectStatus.StatusEnum.RETURNEDTOAPPLICANT
      })),
    );

  details$ = combineLatest([
    this.projectDetails$,
    this.callObjectives$,
    this.callStrategies$
  ])
    .pipe(
      map(
        ([projectDetails, callObjectives, callStrategies]) => ({projectDetails, callObjectives, callStrategies})
      )
    );

  ngOnInit() {
    this.projectStore.init(this.projectId);
    this.projectApplicationFormStore.init(this.projectId);
    this.projectApplicationFormSidenavService.init(this.destroyed$, this.projectId);
  }

  private getObjectivesWithPolicies(objectives: OutputCallProgrammePriority[]): { [key: string]: InputProjectData.SpecificObjectiveEnum[] } {
    const objectivesWithPolicies: any = {};
    objectives.forEach(objective =>
      objectivesWithPolicies[objective.code + ' - ' + objective.title] =
        objective.programmePriorityPolicies.map(priority => priority.programmeObjectivePolicy));
    return objectivesWithPolicies;
  }
}
