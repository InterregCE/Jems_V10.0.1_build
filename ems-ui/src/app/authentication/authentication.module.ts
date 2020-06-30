import {NgModule} from '@angular/core';
import {AuthenticationRoutingModule} from './authentication-routing.module';
import {LoginComponent} from './login/login-page/login.component';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatCardModule} from '@angular/material/card';
import {CoreModule} from '../common/core-module';
import {SharedModule} from '../common/shared-module';
import {LoginPageService} from './login/services/login-page-service';
import { RegistrationPageComponent } from './registration/containers/registration-page/registration-page.component';
import {UserRegistrationComponent} from './registration/components/user-registration/user-registration.component';
import {RegistrationPageService} from './registration/services/registration-page.service';


@NgModule({
  declarations: [
    LoginComponent,
    RegistrationPageComponent,
    UserRegistrationComponent,
  ],
  imports: [
    SharedModule,
    CoreModule,
    MatCardModule,
    MatFormFieldModule,
    AuthenticationRoutingModule,
    MatCardModule,
  ],
  providers: [
    LoginPageService,
    RegistrationPageService,
  ]
})
export class AuthenticationModule {
}
