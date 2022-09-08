import {ChangeDetectionStrategy, Component} from '@angular/core';
import {ProjectDescriptionService} from '@cat/api';
import {ProjectApplicationFormStore} from '../services/project-application-form-store.service';
import {ProjectStore} from '../../project-application-detail/services/project-store.service';
import {ActivatedRoute} from '@angular/router';

@Component({
  selector: 'jems-project-application-form-project-partnership-section',
  templateUrl: './project-application-form-project-partnership-section.component.html',
  styleUrls: ['./project-application-form-project-partnership-section.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormProjectPartnershipSectionComponent {
  projectId = this.activatedRoute?.snapshot?.params?.projectId;

  projectPartnership$ = this.projectApplicationFormStore.projectPartnership$;

  constructor(public projectStore: ProjectStore,
              private activatedRoute: ActivatedRoute,
              private projectDescriptionService: ProjectDescriptionService,
              private projectApplicationFormStore: ProjectApplicationFormStore) {
    this.projectApplicationFormStore.init(this.projectId);
  }

}
