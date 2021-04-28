import {NgModule} from '@angular/core';
import {UserPageComponent} from './user-page/user-page.component';
import {routes} from './system-routing.module';
import {SharedModule} from '../common/shared-module';
import {UserNameResolver} from './user-page/user-detail-page/user-name.resolver';
import {RouterModule} from '@angular/router';
import {UserDetailSharedModule} from '../common/user-detail-shared-module';
import {AuditLogComponent} from './audit-log/audit-log.component';
import {SystemPageSidenavService} from './services/system-page-sidenav.service';
import {UserPageRoleComponent} from './user-page-role/user-page-role.component';
import {UserRoleNameResolver} from './user-page-role/user-role-detail-page/user-role-name.resolver';
import {UserRoleDetailPageComponent} from './user-page-role/user-role-detail-page/user-role-detail-page.component';
import {UserRoleDetailPageStore} from './user-page-role/user-role-detail-page/user-role-detail-page-store.service';

@NgModule({
  declarations: [
    UserPageComponent,
    UserPageRoleComponent,
    UserRoleDetailPageComponent,
    AuditLogComponent
  ],
  imports: [
    SharedModule,
    UserDetailSharedModule,
    RouterModule.forChild(routes),
  ],
  providers: [
    UserNameResolver,
    UserRoleNameResolver,
    UserRoleDetailPageStore,
    SystemPageSidenavService
  ]
})
export class SystemModule {
}
