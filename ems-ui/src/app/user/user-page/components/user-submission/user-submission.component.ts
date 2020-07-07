import {ChangeDetectionStrategy, ChangeDetectorRef, Component, EventEmitter, Input, Output} from '@angular/core';
import {FormBuilder, FormGroup, FormGroupDirective, Validators} from '@angular/forms';
import {InputUserCreate, OutputUserRole} from '@cat/api';
import {AbstractForm} from '@common/components/forms/abstract-form';


@Component({
  selector: 'app-user-submission',
  templateUrl: './user-submission.component.html',
  styleUrls: ['./user-submission.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class UserSubmissionComponent extends AbstractForm {

  @Input()
  userRoles: OutputUserRole[];
  @Output()
  submitUser: EventEmitter<InputUserCreate> = new EventEmitter<InputUserCreate>();

  clearOnSuccess = true;

  userForm = this.formBuilder.group({
    name: ['', Validators.compose([
      Validators.required,
      Validators.maxLength(50),
      Validators.minLength(1),
    ])],
    surname: ['', Validators.compose([
      Validators.required,
      Validators.maxLength(50),
      Validators.minLength(1),
    ])],
    email: ['', Validators.compose([
      Validators.required,
      Validators.maxLength(255),
      Validators.email,
    ])],
    role: ['', Validators.required]
  });

  nameErrors = {
    maxlength: 'user.name.wrong.size',
    minlength: 'user.name.wrong.size',
    required: 'user.name.wrong.size',
  };

  surnameErrors = {
    maxlength: 'user.surname.wrong.size',
    minlength: 'user.surname.wrong.size',
    required: 'user.surname.wrong.size'
  };

  emailErrors = {
    required:'user.email.should.not.be.empty',
    maxlength: 'user.email.wrong.size',
    email: 'user.email.wrong.format'
  };

  roleErrors = {
    required: 'user.userRoleId.should.not.be.empty'
  };

  constructor(private formBuilder: FormBuilder,
              protected changeDetectorRef: ChangeDetectorRef) {
    super(changeDetectorRef);
  }

  getForm(): FormGroup | null {
    return this.userForm;
  }

  onSubmit(formDirective: FormGroupDirective): void {
    this.submitted = true;
    this.submitUser.emit({
      name: this.userForm?.controls?.name?.value,
      surname: this.userForm?.controls?.surname?.value,
      email: this.userForm?.controls?.email.value,
      userRoleId: this.userForm?.controls?.role?.value?.id
    });
    formDirective.resetForm();
  }
}
