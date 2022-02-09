import {Injectable} from '@angular/core';
import {SideNavService} from '@common/components/side-nav/side-nav.service';
import {CallStore} from './call-store.service';
import {RoutingService} from '@common/services/routing.service';
import {I18nLabel} from '@common/i18n/i18n-label';
import {distinctUntilChanged, filter, map, take, tap} from 'rxjs/operators';
import {HeadlineRoute} from '@common/components/side-nav/headline-route';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {CallDetailDTO} from '@cat/api';
import {CallPageSideNavConstants} from './call-page-sidenav.constants';

@UntilDestroy()
@Injectable()
export class CallPageSidenavService {

  private sideNavConstants = CallPageSideNavConstants;

  constructor(private sideNavService: SideNavService,
              private callStore: CallStore,
              private routingService: RoutingService) {
    this.callStore.call$
      .pipe(
        map(call => call),
        distinctUntilChanged(),
        filter(call => !!call?.id),
        tap(call => this.setHeadlines(call)),
        untilDestroyed(this)
      ).subscribe();
  }

  init(call: CallDetailDTO): void {
    this.setHeadlines(call);
  }

  private setHeadlines(call: CallDetailDTO): void {
    const callId = call.id;
    const bulletsArray: HeadlineRoute[] = [{
      headline: {i18nKey: 'call.general.settings'},
      route: `/app/call/detail/${callId}`,
      scrollToTop: true,
    }];
    this.callStore.callIsReadable$
      .pipe(
        take(1),
        tap(callIsReadable => {
          if (callId && callIsReadable) {
            bulletsArray.push(...this.getSideNavHeadlines(call.type, callId));
          }
          this.sideNavService.setHeadlines(CallStore.CALL_DETAIL_PATH, [
            {
              headline: {i18nKey: 'call.detail.title'},
              bullets: bulletsArray
            },
          ]);
        })
      ).subscribe();
  }

  redirectToCallOverview(successMessage?: I18nLabel): void {
    this.routingService.navigate(['/app/call'], {state: {success: successMessage}});
  }

  redirectToCallDetail(callId: number, callType: CallDetailDTO.TypeEnum, successMessage?: I18nLabel): void {
    this.routingService.navigate(['/app/call/detail/' + callId], {state: {success: successMessage}});
  }

  private getSideNavHeadlines(callType: CallDetailDTO.TypeEnum, callId: number): HeadlineRoute[] {
    switch (callType) {
      case CallDetailDTO.TypeEnum.STANDARD:
        return [
          this.sideNavConstants.FLAT_RATES(callId),
          this.sideNavConstants.APPLICATION_FORM_CONFIGURATION(callId),
          this.sideNavConstants.PRE_SUBMISSION_CHECK_SETTINGS(callId)
        ];
      case CallDetailDTO.TypeEnum.SPF:
        return [this.sideNavConstants.APPLICATION_FORM_CONFIGURATION(callId)];
      default:
        return [];
    }
  }

}
