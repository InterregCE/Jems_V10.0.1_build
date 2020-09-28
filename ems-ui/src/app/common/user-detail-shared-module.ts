import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {MatSelectModule} from '@angular/material/select';

import {UserDetailComponent} from '../user/user-page/containers/user-detail/user-detail.component';
import {UserPasswordComponent} from '../user/user-page/components/user-detail/user-password/user-password.component';
import {UserEditComponent} from '../user/user-page/components/user-detail/user-edit/user-edit.component';
import {CoreModule} from './core-module';
import {SharedModule} from './shared-module';
import {UserRoleFormFieldComponent} from '../user/user-page/components/user-detail/user-role-form-field/user-role-form-field.component';
import {PasswordFieldComponent} from '../user/user-page/components/user-detail/user-password/password-field/password-field.component';
import {UserStore} from '../user/user-page/services/user-store.service';
import {RolePageService} from '../user/user-role/services/role-page/role-page.service';

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
    CommonModule,
    CoreModule,
    SharedModule,
    MatSelectModule,
  ],
  exports: [
    declarations
  ],
  providers: [
    UserStore,
    RolePageService,
  ]
})
export class UserDetailSharedModule {
}
