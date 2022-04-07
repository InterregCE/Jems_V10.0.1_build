import {NgModule} from '@angular/core';
import {LoginComponent} from './login/components/login/login.component';
import {SharedModule} from '@common/shared-module';
import {RegistrationPageComponent} from './registration/containers/registration-page/registration-page.component';
import {UserRegistrationComponent} from './registration/components/user-registration/user-registration.component';
import {RegistrationPageService} from './registration/services/registration-page.service';
import {LoginPageComponent} from './login/containers/login-page/login-page.component';
import {RouterModule} from '@angular/router';
import {routes} from './authentication-routing.module';
import {NoDoubleLoginGuard} from './service/no-double-login-guard.service';
import {LoginPageService} from './login/services/login-page-service';
import {ConfirmationComponent} from './confirmation/confirmation.component';
import { ForgotPasswordPageComponent } from './forgot-password-page/forgot-password-page.component';
import { ResetPasswordPageComponent } from './reset-password-page/reset-password-page.component';

@NgModule({
  declarations: [
    LoginComponent,
    LoginPageComponent,
    RegistrationPageComponent,
    UserRegistrationComponent,
    ConfirmationComponent,
    ForgotPasswordPageComponent,
    ResetPasswordPageComponent,
  ],
  imports: [
    SharedModule,
    RouterModule.forChild(routes)
  ],
  providers: [
    RegistrationPageService,
    NoDoubleLoginGuard,
    LoginPageService
  ]
})
export class AuthenticationModule {
}
