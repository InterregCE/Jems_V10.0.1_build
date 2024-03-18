 import {Injectable} from '@angular/core';
import {BehaviorSubject, combineLatest, Observable, of, Subject} from 'rxjs';
import {
    PagePaymentToEcLinkingDTO,
    PaymentSearchRequestDTO,
    PaymentToEcAmountSummaryDTO,
    PaymentToECLinkingAPIService,
    PaymentToEcLinkingUpdateDTO
} from '@cat/api';
import {catchError, map, startWith, switchMap, tap} from 'rxjs/operators';
import {MatSort} from '@angular/material/sort';
import {Log} from '@common/utils/log';
import {PaymentsToEcDetailPageStore} from '../payment-to-ec-detail-page-store.service';
import {Tables} from '@common/utils/tables';
import {APIError} from '@common/models/APIError';
 import PaymentTypeEnum = PaymentSearchRequestDTO.PaymentTypeEnum;

@Injectable({providedIn: 'root'})
export class PaymentToEcRegularProjectsTabStoreService {

  ftlsPage$: Observable<PagePaymentToEcLinkingDTO>;
  regularPage$: Observable<PagePaymentToEcLinkingDTO>;
  refresh$ = new Subject<void>();

  constructor(
    private detailPageStore: PaymentsToEcDetailPageStore,
    private paymentToECLinkingAPIService: PaymentToECLinkingAPIService
  ) {
    this.ftlsPage$ = this.getEcFTLSArtNot94Not95s();
    this.regularPage$ = this.getEcRegularArtNot94Not95s();
  }

  ftlsNewPageSize$ = new BehaviorSubject<number>(Tables.DEFAULT_INITIAL_PAGE_SIZE);
  ftlsNewPageIndex$ = new BehaviorSubject<number>(Tables.DEFAULT_INITIAL_PAGE_INDEX);
  ftlsNewSort$ = new Subject<Partial<MatSort>>();
  ftlsRetrieveListError$ = new Subject<APIError | null>();

  regularNewPageSize$ = new BehaviorSubject<number>(Tables.DEFAULT_INITIAL_PAGE_SIZE);
  regularNewPageIndex$ = new BehaviorSubject<number>(Tables.DEFAULT_INITIAL_PAGE_INDEX);
  regularNewSort$ = new Subject<Partial<MatSort>>();
  regularRetrieveListError$ = new Subject<APIError | null>();

  public getEcFTLSArtNot94Not95s(): Observable<PagePaymentToEcLinkingDTO> {
    return combineLatest([
      this.detailPageStore.paymentToEcDetail$,
      this.ftlsNewPageIndex$,
      this.ftlsNewPageSize$,
      this.ftlsNewSort$.pipe(
        startWith(({ active: undefined, direction: undefined }) as Partial<MatSort>),
        map((sort: Partial<MatSort>) => sort?.direction ? `${sort.active},${sort.direction}` : 'id,desc'),
      ),
      this.refresh$.pipe(startWith(1)),
      this.detailPageStore.updatedPaymentApplicationStatus$, // to refresh list on status change
    ]).pipe(
      switchMap(([ecPayment, page, size, sort]) =>
        this.paymentToECLinkingAPIService.getPaymentsLinkedWithEcNotArt94NotArt95(ecPayment.id, PaymentTypeEnum.FTLS, page, size, sort)),
        tap(data => Log.info('Fetched ec FTLS payments with articles not 94/95', this, data)),
        catchError(error => {
          this.ftlsRetrieveListError$.next(error.error);
          return of({} as PagePaymentToEcLinkingDTO);
        })
    );
  }

  public getEcRegularArtNot94Not95s(): Observable<PagePaymentToEcLinkingDTO> {
    return combineLatest([
      this.detailPageStore.paymentToEcDetail$,
      this.regularNewPageIndex$,
      this.regularNewPageSize$,
      this.regularNewSort$.pipe(
        startWith(({ active: undefined, direction: undefined }) as Partial<MatSort>),
        map((sort: Partial<MatSort>) => sort?.direction ? `${sort.active},${sort.direction}` : 'id,desc'),
      ),
      this.refresh$.pipe(startWith(1)),
      this.detailPageStore.updatedPaymentApplicationStatus$, // to refresh list on status change
    ]).pipe(
      switchMap(([ecPayment, page, size, sort]) =>
        this.paymentToECLinkingAPIService.getPaymentsLinkedWithEcNotArt94NotArt95(ecPayment.id, PaymentTypeEnum.REGULAR ,page, size, sort)),
      tap(data => Log.info('Fetched ec regular payments with articles not 94/95', this, data)),
      catchError(error => {
        this.regularRetrieveListError$.next(error.error);
        return of({} as PagePaymentToEcLinkingDTO);
      })
    );
  }

  cumulativeForCurrentTab(): Observable<PaymentToEcAmountSummaryDTO> {
    return combineLatest([
      this.detailPageStore.paymentToEcId$,
      this.refresh$.pipe(startWith(1))
    ]).pipe(
      switchMap(([ecPaymentId]) => this.paymentToECLinkingAPIService.getPaymentApplicationToEcOverviewAmountsByType(ecPaymentId, 'DoesNotFallUnderArticle94Nor95')),
      tap(data => Log.info('Fetched cumulative for FTLSArtNot94Not95s tab', this, data))
    );
  }

  deselectPaymentFromEc(ecId: number, paymentId: number): Observable<any> {
    return this.paymentToECLinkingAPIService.deselectPaymentFromEcPayment(ecId, paymentId)
      .pipe(
        tap(() => this.refresh$.next())
      );
  }

  selectPaymentToEc(ecId: number, paymentId: number): Observable<any> {
    return this.paymentToECLinkingAPIService.selectPaymentToEcPayment(ecId, paymentId)
      .pipe(
        tap(() => this.refresh$.next())
      );
  }

  updateLinkedPayment(ecId: number, paymentId: number, updateDTO: PaymentToEcLinkingUpdateDTO): Observable<any> {
    return this.paymentToECLinkingAPIService.updateLinkedPayment(ecId, paymentId, updateDTO)
      .pipe(
        tap(() => this.refresh$.next())
      );
  }
}
