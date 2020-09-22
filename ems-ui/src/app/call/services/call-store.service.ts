import {Injectable} from '@angular/core';
import {CallService, InputCallUpdate, OutputCall} from '@cat/api'
import {merge, Observable, of, ReplaySubject, Subject} from 'rxjs';
import {I18nValidationError} from '@common/validation/i18n-validation-error';
import {catchError, distinctUntilChanged, flatMap, tap} from 'rxjs/operators';
import {Log} from '../../common/utils/log';
import {HttpErrorResponse} from '@angular/common/http';

@Injectable()
export class CallStore {
  private callId$ = new ReplaySubject<number>(1);
  private publishedCall$ = new ReplaySubject<string | null>(1);
  private callName$ = new ReplaySubject<string>(1);

  callSaveSuccess$ = new Subject<boolean>()
  callSaveError$ = new Subject<I18nValidationError | null>();
  saveCall$ = new Subject<InputCallUpdate>();

  private callById$ = this.callId$
    .pipe(
      distinctUntilChanged(),
      flatMap(id => id ? this.callService.getCallById(id) : of({})),
      tap(call => Log.info('Fetched call:', this, call)),
      tap((call: OutputCall) => {
        if (call.name) this.callName$.next(call.name);
      }),
    );

  private savedCall$ = this.saveCall$
    .pipe(
      flatMap(callUpdate => this.callService.updateCall(callUpdate)),
      tap(() => this.callSaveError$.next(null)),
      tap(() => this.callSaveSuccess$.next(true)),
      tap(saved => Log.info('Updated call:', this, saved)),
      tap(saved => this.callName$.next(saved.name)),
      catchError((error: HttpErrorResponse) => {
        this.callSaveError$.next(error.error);
        throw error;
      })
    );

  constructor(private callService: CallService) {
  }

  init(callId: number | string) {
    this.callId$.next(Number(callId));
  }

  getCall(): Observable<OutputCall> {
    return merge(this.callById$, this.savedCall$);
  }

  getCallById(): Observable<OutputCall> {
    return this.callById$;
  }

  callPublished(call: OutputCall): void {
    this.publishedCall$.next(call?.name);
    setTimeout(() => this.publishedCall$.next(null), 4000);
  }

  publishedCall(): Observable<string | null> {
    return this.publishedCall$.asObservable();
  }

  getCallName(): Observable<string> {
    return this.callName$.asObservable();
  }
}
