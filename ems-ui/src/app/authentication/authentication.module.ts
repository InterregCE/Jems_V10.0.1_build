import {NgModule} from '@angular/core';
import {AuthenticationRoutingModule} from './authentication-routing.module';
import {LoginComponent} from './login/login-page/login.component';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatCardModule} from '@angular/material/card';
import {CoreModule} from '../common/core-module';
import {SharedModule} from '../common/shared-module';
import {LoginPageService} from './login/services/login-page-service';


@NgModule({
  declarations: [
    LoginComponent,
  ],
  imports: [
    SharedModule,
    CoreModule,
    MatCardModule,
    MatFormFieldModule,
    AuthenticationRoutingModule
  ],
  providers: [
    LoginPageService
  ]
})
export class AuthenticationModule {
}
