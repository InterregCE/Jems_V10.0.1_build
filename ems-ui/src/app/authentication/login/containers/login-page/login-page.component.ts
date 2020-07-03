import { Component } from '@angular/core';
import {Observable} from "rxjs";
import {I18nValidationError} from "@common/validation/i18n-validation-error";
import {LoginPageService} from "../../services/login-page-service";
import { LoginRequest } from '@cat/api';

@Component({
  selector: 'app-login-page',
  templateUrl: './login-page.component.html',
  styleUrls: ['./login-page.component.scss']
})
export class LoginPageComponent {

  authenticationError$: Observable<I18nValidationError | null> = this.loginPageService.authenticationError();

  constructor(private loginPageService: LoginPageService) { }

  login(loginRequest: LoginRequest): void {
    this.loginPageService.login(loginRequest);
  }

}
