import {ChangeDetectionStrategy, Component} from '@angular/core';
import {CallStore} from '../../services/call-store.service';
import {ActivatedRoute} from '@angular/router';
import {PermissionService} from '../../../security/permissions/permission.service';
import {CallPageSidenavService} from '../../services/call-page-sidenav.service';
import {ProgrammeCostOptionService} from '@cat/api';

@Component({
  selector: 'app-call-budget-settings-page',
  templateUrl: './call-budget-settings-page.component.html',
  styleUrls: ['./call-budget-settings-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CallBudgetSettingsPageComponent {
  callId = this.activatedRoute?.snapshot?.params?.callId;
  call$ = this.callStore.call$;

  constructor(public callStore: CallStore,
              private activatedRoute: ActivatedRoute,
              private permissionService: PermissionService,
              private programmeCostOptionService: ProgrammeCostOptionService,
              private callNavService: CallPageSidenavService) {
    this.callStore.init(this.callId);
    this.callNavService.init(this.callId);
  }
}
