import {ChangeDetectorRef, Component, EventEmitter, OnInit, Output} from '@angular/core';
import {AbstractForm} from '@common/components/forms/abstract-form';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {InputUserRegistration} from '@cat/api';

@Component({
  selector: 'app-user-registration',
  templateUrl: './user-registration.component.html',
  styleUrls: ['./user-registration.component.scss']
})
export class UserRegistrationComponent extends AbstractForm implements OnInit{

  private static readonly USER_NAME_WRONG_SIZE = 'user.name.wrong.size';
  private static readonly USER_SURNAME_WRONG_SIZE = 'user.surname.wrong.size';

  @Output()
  submitUser: EventEmitter<InputUserRegistration> = new EventEmitter<InputUserRegistration>();
  @Output()
  loginRedirect: EventEmitter<null> = new EventEmitter<null>();

  hide = true;
  clearOnSuccess = true;
  permanentSuccessAlert = true;

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
    password: ['', Validators.compose([
      Validators.required,
      Validators.minLength(10),
    ])],
  });

  nameErrors = {
    maxlength: UserRegistrationComponent.USER_NAME_WRONG_SIZE,
    minlength: UserRegistrationComponent.USER_NAME_WRONG_SIZE,
    required: UserRegistrationComponent.USER_NAME_WRONG_SIZE,
  };

  surnameErrors = {
    maxlength: UserRegistrationComponent.USER_SURNAME_WRONG_SIZE,
    minlength: UserRegistrationComponent.USER_SURNAME_WRONG_SIZE,
    required: UserRegistrationComponent.USER_SURNAME_WRONG_SIZE,
  };

  emailErrors = {
    required: 'user.email.should.not.be.empty',
    maxlength: 'user.email.wrong.size',
    email: 'user.email.wrong.format'
  };

  passwordErrors = {
    required: 'user.password.should.not.be.empty',
    minlength: 'user.password.wrong.size',
  };

  constructor(private formBuilder: FormBuilder,
              protected changeDetectorRef: ChangeDetectorRef) {
    super(changeDetectorRef);
  }

  ngOnInit(): void {
    super.ngOnInit();
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
      password: this.userForm?.controls?.password?.value
    });
  }

  redirectToLogin(): void {
    this.loginRedirect.emit();
  }
}
