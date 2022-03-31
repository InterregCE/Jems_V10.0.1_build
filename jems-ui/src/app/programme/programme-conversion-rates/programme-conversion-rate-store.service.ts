import {Injectable} from '@angular/core';
import {catchError, mergeMap, tap} from 'rxjs/operators';
import {HttpErrorResponse} from '@angular/common/http';
import {Log} from '@common/utils/log';
import {merge, Observable, Subject} from 'rxjs';
import {CurrencyImportAndConversionService, CurrencyDTO} from '@cat/api';
import {APIError} from '@common/models/APIError';

@Injectable()
export class ProgrammeConversionRateStore {

  currencies$: Observable<CurrencyDTO[]>;
  success$: Observable<any>;
  error$: Observable<APIError | null>;

  downloadConversionRates$ = new Subject<void>();
  downloadSuccess$ = new Subject<any>();
  downloadError$ = new Subject<APIError | null>();

  constructor(private currencyService: CurrencyImportAndConversionService) {
    this.currencies$ = this.getCurrencies();
    this.success$ = this.downloadSuccess$.asObservable();
    this.error$ = this.downloadError$.asObservable();
  }

  getCurrencies(): Observable<CurrencyDTO[]> {
  let latestConversionMetadata = this.downloadConversionRates$
      .pipe(
        mergeMap(() => this.currencyService.fetchCurrencyRates()),
        tap(() => this.downloadSuccess$.next(true)),
        tap(() => this.downloadError$.next(null)),
        catchError((error: HttpErrorResponse) => {
          this.downloadError$.next(error.error);
          throw error;
        }),
        tap(metadata => Log.info('Download latest currencies and conversions', this, metadata))
      );

  let initialConversionMetadata = this.currencyService.getCurrencyRates()
      .pipe(
        tap(metadata => Log.info('Fetched initial currencies and conversions', this, metadata))
      );

    return merge(initialConversionMetadata, latestConversionMetadata);
  }

}
