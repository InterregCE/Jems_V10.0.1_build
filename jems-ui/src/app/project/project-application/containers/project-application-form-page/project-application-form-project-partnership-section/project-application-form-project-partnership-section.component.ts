import {ChangeDetectionStrategy, Component} from '@angular/core';
import {merge, Subject} from 'rxjs';
import {I18nValidationError} from '@common/validation/i18n-validation-error';
import {catchError, map, mergeMap, tap} from 'rxjs/operators';
import {Log} from '../../../../../common/utils/log';
import {HttpErrorResponse} from '@angular/common/http';
import {InputProjectPartnership, ProjectDescriptionService} from '@cat/api';
import {ProjectApplicationFormStore} from '../services/project-application-form-store.service';
import {ProjectStore} from '../../project-application-detail/services/project-store.service';
import {ActivatedRoute} from '@angular/router';
import {ProjectApplicationFormSidenavService} from '../services/project-application-form-sidenav.service';

@Component({
  selector: 'app-project-application-form-project-partnership-section',
  templateUrl: './project-application-form-project-partnership-section.component.html',
  styleUrls: ['./project-application-form-project-partnership-section.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormProjectPartnershipSectionComponent {
  projectId = this.activatedRoute?.snapshot?.params?.projectId;

  saveError$ = new Subject<I18nValidationError | null>();
  saveSuccess$ = new Subject<boolean>();
  updatePartnership$ = new Subject<InputProjectPartnership>();

  private savedDescription$ = this.projectApplicationFormStore.getProjectDescription()
    .pipe(
      map(project => project.projectPartnership)
    );

  private updatedPartnership$ = this.updatePartnership$
    .pipe(
      mergeMap((data) => this.projectDescriptionService.updateProjectPartnership(this.projectId, data)),
      tap(() => this.saveSuccess$.next(true)),
      tap(() => this.saveError$.next(null)),
      tap(saved => Log.info('Updated project partnership:', this, saved)),
      catchError((error: HttpErrorResponse) => {
        this.saveError$.next(error.error);
        throw error;
      })
    );

  partnership$ = merge(this.savedDescription$, this.updatedPartnership$);

  constructor(public projectStore: ProjectStore,
              private activatedRoute: ActivatedRoute,
              private projectApplicationFormSidenavService: ProjectApplicationFormSidenavService,
              private projectDescriptionService: ProjectDescriptionService,
              private projectApplicationFormStore: ProjectApplicationFormStore) {
    this.projectApplicationFormStore.init(this.projectId);
  }

}
