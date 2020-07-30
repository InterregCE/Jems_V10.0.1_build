import {ChangeDetectionStrategy, Component} from '@angular/core';
import {CallService, InputCallCreate, InputCallUpdate, OutputCall} from '@cat/api'
import {BaseComponent} from '@common/components/base-component';
import {catchError, flatMap, map, take, takeUntil, tap} from 'rxjs/operators';
import {Log} from '../../../common/utils/log';
import {ActivatedRoute, Router} from '@angular/router';
import {combineLatest, merge, of, Subject} from 'rxjs';
import {I18nValidationError} from '@common/validation/i18n-validation-error';
import {HttpErrorResponse} from '@angular/common/http';
import {CallStore} from '../../services/call-store.service';
import {Permission} from '../../../security/permissions/permission';
import {PermissionService} from '../../../security/permissions/permission.service';

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
  publishCall$ = new Subject<number>();

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

  private publishedCall$ = this.publishCall$
    .pipe(
      flatMap(callUpdate => this.callService.publishCall(callUpdate)),
      tap(() => this.callSaveError$.next(null)),
      tap(() => this.redirectToCallOverview()),
      tap(published => this.callStore.callPublished(published)),
      tap(saved => Log.info('Published call:', this, saved)),
      catchError((error: HttpErrorResponse) => {
        this.callSaveError$.next(error.error);
        throw error;
      })
    );

  details$ = combineLatest([
    merge(this.callById$, this.savedCall$, this.publishedCall$),
    this.permissionService.permissionsChanged()
  ])
    .pipe(
      map(([call, permissions]) => ({
        call,
        applicantCanAccessCall: permissions[0] !== Permission.APPLICANT_USER
          || (call as OutputCall).status !== OutputCall.StatusEnum.PUBLISHED
      })),
      tap(details => {
        // applicant user cannot see published calls
        if (!details.applicantCanAccessCall) {
          this.redirectToCallOverview();
        }
      })
    );

  constructor(private callService: CallService,
              private callStore: CallStore,
              private activatedRoute: ActivatedRoute,
              private permissionService: PermissionService,
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
