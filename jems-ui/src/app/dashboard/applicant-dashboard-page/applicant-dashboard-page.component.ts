import {ChangeDetectionStrategy, Component} from '@angular/core';
import {ApplicantDashboardPageStore} from './applicant-dashboard-page-store.service';
import {UserRoleDTO} from '@cat/api';
import Permissions = UserRoleDTO.PermissionsEnum;

@Component({
  selector: 'app-applicant-dashboard-page',
  templateUrl: './applicant-dashboard-page.component.html',
  styleUrls: ['./applicant-dashboard-page.component.scss'],
  providers: [ApplicantDashboardPageStore],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ApplicantDashboardPageComponent {
  Permissions = Permissions;

  constructor(public pageStore: ApplicantDashboardPageStore) {
  }

}
