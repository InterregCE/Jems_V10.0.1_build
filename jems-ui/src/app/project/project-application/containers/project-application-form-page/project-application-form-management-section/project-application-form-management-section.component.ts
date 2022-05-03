import {ChangeDetectionStrategy, Component} from '@angular/core';
import {Observable} from 'rxjs';
import {map} from 'rxjs/operators';
import {OutputProjectManagement, ProjectDescriptionService} from '@cat/api';
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

  projectManagement$: Observable<OutputProjectManagement> = this.projectApplicationFormStore.getProjectDescription()
    .pipe(
      map(project => project.projectManagement || {})
    );

  constructor(public projectStore: ProjectStore,
              private activatedRoute: ActivatedRoute,
              private projectDescriptionService: ProjectDescriptionService,
              private projectApplicationFormStore: ProjectApplicationFormStore) {
    this.projectApplicationFormStore.init(this.projectId);
  }

}
