import {Injectable} from '@angular/core';
import {CallService, InputCallUpdate, OutputCall} from '@cat/api'
import {merge, Observable, of, ReplaySubject, Subject} from 'rxjs';
import {catchError, distinctUntilChanged, mergeMap, switchMap, tap} from 'rxjs/operators';
import {Log} from '../../common/utils/log';
import {HttpErrorResponse} from '@angular/common/http';
import {CallDetailComponent} from '../components/call-detail/call-detail.component';
import {EventBusService} from '../../common/services/event-bus/event-bus.service';

@Injectable()
export class CallStore {
  private callId$ = new ReplaySubject<number>(1);
  private callName$ = new ReplaySubject<string>(1);

  saveCall$ = new Subject<InputCallUpdate>();

  private callById$ = this.callId$
    .pipe(
      distinctUntilChanged(),
      mergeMap(id => id ? this.callService.getCallById(id) : of({})),
      tap(call => Log.info('Fetched call:', this, call)),
      tap((call: OutputCall) => {
        if (call.name) this.callName$.next(call.name);
      }),
    );

  private savedCall$ = this.saveCall$
    .pipe(
      switchMap(callUpdate =>
        this.callService.updateCall(callUpdate)
          .pipe(
            tap(() => this.eventBusService.newSuccessMessage(
              CallDetailComponent.name, 'call.detail.save.success')
            ),
            tap(saved => Log.info('Updated call:', this, saved)),
            tap(saved => this.callName$.next(saved.name)),
            catchError((error: HttpErrorResponse) => {
              this.eventBusService.newErrorMessage(CallDetailComponent.name, error.error);
              return this.getCall();
            })
          )
      ),
    );

  constructor(private callService: CallService,
              private eventBusService: EventBusService) {
  }

  init(callId: number | string) {
    this.callId$.next(Number(callId));
  }

  getCall(): Observable<OutputCall> {
    return merge(this.callById$, this.savedCall$);
  }

  getCallName(): Observable<string> {
    return this.callName$.asObservable();
  }
}
