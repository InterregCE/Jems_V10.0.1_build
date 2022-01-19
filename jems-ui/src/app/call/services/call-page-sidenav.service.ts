import {Injectable} from '@angular/core';
import {SideNavService} from '@common/components/side-nav/side-nav.service';
import {CallStore} from './call-store.service';
import {RoutingService} from '@common/services/routing.service';
import {I18nLabel} from '@common/i18n/i18n-label';
import {take} from 'rxjs/internal/operators';
import {distinctUntilChanged, filter, map, tap} from 'rxjs/operators';
import {HeadlineRoute} from '@common/components/side-nav/headline-route';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {CallDetailDTO} from '@cat/api';

@UntilDestroy()
@Injectable()
export class CallPageSidenavService {

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
    const flatRates = {
      headline: {i18nKey: 'call.detail.budget.settings'},
      route: `/app/call/detail/${callId}/budgetSettings`,
    };
    const applicationFormConfiguration = {
      headline: {i18nKey: 'call.detail.application.form.config.title'},
      route: `/app/call/detail/${callId}/applicationFormConfiguration`,
    };
    const preSubmissionCheckSettings = {
      headline: {i18nKey: 'call.detail.pre.submission.check.config.title'},
      route: `/app/call/detail/${callId}/preSubmissionCheckSettings`,
    };

    this.callStore.callIsReadable$
      .pipe(
        take(1),
        tap(callIsReadable => {
          if (callId && callIsReadable && call.type == CallDetailDTO.TypeEnum.STANDARD) { // TODO remove call type check after implementing MP2-2211
            bulletsArray.push(flatRates, applicationFormConfiguration, preSubmissionCheckSettings);
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
}
