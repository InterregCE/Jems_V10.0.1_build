import {Routes} from '@angular/router';
import {RegistrationPageComponent} from './registration/containers/registration-page/registration-page.component';
import {LoginPageComponent} from './login/containers/login-page/login-page.component';
import {NoDoubleLoginGuard} from './service/no-double-login-guard.service';
import {ConfirmationComponent} from './confirmation/confirmation.component';
import {ForgotPasswordPageComponent} from './forgot-password-page/forgot-password-page.component';
import {ResetPasswordPageComponent} from './reset-password-page/reset-password-page.component';

export const routes: Routes = [
  {
    path: 'login',
    component: LoginPageComponent,
    canActivate: [NoDoubleLoginGuard]
  },
  {
    path: 'register',
    component: RegistrationPageComponent,
  },
  {
    path: 'forgotPassword',
    component: ForgotPasswordPageComponent,

  },
  {
    path: 'resetPassword',
    component: ResetPasswordPageComponent,
  },
  {
    path: 'registrationConfirmation',
    component: ConfirmationComponent,
  }
];
