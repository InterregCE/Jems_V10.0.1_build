import {NgModule} from '@angular/core';
import {routes} from './user-profile-routing.module';
import {SharedModule} from '../common/shared-module';
import {MatSelectModule} from '@angular/material/select';
import {CoreModule} from '../common/core-module';
import {MatCardModule} from '@angular/material/card';
import {RouterModule} from '@angular/router';
import {UserDetailSharedModule} from '../common/user-detail-shared-module';
import {CurrentUserResolver} from './services/current-user.resolver';

@NgModule({
  declarations: [
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
    CurrentUserResolver,
  ]
})
export class UserProfileModule {
}
