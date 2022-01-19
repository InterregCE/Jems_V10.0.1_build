import {ChangeDetectionStrategy, Component} from '@angular/core';
import {BaseComponent} from '@common/components/base-component';
import {of, Subject} from 'rxjs';
import {I18nValidationError} from '@common/validation/i18n-validation-error';
import {catchError, take, takeUntil, tap} from 'rxjs/operators';
import {Log} from '@common/utils/log';
import {ActivatedRoute} from '@angular/router';
import {ProgrammePageSidenavService} from '../../services/programme-page-sidenav.service';
import {HttpErrorResponse} from '@angular/common/http';
import {ProgrammeCostOptionService, ProgrammeUnitCostDTO} from '@cat/api';

@Component({
  selector: 'jems-programme-unit-costs-submission-page',
  templateUrl: './programme-unit-costs-submission-page.component.html',
  styleUrls: ['./programme-unit-costs-submission-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProgrammeUnitCostsSubmissionPageComponent extends BaseComponent {

  unitCostId = this.activatedRoute?.snapshot?.params?.unitCostId;
  isCreate = !this.unitCostId;

  unitCostSaveError$ = new Subject<I18nValidationError | null>();
  unitCostSaveSuccess$ = new Subject<boolean>();

  unitCost$ = this.unitCostId
    ? this.programmeCostOptionService.getProgrammeUnitCost(this.unitCostId).pipe(
      tap(unitCostData => Log.info('Fetched output Unit Cost data:', this, unitCostData)))
    : of({});

  constructor(private programmeCostOptionService: ProgrammeCostOptionService,
              private activatedRoute: ActivatedRoute,
              private programmePageSidenavService: ProgrammePageSidenavService) {
    super();
  }

  createUnitCost(unitCost: ProgrammeUnitCostDTO): void {
    this.programmeCostOptionService.createProgrammeUnitCost(unitCost)
      .pipe(
        take(1),
        takeUntil(this.destroyed$),
        tap(saved => Log.info('Saved unit cost:', this, saved)),
        tap(() => this.unitCostSaveSuccess$.next(true)),
        tap(() => this.unitCostSaveError$.next(null)),
        tap(() => this.programmePageSidenavService.goToCosts()),
        catchError((error: HttpErrorResponse) => {
          this.unitCostSaveError$.next(error.error);
          throw error;
        })
      ).subscribe();
  }

  updateUnitCost(unitCost: ProgrammeUnitCostDTO): void {
    this.programmeCostOptionService.updateProgrammeUnitCost(unitCost)
      .pipe(
        take(1),
        takeUntil(this.destroyed$),
        tap(saved => Log.info('Saved unit cost:', this, saved)),
        tap(() => this.unitCostSaveSuccess$.next(true)),
        tap(() => this.unitCostSaveError$.next(null)),
        catchError((error: HttpErrorResponse) => {
          this.unitCostSaveError$.next(error.error);
          throw error;
        })
      ).subscribe();
  }

  cancelCreate(): void {
    this.programmePageSidenavService.goToCosts();
  }
}
