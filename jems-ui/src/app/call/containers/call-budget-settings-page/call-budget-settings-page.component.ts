import {ChangeDetectionStrategy, Component} from '@angular/core';
import {combineLatest} from 'rxjs';
import {map} from 'rxjs/operators';
import {Permission} from '../../../security/permissions/permission';
import {CallStore} from '../../services/call-store.service';
import {ActivatedRoute} from '@angular/router';
import {PermissionService} from '../../../security/permissions/permission.service';
import {CallPageSidenavService} from '../../services/call-page-sidenav.service';
import {ProgrammeCostOptionService, OutputCall} from '@cat/api';

@Component({
  selector: 'app-call-budget-settings-page',
  templateUrl: './call-budget-settings-page.component.html',
  styleUrls: ['./call-budget-settings-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CallBudgetSettingsPageComponent {
  callId = this.activatedRoute?.snapshot?.params?.callId;

  call$ = combineLatest([
    this.callStore.call$,
    this.permissionService.permissionsChanged()
  ])
    .pipe(
      map(([call, permissions]) => {
        if (permissions[0] === Permission.APPLICANT_USER && call.status === OutputCall.StatusEnum.PUBLISHED) {
          this.callNavService.redirectToCallOverview();
        }
        return call;
      }),
    );

  constructor(public callStore: CallStore,
              private activatedRoute: ActivatedRoute,
              private permissionService: PermissionService,
              private programmeCostOptionService: ProgrammeCostOptionService,
              private callNavService: CallPageSidenavService) {
    this.callStore.init(this.callId);
    this.callNavService.init(this.callId);
  }
}
