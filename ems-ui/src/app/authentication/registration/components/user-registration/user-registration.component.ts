import {ChangeDetectorRef, Component, EventEmitter, OnInit, Input, Output} from '@angular/core';
import {AbstractForm} from '@common/components/forms/abstract-form';
import {FormBuilder, FormGroup, FormGroupDirective, Validators} from '@angular/forms';
import {InputUserRegistration} from '@cat/api';
import {Observable} from 'rxjs';
import {Router} from '@angular/router';

@Component({
  selector: 'app-user-registration',
  templateUrl: './user-registration.component.html',
  styleUrls: ['./user-registration.component.scss']
})
export class UserRegistrationComponent extends AbstractForm implements OnInit{

  @Input()
  success: boolean;
  @Output()
  submitUser: EventEmitter<InputUserRegistration> = new EventEmitter<InputUserRegistration>();
  @Output()
  loginRedirect: EventEmitter<null> = new EventEmitter<null>();

  hide = true;

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
    maxlength: 'user.name.wrong.size',
    minlength: 'user.name.wrong.size',
    required: 'user.name.wrong.size'
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

  passwordErrors = {
    required: 'user.password.should.not.be.empty',
    minlength: 'user.password.wrong.size',
  };

  constructor(private formBuilder: FormBuilder,
              protected changeDetectorRef: ChangeDetectorRef,) {
    super(changeDetectorRef);
  }

  ngOnInit(): void {
    super.ngOnInit();
  }

  getForm(): FormGroup | null {
    return this.userForm;
  }

  onSubmit(formDirective: FormGroupDirective): void {
    this.submitUser.emit({
      name: this.userForm?.controls?.name?.value,
      surname: this.userForm?.controls?.surname?.value,
      email: this.userForm?.controls?.email.value,
      password: this.userForm?.controls?.password?.value
    });
    formDirective.resetForm();
  }

  redirectToLogin() {
    this.loginRedirect.emit();
  }
}
