import {ChangeDetectionStrategy, Component, Input, OnChanges} from '@angular/core';
import {ProjectDecisionDTO, ProjectStatusDTO} from '@cat/api';
import {ProjectStepStatus} from '../project-step-status';

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

  ngOnChanges(): void {
    this.stepStatus = new ProjectStepStatus(this.step);
  }

}
