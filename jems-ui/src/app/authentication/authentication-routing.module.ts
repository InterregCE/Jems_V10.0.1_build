import {Routes} from '@angular/router';
import {RegistrationPageComponent} from './registration/containers/registration-page/registration-page.component';
import {LoginPageComponent} from './login/containers/login-page/login-page.component';
import {NoDoubleLoginGuard} from './service/no-double-login-guard.service';
import {ConfirmationComponent} from './confirmation/confirmation.component';

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
    path: 'registrationConfirmation',
    component: ConfirmationComponent,
  }
];
