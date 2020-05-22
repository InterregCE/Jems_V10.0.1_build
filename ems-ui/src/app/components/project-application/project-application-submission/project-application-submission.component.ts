import {Component, ViewChild} from '@angular/core';
import {FormBuilder, NgForm} from '@angular/forms';
import {ProjectService} from 'build/generated-sources/openapi/api/project.service';
import {InputProject} from 'build/generated-sources/openapi/model/models';

@Component({
  selector: 'app-project-application-submission',
  templateUrl: 'project-application-submission.component.html'
})

export class ProjectApplicationSubmissionComponent {
  @ViewChild('projectApplicationSubmitForm', {static: false}) projectApplicationSubmitForm: NgForm;

  project = {} as InputProject;
  success = false;
  serverError = false;
  inputError = false;

  constructor(private formBuilder: FormBuilder,
              private projectService: ProjectService) {
  }

  onSubmit() {
    this.serverError = false;
    this.success = false;
    this.inputError = false;
    if (!this.project.acronym || !this.project.submissionDate ) {
      this.inputError = true;
    } else {
      this.projectService.createProject(this.project).toPromise()
        .then(() => {
          this.success = true;
        })
        .catch(() => {
          this.serverError = true;
        })
        .finally(() => {
          this.project = {} as InputProject;
          this.projectApplicationSubmitForm.resetForm();
        });
    }
  }
}
