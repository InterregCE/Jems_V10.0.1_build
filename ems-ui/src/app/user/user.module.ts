import {NgModule} from '@angular/core';
import {UserPageService} from './user-page/services/user-page/user-page.service';
import {UserPageComponent} from './user-page/containers/user-page/user-page.component';
import {UserRoutingModule} from './user-routing.module';
import {UserListComponent} from './user-page/components/user-list/user-list.component';
import {SharedModule} from '../common/shared-module';
import {UserSubmissionComponent} from './user-page/components/user-submission/user-submission.component';
import {MatSelectModule} from '@angular/material/select';
import {CoreModule} from '../common/core-module';
import {UserEditComponent} from './user-page/components/user-detail/user-edit.component';
import {UserDetailService} from './user-page/services/user-detail/user-detail.service';
import {UserDetailComponent} from './user-page/containers/user-detail/user-detail.component';
import {MatCardModule} from '@angular/material/card';
import {RolePageService} from './user-role/services/role-page/role-page.service';

@NgModule({
  declarations: [
    UserPageComponent,
    UserListComponent,
    UserSubmissionComponent,
    UserDetailComponent,
    UserEditComponent,
  ],
  imports: [
    SharedModule,
    UserRoutingModule,
    MatSelectModule,
    MatCardModule,
    CoreModule,
  ],
  providers: [
    UserPageService,
    UserDetailService,
    RolePageService
  ]
})
export class UserModule {
}
