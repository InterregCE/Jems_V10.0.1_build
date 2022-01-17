import {ChangeDetectionStrategy, Component} from '@angular/core';
import {InputProgrammeStrategy, ProgrammeStrategyService} from '@cat/api';
import {catchError, map, mergeMap, tap} from 'rxjs/operators';
import {Log} from '../../../../common/utils/log';
import {HttpErrorResponse} from '@angular/common/http';
import {merge, Subject} from 'rxjs';
import {I18nValidationError} from '@common/validation/i18n-validation-error';
import {ProgrammePageSidenavService} from '../../services/programme-page-sidenav.service';

@Component({
  selector: 'app-programme-strategies-page',
  templateUrl: './programme-strategies-page.component.html',
  styleUrls: ['./programme-strategies-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProgrammeStrategiesPageComponent {
  strategies$ = this.programmeStrategyService.getProgrammeStrategies()
    .pipe(
      map(programmeStrategies => programmeStrategies.filter( programmeStrategy => programmeStrategy.strategy  !== InputProgrammeStrategy.StrategyEnum.Other)),
      tap(programmeStrategies => Log.info('Fetched programme strategies:', this, programmeStrategies))
    );
  strategiesSaveError$ = new Subject<I18nValidationError | null>();
  strategiesSaveSuccess$ = new Subject<boolean>();
  saveStrategies$ = new Subject<InputProgrammeStrategy[]>();

  private savedStrategies$ = this.saveStrategies$
    .pipe(
      mergeMap(strategies => this.programmeStrategyService.updateProgrammeStrategies(strategies)),
      tap(() => this.strategiesSaveSuccess$.next(true)),
      tap(() => this.strategiesSaveError$.next(null)),
      tap(saved => Log.info('Saved strategies:', this, saved)),
      catchError((error: HttpErrorResponse) => {
        this.strategiesSaveError$.next(error.error);
        throw error;
      })
    );

  details$ = merge(this.strategies$, this.savedStrategies$);

  constructor(private programmeStrategyService: ProgrammeStrategyService,
              private programmePageSidenavService: ProgrammePageSidenavService) {
  }
}
