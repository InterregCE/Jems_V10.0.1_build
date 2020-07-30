import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {AuthenticationGuard} from '../security/authentication-guard.service';
import {ProgrammePageComponent} from './programme-page/containers/programme-page/programme-page.component';
import {ProgrammePrioritiesComponent} from './programme-page/containers/programme-priorities/programme-priorities.component';
import {ProgrammePriorityComponent} from './programme-page/containers/programme-priority/programme-priority.component';

const routes: Routes = [
  {
    path: 'programme',
    component: ProgrammePageComponent,
    canActivate: [AuthenticationGuard],
  },
  {
    path: 'priorities',
    component: ProgrammePrioritiesComponent,
    canActivate: [AuthenticationGuard],
  },
  {
    path: 'priority',
    component: ProgrammePriorityComponent,
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
