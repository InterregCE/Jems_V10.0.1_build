import {NgModule} from '@angular/core';
import {UserPageComponent} from './user-page/user-page.component';
import {routes} from './system-routing.module';
import {SharedModule} from '../common/shared-module';
import {UserNameResolver} from './user-page/user-detail-page/user-name.resolver';
import {RouterModule} from '@angular/router';
import {UserDetailSharedModule} from '../common/user-detail-shared-module';
import {AuditLogComponent} from './audit-log/audit-log.component';
import {SystemPageSidenavService} from './services/system-page-sidenav.service';

@NgModule({
  declarations: [
    UserPageComponent,
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
