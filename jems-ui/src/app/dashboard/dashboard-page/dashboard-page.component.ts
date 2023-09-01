import {ChangeDetectionStrategy, Component} from '@angular/core';
import {DashboardPageStore} from './dashboard-page-store.service';
import {UserRoleDTO} from '@cat/api';
import Permissions = UserRoleDTO.PermissionsEnum;

@Component({
  selector: 'jems-dashboard-page',
  templateUrl: './dashboard-page.component.html',
  styleUrls: ['./dashboard-page.component.scss'],
  providers: [DashboardPageStore],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class DashboardPageComponent {
  Permissions = Permissions;
  currentApplicationPageSize = 10;
  currentCallPageSize = 5;

  constructor(public pageStore: DashboardPageStore) {
  }

}
