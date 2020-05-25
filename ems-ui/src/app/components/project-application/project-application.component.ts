import {Component, OnInit, ViewChild} from '@angular/core';
import { InputProject, OutputProject } from '@cat/api';
import {MatTableDataSource} from '@angular/material/table';
import {ProjectApplicationSubmissionComponent} from './project-application-submission/project-application-submission.component';
import {ProjectApplicationService} from '../../services/project-application.service';

@Component({
  selector: 'app-project-application',
  templateUrl: 'project-application.component.html'
})

export class ProjectApplicationComponent implements OnInit {

  @ViewChild(ProjectApplicationSubmissionComponent, {static: false}) projectSubmissionComponent: ProjectApplicationSubmissionComponent;

  success = false;
  inputError = false;
  serverError = false;
  dataSource: MatTableDataSource<OutputProject>;

  constructor(private projectService: ProjectApplicationService) {
  }

  ngOnInit(): void {
    this.getProjectsFromServer();
  }

  submitProjectApplication(project: InputProject) {
    this.serverError = false;
    this.success = false;
    this.inputError = false;
    if (!project.acronym || !project.submissionDate ) {
      this.inputError = true;
    } else {
      this.projectService.addProject(project).toPromise()
        .then(() => {
          this.success = true;
          this.getProjectsFromServer();
          this.projectSubmissionComponent.resetFormFields();
        })
        .catch(() => {
          this.serverError = true;
          this.projectSubmissionComponent.resetFormFields();
        });
    }
  }

  getProjectsFromServer(): void {
    this.projectService.getProjects(100).toPromise()
      .then((results) => {
        this.dataSource = new MatTableDataSource<OutputProject>(results.content);
      });
  }
}
