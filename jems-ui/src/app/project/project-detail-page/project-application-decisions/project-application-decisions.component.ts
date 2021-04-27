import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {ProjectDecisionDTO, ProjectStatusDTO} from '@cat/api';
import {ProjectStepStatus} from '../project-step-status';

@Component({
  selector: 'app-project-application-decisions',
  templateUrl: './project-application-decisions.component.html',
  styleUrls: ['./project-application-decisions.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationDecisionsComponent {
  STATUS = ProjectStatusDTO.StatusEnum;

  @Input()
  callHasTwoSteps: boolean;
  @Input()
  step: number;
  @Input()
  decisions: ProjectDecisionDTO;
  @Input()
  projectStatus: ProjectStatusDTO;

  stepStatus = new ProjectStepStatus(this.step);

  updateOrViewFundingLabel(): string {
    return this.decisions?.fundingDecision?.status === ProjectStatusDTO.StatusEnum.APPROVEDWITHCONDITIONS
      ? 'project.assessment.fundingDecision.assessment.update'
      : 'project.assessment.fundingDecision.assessment.view';
  }

  fundingDecisionEnabled(): boolean {
    return (this.decisions?.eligibilityDecision?.status === this.STATUS.ELIGIBLE
      || this.decisions?.eligibilityDecision?.status === this.STATUS.STEP1ELIGIBLE)
      && !!this.decisions?.qualityAssessment;
  }
}
