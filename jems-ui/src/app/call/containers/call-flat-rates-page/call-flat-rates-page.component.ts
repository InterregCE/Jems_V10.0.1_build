import {ChangeDetectionStrategy, Component} from '@angular/core';
import {combineLatest, Subject} from 'rxjs';
import {catchError, map, startWith, take, takeUntil, tap} from 'rxjs/operators';
import {Permission} from '../../../security/permissions/permission';
import {CallStore} from '../../services/call-store.service';
import {ActivatedRoute} from '@angular/router';
import {PermissionService} from '../../../security/permissions/permission.service';
import {EventBusService} from '../../../common/services/event-bus/event-bus.service';
import {CallPageSidenavService} from '../../services/call-page-sidenav.service';
import {BaseComponent} from '@common/components/base-component';
import {CallService, OutputCall, InputCallFlatRateSetup} from '@cat/api';
import {Log} from '../../../common/utils/log';
import {CallPageComponent} from '../call-page/call-page.component';
import {HttpErrorResponse} from '@angular/common/http';
import {CallFlatRatesComponent} from '../../components/call-detail/call-flat-rates/call-flat-rates.component';

@Component({
  selector: 'app-call-flat-rates-page',
  templateUrl: './call-flat-rates-page.component.html',
  styleUrls: ['./call-flat-rates-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CallFlatRatesPageComponent extends BaseComponent {
  callId = this.activatedRoute?.snapshot?.params?.callId;
  canceledEdit$ = new Subject<void>();

  details$ = combineLatest([
    this.callStore.getCall(),
    this.permissionService.permissionsChanged(),
    this.canceledEdit$.pipe(startWith(null))
  ])
    .pipe(
      map(([call, permissions]) => ({
        call,
        applicantCanAccessCall: permissions[0] !== Permission.APPLICANT_USER
          || call.status !== OutputCall.StatusEnum.PUBLISHED,
      })),
      tap(details => {
        // applicant user cannot see published calls
        if (!details.applicantCanAccessCall) {
          this.redirectToCallOverview();
        }
      }),
    );

  constructor( public callStore: CallStore,
               private activatedRoute: ActivatedRoute,
               private permissionService: PermissionService,
               private eventBusService: EventBusService,
               private callNavService: CallPageSidenavService,
               private callService: CallService)
  {
    super();
    this.callStore.init(this.callId);
    this.callNavService.init(this.callId);
  }

  updateCallFlatRates(flatRates: InputCallFlatRateSetup[]): void {
    this.callService.updateCallFlatRateSetup(this.callId, flatRates)
      .pipe(
        take(1),
        takeUntil(this.destroyed$),
        tap(saved => Log.info('Created call:', this, saved)),
        tap(() => this.eventBusService.newSuccessMessage(
          CallFlatRatesComponent.ID,
          {i18nKey: 'call.detail.flat.rate.updated.success'}
          )
        ),
        catchError((error: HttpErrorResponse) => {
          this.eventBusService.newErrorMessage(CallFlatRatesComponent.ID, error.error);
          throw error;
        })
      )
      .subscribe();
  }

  cancel(): void {
    if (this.callId) {
      this.canceledEdit$.next();
    } else {
      this.redirectToCallOverview();
    }
  }

  redirectToCallOverview(): void {
    this.callNavService.redirectToCallOverview();
  }
}
