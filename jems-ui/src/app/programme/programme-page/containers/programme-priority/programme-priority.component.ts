import {ChangeDetectionStrategy, Component} from '@angular/core';
import {BaseComponent} from '@common/components/base-component';
import {ProgrammePriorityService, ProgrammePriorityDTO} from '@cat/api';
import {catchError, map, take, takeUntil, tap} from 'rxjs/operators';
import {Log} from '../../../../common/utils/log';
import {Subject} from 'rxjs';
import {I18nValidationError} from '@common/validation/i18n-validation-error';
import {HttpErrorResponse} from '@angular/common/http';
import {TabService} from '../../../../common/services/tab.service';
import {ProgrammePageSidenavService} from '../../services/programme-page-sidenav.service';

@Component({
  selector: 'app-programme-priority',
  templateUrl: './programme-priority.component.html',
  styleUrls: ['./programme-priority.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProgrammePriorityComponent extends BaseComponent {

  prioritySaveError$ = new Subject<I18nValidationError | null>();
  prioritySaveSuccess$ = new Subject<boolean>();

  details$ = this.programmePriorityService.getAvailableSetup()
    .pipe(
      map(setup => ({
        objectives: Object.keys(setup.freePrioritiesWithPolicies),
        objectivesWithPolicies: setup.freePrioritiesWithPolicies
      })),
      tap(freePriorityData =>
        Log.info('Fetched free programme priorities with policies:', this, freePriorityData)),
    );

  constructor(private programmePriorityService: ProgrammePriorityService,
              private tabService: TabService,
              private programmePageSidenavService: ProgrammePageSidenavService) {
    super();
    this.programmePageSidenavService.init(this.destroyed$);
  }

  savePriority(priority: ProgrammePriorityDTO): void {
    this.programmePriorityService.create(priority)
      .pipe(
        take(1),
        takeUntil(this.destroyed$),
        tap(saved => Log.info('Saved priority:', this, saved)),
        tap(() => this.prioritySaveSuccess$.next(true)),
        tap(() => this.prioritySaveError$.next(null)),
        tap(() => this.programmePageSidenavService.goToPriorities()),
        catchError((error: HttpErrorResponse) => {
          this.prioritySaveError$.next(error.error);
          throw error;
        })
      ).subscribe();
  }

  cancelPriority(): void {
    this.programmePageSidenavService.goToPriorities();
  }
}
