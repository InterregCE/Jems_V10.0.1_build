import {ChangeDetectionStrategy, ChangeDetectorRef, Component, EventEmitter, Output} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {InputProject} from '@cat/api';
import {AbstractForm} from '@common/components/forms/abstract-form';

@Component({
  selector: 'app-project-application-submission',
  templateUrl: 'project-application-submission.component.html',
  styleUrls: ['project-application-submission.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})

export class ProjectApplicationSubmissionComponent extends AbstractForm {

  @Output()
  submitProjectApplication: EventEmitter<InputProject> = new EventEmitter<InputProject>();

  clearOnSuccess = true;

  submissionForm = this.formBuilder.group({
    acronym: ['', Validators.compose([
      Validators.required,
      Validators.maxLength(25)
    ])],
    submissionDate: ['', Validators.compose([
      Validators.required,
      Validators.pattern('^\\d{4}\\-(0?[1-9]|1[012])\\-(0?[1-9]|[12][0-9]|3[01])$')
    ])]
  });

  acronymErrors = {
    maxlength: 'project.acronym.size.too.long',
    required: 'project.acronym.should.not.be.empty'
  };

  submissionDateErrors = {
    pattern: 'project.submissionDate.should.be.valid',
    required: 'project.submissionDate.should.not.be.empty'
  };

  constructor(private formBuilder: FormBuilder,
              protected changeDetectorRef: ChangeDetectorRef) {
    super(changeDetectorRef);
  }


  getForm(): FormGroup | null {
    return this.submissionForm;
  }

  onSubmit(): void {
    this.submitProjectApplication.emit({
      acronym: this.submissionForm?.controls?.acronym?.value,
      submissionDate: this.submissionForm?.controls?.submissionDate.value
    });
  }
}
