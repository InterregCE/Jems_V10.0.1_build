import {Injectable} from '@angular/core';
import {CallService, InputCallUpdate, OutputCall, InputCallCreate, InputCallFlatRateSetup} from '@cat/api';
import {Observable, ReplaySubject, Subject} from 'rxjs';
import {tap} from 'rxjs/operators';
import {Log} from '../../common/utils/log';

@Injectable()
export class CallStore {
  public static CALL_DETAIL_PATH = '/app/call/detail';
  private callId: number;
  call$ = new ReplaySubject<OutputCall | any>(1);

  constructor(private callService: CallService) {
  }

  init(callId: number | string): void {
    if (callId && callId === this.callId) {
      return;
    }
    if (!callId) {
      this.call$.next({});
      return;
    }
    this.callId = Number(callId);
    this.callService.getCallById(this.callId)
      .pipe(
        tap(call => Log.info('Fetched project call:', this, call)),
        tap(call => this.call$.next(call)),
      ).subscribe();
  }

  saveCall(call: InputCallUpdate): Observable<OutputCall> {
    return this.callService.updateCall(call)
      .pipe(
        tap(saved => this.call$.next(saved)),
        tap(saved => Log.info('Updated call:', this, saved))
      );
  }

  createCall(call: InputCallCreate): Observable<OutputCall> {
    return this.callService.createCall(call)
      .pipe(
        tap(created => this.call$.next(created)),
        tap(created => Log.info('Created call:', this, created)),
      );
  }

  publishCall(callId: number): Observable<OutputCall> {
    return this.callService.publishCall(callId)
      .pipe(
        tap(saved => this.call$.next(saved)),
        tap(saved => Log.info('Published call:', this, saved)),
      );
  }

  saveFlatRates(flatRates: InputCallFlatRateSetup[]): Observable<OutputCall> {
    return this.callService.updateCallFlatRateSetup(this.callId, flatRates)
      .pipe(
        tap(saved => Log.info('Updated call flat rates:', this, saved))
      );
  }
}
