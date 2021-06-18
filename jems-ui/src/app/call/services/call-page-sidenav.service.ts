import {Injectable} from '@angular/core';
import {SideNavService} from '@common/components/side-nav/side-nav.service';
import {CallStore} from './call-store.service';
import {RoutingService} from '../../common/services/routing.service';
import {I18nLabel} from '../../common/i18n/i18n-label';
import {take} from 'rxjs/internal/operators';
import {tap} from 'rxjs/operators';

@Injectable()
export class CallPageSidenavService {

  constructor(private sideNavService: SideNavService,
              private callStore: CallStore,
              private routingService: RoutingService) {
  }

  init(callId: number): void {
    this.setHeadlines(callId);
  }

  private setHeadlines(callId: number): void {
    const bulletsArray = [
      {
        headline: {i18nKey: 'call.general.settings'},
        route: `/app/call/detail/${callId}`,
        scrollToTop: true,
      }
    ];

    const flatRates = {
      headline: {i18nKey: 'call.detail.budget.settings'},
      route: `/app/call/detail/${callId}/budgetSettings`,
    };

    this.callStore.userCannotAccessCalls$
      .pipe(
        take(1),
        tap(userCannotAccessCalls => {
          const userCanAccessCalls = !userCannotAccessCalls;
          if (callId && userCanAccessCalls) {
            bulletsArray.push(flatRates as any);
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

  redirectToCallDetail(callId: number, successMessage?: I18nLabel): void {
    this.routingService.navigate(['/app/call/detail/' + callId], {state: {success: successMessage}});
  }
}
