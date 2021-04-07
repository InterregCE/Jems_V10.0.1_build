import {NgModule} from '@angular/core';
import {UserDetailComponent} from '../system/user-page/containers/user-detail/user-detail.component';
import {UserEditComponent} from '../system/user-page/components/user-detail/user-edit/user-edit.component';
import {UserPasswordComponent} from '../system/user-page/components/user-detail/user-password/user-password.component';
import {UserRoleFormFieldComponent} from '../system/user-page/components/user-detail/user-role-form-field/user-role-form-field.component';
import {PasswordFieldComponent} from '../system/user-page/components/user-detail/user-password/password-field/password-field.component';
import {SharedModule} from './shared-module';
import {UserStore} from '../system/user-page/services/user-store.service';


const declarations = [
  UserDetailComponent,
  UserEditComponent,
  UserPasswordComponent,
  UserRoleFormFieldComponent,
  PasswordFieldComponent,
];

@NgModule({
  declarations: [
    declarations
  ],
  imports: [
    SharedModule
  ],
  exports: [
    declarations
  ],
  providers: [
    UserStore,
  ]
})
export class UserDetailSharedModule {
}
