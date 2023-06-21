import {ChangeDetectionStrategy, Component} from '@angular/core';
import {BaseComponent} from '@common/components/base-component';
import {of, Subject} from 'rxjs';
import {I18nValidationError} from '@common/validation/i18n-validation-error';
import {catchError, finalize, take, takeUntil, tap} from 'rxjs/operators';
import {Log} from '../../../../common/utils/log';
import {ActivatedRoute} from '@angular/router';
import {ProgrammePageSidenavService} from '../../services/programme-page-sidenav.service';
import {HttpErrorResponse} from '@angular/common/http';
import {ProgrammeCostOptionService, ProgrammeLumpSumDTO} from '@cat/api';

@Component({
  selector: 'jems-programme-lump-sums-submission-page',
  templateUrl: './programme-lump-sums-submission-page.component.html',
  styleUrls: ['./programme-lump-sums-submission-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProgrammeLumpSumsSubmissionPageComponent extends BaseComponent {

  lumpSumId = this.activatedRoute?.snapshot?.params?.lumpSumId;
  isCreate = !this.lumpSumId;
  isSaveDone = false;

  lumpSumSaveError$ = new Subject<I18nValidationError | null>();
  lumpSumSaveSuccess$ = new Subject<boolean>();

  lumpSum$ = this.lumpSumId
    ? this.programmeCostOptionService.getProgrammeLumpSum(this.lumpSumId).pipe(
      tap(lumpSumData => Log.info('Fetched output Lump Sum data:', this, lumpSumData)))
    : of({});

  constructor(private programmeCostOptionService: ProgrammeCostOptionService,
              private activatedRoute: ActivatedRoute,
              private programmePageSidenavService: ProgrammePageSidenavService) {
    super();
  }

  createLumpSum(lumpSum: ProgrammeLumpSumDTO): void {
    this.isSaveDone = false;
    this.programmeCostOptionService.createProgrammeLumpSum(lumpSum)
      .pipe(
        take(1),
        takeUntil(this.destroyed$),
        tap(saved => Log.info('Saved lump sum:', this, saved)),
        tap(() => this.lumpSumSaveSuccess$.next(true)),
        tap(() => this.lumpSumSaveError$.next(null)),
        tap(() => this.programmePageSidenavService.goToCosts()),
        catchError((error: HttpErrorResponse) => {
          this.lumpSumSaveError$.next(error.error);
          throw error;
        }),
        finalize(() => this.isSaveDone = true),
      ).subscribe();
  }

  updateLumpSum(lumpSum: ProgrammeLumpSumDTO): void {
    this.isSaveDone = false;
    this.programmeCostOptionService.updateProgrammeLumpSum(lumpSum)
      .pipe(
        take(1),
        takeUntil(this.destroyed$),
        tap(saved => Log.info('Saved lump sum:', this, saved)),
        tap(() => this.lumpSumSaveSuccess$.next(true)),
        tap(() => this.lumpSumSaveError$.next(null)),
        catchError((error: HttpErrorResponse) => {
          this.lumpSumSaveError$.next(error.error);
          throw error;
        }),
        finalize(() => this.isSaveDone = true),
      ).subscribe();
  }

  cancelCreate(): void {
    this.programmePageSidenavService.goToCosts();
  }
}
