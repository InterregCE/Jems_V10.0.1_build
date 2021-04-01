import {NgModule} from '@angular/core';
import {UserPageComponent} from './user-page/containers/user-page/user-page.component';
import {routes} from './system-routing.module';
import {UserListComponent} from './user-page/components/user-list/user-list.component';
import {SharedModule} from '../common/shared-module';
import {UserSubmissionComponent} from './user-page/components/user-submission/user-submission.component';
import {UserNameResolver} from './user-page/services/user-name.resolver';
import {RouterModule} from '@angular/router';
import {UserDetailSharedModule} from '../common/user-detail-shared-module';
import {AuditLogComponent} from './audit-log/audit-log.component';
import {SystemPageSidenavService} from './services/system-page-sidenav.service';

@NgModule({
  declarations: [
    UserPageComponent,
    UserListComponent,
    UserSubmissionComponent,
    AuditLogComponent
  ],
  imports: [
    SharedModule,
    UserDetailSharedModule,
    RouterModule.forChild(routes),
  ],
  providers: [
    UserNameResolver,
    SystemPageSidenavService
  ]
})
export class SystemModule {
}
