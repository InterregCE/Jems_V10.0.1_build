import {Component} from '@angular/core';
import {FormBuilder} from '@angular/forms';
import {ProjectService} from 'build/generated-sources/openapi/api/project.service';
import {InputProject} from 'build/generated-sources/openapi/model/models';

@Component({
  selector: 'app-project-application-submission',
  templateUrl: 'project-application-submission.component.html'
})

export class ProjectApplicationSubmissionComponent {
  project = {} as InputProject;
  success = false;
  error = false;

  constructor(private formBuilder: FormBuilder,
              private projectService: ProjectService) {
  }

  onSubmit() {
    this.error = false;
    this.success = false;
    this.projectService.createProject(this.project).toPromise()
      .then(() => {
        this.success = true;
      })
      .catch(() => {
        this.error = true;
    })
      .finally(() => {
        this.project = {} as InputProject;
      });
  }
}
