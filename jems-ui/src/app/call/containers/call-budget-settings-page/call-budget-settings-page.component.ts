import {ChangeDetectionStrategy, Component} from '@angular/core';
import {CallStore} from '../../services/call-store.service';
import {CallPageSidenavService} from '../../services/call-page-sidenav.service';

@Component({
  selector: 'app-call-budget-settings-page',
  templateUrl: './call-budget-settings-page.component.html',
  styleUrls: ['./call-budget-settings-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CallBudgetSettingsPageComponent {
  call$ = this.callStore.call$;

  constructor(public callStore: CallStore,
              private callNavService: CallPageSidenavService) {
  }
}
