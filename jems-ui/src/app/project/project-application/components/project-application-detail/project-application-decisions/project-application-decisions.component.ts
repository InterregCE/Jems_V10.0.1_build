import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {ProjectDetailDTO, ProjectStatusDTO} from '@cat/api';
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
  project: ProjectDetailDTO;
  @Input()
  projectStatus: ProjectStatusDTO.StatusEnum;

  updateOrViewFundingLabel(): string {
    return this.projectStatus === ProjectStatusDTO.StatusEnum.APPROVEDWITHCONDITIONS
      ? 'project.assessment.fundingDecision.assessment.update'
      : 'project.assessment.fundingDecision.assessment.view';
  }
}
