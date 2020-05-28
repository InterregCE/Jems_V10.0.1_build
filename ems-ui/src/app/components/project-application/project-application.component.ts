import {Component, OnInit, ViewChild} from '@angular/core';
import { InputProject, OutputProject } from '@cat/api';
import {MatTableDataSource} from '@angular/material/table';
import {ProjectApplicationSubmissionComponent} from './project-application-submission/project-application-submission.component';
import {ProjectApplicationService} from '../../services/project-application.service';

@Component({
  selector: 'app-project-application',
  templateUrl: 'project-application.component.html',
  styleUrls: ['project-application.component.scss']
})

export class ProjectApplicationComponent implements OnInit {

  @ViewChild(ProjectApplicationSubmissionComponent, {static: false}) projectSubmissionComponent: ProjectApplicationSubmissionComponent;

  ERROR_MESSAGE_FIELDS_REQUIRED = 'To create a project application, please enter acronym and submission date!';
  ERROR_MESSAGE_ACRONYM_TOO_LONG = 'Acronym is too long. Maximum 25 characters.';
  ERROR_MESSAGE_BAD_REQUEST = 'There was a problem saving the project application.';

  success = false;
  error = false;
  errorMessages: string[];
  dataSource: MatTableDataSource<OutputProject>;

  constructor(private projectService: ProjectApplicationService) {
  }

  ngOnInit(): void {
    this.getProjectsFromServer();
  }

  submitProjectApplication(project: InputProject) {
    this.error = false;
    this.success = false;
    this.errorMessages = [];
    if (!project.acronym || !project.submissionDate ) {
      this.error = true;
      this.errorMessages.push(this.ERROR_MESSAGE_FIELDS_REQUIRED);
    } else {
      this.projectService.addProject(project).toPromise()
        .then(() => {
          this.success = true;
          this.getProjectsFromServer();
          this.projectSubmissionComponent.resetFormFields();
        })
        .catch((response: any) => {
          this.error = true;
          this.projectSubmissionComponent.resetFormFields();
          if (response.error) {
            this.setErrorMessagesFromResponse(response.error);
          }
        });
    }
  }

  getProjectsFromServer(): void {
    this.projectService.getProjects(100).toPromise()
      .then((results) => {
        this.dataSource = new MatTableDataSource<OutputProject>(results.content);
      });
  }

  setErrorMessagesFromResponse(error: any) {
    if (error.acronym) {
      error.acronym.forEach((errorMessage: string) => {
        if (errorMessage === 'long') {
          this.errorMessages.push(this.ERROR_MESSAGE_ACRONYM_TOO_LONG);
        }
      });
    }
    if (this.errorMessages.length <= 0) {
      this.errorMessages.push(this.ERROR_MESSAGE_BAD_REQUEST);
    }
  }
}
