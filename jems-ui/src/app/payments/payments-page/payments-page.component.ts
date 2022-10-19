import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {PermissionService} from '../../security/permissions/permission.service';
import {Observable} from 'rxjs';
import {UserRoleDTO} from '@cat/api';
import {MatTabChangeEvent} from '@angular/material/tabs';

@Component({
  selector: 'jems-payments-page',
  templateUrl: './payments-page.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PaymentsPageComponent{

  PermissionEnum = UserRoleDTO.PermissionsEnum;

  userHasAccessToAdvancePayments$: Observable<Boolean>;
  userHasAccessToPaymentsToProjects$: Observable<Boolean>;
  static selectedTabIndex = 0;

  constructor(private permissionService: PermissionService) {
    this.userHasAccessToAdvancePayments$ = this.permissionService.hasPermission(UserRoleDTO.PermissionsEnum.AdvancePaymentsRetrieve);
    this.userHasAccessToPaymentsToProjects$ = this.permissionService.hasPermission(UserRoleDTO.PermissionsEnum.PaymentsRetrieve);
  }

  setDefaultTabIndex(selectedIndex: MatTabChangeEvent) {
    PaymentsPageComponent.selectedTabIndex = selectedIndex.index;
  }

  get selectedTabIndex() {
    return PaymentsPageComponent.selectedTabIndex;
  }
}
