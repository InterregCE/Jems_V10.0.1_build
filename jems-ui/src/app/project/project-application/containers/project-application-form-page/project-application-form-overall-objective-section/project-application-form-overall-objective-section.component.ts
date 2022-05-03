import {ChangeDetectionStrategy, Component} from '@angular/core';
import {Permission} from '../../../../../security/permissions/permission';
import {combineLatest} from 'rxjs';
import {map} from 'rxjs/operators';
import {ProjectDescriptionService} from '@cat/api';
import {ProjectApplicationFormStore} from '../services/project-application-form-store.service';
import {ProjectStore} from '../../project-application-detail/services/project-store.service';
import {ActivatedRoute} from '@angular/router';

@Component({
  selector: 'jems-project-application-form-overall-objective-section',
  templateUrl: './project-application-form-overall-objective-section.component.html',
  styleUrls: ['./project-application-form-overall-objective-section.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormOverallObjectiveSectionComponent {
  Permission = Permission;
  projectId = this.activatedRoute?.snapshot?.params?.projectId;

  details$ = combineLatest([
    this.projectApplicationFormStore.projectDescription$,
    this.projectStore.project$
  ])
    .pipe(
      map(([projectDescription, project]) => ({
        projectDescription,
        project
      }))
    );

  constructor(public projectStore: ProjectStore,
              private activatedRoute: ActivatedRoute,
              private projectDescriptionService: ProjectDescriptionService,
              private projectApplicationFormStore: ProjectApplicationFormStore) {
    this.projectApplicationFormStore.init(this.projectId);
  }
}
