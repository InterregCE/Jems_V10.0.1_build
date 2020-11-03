import {ChangeDetectionStrategy, Component} from '@angular/core';
import {combineLatest, merge, Subject} from 'rxjs';
import {I18nValidationError} from '@common/validation/i18n-validation-error';
import {catchError, mergeMap, map, tap} from 'rxjs/operators';
import {Log} from '../../../../../common/utils/log';
import {HttpErrorResponse} from '@angular/common/http';
import {InputProjectRelevance, ProjectDescriptionService, CallService} from '@cat/api';
import {ProjectApplicationFormStore} from '../services/project-application-form-store.service';
import {ProjectStore} from '../../project-application-detail/services/project-store.service';
import {ActivatedRoute} from '@angular/router';
import {ProjectApplicationFormSidenavService} from '../services/project-application-form-sidenav.service';

@Component({
  selector: 'app-project-application-form-project-relevance-and-context-section',
  templateUrl: './project-application-form-project-relevance-and-context-section.component.html',
  styleUrls: ['./project-application-form-project-relevance-and-context-section.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormProjectRelevanceAndContextSectionComponent {
  projectId = this.activatedRoute?.snapshot?.params?.projectId;

  saveError$ = new Subject<I18nValidationError | null>();
  saveSuccess$ = new Subject<boolean>();
  updateProjectRelevance$ = new Subject<InputProjectRelevance>();
  deleteEntriesFromTables$ = new Subject<InputProjectRelevance>();

  private savedDescription$ = this.projectApplicationFormStore.getProjectDescription()
    .pipe(
      map(project => project.projectRelevance)
    )

  private updatedProjectRelevance$ = this.updateProjectRelevance$
    .pipe(
      mergeMap((data) => this.projectDescriptionService.updateProjectRelevance(this.projectId, data)),
      tap(() => this.saveSuccess$.next(true)),
      tap(() => this.saveError$.next(null)),
      tap(saved => Log.info('Updated project relevance and context:', this, saved)),
      catchError((error: HttpErrorResponse) => {
        this.saveError$.next(error.error);
        throw error;
      })
    );

  private deletedEntriesFromTables$ = this.deleteEntriesFromTables$
    .pipe(
      mergeMap((data) => this.projectDescriptionService.updateProjectRelevance(this.projectId, data)),
      tap(() => this.saveError$.next(null)),
      tap(saved => Log.info('Deleted entries from project relevance tables:', this, saved)),
      catchError((error: HttpErrorResponse) => {
        this.saveError$.next(error.error);
        throw error;
      })
    );

  private callStrategies$ = this.projectStore.getProject()
    .pipe(
      mergeMap(project => this.callService.getCallById(project.call.id)),
      tap(call => Log.info('Fetched strategies from call', this, call.strategies)),
      map(call => call.strategies)
    );


  details$ = combineLatest([
    merge(this.savedDescription$, this.updatedProjectRelevance$, this.deletedEntriesFromTables$),
    this.projectStore.getProject(),
    this.callStrategies$
  ])
    .pipe(
      map(([relevance, project, strategies]) => ({
        relevance,
        project,
        strategies
      })),
    );

  constructor(public projectStore: ProjectStore,
              private activatedRoute: ActivatedRoute,
              private callService: CallService,
              private projectApplicationFormSidenavService: ProjectApplicationFormSidenavService,
              private projectDescriptionService: ProjectDescriptionService,
              private projectApplicationFormStore: ProjectApplicationFormStore) {
    this.projectStore.init(this.projectId);
    this.projectApplicationFormStore.init(this.projectId);
  }
}
