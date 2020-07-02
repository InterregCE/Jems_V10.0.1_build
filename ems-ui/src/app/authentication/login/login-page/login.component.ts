import {Component} from '@angular/core';
import {FormBuilder, Validators} from '@angular/forms';
import {I18nValidationError} from '@common/validation/i18n-validation-error';
import {Observable} from 'rxjs';
import {LoginPageService} from '../services/login-page-service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
})
export class LoginComponent {

  loginForm = this.formBuilder.group({
    email: ['', Validators.required],
    password: ['', Validators.required]
  });

  authenticationError: Observable<I18nValidationError | null> = this.loginPageService.authenticationError();
  registerLink = '/register';

  constructor(private formBuilder: FormBuilder,
              private loginPageService: LoginPageService) {
  }

  onSubmit() {
    this.loginPageService.login({
      email: this.loginForm.controls.email.value,
      password: this.loginForm.controls.password.value
    });
  }
}
