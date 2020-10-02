import {NgModule} from '@angular/core';
import {UserPageComponent} from './user-page/containers/user-page/user-page.component';
import {routes} from './user-routing.module';
import {UserListComponent} from './user-page/components/user-list/user-list.component';
import {SharedModule} from '../common/shared-module';
import {UserSubmissionComponent} from './user-page/components/user-submission/user-submission.component';
import {MatSelectModule} from '@angular/material/select';
import {CoreModule} from '../common/core-module';
import {MatCardModule} from '@angular/material/card';
import {UserNameResolver} from './user-page/services/user-name.resolver';
import {RouterModule} from '@angular/router';
import {UserDetailSharedModule} from '../common/user-detail-shared-module';

@NgModule({
  declarations: [
    UserPageComponent,
    UserListComponent,
    UserSubmissionComponent,
  ],
  imports: [
    SharedModule,
    RouterModule.forChild(routes),
    MatSelectModule,
    MatCardModule,
    CoreModule,
    UserDetailSharedModule,
  ],
  providers: [
    UserNameResolver,
  ]
})
export class UserModule {
}
