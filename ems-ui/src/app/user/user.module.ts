import {NgModule} from '@angular/core';
import {UserPageService} from './user-page/services/user-page/user-page.service';
import {UserPageComponent} from './user-page/containers/user-page/user-page.component';
import {UserRoutingModule} from './user-routing.module';
import {UserListComponent} from './user-page/components/user-list/user-list.component';
import {SharedModule} from '../common/shared-module';

@NgModule({
  declarations: [
    UserPageComponent,
    UserListComponent
  ],
  imports: [
    SharedModule,
    UserRoutingModule,
  ],
  providers: [
    UserPageService,
  ]
})
export class UserModule {
}
