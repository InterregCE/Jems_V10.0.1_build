import {ChangeDetectionStrategy, Component, Input, OnInit} from '@angular/core';
import {BaseComponent} from '@common/components/base-component';
import {merge, Observable, Subject} from 'rxjs';
import {ProjectDescriptionService, InputProjectLongTermPlans} from '@cat/api';
import {I18nValidationError} from '@common/validation/i18n-validation-error';
import {catchError, flatMap, map, tap} from 'rxjs/operators';
import {Log} from '../../../../../common/utils/log';
import {HttpErrorResponse} from '@angular/common/http';
import {ProjectApplicationFormStore} from '../services/project-application-form-store.service';

@Component({
  selector: 'app-project-application-form-future-plans-section',
  templateUrl: './project-application-form-future-plans-section.component.html',
  styleUrls: ['./project-application-form-future-plans-section.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormFuturePlansSectionComponent extends BaseComponent implements OnInit {

  @Input()
  projectId: number;
  @Input()
  editable: boolean;

  saveError$ = new Subject<I18nValidationError | null>();
  saveSuccess$ = new Subject<boolean>();
  updateProjectDescription$ = new Subject<InputProjectLongTermPlans>();
  projectDescriptionDetails$: Observable<any>;

  constructor(private projectDescriptionService: ProjectDescriptionService,
              private projectApplicationFormStore: ProjectApplicationFormStore) {
    super();
  }

  ngOnInit(): void {
    const savedDescription$ = this.projectApplicationFormStore.getProjectDescription()
      .pipe(
        map(project => project.projectLongTermPlans)
      )

    const updatedProjectDescription$ = this.updateProjectDescription$
      .pipe(
        flatMap((data) => this.projectDescriptionService.updateProjectLongTermPlans(this.projectId, data)),
        tap(() => this.saveSuccess$.next(true)),
        tap(() => this.saveError$.next(null)),
        tap(saved => Log.info('Updated project long-term plans:', this, saved)),
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
