import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {ProjectDetailDTO, ProjectStatusDTO} from '@cat/api';

@Component({
  selector: 'app-project-application-assessments',
  templateUrl: './project-application-assessments.component.html',
  styleUrls: ['./project-application-assessments.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationAssessmentsComponent {
  STATUS = ProjectStatusDTO.StatusEnum;

  @Input()
  project: ProjectDetailDTO;
  @Input()
  projectStatus: ProjectStatusDTO.StatusEnum;
}
