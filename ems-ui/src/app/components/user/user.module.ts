import {NgModule} from '@angular/core';
import {UserPageService} from './user-page/user-page.service';
import {UserPageComponent} from './user-page/user-page.component';
import {UserRoutingModule} from './user-routing.module';
import {LoginComponent} from './login/login.component';
import {CoreModule} from '../../common/core-module/core-module';
import {MatCardModule} from '@angular/material/card';
import {MatFormFieldModule} from '@angular/material/form-field';
import {UserListComponent} from './user-page/user-list/user-list.component';

@NgModule({
  declarations: [
    LoginComponent,
    UserPageComponent,
    UserListComponent
  ],
  imports: [
    CoreModule,
    MatCardModule,
    MatFormFieldModule,
    UserRoutingModule
  ],
  providers: [
    UserPageService,
  ]
})
export class UserModule {
}
