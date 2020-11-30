import {ChangeDetectionStrategy, Component} from '@angular/core';
import {Permission} from '../../../../../security/permissions/permission';
import {combineLatest, merge, Observable, Subject} from 'rxjs';
import {I18nValidationError} from '@common/validation/i18n-validation-error';
import {catchError, mergeMap, map, tap} from 'rxjs/operators';
import {Log} from '../../../../../common/utils/log';
import {HttpErrorResponse} from '@angular/common/http';
import {InputProjectOverallObjective, ProjectDescriptionService} from '@cat/api';
import {ProjectApplicationFormStore} from '../services/project-application-form-store.service';
import {ProjectStore} from '../../project-application-detail/services/project-store.service';
import {ActivatedRoute} from '@angular/router';
import {ProjectApplicationFormSidenavService} from '../services/project-application-form-sidenav.service';

@Component({
  selector: 'app-project-application-form-overall-objective-section',
  templateUrl: './project-application-form-overall-objective-section.component.html',
  styleUrls: ['./project-application-form-overall-objective-section.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormOverallObjectiveSectionComponent {
  Permission = Permission;

  projectId = this.activatedRoute?.snapshot?.params?.projectId;

  saveError$ = new Subject<I18nValidationError | null>();
  saveSuccess$ = new Subject<boolean>();
  updateProjectDescription$ = new Subject<InputProjectOverallObjective>();

  private savedDescription$ = this.projectApplicationFormStore.getProjectDescription()
    .pipe(
      map(project => project.projectOverallObjective)
    );

  private updatedProjectDescription$ = this.updateProjectDescription$
    .pipe(
      mergeMap((data) =>
        this.projectDescriptionService.updateProjectOverallObjective(this.projectId, data)
      ),
      tap(() => this.saveSuccess$.next(true)),
      tap(() => this.saveError$.next(null)),
      tap(saved => Log.info('Updated project overall objective:', this, saved)),
      catchError((error: HttpErrorResponse) => {
        this.saveError$.next(error.error);
        throw error;
      })
    );

  details$ = combineLatest([
    merge(this.savedDescription$, this.updatedProjectDescription$),
    this.projectStore.getProject()
  ])
    .pipe(
      map(([description, project]) => ({
        description,
        project
      })),
    );

  constructor(public projectStore: ProjectStore,
              private activatedRoute: ActivatedRoute,
              private projectApplicationFormSidenavService: ProjectApplicationFormSidenavService,
              private projectDescriptionService: ProjectDescriptionService,
              private projectApplicationFormStore: ProjectApplicationFormStore) {
    this.projectStore.init(this.projectId);
    this.projectApplicationFormStore.init(this.projectId);
  }
}
