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

    onSubmit(formDirective: FormGroupDirective) {
    this.submitProjectApplication.emit({
      acronym: this.acronym.value,
      submissionDate: this.submissionDate.value
    });
    formDirective.resetForm();
  }

  getSubmissionDateError(): string {
    // backend validations
    if (this.error && this.error.i18nFieldErrors && this.error.i18nFieldErrors.submissionDate) {
      return this.error.i18nFieldErrors.submissionDate.i18nKey;
    }
    // frontend validations
    if (!this.submissionDate || !this.submissionDate.errors) {
      return '';
    }
    if (this.submissionDate.errors.pattern) {
      return 'project.submissionDate.should.be.valid';
    }
    if (this.submissionDate.errors.required) {
      return 'project.submissionDate.should.not.be.empty';
    }
    return '';
  }

  getAcronymError() {
    // backend validations
    if (this.error && this.error.i18nFieldErrors && this.error.i18nFieldErrors.acronym) {
      return this.error.i18nFieldErrors.acronym.i18nKey;
    }
    // frontend validations
    if (!this.acronym || !this.acronym.errors) {
      return '';
    }
    if (this.acronym.errors.maxlength) {
      return 'project.acronym.size.too.long';
    }
    if (this.acronym.errors.required) {
      return 'project.acronym.should.not.be.empty';
    }
    return '';
  }
}
