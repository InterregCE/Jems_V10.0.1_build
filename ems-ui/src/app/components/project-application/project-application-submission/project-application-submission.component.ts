import {Component, EventEmitter, Input, Output} from '@angular/core';
import {FormBuilder, FormGroupDirective, Validators} from '@angular/forms';
import {InputProject} from '@cat/api';
import {I18nValidationError} from '../../../common/i18n-validation-error';

@Component({
  selector: 'app-project-application-submission',
  templateUrl: 'project-application-submission.component.html',
  styleUrls: ['project-application-submission.component.scss']
})

export class ProjectApplicationSubmissionComponent {

  @Input()
  success: boolean;
  @Input()
  error: I18nValidationError;

  @Output()
  submitProjectApplication: EventEmitter<InputProject> = new EventEmitter<InputProject>();

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

  constructor(private formBuilder: FormBuilder) {
  }

  get submissionDate() {
    return this.submissionForm.controls.submissionDate;
  }

  get acronym() {
    return this.submissionForm.controls.acronym;
  }

  onSubmit(formDirective: FormGroupDirective): void {
    this.submitProjectApplication.emit({
      acronym: this.acronym.value,
      submissionDate: this.submissionDate.value
    });
    formDirective.resetForm();
  }

  getSubmissionDateError(): string {
    return this.error?.i18nFieldErrors?.submissionDate?.i18nKey
      || (this.submissionDate?.errors?.pattern && 'project.submissionDate.should.be.valid')
      || (this.submissionDate?.errors?.required && 'project.submissionDate.should.not.be.empty')
      || ''
  }

  getAcronymError(): string {
    return this.error?.i18nFieldErrors?.acronym?.i18nKey
      || (this.acronym?.errors?.maxlength && 'project.acronym.size.too.long')
      || (this.acronym?.errors?.required && 'project.acronym.should.not.be.empty')
      || ''
  }
}
