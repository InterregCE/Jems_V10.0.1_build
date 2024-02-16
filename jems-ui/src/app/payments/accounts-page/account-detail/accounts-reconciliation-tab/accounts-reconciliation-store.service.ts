import {Injectable} from '@angular/core';
import {combineLatest, Observable, Subject} from 'rxjs';
import {
  PaymentAccountReconciliationAPIService,
  ProgrammePriorityDTO,
  ProgrammePriorityService, ReconciledAmountPerPriorityDTO, ReconciledAmountUpdateDTO
} from '@cat/api';
import {AccountsPageStore} from '../../accounts-page.store';
import {startWith, switchMap, tap} from 'rxjs/operators';
import {Log} from '@common/utils/log';


@Injectable()
export class AccountsReconciliationStoreService {
  accountReconciliationOverview$: Observable<ReconciledAmountPerPriorityDTO[]>;
  programmePriorities$: Observable<ProgrammePriorityDTO[]>;
  refresh$ = new Subject<void>();

  constructor(private accountReconciliationService: PaymentAccountReconciliationAPIService,
              private pageStore: AccountsPageStore,
              private programmePriorityService: ProgrammePriorityService
  ) {
    this.accountReconciliationOverview$ = this.getReconciliationOverview();
    this.programmePriorities$ = this.getProgrammePriorities();
  }

  getReconciliationOverview(): Observable<ReconciledAmountPerPriorityDTO[]> {
    return combineLatest([
      this.pageStore.accountId$,
      this.refresh$.pipe(startWith(1))
    ]).pipe(
      switchMap(([accountId]) => this.accountReconciliationService.getReconciliationOverview(accountId)),
      tap(data => Log.info('Fetched payment account reconciliation overview', this, data)),
    );
  }

  getProgrammePriorities(): Observable<ProgrammePriorityDTO[]> {
    return this.programmePriorityService.get().pipe(
      tap(data => Log.info('Fetched programme priorities', this, data)),
    );
  }

  updateReconciliation(updateDTO: ReconciledAmountUpdateDTO): Observable<any> {
    return combineLatest([
      this.pageStore.accountId$
    ]).pipe(
      switchMap(([accountId]) => this.accountReconciliationService.updateReconciliationComment(accountId, updateDTO)),
        tap(() => this.refresh$.next())
    );
  }
}
