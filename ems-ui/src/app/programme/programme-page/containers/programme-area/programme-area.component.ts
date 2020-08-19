import {ChangeDetectionStrategy, Component} from '@angular/core';
import {NutsImportService} from '@cat/api';
import {merge, of, Subject} from 'rxjs';
import {catchError, flatMap, tap} from 'rxjs/operators';
import {BaseComponent} from '@common/components/base-component';
import {Log} from '../../../../common/utils/log';
import {I18nValidationError} from '@common/validation/i18n-validation-error';
import {HttpErrorResponse} from '@angular/common/http';

@Component({
  selector: 'app-programme-area',
  templateUrl: './programme-area.component.html',
  styleUrls: ['./programme-area.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProgrammeAreaComponent extends BaseComponent {
  downloadLatestNuts$ = new Subject<void>();
  downloadSuccess$ = new Subject<boolean>();
  downloadError$ = new Subject<I18nValidationError | null>();

  private latestNuts$ = this.downloadLatestNuts$
    .pipe(
      flatMap(() => this.nutsService.downloadLatestNuts()),
      tap(() => this.downloadSuccess$.next(true)),
      tap(() => this.downloadError$.next(null)),
      catchError((error: HttpErrorResponse) => {
        this.downloadError$.next(error.error);
        return of(null);
      }),
      tap(metadata => Log.info('Download latest nuts', this, metadata))
    );
  private initialNutsMetadata$ = this.nutsService.getNutsMetadata()
    .pipe(
      tap(metadata => Log.info('Fetched initial metadata', this, metadata))
    );

  metaData$ = merge(this.initialNutsMetadata$, this.latestNuts$);

  constructor(private nutsService: NutsImportService) {
    super();
  }
}
