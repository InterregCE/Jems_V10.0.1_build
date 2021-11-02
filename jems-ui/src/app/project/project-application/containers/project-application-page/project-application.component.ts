import {ChangeDetectionStrategy, Component} from '@angular/core';
import {ProjectService, UserRoleDTO} from '@cat/api';
import {SecurityService} from '../../../../security/security.service';
import PermissionsEnum = UserRoleDTO.PermissionsEnum;

@Component({
  selector: 'app-project-application',
  templateUrl: 'project-application.component.html',
  styleUrls: ['project-application.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})

export class ProjectApplicationComponent {
  PermissionsEnum = PermissionsEnum;

  currentUser$ = this.securityService.currentUserDetails;

  constructor(private projectService: ProjectService,
              private securityService: SecurityService) {
  }
}
