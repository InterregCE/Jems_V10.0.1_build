import {Component, OnInit, ViewChild} from '@angular/core';
import {InputProject, OutputProject} from '@cat/api';
import {MatTableDataSource} from '@angular/material/table';
import {ProjectApplicationSubmissionComponent} from './project-application-submission/project-application-submission.component';
import {ProjectApplicationService} from '../../services/project-application.service';
import {I18nValidationError} from '../../common/i18n-validation-error';

@Component({
  selector: 'app-project-application',
  templateUrl: 'project-application.component.html',
  styleUrls: ['project-application.component.scss']
})

export class ProjectApplicationComponent implements OnInit {

  @ViewChild(ProjectApplicationSubmissionComponent, {static: false}) projectSubmissionComponent: ProjectApplicationSubmissionComponent;

  success = false;
  error: I18nValidationError;
  dataSource: MatTableDataSource<OutputProject>;

  constructor(private projectService: ProjectApplicationService) {
  }

  ngOnInit(): void {
    this.getProjectsFromServer();
  }

  submitProjectApplication(project: InputProject) {
    this.success = false;
    this.projectService.addProject(project).toPromise()
      .then(() => {
        this.success = true;
        this.getProjectsFromServer();
      })
      .catch((response: any) => {
        this.error = response.error;
      });
  }

  getProjectsFromServer(): void {
    this.projectService.getProjects(100).toPromise()
      .then((results) => {
        this.dataSource = new MatTableDataSource<OutputProject>(results.content);
      });
  }
}
