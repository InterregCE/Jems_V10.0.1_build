import {Injectable} from '@angular/core';
import {combineLatest, Observable} from 'rxjs';
import {
  AmountWithdrawnPerPriorityDTO,
  PaymentAccountWithdrawnService,
  ProgrammePriorityDTO,
  ProgrammePriorityService
} from '@cat/api';
import {AccountsPageStore} from '../../accounts-page.store';
import {switchMap, tap} from 'rxjs/operators';
import {Log} from '@common/utils/log';


@Injectable()
export class AccountsWithdrawnStoreService {
  accountWithdrawalOverview$: Observable<AmountWithdrawnPerPriorityDTO[]>;
  programmePriorities$: Observable<ProgrammePriorityDTO[]>;

  constructor(private accountWithdrawnService: PaymentAccountWithdrawnService,
              private pageStore: AccountsPageStore,
              private programmePriorityService: ProgrammePriorityService
  ) {
    this.accountWithdrawalOverview$ = this.getWithdrawnOverview();
    this.programmePriorities$ = this.getProgrammePriorities();
  }

  getWithdrawnOverview(): Observable<AmountWithdrawnPerPriorityDTO[]> {
    return combineLatest([
      this.pageStore.accountId$
    ]).pipe(
      switchMap(([accountId]) => this.accountWithdrawnService.getWithdrawnOverview(accountId)),
      tap(data => Log.info('Fetched payment account withdrawal overview', this, data)),
    );
  }

  getProgrammePriorities(): Observable<ProgrammePriorityDTO[]> {
    return this.programmePriorityService.get().pipe(
      tap(data => Log.info('Fetched programme priorities', this, data)),
    );
  }
}
