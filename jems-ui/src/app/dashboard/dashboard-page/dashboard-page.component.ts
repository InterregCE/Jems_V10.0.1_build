import {ChangeDetectionStrategy, Component} from '@angular/core';
import {DashboardPageStore} from './dashboard-page-store.service';
import {UserRoleDTO} from '@cat/api';
import Permissions = UserRoleDTO.PermissionsEnum;

@Component({
  selector: 'app-dashboard-page',
  templateUrl: './dashboard-page.component.html',
  styleUrls: ['./dashboard-page.component.scss'],
  providers: [DashboardPageStore],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class DashboardPageComponent {
  Permissions = Permissions;

  constructor(public pageStore: DashboardPageStore) {
  }

}
