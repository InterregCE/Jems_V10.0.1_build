import {ChangeDetectionStrategy, Component} from '@angular/core';
import {BaseComponent} from '@common/components/base-component';
import {of, Subject} from 'rxjs';
import {I18nValidationError} from '@common/validation/i18n-validation-error';
import {catchError, map, take, takeUntil, tap} from 'rxjs/operators';
import {Log} from '../../../../common/utils/log';
import {ActivatedRoute} from '@angular/router';
import {TabService} from '../../../../common/services/tab.service';
import {HttpErrorResponse} from '@angular/common/http';
import {
  InputIndicatorResultCreate,
  InputIndicatorResultUpdate,
  ProgrammeIndicatorService,
  ProgrammePriorityService
} from '@cat/api';
import {ProgrammePageSidenavService} from '../../services/programme-page-sidenav.service';

@Component({
  selector: 'app-programme-result-indicator-submission-page',
  templateUrl: './programme-result-indicator-submission-page.component.html',
  styleUrls: ['./programme-result-indicator-submission-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProgrammeResultIndicatorSubmissionPageComponent extends BaseComponent {

  resultIndicatorId = this.activatedRoute?.snapshot?.params?.indicatorId;
  isCreate = !this.resultIndicatorId;

  resultIndicatorSaveError$ = new Subject<I18nValidationError | null>();
  resultIndicatorSaveSuccess$ = new Subject<boolean>();

  resultIndicator$ = this.resultIndicatorId
    ? this.programmeIndicatorService.getIndicatorResult(this.resultIndicatorId).pipe(
      tap(resultIndicatorData => Log.info('Fetched result Indicator data:', this, resultIndicatorData)))
    : of({});

  priorities$ = this.programmePriorityService.get()
    .pipe(
      tap(page => Log.info('Fetched the priorities:', this, page)),
    );

  constructor(private programmeIndicatorService: ProgrammeIndicatorService,
              private activatedRoute: ActivatedRoute,
              private tabService: TabService,
              private programmePriorityService: ProgrammePriorityService,
              private programmePageSidenavService: ProgrammePageSidenavService) {
    super();
  }

  createResultIndicator(indicator: InputIndicatorResultCreate): void {
    this.programmeIndicatorService.createIndicatorResult(indicator)
      .pipe(
        take(1),
        takeUntil(this.destroyed$),
        tap(saved => Log.info('Saved indicator:', this, saved)),
        tap(() => this.resultIndicatorSaveSuccess$.next(true)),
        tap(() => this.resultIndicatorSaveError$.next(null)),
        tap(() => this.programmePageSidenavService.goToIndicators()),
        catchError((error: HttpErrorResponse) => {
          this.resultIndicatorSaveError$.next(error.error);
          throw error;
        })
      ).subscribe();
  }

  updateResultIndicator(indicator: InputIndicatorResultUpdate): void {
    this.programmeIndicatorService.updateIndicatorResult(indicator)
      .pipe(
        take(1),
        takeUntil(this.destroyed$),
        tap(saved => Log.info('Saved indicator:', this, saved)),
        tap(() => this.resultIndicatorSaveSuccess$.next(true)),
        tap(() => this.resultIndicatorSaveError$.next(null)),
        catchError((error: HttpErrorResponse) => {
          this.resultIndicatorSaveError$.next(error.error);
          throw error;
        })
      ).subscribe();
  }

  cancelCreate(): void {
    this.programmePageSidenavService.goToIndicators();
  }

}
