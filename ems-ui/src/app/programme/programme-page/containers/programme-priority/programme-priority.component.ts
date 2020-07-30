import {ChangeDetectionStrategy, Component} from '@angular/core';
import {BaseComponent} from '@common/components/base-component';
import {InputProgrammePriorityCreate, ProgrammePriorityService} from '@cat/api';
import {catchError, map, take, takeUntil, tap} from 'rxjs/operators';
import {Log} from '../../../../common/utils/log';
import {Subject} from 'rxjs';
import {I18nValidationError} from '@common/validation/i18n-validation-error';
import {HttpErrorResponse} from '@angular/common/http';
import {Router} from '@angular/router';
import {ProgrammeNavigationStateManagementService} from '../../services/programme-navigation-state-management.service';

@Component({
  selector: 'app-programme-priority',
  templateUrl: './programme-priority.component.html',
  styleUrls: ['./programme-priority.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProgrammePriorityComponent extends BaseComponent {

  prioritySaveError$ = new Subject<I18nValidationError | null>();
  prioritySaveSuccess$ = new Subject<boolean>();

  details$ = this.programmePriorityService.getFreePrioritiesWithPolicies()
    .pipe(
      map(freePrioritiesWithPolicies => ({
        objectives: Object.keys(freePrioritiesWithPolicies),
        objectivesWithPolicies: freePrioritiesWithPolicies
      })),
      tap(freePriorityData =>
        Log.info('Fetched free programme priorities with policies:', this, freePriorityData)),
    )

  constructor(private programmePriorityService: ProgrammePriorityService,
              private programmeNavigationStateManagementService: ProgrammeNavigationStateManagementService,
              private router: Router) {
    super();
  }

  savePriority(priority: InputProgrammePriorityCreate) {
    this.programmePriorityService.create(priority)
      .pipe(
        take(1),
        takeUntil(this.destroyed$),
        tap(saved => Log.info('Saved priority:', this, saved)),
        tap(() => this.prioritySaveSuccess$.next(true)),
        tap(() => this.prioritySaveError$.next(null)),
        tap(() => {
          this.router.navigate(['/programme']).then(() => {
            this.programmeNavigationStateManagementService.changeTab(1);
          })
        }),
        catchError((error: HttpErrorResponse) => {
          this.prioritySaveError$.next(error.error);
          throw error;
        })
      ).subscribe();
  }

  cancelPriority(index: number) {
    this.router.navigate(['/programme']).then(() => {
      this.programmeNavigationStateManagementService.changeTab(1);
    })
  }
}
