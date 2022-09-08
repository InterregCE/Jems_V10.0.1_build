import {Injectable} from '@angular/core';
import {AllowedRealCostsDTO, CallCostOptionDTO, CallService} from '@cat/api';
import {merge, Observable, Subject} from 'rxjs';
import {CallStore} from '../services/call-store.service';
import {filter, switchMap, tap} from 'rxjs/operators';
import {Log} from '@common/utils/log';

@Injectable()
export class CallBudgetSettingsPageStore {

  allowedRealCosts$: Observable<AllowedRealCostsDTO>;
  allowedCostOptions$: Observable<CallCostOptionDTO>;
  callIsEditable$: Observable<boolean>;
  callIsPublished$: Observable<boolean>;
  isSPFCall$: Observable<boolean>;

  private allowedRealCostsSaved$ = new Subject<AllowedRealCostsDTO>();
  private allowedCostOptionsSaved$ = new Subject<CallCostOptionDTO>();

  constructor(private callService: CallService,
              private callStore: CallStore) {
    this.allowedRealCosts$ = this.allowedRealCosts();
    this.allowedCostOptions$ = this.allowedCostOptions();
    this.callIsEditable$ = this.callStore.callIsEditable$;
    this.callIsPublished$ = this.callStore.callIsPublished$;
    this.isSPFCall$ = this.callStore.isSPFCall();
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

  updateAllowedCostOptions(allowedCostOptions: CallCostOptionDTO): Observable<CallCostOptionDTO> {
    return this.callStore.call$
      .pipe(
        filter(call => !!call?.id),
        switchMap(call => this.callService.updateAllowedCostOption(call.id, allowedCostOptions)),
        tap(costOptions => this.allowedCostOptionsSaved$.next(costOptions)),
        tap(costOptions => Log.info('Updated call allow cost options', this, costOptions))
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

  private allowedCostOptions(): Observable<CallCostOptionDTO> {
    const initialAllowedRealCosts$ = this.callStore.call$
      .pipe(
        filter(call => !!call?.id),
        switchMap(call => this.callService.getAllowedCostOptions(call.id)),
        tap(allowedCostOptions => Log.info('Fetched call allow cost options', this, allowedCostOptions))
      );

    return merge(initialAllowedRealCosts$, this.allowedCostOptionsSaved$);
  }
}
