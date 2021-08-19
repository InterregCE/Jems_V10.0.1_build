import {Injectable} from '@angular/core';
import {AllowRealCostsDTO, CallService} from '@cat/api';
import {merge, Observable, Subject} from 'rxjs';
import {CallStore} from '../services/call-store.service';
import {filter, switchMap, tap} from 'rxjs/operators';
import {Log} from '@common/utils/log';

@Injectable()
export class CallBudgetSettingsPageStore {

  allowRealCosts$: Observable<AllowRealCostsDTO>;
  callIsEditable$: Observable<boolean>;

  private allowRealCostsSaved$ = new Subject<AllowRealCostsDTO>();

  constructor(private callService: CallService,
              private callStore: CallStore) {
    this.allowRealCosts$ = this.allowRealCosts();
    this.callIsEditable$ = this.callStore.callIsEditable$;
  }

  updateAllowRealCosts(allowRealCosts: AllowRealCostsDTO): Observable<AllowRealCostsDTO> {
    return this.callStore.call$
      .pipe(
        filter(call => !!call?.id),
        switchMap(call => this.callService.updateAllowRealCosts(call.id, allowRealCosts)),
        tap(realCosts => this.allowRealCostsSaved$.next(realCosts)),
        tap(realCosts => Log.info('Updated call allow real costs', this, realCosts))
      );
  }

  private allowRealCosts(): Observable<AllowRealCostsDTO> {
    const initialAllowRealCosts$ = this.callStore.call$
      .pipe(
        filter(call => !!call?.id),
        switchMap(call => this.callService.getAllowRealCosts(call.id)),
        tap(allowRealCosts => Log.info('Fetched call allow real costs', this, allowRealCosts))
      );

    return merge(initialAllowRealCosts$, this.allowRealCostsSaved$);
  }
}
