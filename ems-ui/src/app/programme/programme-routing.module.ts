import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {AuthenticationGuard} from '../security/authentication-guard.service';
import {ProgrammePageComponent} from './programme-page/containers/programme-page/programme-page.component';

const routes: Routes = [
  {
    path: 'programme',
    component: ProgrammePageComponent,
    canActivate: [AuthenticationGuard],
  }
];

@NgModule({
  imports: [
    RouterModule.forChild(routes),
  ],
  exports: [
    RouterModule,
  ],
})
export class ProgrammeRoutingModule {
}
