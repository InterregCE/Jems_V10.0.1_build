import {ChangeDetectionStrategy, Component} from '@angular/core';
import {CallService, InputCallCreate, InputCallUpdate} from '@cat/api'
import {BaseComponent} from '@common/components/base-component';
import {catchError, flatMap, take, takeUntil, tap} from 'rxjs/operators';
import {Log} from '../../../common/utils/log';
import {ActivatedRoute, Router} from '@angular/router';
import {merge, of, Subject} from 'rxjs';
import {I18nValidationError} from '@common/validation/i18n-validation-error';
import {HttpErrorResponse} from '@angular/common/http';

@Component({
  selector: 'app-call-configuration',
  templateUrl: './call-configuration.component.html',
  styleUrls: ['./call-configuration.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CallConfigurationComponent extends BaseComponent {

  callId = this.activatedRoute?.snapshot?.params?.callId;
  callSaveError$ = new Subject<I18nValidationError | null>();
  saveCall$ = new Subject<InputCallUpdate>();

  private callById$ = this.callId
    ? this.callService.getCallById(this.callId)
      .pipe(
        tap(call => Log.info('Fetched call:', this, call))
      )
    : of({});

  private savedCall$ = this.saveCall$
    .pipe(
      flatMap(callUpdate => this.callService.updateCall(callUpdate)),
      tap(() => this.callSaveError$.next(null)),
      tap(() => this.redirectToCallOverview()),
      tap(saved => Log.info('Updated call:', this, saved)),
      catchError((error: HttpErrorResponse) => {
        this.callSaveError$.next(error.error);
        throw error;
      })
    );

  call$ = merge(this.callById$, this.savedCall$);

  constructor(private callService: CallService,
              private activatedRoute: ActivatedRoute,
              private router: Router) {
    super();
  }

  createCall(call: InputCallCreate): void {
    this.callService.createCall(call)
      .pipe(
        take(1),
        takeUntil(this.destroyed$),
        tap(() => this.callSaveError$.next(null)),
        tap(() => this.redirectToCallOverview()),
        tap(saved => Log.info('Created call:', this, saved)),
        catchError((error: HttpErrorResponse) => {
          this.callSaveError$.next(error.error);
          throw error;
        })
      )
      .subscribe();
  }

  redirectToCallOverview() {
    this.router.navigate(['/calls'])
  }
}
