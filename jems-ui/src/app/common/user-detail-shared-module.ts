import {NgModule} from '@angular/core';
import {UserPasswordComponent} from '../system/user-page/user-detail-page/user-password/user-password.component';
import {PasswordFieldComponent} from '../system/user-page/user-detail-page/user-password/password-field/password-field.component';
import {SharedModule} from './shared-module';
import {UserDetailPageStore} from '../system/user-page/user-detail-page/user-detail-page-store.service';
import {UserDetailPageComponent} from '../system/user-page/user-detail-page/user-detail-page.component';
import {SystemPageSidenavService} from '../system/services/system-page-sidenav.service';

const declarations = [
  UserDetailPageComponent,
  UserPasswordComponent,
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
    UserDetailPageStore,
    SystemPageSidenavService
  ]
})
export class UserDetailSharedModule {
}
