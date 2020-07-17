import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {OutputProject, OutputProjectStatus} from '@cat/api';

@Component({
  selector: 'app-project-application-decisions',
  templateUrl: './project-application-decisions.component.html',
  styleUrls: ['./project-application-decisions.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationDecisionsComponent {
  OutputProjectStatus = OutputProjectStatus;

  @Input()
  project: OutputProject;
  @Input()
  projectStatus: OutputProjectStatus.StatusEnum;

}
