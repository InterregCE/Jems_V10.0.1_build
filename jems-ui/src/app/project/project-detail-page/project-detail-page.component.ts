import {ChangeDetectionStrategy, Component} from '@angular/core';
import {ProjectDetailPageStore} from './project-detail-page-store';
import {OutputProjectFile, ProjectStatusDTO, UserRoleDTO} from '@cat/api';
import {ProjectApplicationFormSidenavService} from '../project-application/containers/project-application-form-page/services/project-application-form-sidenav.service';
import {ActivatedRoute} from '@angular/router';
import {Permission} from '../../security/permissions/permission';

@Component({
  selector: 'app-project-detail-page',
  templateUrl: './project-detail-page.component.html',
  styleUrls: ['./project-detail-page.component.scss'],
  providers: [ProjectDetailPageStore],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectDetailPageComponent {
  fileType = OutputProjectFile.TypeEnum;
  Permission = Permission;
  STATUS = ProjectStatusDTO.StatusEnum;
  // tslint:disable-next-line:variable-name
  Permissions = UserRoleDTO.PermissionsEnum;

  projectId = this.activatedRoute.snapshot.params.projectId;

  constructor(public projectDetailStore: ProjectDetailPageStore,
              private activatedRoute: ActivatedRoute,
              private sidenavService: ProjectApplicationFormSidenavService) {
  }

}
