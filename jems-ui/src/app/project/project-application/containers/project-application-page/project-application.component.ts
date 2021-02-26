import {ChangeDetectionStrategy, Component} from '@angular/core';
import {ProjectService} from '@cat/api';
import {Permission} from '../../../../security/permissions/permission';
import {SecurityService} from '../../../../security/security.service';

@Component({
  selector: 'app-project-application',
  templateUrl: 'project-application.component.html',
  styleUrls: ['project-application.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})

export class ProjectApplicationComponent {
  Permission = Permission;

  currentUser$ = this.securityService.currentUserDetails;

  constructor(private projectService: ProjectService,
              private securityService: SecurityService) {
  }
}
