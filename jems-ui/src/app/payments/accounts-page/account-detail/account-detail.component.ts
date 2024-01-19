import {ChangeDetectionStrategy, Component} from '@angular/core';
import {UntilDestroy} from '@ngneat/until-destroy';
import {combineLatest, Observable} from 'rxjs';
import {PaymentAccountDTO} from '@cat/api';
import { Alert } from '@common/components/forms/alert';
import {RoutingService} from '@common/services/routing.service';
import {ActivatedRoute} from '@angular/router';
import {PaymentsPageSidenavService} from '../../payments-page-sidenav.service';
import {map} from 'rxjs/operators';
import {AccountsPageStore} from '../accounts-page.store';

@UntilDestroy()
@Component({
  selector: 'jems-account-detail',
  templateUrl: './account-detail.component.html',
  styleUrls: ['./account-detail.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AccountDetailComponent {
  Alert = Alert;
  accountStatusEnum = PaymentAccountDTO.StatusEnum;

  data$: Observable<{
    accountDetail: PaymentAccountDTO;
    userCanView: boolean;
    userCanEdit: boolean;
  }>;

  constructor(public pageStore: AccountsPageStore,
              private router: RoutingService,
              private activatedRoute: ActivatedRoute,
              private paymentsPageSidenav: PaymentsPageSidenavService) {
    this.data$ = combineLatest([
      this.pageStore.accountDetail$,
      this.pageStore.userCanEdit$,
      this.pageStore.userCanView$,
    ]).pipe(
      map(([accountDetail, userCanEdit, userCanView]) => ({
          accountDetail,
          userCanView,
          userCanEdit,
        })
      )
    );
  }

  activeTab(route: string): boolean {
    return this.router.url?.includes(route);
  }

  routeTo(route: string): void {
    this.router.navigate([route], {relativeTo: this.activatedRoute, queryParamsHandling: 'merge'});
  }

}
