import {Injectable} from '@angular/core';
import {BehaviorSubject, combineLatest, Observable, of, Subject} from 'rxjs';
import {
  CorrectionLinkingAPIService, PagePaymentToEcCorrectionLinkingDTO,
  PagePaymentToEcLinkingDTO,
  PaymentApplicationToECService,
  PaymentToEcAmountSummaryDTO,
  PaymentToEcCorrectionLinkingDTO, PaymentToEcCorrectionLinkingUpdateDTO,
  PaymentToECLinkingAPIService,
  PaymentToEcLinkingUpdateDTO
} from '@cat/api';
import {catchError, map, startWith, switchMap, tap} from 'rxjs/operators';
import {MatSort} from '@angular/material/sort';
import {Log} from '@common/utils/log';
import {PaymentsToEcDetailPageStore} from '../payment-to-ec-detail-page-store.service';
import {Tables} from '@common/utils/tables';
import {APIError} from '@common/models/APIError';

@Injectable({providedIn: 'root'})
export class PaymentToEcCorrectionTabStoreService {

  correctionPage$: Observable<PagePaymentToEcCorrectionLinkingDTO>;
  refresh$ = new Subject<void>();

  constructor(
    private detailPageStore: PaymentsToEcDetailPageStore,
    private correctionECLinkingAPIService: CorrectionLinkingAPIService,
    private paymentToECLinkingAPIService: PaymentToECLinkingAPIService,
  ) {
    this.correctionPage$ = this.getEcCorrections();
  }

  correctionNewPageSize$ = new BehaviorSubject<number>(Tables.DEFAULT_INITIAL_PAGE_SIZE);
  correctionNewPageIndex$ = new BehaviorSubject<number>(Tables.DEFAULT_INITIAL_PAGE_INDEX);
  correctionNewSort$ = new Subject<Partial<MatSort>>();
  correctionRetrieveListError$ = new Subject<APIError | null>();

  public getEcCorrections(): Observable<PagePaymentToEcCorrectionLinkingDTO> {
    return combineLatest([
      this.detailPageStore.paymentToEcDetail$,
      this.correctionNewPageIndex$,
      this.correctionNewPageSize$,
      this.correctionNewSort$.pipe(
        startWith(({ active: undefined, direction: undefined }) as Partial<MatSort>),
        map((sort: Partial<MatSort>) => sort?.direction ? `${sort.active},${sort.direction}` : 'id,desc'),
      ),
      this.refresh$.pipe(startWith(1)),
      this.detailPageStore.updatedPaymentApplicationStatus$, // to refresh list on status change
    ]).pipe(
      switchMap(([ecPayment, page, size, sort]) =>
        this.correctionECLinkingAPIService.getAvailableCorrections(ecPayment.id, page, size, sort)),
        tap(data => Log.info('Fetched ec corrections', this, data)),
        catchError(error => {
          this.correctionRetrieveListError$.next(error.error);
          return of({} as PagePaymentToEcCorrectionLinkingDTO);
        })
    );
  }

  cumulativeForCurrentTab(): Observable<PaymentToEcAmountSummaryDTO> {
    return combineLatest([
      this.detailPageStore.paymentToEcId$,
      this.refresh$.pipe(startWith(1))
    ]).pipe(
      switchMap(([paymentId]) => this.paymentToECLinkingAPIService.getPaymentApplicationToEcOverviewAmountsByType(paymentId, 'Correction')),
      tap(data => Log.info('Fetched cumulative for Corrections tab', this, data))
    );
  }

  deselectPaymentFromEc(correctionId: number): Observable<any> {
    return this.correctionECLinkingAPIService.deselectCorrectionFromEcPayment(correctionId)
      .pipe(
        tap(() => this.refresh$.next())
      );
  }

  selectPaymentToEc(ecId: number, correctionId: number): Observable<any> {
    return this.correctionECLinkingAPIService.selectCorrectionToEcPayment(correctionId, ecId)
      .pipe(
        tap(() => this.refresh$.next())
      );
  }

  updateLinkedPayment(correctionId: number, updateDTO: PaymentToEcCorrectionLinkingUpdateDTO): Observable<any> {
    return this.correctionECLinkingAPIService.updateLinkedCorrection(correctionId, updateDTO)
      .pipe(
        tap(() => this.refresh$.next())
      );
  }
}
