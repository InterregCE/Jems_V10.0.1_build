import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {OutputProject, OutputProjectStatus} from '@cat/api';
import {Tables} from 'src/app/common/utils/tables';

@Component({
  selector: 'app-project-application-decisions',
  templateUrl: './project-application-decisions.component.html',
  styleUrls: ['./project-application-decisions.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationDecisionsComponent {
  OutputProjectStatus = OutputProjectStatus;
  Tables = Tables;

  @Input()
  project: OutputProject;
  @Input()
  projectStatus: OutputProjectStatus.StatusEnum;

  updateOrViewFundingLabel(): string {
    return this.projectStatus === OutputProjectStatus.StatusEnum.APPROVEDWITHCONDITIONS
      ? 'project.assessment.fundingDecision.assessment.update'
      : 'project.assessment.fundingDecision.assessment.view'
  }
}
