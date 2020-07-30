import {RouterModule, Routes} from '@angular/router';
import {AuthenticationGuard} from '../security/authentication-guard.service';
import {NgModule} from '@angular/core';
import {CallPageComponent} from './containers/call-page/call-page.component';
import {CallConfigurationComponent} from './containers/call-configuration/call-configuration.component';

const routes: Routes = [
  {
    path: 'calls',
    component: CallPageComponent,
    canActivate: [AuthenticationGuard],
  },
  {
    path: 'call/create',
    component: CallConfigurationComponent,
    canActivate: [AuthenticationGuard]
  },
  {
    path: 'call/:callId',
    component: CallConfigurationComponent,
    canActivate: [AuthenticationGuard],
  }
]

@NgModule({
  imports: [
    RouterModule.forChild(routes),
  ],
  exports: [
    RouterModule,
  ],
})
export class CallRoutingModule {
}
