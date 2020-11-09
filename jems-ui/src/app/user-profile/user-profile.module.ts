import {NgModule} from '@angular/core';
import {routes} from './user-profile-routing.module';
import {SharedModule} from '../common/shared-module';
import {RouterModule} from '@angular/router';
import {UserDetailSharedModule} from '../common/user-detail-shared-module';

@NgModule({
  declarations: [],
  imports: [
    SharedModule,
    UserDetailSharedModule,
    RouterModule.forChild(routes),
  ]
})
export class UserProfileModule {
}
