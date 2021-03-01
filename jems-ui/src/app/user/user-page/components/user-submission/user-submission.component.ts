import {ChangeDetectionStrategy, ChangeDetectorRef, Component, EventEmitter, Input, Output} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {InputUserCreate, OutputUserRole} from '@cat/api';
import {AbstractForm} from '@common/components/forms/abstract-form';
import {TranslateService} from '@ngx-translate/core';


@Component({
  selector: 'app-user-submission',
  templateUrl: './user-submission.component.html',
  styleUrls: ['./user-submission.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class UserSubmissionComponent extends AbstractForm {
  private static readonly USER_NAME_WRONG_SIZE = 'user.name.wrong.size';
  private static readonly USER_SURNAME_WRONG_SIZE = 'user.surname.wrong.size';

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
    maxlength: UserSubmissionComponent.USER_NAME_WRONG_SIZE,
    minlength: UserSubmissionComponent.USER_NAME_WRONG_SIZE,
    required: UserSubmissionComponent.USER_NAME_WRONG_SIZE,
  };

  surnameErrors = {
    maxlength: UserSubmissionComponent.USER_SURNAME_WRONG_SIZE,
    minlength: UserSubmissionComponent.USER_SURNAME_WRONG_SIZE,
    required: UserSubmissionComponent.USER_SURNAME_WRONG_SIZE,
  };

  emailErrors = {
    required: 'user.email.should.not.be.empty',
    maxlength: 'user.email.wrong.size',
    email: 'user.email.wrong.format'
  };

  roleErrors = {
    required: 'user.userRoleId.should.not.be.empty'
  };

  constructor(private formBuilder: FormBuilder,
              protected changeDetectorRef: ChangeDetectorRef,
              protected translationService: TranslateService
  ) {
    super(changeDetectorRef, translationService);
  }

  getForm(): FormGroup | null {
    return this.userForm;
  }

  onSubmit(): void {
    this.submitted = true;
    this.submitUser.emit({
      name: this.userForm?.controls?.name?.value,
      surname: this.userForm?.controls?.surname?.value,
      email: this.userForm?.controls?.email.value,
      userRoleId: this.userForm?.controls?.role?.value?.id
    });
    this.userForm.reset();
  }
}
