import {ChangeDetectionStrategy, Component} from '@angular/core';
import {ProjectDescriptionService} from '@cat/api';
import {ProjectApplicationFormStore} from '../services/project-application-form-store.service';
import {ActivatedRoute} from '@angular/router';
import {ProjectStore} from '../../project-application-detail/services/project-store.service';

@Component({
  selector: 'jems-project-application-form-management-section',
  templateUrl: './project-application-form-management-section.component.html',
  styleUrls: ['./project-application-form-management-section.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormManagementSectionComponent {
  projectId = this.activatedRoute?.snapshot?.params?.projectId;

  projectManagement$ = this.projectApplicationFormStore.projectManagement$;

  constructor(public projectStore: ProjectStore,
              private activatedRoute: ActivatedRoute,
              private projectDescriptionService: ProjectDescriptionService,
              private projectApplicationFormStore: ProjectApplicationFormStore) {
    this.projectApplicationFormStore.init(this.projectId);
  }

}
