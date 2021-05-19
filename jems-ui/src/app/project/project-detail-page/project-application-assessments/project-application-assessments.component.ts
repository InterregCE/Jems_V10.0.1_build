import {ChangeDetectionStrategy, Component, Input, OnChanges} from '@angular/core';
import {ProjectDecisionDTO, ProjectStatusDTO} from '@cat/api';
import {ProjectStepStatus} from '../project-step-status';
import {ProjectDetailPageStore} from '../project-detail-page-store';
import {Observable} from 'rxjs';
import {map} from 'rxjs/operators';

@Component({
  selector: 'app-project-application-assessments',
  templateUrl: './project-application-assessments.component.html',
  styleUrls: ['./project-application-assessments.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationAssessmentsComponent implements OnChanges {

  @Input()
  step: number;
  @Input()
  decisions: ProjectDecisionDTO;
  @Input()
  projectStatus: ProjectStatusDTO;

  STATUS = ProjectStatusDTO.StatusEnum;

  stepStatus: ProjectStepStatus;

  data$: Observable<{
    isProjectLatestVersion: boolean;
  }>;

  constructor(private projectDetailPageStore: ProjectDetailPageStore) {
    this.data$ = this.projectDetailPageStore.isProjectLatestVersion$
      .pipe(
        map(isProjectLatestVersion => ({isProjectLatestVersion}))
      );
  }

  ngOnChanges(): void {
    this.stepStatus = new ProjectStepStatus(this.step);
  }

}
