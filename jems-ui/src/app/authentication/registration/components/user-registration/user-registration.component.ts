import {ChangeDetectorRef, Component, EventEmitter, OnInit, Output} from '@angular/core';
import {AbstractForm} from '@common/components/forms/abstract-form';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {UserRegistrationDTO} from '@cat/api';
import {TranslateService} from '@ngx-translate/core';
import {UserPasswordComponent} from '../../../../system/user-page/user-detail-page/user-password/user-password.component';


@Component({
  selector: 'app-user-registration',
  templateUrl: './user-registration.component.html',
  styleUrls: ['./user-registration.component.scss']
})
export class UserRegistrationComponent extends AbstractForm implements OnInit {
  @Output()
  submitUser: EventEmitter<UserRegistrationDTO> = new EventEmitter<UserRegistrationDTO>();
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
      Validators.pattern(UserPasswordComponent.PASSWORD_REGEX)
    ])],
  });

  emailErrors = {
    email: 'user.email.wrong.format'
  };

  passwordErrors = {
    pattern: 'user.password.constraints.not.satisfied',
  };

  constructor(private formBuilder: FormBuilder,
              protected changeDetectorRef: ChangeDetectorRef,
              protected translationService: TranslateService
  ) {
    super(changeDetectorRef, translationService);
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
