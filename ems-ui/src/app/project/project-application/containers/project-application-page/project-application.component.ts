import {ChangeDetectionStrategy, Component} from '@angular/core';
import {InputProject, ProjectService} from '@cat/api';
import {I18nValidationError} from '@common/validation/i18n-validation-error';
import {Permission} from '../../../../security/permissions/permission';
import {PageEvent} from '@angular/material/paginator';
import {HttpErrorResponse} from '@angular/common/http';
import {catchError, flatMap, startWith, take, takeUntil, tap} from 'rxjs/operators';
import {Log} from '../../../../common/utils/log';
import {BaseComponent} from '@common/components/base-component';
import {combineLatest, Subject} from 'rxjs';
import {SecurityService} from '../../../../security/security.service';

@Component({
  selector: 'app-project-application',
  templateUrl: 'project-application.component.html',
  styleUrls: ['project-application.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})

export class ProjectApplicationComponent extends BaseComponent {
  Permission = Permission;

  private INITIAL_PAGE: PageEvent = {pageIndex: 0, pageSize: 100, length: 0};

  private newPage$ = new Subject<PageEvent>();
  private newSort$ = new Subject<string>();

  currentPage$ =
    combineLatest([
      this.newPage$.pipe(startWith(this.INITIAL_PAGE)),
      this.newSort$.pipe(startWith('id,desc'))
    ])
      .pipe(
        flatMap(([page, sort]) => this.projectService.getProjects(page?.pageIndex, page?.pageSize, sort)),
        tap(page => Log.info('Fetched the projects:', this, page.content)),
      );

  currentUser$ = this.securityService.currentUserDetails;
  applicationSaveError$ = new Subject<I18nValidationError | null>();
  applicationSaveSuccess$ = new Subject<boolean>();

  constructor(private projectService: ProjectService,
              private securityService: SecurityService) {
    super();
  }

  createApplication(application: InputProject): void {
    this.projectService.createProject(application)
      .pipe(
        take(1),
        takeUntil(this.destroyed$),
        tap(() => this.applicationSaveSuccess$.next(true)),
        tap(() => this.applicationSaveError$.next(null)),
        tap(() => this.newPage(this.INITIAL_PAGE)),
        tap(saved => Log.info('Created project application:', this, saved)),
        catchError((error: HttpErrorResponse) => {
          this.applicationSaveError$.next(error.error);
          throw error;
        })
      )
      .subscribe();
  }

  newPage(page: PageEvent) {
    this.newPage$.next(page);
  }
}
