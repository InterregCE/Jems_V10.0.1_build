import {ChangeDetectionStrategy, Component} from '@angular/core';
import {BaseComponent} from '@common/components/base-component';
import {combineLatest, merge, of, Subject} from 'rxjs';
import {MatSort} from '@angular/material/sort';
import {I18nValidationError} from '@common/validation/i18n-validation-error';
import {catchError, flatMap, map, startWith, take, takeUntil, tap} from 'rxjs/operators';
import {Log} from '../../../../common/utils/log';
import {Tables} from '../../../../common/utils/tables';
import {ActivatedRoute, Router} from '@angular/router';
import {TabService} from '../../../../common/services/tab.service';
import {ProgrammePageComponent} from '../programme-page/programme-page.component';
import {HttpErrorResponse} from '@angular/common/http';
import {
  InputIndicatorResultCreate,
  InputIndicatorResultUpdate,
  ProgrammeIndicatorService,
  ProgrammePriorityService
} from '@cat/api';

@Component({
  selector: 'app-programme-result-indicator-submission-page',
  templateUrl: './programme-result-indicator-submission-page.component.html',
  styleUrls: ['./programme-result-indicator-submission-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProgrammeResultIndicatorSubmissionPageComponent extends BaseComponent {

  resultIndicatorId = this.activatedRoute?.snapshot?.params?.indicatorId;
  isCreate = !this.resultIndicatorId;

  newPageSize$ = new Subject<number>();
  newPageIndex$ = new Subject<number>();
  newSort$ = new Subject<Partial<MatSort>>();

  resultIndicatorSaveError$ = new Subject<I18nValidationError | null>();
  resultIndicatorSaveSuccess$ = new Subject<boolean>();

resultIndicator$ = this.resultIndicatorId
    ? this.programmeIndicatorService.getIndicatorResult(this.resultIndicatorId).pipe(
      tap(resultIndicatorData => Log.info('Fetched result Indicator data:', this, resultIndicatorData)))
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

  createResultIndicator(indicator: InputIndicatorResultCreate) {
    this.programmeIndicatorService.createIndicatorResult(indicator)
      .pipe(
        take(1),
        takeUntil(this.destroyed$),
        tap(saved => Log.info('Saved indicator:', this, saved)),
        tap(() => this.resultIndicatorSaveSuccess$.next(true)),
        tap(() => this.resultIndicatorSaveError$.next(null)),
        tap(() => {
          this.tabService.changeTab(ProgrammePageComponent.name, 3);
          this.router.navigate(['/programme']);
        }),
        catchError((error: HttpErrorResponse) => {
          this.resultIndicatorSaveError$.next(error.error);
          throw error;
        })
      ).subscribe();
  }

  updateResultIndicator(indicator: InputIndicatorResultUpdate) {
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

  cancelCreate() {
    this.tabService.changeTab(ProgrammePageComponent.name, 3);
    this.router.navigate(['/programme']);
  }

}
