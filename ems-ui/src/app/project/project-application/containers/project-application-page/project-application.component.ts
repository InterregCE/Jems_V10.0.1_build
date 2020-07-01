import {Component, OnInit, ViewChild} from '@angular/core';
import {InputProject, OutputProject, ProjectService} from '@cat/api';
import {MatTableDataSource} from '@angular/material/table';
import {ProjectApplicationSubmissionComponent} from '../../components/project-application-submission/project-application-submission.component';
import {I18nValidationError} from '@common/validation/i18n-validation-error';
import {Observable, Subject} from 'rxjs';

@Component({
  selector: 'app-project-application',
  templateUrl: 'project-application.component.html',
  styleUrls: ['project-application.component.scss']
})

export class ProjectApplicationComponent implements OnInit {

  @ViewChild(ProjectApplicationSubmissionComponent) projectSubmissionComponent: ProjectApplicationSubmissionComponent;

  success = false;
  error: I18nValidationError;
  dataSource: MatTableDataSource<OutputProject>;
  disableButton$ = new Subject<boolean>();

  constructor(private projectService: ProjectService) {
  }

  ngOnInit(): void {
    this.getProjectsFromServer();
  }

  disableButton(): Observable<boolean> {
    return this.disableButton$.asObservable();
  }

  submitProjectApplication(project: InputProject) {
    this.disableButton$.next(true);
    this.success = false;
    this.projectService.createProject(project).toPromise()
      .then(() => {
        this.disableButton$.next(false);
        this.success = true;
        this.getProjectsFromServer();
      })
      .catch((response: any) => {
        this.disableButton$.next(false);
        this.error = response.error;
      });
  }

  getProjectsFromServer(): void {
    this.projectService.getProjects(0, 100, 'id,desc').toPromise()
      .then((results) => {
        this.dataSource = new MatTableDataSource<OutputProject>(results.content);
      });
  }
}
