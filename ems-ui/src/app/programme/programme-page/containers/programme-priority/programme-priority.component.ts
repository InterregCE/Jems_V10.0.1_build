import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {BaseComponent} from '@common/components/base-component';
import {ProgrammePriorityService} from '@cat/api';
import {catchError, takeUntil, tap} from 'rxjs/operators';
import {Log} from '../../../../common/utils/log';
import {
  InputProgrammePriorityCreate
} from '@cat/api';
import {Subject} from 'rxjs';
import {I18nValidationError} from '@common/validation/i18n-validation-error';
import {HttpErrorResponse} from '@angular/common/http';
import {Router} from '@angular/router';

@Component({
  selector: 'app-programme-priority',
  templateUrl: './programme-priority.component.html',
  styleUrls: ['./programme-priority.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProgrammePriorityComponent extends BaseComponent implements OnInit {

  objectives: string[] = [];
  objectivesWithPolicies: { [key: string]: string[] } = {};

  prioritySaveError$ = new Subject<I18nValidationError | null>();
  prioritySaveSuccess$ = new Subject<boolean>();
  savePriority$ = new Subject<InputProgrammePriorityCreate>();

  constructor(private programmePriorityService: ProgrammePriorityService,
              private router: Router) {
    super();
  }

  ngOnInit(): void {
    this.programmePriorityService.getFreePrioritiesWithPolicies()
    .pipe(
      tap(freePriorityData => Log.info('Fetched free programme priorities with policies:', this, freePriorityData)),
      takeUntil(this.destroyed$),
    ).subscribe(freePrioritiesWithPolicies => {
      this.objectives = Object.keys(freePrioritiesWithPolicies);
      this.objectivesWithPolicies = freePrioritiesWithPolicies;
    });
  }

  savePriority(priority: InputProgrammePriorityCreate) {
    this.programmePriorityService.create(priority)
      .pipe(
        tap(saved => Log.info('Saved priority:', this, saved)),
        tap(() => this.prioritySaveSuccess$.next(true)),
        tap(() => this.prioritySaveError$.next(null)),
        tap(() => this.router.navigate(['/programme'])),
        catchError((error: HttpErrorResponse) => {
          this.prioritySaveError$.next(error.error);
          throw error;
        })
      ).subscribe();
  }

}
