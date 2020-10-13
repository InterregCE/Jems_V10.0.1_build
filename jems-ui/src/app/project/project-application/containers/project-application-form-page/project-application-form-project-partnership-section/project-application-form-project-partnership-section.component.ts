import {ChangeDetectionStrategy, Component, Input, OnInit} from '@angular/core';
import {BaseComponent} from '@common/components/base-component';
import {merge, Observable, Subject} from 'rxjs';
import {I18nValidationError} from '@common/validation/i18n-validation-error';
import {catchError, flatMap, map, tap} from 'rxjs/operators';
import {Log} from '../../../../../common/utils/log';
import {HttpErrorResponse} from '@angular/common/http';
import {InputProjectPartnership, ProjectDescriptionService} from '@cat/api';
import {Permission} from 'src/app/security/permissions/permission';
import {ProjectApplicationFormStore} from '../services/project-application-form-store.service';

@Component({
  selector: 'app-project-application-form-project-partnership-section',
  templateUrl: './project-application-form-project-partnership-section.component.html',
  styleUrls: ['./project-application-form-project-partnership-section.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormProjectPartnershipSectionComponent extends BaseComponent implements OnInit {
  Permission = Permission;

  @Input()
  projectId: number;
  @Input()
  editable: boolean;

  saveError$ = new Subject<I18nValidationError | null>();
  saveSuccess$ = new Subject<boolean>();
  updateProjectDescription$ = new Subject<InputProjectPartnership>();
  projectDescriptionDetails$: Observable<any>;

  constructor(private projectDescriptionService: ProjectDescriptionService,
              private projectApplicationFormStore: ProjectApplicationFormStore) {
    super();
  }

  ngOnInit(): void {
    const savedDescription$ = this.projectApplicationFormStore.getProjectDescription()
      .pipe(
        map(project => ({partnership: project.projectPartnership}))
      )

    const updatedProjectDescription$ = this.updateProjectDescription$
      .pipe(
        flatMap((data) => this.projectDescriptionService.updateProjectPartnership(this.projectId, data)),
        tap(() => this.saveSuccess$.next(true)),
        tap(() => this.saveError$.next(null)),
        tap(saved => Log.info('Updated project partnership:', this, saved)),
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
