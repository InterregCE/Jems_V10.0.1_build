import {Component, Input} from '@angular/core';
import {OutputProject} from '@cat/api';

@Component({
  selector: 'app-project-application-information',
  templateUrl: './project-application-information.component.html',
  styleUrls: ['./project-application-information.component.scss']
})
export class ProjectApplicationInformationComponent{
  @Input()
  project: OutputProject;
}
