import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {ProjectDecisionDTO, ProjectStatusDTO} from '@cat/api';
import {ProjectStepStatus} from '../project-step-status';

@Component({
  selector: 'app-project-application-assessments',
  templateUrl: './project-application-assessments.component.html',
  styleUrls: ['./project-application-assessments.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationAssessmentsComponent {
  @Input()
  step: number;
  @Input()
  decisions: ProjectDecisionDTO;
  @Input()
  projectStatus: ProjectStatusDTO;

  stepStatus = new ProjectStepStatus(this.step);
}
