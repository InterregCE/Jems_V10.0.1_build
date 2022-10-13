import {ChangeDetectionStrategy, Component} from '@angular/core';
import {combineLatest} from 'rxjs';
import {ProjectStore} from '../../project-application-detail/services/project-store.service';
import {ProjectApplicationFormStore} from '../services/project-application-form-store.service';
import {ActivatedRoute} from '@angular/router';
import {map} from 'rxjs/operators';
import {ProjectService} from '@cat/api';
import {
  ProjectPartnerStore
} from '@project/project-application/containers/project-application-form-page/services/project-partner-store.service';

@Component({
  selector: 'jems-project-application-form-identification-page',
  templateUrl: './project-application-form-identification-page.component.html',
  styleUrls: ['./project-application-form-identification-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormIdentificationPageComponent {
  projectId = this.activatedRoute?.snapshot?.params?.projectId;


  details$ = combineLatest([
    this.projectStore.project$,
    this.projectStore.projectForm$,
    this.projectStore.projectCallObjectives$,
    this.partnerStore.leadPartner$,
  ])
    .pipe(
      map(
        ([project, projectForm, callObjectives, leadPartner]) => ({project, projectForm, callObjectives, leadPartner})
      )
    );

  constructor(public projectStore: ProjectStore,
              private projectApplicationFormStore: ProjectApplicationFormStore,
              private projectService: ProjectService,
              private activatedRoute: ActivatedRoute,
              private partnerStore: ProjectPartnerStore) {
    this.projectApplicationFormStore.init(this.projectId);
  }

}
