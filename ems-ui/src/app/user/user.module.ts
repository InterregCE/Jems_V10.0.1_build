import {NgModule} from '@angular/core';
import {UserPageService} from './user-page/services/user-page/user-page.service';
import {UserPageComponent} from './user-page/containers/user-page/user-page.component';
import {UserRoutingModule} from './user-routing.module';
import {UserListComponent} from './user-page/components/user-list/user-list.component';
import {SharedModule} from '../common/shared-module';
import {UserSubmissionComponent} from './user-page/components/user-submission/user-submission.component';
import {MatSelectModule} from '@angular/material/select';

@NgModule({
  declarations: [
    UserPageComponent,
    UserListComponent,
    UserSubmissionComponent
  ],
    imports: [
        SharedModule,
        UserRoutingModule,
        MatSelectModule,
    ],
  providers: [
    UserPageService,
  ]
})
export class UserModule {
}
