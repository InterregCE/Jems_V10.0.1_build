import {ChangeDetectionStrategy, Component} from '@angular/core';
import {merge, Subject} from 'rxjs';
import {InputProjectLongTermPlans, ProjectDescriptionService} from '@cat/api';
import {catchError, map, mergeMap, tap} from 'rxjs/operators';
import {Log} from '../../../../../common/utils/log';
import {HttpErrorResponse} from '@angular/common/http';
import {ProjectApplicationFormStore} from '../services/project-application-form-store.service';
import {ActivatedRoute} from '@angular/router';
import {ProjectStore} from '../../project-application-detail/services/project-store.service';

@Component({
  selector: 'jems-project-application-form-future-plans-section',
  templateUrl: './project-application-form-future-plans-section.component.html',
  styleUrls: ['./project-application-form-future-plans-section.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormFuturePlansSectionComponent {
  projectId = this.activatedRoute?.snapshot?.params?.projectId;

  saveError$ = new Subject<HttpErrorResponse | null>();
  saveSuccess$ = new Subject<boolean>();
  updateFuturePlans$ = new Subject<InputProjectLongTermPlans>();

  private savedDescription$ = this.projectApplicationFormStore.getProjectDescription()
    .pipe(
      map(project => project.projectLongTermPlans || {})
    );

  private updatedFuturePlans$ = this.updateFuturePlans$
    .pipe(
      mergeMap((data) => this.projectDescriptionService.updateProjectLongTermPlans(this.projectId, data)),
      tap(() => this.saveSuccess$.next(true)),
      tap(() => this.saveError$.next(null)),
      tap(saved => Log.info('Updated project long-term plans:', this, saved)),
      catchError((error: HttpErrorResponse) => {
        this.saveError$.next(error);
        throw error;
      })
    );

  futurePlans$ = merge(this.savedDescription$, this.updatedFuturePlans$);

  constructor(public projectStore: ProjectStore,
              private activatedRoute: ActivatedRoute,
              private projectDescriptionService: ProjectDescriptionService,
              private projectApplicationFormStore: ProjectApplicationFormStore) {
    this.projectApplicationFormStore.init(this.projectId);
  }

}
