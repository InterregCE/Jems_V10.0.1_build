import {
  ChangeDetectionStrategy,
  Component,
  EventEmitter,
  Input,
  OnChanges,
  OnInit,
  Output,
  SimpleChanges
} from '@angular/core';
import {I18nValidationError} from '@common/validation/i18n-validation-error';
import {FormBuilder, FormGroupDirective, Validators} from '@angular/forms';
import {OutputUserRole, InputUser} from '@cat/api';


@Component({
  selector: 'app-user-submission',
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './user-submission.component.html',
  styleUrls: ['./user-submission.component.scss']
})
export class UserSubmissionComponent implements OnInit, OnChanges {

  @Input()
  success: boolean;
  @Input()
  error: I18nValidationError | null;
  @Input()
  userRoles: OutputUserRole[];

  @Output()
  submitUser: EventEmitter<InputUser> = new EventEmitter<InputUser>();

  userForm = this.formBuilder.group({
    name: ['', Validators.compose([
      Validators.required,
      Validators.maxLength(50),
      Validators.minLength(2),
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

  constructor(private formBuilder: FormBuilder) {
  }

  get name() {
    return this.userForm.controls.name;
  }

  get surname() {
    return this.userForm.controls.surname;
  }

  get email() {
    return this.userForm.controls.email;
  }

  get role() {
    return this.userForm.controls.role;
  }

  ngOnInit(): void {
    this.success = false;
    this.email.valueChanges.subscribe(() => {
      if ( this.error?.i18nFieldErrors?.email) {
        this.error.i18nFieldErrors.email = null;
      }
    })
  }

  ngOnChanges(changes: SimpleChanges) {
    // TODO this should be improved
    if (changes?.error?.currentValue?.i18nFieldErrors?.email) {
      this.email.markAsTouched();
    }
  }

  onSubmit(formDirective: FormGroupDirective): void {
    this.submitUser.emit({
      name: this.name.value,
      surname: this.surname.value,
      email: this.email.value,
      accountRoleId: this.role.value.id
    });
    formDirective.resetForm();
  }

  getNameError(): string {
    return this.error?.i18nFieldErrors?.name?.i18nKey
      || (this.name?.errors?.maxlength && 'user.name.wrong.size')
      || (this.name?.errors?.minlength && 'user.name.wrong.size')
      || (this.name?.errors?.required && 'user.name.should.not.be.empty')
      || ''
  }

  getSurnameError(): string {
    return this.error?.i18nFieldErrors?.surname?.i18nKey
      || (this.surname?.errors?.maxlength && 'user.surname.wrong.size')
      || (this.surname?.errors?.minlength && 'user.surname.wrong.size')
      || (this.surname?.errors?.required && 'user.surname.wrong.size')
      || ''
  }

  getEmailError(): string {
    return this.error?.i18nFieldErrors?.email?.i18nKey
      || (this.email?.errors?.maxlength && 'user.email.wrong.size')
      || (this.email?.errors?.email && 'user.email.wrong.format')
      || (this.email?.errors?.required && 'user.email.should.not.be.empty')
      || ''
  }

  getRoleError(): string {
    return this.error?.i18nFieldErrors?.role?.i18nKey || ''
  }
}
