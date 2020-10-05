import {NgModule} from '@angular/core';
import {LoginComponent} from './login/components/login/login.component';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatCardModule} from '@angular/material/card';
import {CoreModule} from '../common/core-module';
import {SharedModule} from '../common/shared-module';
import {RegistrationPageComponent} from './registration/containers/registration-page/registration-page.component';
import {UserRegistrationComponent} from './registration/components/user-registration/user-registration.component';
import {RegistrationPageService} from './registration/services/registration-page.service';
import {LoginPageComponent} from './login/containers/login-page/login-page.component';
import {RouterModule} from '@angular/router';
import {routes} from './authentication-routing.module';
import {CommonModule} from '@angular/common';
import {NoDoubleLoginGuard} from './service/no-double-login-guard.service';
import {LoginPageService} from './login/services/login-page-service';

@NgModule({
  declarations: [
    LoginComponent,
    LoginPageComponent,
    RegistrationPageComponent,
    UserRegistrationComponent,
  ],
  imports: [
    CommonModule,
    SharedModule,
    CoreModule,
    RouterModule.forChild(routes),
    MatCardModule,
    MatFormFieldModule,
    MatCardModule,
  ],
  providers: [
    RegistrationPageService,
    NoDoubleLoginGuard,
    LoginPageService
  ]
})
export class AuthenticationModule {
}
