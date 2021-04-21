import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {ProjectDecisionDTO, ProjectStatusDTO} from '@cat/api';
import {Tables} from '../../../../../common/utils/tables';

@Component({
  selector: 'app-project-application-decisions',
  templateUrl: './project-application-decisions.component.html',
  styleUrls: ['./project-application-decisions.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationDecisionsComponent {
  STATUS = ProjectStatusDTO.StatusEnum;
  Tables = Tables;

  @Input()
  decisions: ProjectDecisionDTO;
  @Input()
  projectStatus: ProjectStatusDTO;

  updateOrViewFundingLabel(): string {
    return this.projectStatus.status === ProjectStatusDTO.StatusEnum.APPROVEDWITHCONDITIONS
      ? 'project.assessment.fundingDecision.assessment.update'
      : 'project.assessment.fundingDecision.assessment.view';
  }
}
