import {Injectable} from '@angular/core';
import {BehaviorSubject, combineLatest, Observable, of, Subject} from 'rxjs';
import {
  PagePaymentToEcLinkingDTO,
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
export class PaymentToEcFtlsTabStoreService {

  page$: Observable<PagePaymentToEcLinkingDTO>;
  refresh$ = new Subject<void>();

  constructor(
    private detailPageStore: PaymentsToEcDetailPageStore,
    private paymentToECLinkingAPIService: PaymentToECLinkingAPIService
  ) {
    this.page$ = this.getEcFTLSArtNot94Not95s();
  }

  newPageSize$ = new BehaviorSubject<number>(Tables.DEFAULT_INITIAL_PAGE_SIZE);
  newPageIndex$ = new BehaviorSubject<number>(Tables.DEFAULT_INITIAL_PAGE_INDEX);
  newSort$ = new Subject<Partial<MatSort>>();
  retrieveListError$ = new Subject<APIError | null>();

  public getEcFTLSArtNot94Not95s(): Observable<PagePaymentToEcLinkingDTO> {
    return combineLatest([
      this.detailPageStore.paymentToEcId$,
      this.newPageIndex$,
      this.newPageSize$,
      this.newSort$.pipe(
        startWith(({ active: undefined, direction: undefined }) as Partial<MatSort>),
        map((sort: Partial<MatSort>) => sort?.direction ? `${sort.active},${sort.direction}` : 'id,desc'),
      ),
      this.refresh$.pipe(startWith(1)),
      this.detailPageStore.paymentToEcDetail$
    ]).pipe(
      switchMap(([ecId, page, size, sort]) =>
        this.paymentToECLinkingAPIService.getFTLSPaymentsLinkedWithEcForArtNot94Not95(ecId, page, size, sort)),
        tap(data => Log.info('Fetched ec FTLS articles not 94/95', this, data)),
        catchError(error => {
          this.retrieveListError$.next(error.error);
          return of({} as PagePaymentToEcLinkingDTO);
        })
    );
  }

  deselectPaymentFromEc(paymentId: number): Observable<any> {
    return this.paymentToECLinkingAPIService.deselectPaymentFromEcPayment(paymentId)
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

  updateLinkedPayment(paymentId: number, updateDTO: PaymentToEcLinkingUpdateDTO): Observable<any> {
    return this.paymentToECLinkingAPIService.updateLinkedPayment(paymentId, updateDTO)
      .pipe(
        tap(() => this.refresh$.next())
      );
  }
}
