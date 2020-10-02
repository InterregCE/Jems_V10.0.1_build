import {ChangeDetectionStrategy, Component, Input, OnInit} from '@angular/core';
import {BaseComponent} from '@common/components/base-component';
import {merge, Observable, Subject} from 'rxjs';
import {I18nValidationError} from '@common/validation/i18n-validation-error';
import {catchError, flatMap, map, takeUntil, tap} from 'rxjs/operators';
import {Log} from '../../../../../common/utils/log';
import {HttpErrorResponse} from '@angular/common/http';
import {ProjectDescriptionService, InputProjectManagement, OutputProjectManagement} from '@cat/api';

@Component({
  selector: 'app-project-application-form-management-section',
  templateUrl: './project-application-form-management-section.component.html',
  styleUrls: ['./project-application-form-management-section.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormManagementSectionComponent extends BaseComponent implements OnInit {

  @Input()
  projectId: number;
  @Input()
  editable: boolean;

  saveError$ = new Subject<I18nValidationError | null>();
  saveSuccess$ = new Subject<boolean>();
  updateProjectDescription$ = new Subject<InputProjectManagement>();
  projectDescriptionDetails$: Observable<any>;

  constructor(private projectDescriptionService: ProjectDescriptionService) {
    super();
  }

  ngOnInit(): void {
    const savedDescription$ = this.projectDescriptionService.getProjectDescription(this.projectId)
      .pipe(
        map(project => project.projectManagement)
      )

    const updatedProjectDescription$ = this.updateProjectDescription$
      .pipe(
        flatMap((data) => this.projectDescriptionService.updateProjectManagement(this.projectId, data)),
        tap(() => this.saveSuccess$.next(true)),
        tap(() => this.saveError$.next(null)),
        tap(saved => Log.info('Updated project management:', this, saved)),
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
