import {ChangeDetectionStrategy, Component} from '@angular/core';
import {Observable} from 'rxjs';
import {OutputProjectLongTermPlans, ProjectDescriptionService} from '@cat/api';
import {map} from 'rxjs/operators';
import {ProjectApplicationFormStore} from '../services/project-application-form-store.service';
import {ActivatedRoute} from '@angular/router';
import {ProjectStore} from '../../project-application-detail/services/project-store.service';

@Component({
  selector: 'jems-project-application-form-future-plans-section',
  templateUrl: './project-application-form-future-plans-section.component.html',
  styleUrls: ['./project-application-form-future-plans-section.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormFuturePlansSectionComponent {
  projectId = this.activatedRoute?.snapshot?.params?.projectId;

  projectLongTermPlans$ = this.projectApplicationFormStore.projectLongTermPlans$;

  constructor(public projectStore: ProjectStore,
              private activatedRoute: ActivatedRoute,
              private projectDescriptionService: ProjectDescriptionService,
              private projectApplicationFormStore: ProjectApplicationFormStore) {
    this.projectApplicationFormStore.init(this.projectId);
  }
}
