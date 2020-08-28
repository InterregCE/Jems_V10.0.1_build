import {ChangeDetectionStrategy, Component} from '@angular/core';
import {BaseComponent} from '@common/components/base-component';
import {
  InputIndicatorOutputCreate,
  InputIndicatorOutputUpdate,
  ProgrammeIndicatorService,
  ProgrammePriorityService
} from '@cat/api';
import {ActivatedRoute, Router} from '@angular/router';
import {catchError, flatMap, map, startWith, take, takeUntil, tap} from 'rxjs/operators';
import {Log} from '../../../../common/utils/log';
import {combineLatest, merge, of, Subject} from 'rxjs';
import {Tables} from '../../../../common/utils/tables';
import {MatSort} from '@angular/material/sort';
import {I18nValidationError} from '@common/validation/i18n-validation-error';
import {ProgrammePageComponent} from '../programme-page/programme-page.component';
import {HttpErrorResponse} from '@angular/common/http';
import {TabService} from '../../../../common/services/tab.service';

@Component({
  selector: 'app-programme-output-indicator-submission-page',
  templateUrl: './programme-output-indicator-submission-page.component.html',
  styleUrls: ['./programme-output-indicator-submission-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProgrammeOutputIndicatorSubmissionPageComponent extends BaseComponent {

  outputIndicatorId = this.activatedRoute?.snapshot?.params?.indicatorId;
  isCreate = !this.outputIndicatorId;

  newPageSize$ = new Subject<number>();
  newPageIndex$ = new Subject<number>();
  newSort$ = new Subject<Partial<MatSort>>();

  outputIndicatorSaveError$ = new Subject<I18nValidationError | null>();
  outputIndicatorSaveSuccess$ = new Subject<boolean>();

  outputIndicator$ = this.outputIndicatorId
    ? this.programmeIndicatorService.getIndicatorOutput(this.outputIndicatorId).pipe(
      tap(outputIndicatorData => Log.info('Fetched output Indicator data:', this, outputIndicatorData)))
    : of({});

  priorities$ =
    combineLatest([
      this.newPageIndex$.pipe(startWith(Tables.DEFAULT_INITIAL_PAGE_INDEX)),
      this.newPageSize$.pipe(startWith(100)),
      this.newSort$.pipe(
        startWith({ active: 'code', direction: 'asc' }),
        map(sort => sort?.direction ? sort : { active: 'code', direction: 'asc' }),
        map(sort => `${sort.active},${sort.direction}`)
      ),
    ])
      .pipe(
        flatMap(([pageIndex, pageSize, sort]) =>
          this.programmePriorityService.get(pageIndex, pageSize, sort)),
        tap(page => Log.info('Fetched the priorities:', this, page.content)),
      );

  constructor(private programmeIndicatorService: ProgrammeIndicatorService,
              private activatedRoute: ActivatedRoute,
              private tabService: TabService,
              private router: Router,
              private programmePriorityService: ProgrammePriorityService) {
    super();
  }

  createOutputIndicator(indicator: InputIndicatorOutputCreate) {
    this.programmeIndicatorService.createIndicatorOutput(indicator)
      .pipe(
        take(1),
        takeUntil(this.destroyed$),
        tap(saved => Log.info('Saved indicator:', this, saved)),
        tap(() => this.outputIndicatorSaveSuccess$.next(true)),
        tap(() => this.outputIndicatorSaveError$.next(null)),
        tap(() => {
          this.tabService.changeTab(ProgrammePageComponent.name, 3);
          this.router.navigate(['/programme']);
        }),
        catchError((error: HttpErrorResponse) => {
          this.outputIndicatorSaveError$.next(error.error);
          throw error;
        })
      ).subscribe();
  }

  updateOutputIndicator(indicator: InputIndicatorOutputUpdate) {
    this.programmeIndicatorService.updateIndicatorOutput(indicator)
      .pipe(
        take(1),
        takeUntil(this.destroyed$),
        tap(saved => Log.info('Saved indicator:', this, saved)),
        tap(() => this.outputIndicatorSaveSuccess$.next(true)),
        tap(() => this.outputIndicatorSaveError$.next(null)),
        catchError((error: HttpErrorResponse) => {
          this.outputIndicatorSaveError$.next(error.error);
          throw error;
        })
      ).subscribe();
  }

  cancelCreate() {
    this.tabService.changeTab(ProgrammePageComponent.name, 3);
    this.router.navigate(['/programme']);
  }

}
