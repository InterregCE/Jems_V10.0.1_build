import {NgModule} from '@angular/core';
import {UserPageComponent} from './user-page/containers/user-page/user-page.component';
import {routes} from './user-routing.module';
import {UserListComponent} from './user-page/components/user-list/user-list.component';
import {SharedModule} from '../common/shared-module';
import {UserSubmissionComponent} from './user-page/components/user-submission/user-submission.component';
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
    UserDetailSharedModule,
    RouterModule.forChild(routes),
  ],
  providers: [
    UserNameResolver,
  ]
})
export class UserModule {
}
