import {ChangeDetectionStrategy, Component, Input, OnInit} from '@angular/core';
import {Permission} from '../../../../../security/permissions/permission';
import {merge, Observable, Subject} from 'rxjs';
import {I18nValidationError} from '@common/validation/i18n-validation-error';
import {catchError, flatMap, map, tap} from 'rxjs/operators';
import {Log} from '../../../../../common/utils/log';
import {HttpErrorResponse} from '@angular/common/http';
import {InputProjectOverallObjective, ProjectDescriptionService, OutputProject} from '@cat/api';
import {BaseComponent} from '@common/components/base-component';

@Component({
  selector: 'app-project-application-form-overall-objective-section',
  templateUrl: './project-application-form-overall-objective-section.component.html',
  styleUrls: ['./project-application-form-overall-objective-section.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormOverallObjectiveSectionComponent extends BaseComponent implements OnInit {
  Permission = Permission;

  @Input()
  projectId: number;
  @Input()
  editable: boolean;
  @Input()
  project: OutputProject;

  saveError$ = new Subject<I18nValidationError | null>();
  saveSuccess$ = new Subject<boolean>();
  updateProjectDescription$ = new Subject<InputProjectOverallObjective>();
  projectDescriptionDetails$: Observable<any>;

  constructor(private projectDescriptionService: ProjectDescriptionService) {
    super();
  }

  ngOnInit(): void {
    const savedDescription$ = this.projectDescriptionService.getProjectDescription(this.projectId)
      .pipe(
        map(project => {return {overallObjective: project.projectOverallObjective} as InputProjectOverallObjective})
      )

    const updatedProjectDescription$ = this.updateProjectDescription$
      .pipe(
        flatMap((data) => this.projectDescriptionService.updateProjectOverallObjective(this.projectId, data)),
        tap(() => this.saveSuccess$.next(true)),
        tap(() => this.saveError$.next(null)),
        tap(saved => Log.info('Updated project overall objective:', this, saved)),
        catchError((error: HttpErrorResponse) => {
          this.saveError$.next(error.error);
          throw error;
        })
      );

    this.projectDescriptionDetails$ = merge(savedDescription$, updatedProjectDescription$)
      .pipe(
        map(project => ({
          project,
          editable: this.editable
        })),
      );
  }
}
