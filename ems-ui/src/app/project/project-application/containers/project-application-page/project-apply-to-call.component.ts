import {ChangeDetectionStrategy, Component} from '@angular/core';
import {InputProject, ProjectService} from '@cat/api';
import {I18nValidationError} from '@common/validation/i18n-validation-error';
import {Permission} from '../../../../security/permissions/permission';
import {HttpErrorResponse} from '@angular/common/http';
import {catchError, take, takeUntil, tap} from 'rxjs/operators';
import {Log} from '../../../../common/utils/log';
import {BaseComponent} from '@common/components/base-component';
import {Subject} from 'rxjs';
import {SecurityService} from '../../../../security/security.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-project-apply-to-call',
  templateUrl: 'project-apply-to-call.component.html',
  styleUrls: ['project-apply-to-call.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})

export class ProjectApplyToCallComponent extends BaseComponent {
  Permission = Permission;

  applicationSaveError$ = new Subject<I18nValidationError | null>();
  applicationSaveSuccess$ = new Subject<boolean>();

  constructor(private projectService: ProjectService,
              private securityService: SecurityService,
              private router: Router) {
    super();
  }

  createApplication(application: InputProject): void {
    this.projectService.createProject(application)
      .pipe(
        take(1),
        takeUntil(this.destroyed$),
        tap(() => this.applicationSaveSuccess$.next(true)),
        tap(() => this.applicationSaveError$.next(null)),
        tap(saved => Log.info('Created project application:', this, saved)),
        tap(() => this.router.navigate(['app', 'project'])),
        catchError((error: HttpErrorResponse) => {
          this.applicationSaveError$.next(error.error);
          throw error;
        })
      )
      .subscribe();
  }
}
