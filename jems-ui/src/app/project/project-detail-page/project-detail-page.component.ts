import {ChangeDetectionStrategy, Component} from '@angular/core';
import {ProjectDetailPageStore} from './project-detail-page-store';
import {OutputProjectFile, ProjectStatusDTO} from '@cat/api';
import {ActivatedRoute} from '@angular/router';
import {Permission} from '../../security/permissions/permission';
import StatusEnum = ProjectStatusDTO.StatusEnum;

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
  StatusEnum = StatusEnum;

  projectId = this.activatedRoute.snapshot.params.projectId;

  constructor(public projectDetailStore: ProjectDetailPageStore,
              private activatedRoute: ActivatedRoute) {
  }

}
