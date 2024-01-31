import {Injectable} from '@angular/core';
import {
  PagePaymentAccountCorrectionLinkingDTO,
  PaymentAccountAmountSummaryDTO,
  PaymentAccountCorrectionLinkingAPIService,
  PaymentAccountCorrectionLinkingUpdateDTO
} from '@cat/api';
import {BehaviorSubject, combineLatest, Observable, of, Subject} from 'rxjs';
import {catchError, map, startWith, switchMap, tap} from 'rxjs/operators';
import {MatSort} from '@angular/material/sort';
import {Log} from '@common/utils/log';
import {AccountsPageStore} from '../../accounts-page.store';
import {Tables} from '@common/utils/tables';
import {APIError} from '@common/models/APIError';

@Injectable({providedIn: 'root'})
export class AccountsCorrectionTabStore {

  correctionPage$: Observable<PagePaymentAccountCorrectionLinkingDTO>;
  currentOverview$: Observable<PaymentAccountAmountSummaryDTO>;
  refresh$ = new Subject<void>();

  constructor(
    private accountsPageStore: AccountsPageStore,
    private correctionLinkingService: PaymentAccountCorrectionLinkingAPIService,
  ) {
    this.correctionPage$ = this.correctionPage();
    this.currentOverview$ = this.currentOverview();
  }

  correctionNewPageSize$ = new BehaviorSubject<number>(Tables.DEFAULT_INITIAL_PAGE_SIZE);
  correctionNewPageIndex$ = new BehaviorSubject<number>(Tables.DEFAULT_INITIAL_PAGE_INDEX);
  correctionNewSort$ = new Subject<Partial<MatSort>>();
  correctionRetrieveListError$ = new Subject<APIError | null>();

  private correctionPage() {
    return combineLatest([
      this.accountsPageStore.accountId$,
      this.correctionNewPageIndex$,
      this.correctionNewPageSize$,
      this.correctionNewSort$.pipe(
        startWith(({ active: undefined, direction: undefined }) as Partial<MatSort>),
        map((sort: Partial<MatSort>) => sort?.direction ? `${sort.active},${sort.direction}` : 'id,desc'),
      ),
      this.refresh$.pipe(startWith(1)),
      // this.accountsPageStore., // to refresh list on status change
    ]).pipe(
      switchMap(([paymentAccountId, page, size, sort]) =>
        this.correctionLinkingService.getAvailableCorrections(paymentAccountId, page, size, sort)),
      tap(data => Log.info('Fetched account corrections', this, data)),
      catchError(error => {
        this.correctionRetrieveListError$.next(error.error);
        return of({} as PagePaymentAccountCorrectionLinkingDTO);
      })
    );
  }

  deselectCorrection(correctionId: number): Observable<any> {
    return this.correctionLinkingService.deselectCorrectionFromPaymentAccount(correctionId)
      .pipe(
        tap(() => this.refresh$.next())
      );
  }

  selectCorrection(paymentAccountId: number, correctionId: number): Observable<any> {
    return this.correctionLinkingService.selectCorrectionToPaymentAccount(correctionId, paymentAccountId)
      .pipe(
        tap(() => this.refresh$.next())
      );
  }

  updateLinkedCorrection(correctionId: number, updateDTO: PaymentAccountCorrectionLinkingUpdateDTO): Observable<any> {
    return this.correctionLinkingService.updateLinkedCorrection(correctionId, updateDTO)
      .pipe(
        tap(() => this.refresh$.next())
      );
  }

  currentOverview() {
    return combineLatest([
      this.accountsPageStore.accountId$,
      this.refresh$.pipe(startWith(1)),
    ]).pipe(
      switchMap(([paymentAccountId]) => this.correctionLinkingService.getCurrentOverview(paymentAccountId)),
      tap(data => Log.info('Fetched current overview for Corrections tab', this, data))
    );
  }
}
