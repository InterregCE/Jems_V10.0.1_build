import {ChangeDetectionStrategy, Component} from '@angular/core';
import {BaseComponent} from '@common/components/base-component';
import {merge, Subject} from 'rxjs';
import {I18nValidationError} from '@common/validation/i18n-validation-error';
import {
  ProgrammeTypologyOfErrorsService,
  TypologyErrorsUpdateDTO
} from '@cat/api';
import {catchError, mergeMap, tap} from 'rxjs/operators';
import {HttpErrorResponse} from '@angular/common/http';
import {Log} from '@common/utils/log';

@Component({
  selector: 'jems-programme-typology-errors',
  templateUrl: './programme-typology-errors.component.html',
  styleUrls: ['./programme-typology-errors.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProgrammeTypologyErrorsComponent extends BaseComponent {

  error$ = new Subject<I18nValidationError | null>();
  success$ = new Subject<string>();
  saveStatuses$ = new Subject<TypologyErrorsUpdateDTO>();

  private initialStatus$ = this.typologyErrorsService.getTypologyErrors()
    .pipe(
      tap(typologyErrors => Log.info('Fetched typology of errors list:', this, typologyErrors))
    );

  private savedStatus$ = this.saveStatuses$
    .pipe(
      mergeMap(typologyErrors => this.typologyErrorsService.updateTypologyErrors(typologyErrors)),
      tap(saved => Log.info('Updated programme typology errors:', this, saved)),
      tap(() => this.success$.next('programme.typology.errors.save.success')),
      tap(() => this.error$.next(null)),
      catchError((error: HttpErrorResponse) => {
        this.error$.next(error.error);
        throw error;
      })
    );

  typologyErrors$ = merge(this.initialStatus$, this.savedStatus$);

  constructor(private typologyErrorsService: ProgrammeTypologyOfErrorsService) {
    super();
  }
}
