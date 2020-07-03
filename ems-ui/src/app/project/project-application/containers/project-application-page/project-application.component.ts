import {Component, OnInit, ViewChild} from '@angular/core';
import {InputProject, OutputProject, OutputUser, UserService, ProjectService} from '@cat/api';
import {MatTableDataSource} from '@angular/material/table';
import {ProjectApplicationSubmissionComponent} from '../../components/project-application-submission/project-application-submission.component';
import {I18nValidationError} from '@common/validation/i18n-validation-error';
import {Permission} from '../../../../security/permissions/permission';
import {AuthenticationHolder} from '../../../../security/authentication-holder.service';

@Component({
  selector: 'app-project-application',
  templateUrl: 'project-application.component.html',
  styleUrls: ['project-application.component.scss']
})

export class ProjectApplicationComponent implements OnInit {
  Permission = Permission;

  @ViewChild(ProjectApplicationSubmissionComponent) projectSubmissionComponent: ProjectApplicationSubmissionComponent;

  success = false;
  submitted = false;
  error: I18nValidationError;
  dataSource: MatTableDataSource<OutputProject>;
  currentUser: OutputUser | null = null;

  constructor(private projectService: ProjectService,
              private userService: UserService,
              private authenticationHolder: AuthenticationHolder) {
  }

  ngOnInit(): void {
    this.getCurrentUser();
    this.getProjectsFromServer();
  }
  getCurrentUser(): void {
    if (this.authenticationHolder.currentUserId) {
      this.userService.getById(this.authenticationHolder.currentUserId)
        .subscribe((user: OutputUser) => this.currentUser = user);
    }
  }

  submitProjectApplication(project: InputProject) {
    this.success = false;
    this.submitted = true;
    this.projectService.createProject(project).toPromise()
      .then(() => {
        this.success = true;
        this.submitted = false;
        this.getProjectsFromServer();
      })
      .catch((response: any) => {
        this.error = response.error;
        this.submitted = false;
      });
  }

  getProjectsFromServer(): void {
    this.projectService.getProjects(0, 100, 'id,desc').toPromise()
      .then((results) => {
        this.dataSource = new MatTableDataSource<OutputProject>(results.content);
      });
  }
}
