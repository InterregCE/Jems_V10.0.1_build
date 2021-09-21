import {Injectable} from '@angular/core';
import {AllowedRealCostsDTO, CallService} from '@cat/api';
import {merge, Observable, Subject} from 'rxjs';
import {CallStore} from '../services/call-store.service';
import {filter, switchMap, tap} from 'rxjs/operators';
import {Log} from '@common/utils/log';

@Injectable()
export class CallBudgetSettingsPageStore {

  allowedRealCosts$: Observable<AllowedRealCostsDTO>;
  callIsEditable$: Observable<boolean>;
  callIsPublished$: Observable<boolean>;

  private allowedRealCostsSaved$ = new Subject<AllowedRealCostsDTO>();

  constructor(private callService: CallService,
              private callStore: CallStore) {
    this.allowedRealCosts$ = this.allowedRealCosts();
    this.callIsEditable$ = this.callStore.callIsEditable$;
    this.callIsPublished$ = this.callStore.callIsPublished$;
  }

  updateAllowedRealCosts(allowedRealCosts: AllowedRealCostsDTO): Observable<AllowedRealCostsDTO> {
    return this.callStore.call$
      .pipe(
        filter(call => !!call?.id),
        switchMap(call => this.callService.updateAllowedRealCosts(call.id, allowedRealCosts)),
        tap(realCosts => this.allowedRealCostsSaved$.next(realCosts)),
        tap(realCosts => Log.info('Updated call allow real costs', this, realCosts))
      );
  }

  private allowedRealCosts(): Observable<AllowedRealCostsDTO> {
    const initialAllowedRealCosts$ = this.callStore.call$
      .pipe(
        filter(call => !!call?.id),
        switchMap(call => this.callService.getAllowedRealCosts(call.id)),
        tap(allowedRealCosts => Log.info('Fetched call allow real costs', this, allowedRealCosts))
      );

    return merge(initialAllowedRealCosts$, this.allowedRealCostsSaved$);
  }
}
