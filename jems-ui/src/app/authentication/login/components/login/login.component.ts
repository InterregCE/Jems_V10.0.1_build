import {ChangeDetectorRef, Component, EventEmitter, Output} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {AbstractForm} from '@common/components/forms/abstract-form';
import { LoginRequest } from '@cat/api';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
})
export class LoginComponent extends AbstractForm {

  isChristmas = false;

  @Output()
  submitLogin: EventEmitter<LoginRequest> = new EventEmitter<LoginRequest>();

  hide = true;
  loginForm = this.formBuilder.group({
    email: ['', Validators.required],
    password: ['', Validators.required]
  });

  registerLink = '/no-auth/register';

  constructor(private readonly formBuilder: FormBuilder,
              protected changeDetectorRef: ChangeDetectorRef) {
    super(changeDetectorRef);
    const today = new Date();
    this.isChristmas = today.getMonth() === 11 && today.getDate() >= 24;
  }

  onSubmit(): void {
    this.submitted = true;
    this.submitLogin.emit({
      email: this.loginForm.controls.email.value,
      password: this.loginForm.controls.password.value
    });
  }

  getForm(): FormGroup | null {
    return this.loginForm;
  }
}
