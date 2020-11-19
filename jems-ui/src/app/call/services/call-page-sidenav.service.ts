import {Injectable} from '@angular/core';
import {SideNavService} from '@common/components/side-nav/side-nav.service';
import {CallStore} from './call-store.service';
import {RoutingService} from '../../common/services/routing.service';
import {I18nLabel} from '../../common/i18n/i18n-label';

@Injectable()
export class CallPageSidenavService {

  constructor(private sideNavService: SideNavService,
              private routingService: RoutingService) {
  }

  init(callId: number): void {
    this.setHeadlines(callId);
  }

  private setHeadlines(callId: number): void {
    const bulletsArray = [{
      headline: {i18nKey: 'call.identification.title'},
      route: '/app/call/detail/' + callId,
      scrollToTop: true,
      bullets: [
        {
          headline: {i18nKey: 'call.section.basic.data'},
          scrollRoute: 'callTitle',
          route: '/app/call/detail/' + callId,
        },
        {
          headline: {i18nKey: 'call.programme.priorities.title'},
          scrollRoute: 'callPriorities',
          route: '/app/call/detail/' + callId,
        },
        {
          headline: {i18nKey: 'call.strategy.title'},
          scrollRoute: 'callStrategies',
          route: '/app/call/detail/' + callId,
        },
        {
          headline: {i18nKey: 'call.funds.title'},
          scrollRoute: 'callFunds',
          route: '/app/call/detail/' + callId,
        }
      ]
    }
    ];

    const flatRates = {
      headline: {i18nKey: 'call.detail.flat.rates'},
      route: '/app/call/detail/' + callId + '/flatRates',
    };

    if (callId) {
      bulletsArray.push(flatRates as any);
    }

    this.sideNavService.setHeadlines(CallStore.CALL_DETAIL_PATH, [
      {
        headline: {i18nKey: 'call.detail.title'},
        bullets: bulletsArray
      },
    ]);
  }

  redirectToCallOverview(successMessage?: I18nLabel): void {
    this.routingService.navigate(['/app/call'], {state: {success: successMessage}});
  }
}
