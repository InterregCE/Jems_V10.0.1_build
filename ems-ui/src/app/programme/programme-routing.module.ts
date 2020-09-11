import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {AuthenticationGuard} from '../security/authentication-guard.service';
import {ProgrammePageComponent} from './programme-page/containers/programme-page/programme-page.component';
import {ProgrammePrioritiesComponent} from './programme-page/containers/programme-priorities/programme-priorities.component';
import {ProgrammePriorityComponent} from './programme-page/containers/programme-priority/programme-priority.component';
import {ProgrammeOutputIndicatorSubmissionPageComponent} from './programme-page/containers/programme-output-indicator-submission-page/programme-output-indicator-submission-page.component';
import {ProgrammeResultIndicatorSubmissionPageComponent} from './programme-page/containers/programme-result-indicator-submission-page/programme-result-indicator-submission-page.component';
import {ProgrammeAreaComponent} from './programme-page/containers/programme-area/programme-area.component';
import {ProgrammeIndicatorsOverviewPageComponent} from './programme-page/containers/programme-indicators-overview-page/programme-indicators-overview-page.component';
import {ProgrammeStrategiesPageComponent} from './programme-page/containers/programme-strategies-page/programme-strategies-page.component';

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
  },
  {
    path: 'areas',
    component: ProgrammeAreaComponent,
    canActivate: [AuthenticationGuard],
  },
  {
    path: 'indicators',
    component: ProgrammeIndicatorsOverviewPageComponent,
    canActivate: [AuthenticationGuard]
  },
  {
    path: 'outputIndicator/create',
    component: ProgrammeOutputIndicatorSubmissionPageComponent,
    canActivate: [AuthenticationGuard],
  },
  {
    path: 'outputIndicator/:indicatorId',
    component: ProgrammeOutputIndicatorSubmissionPageComponent,
    canActivate: [AuthenticationGuard],
  },
  {
    path: 'resultIndicator/create',
    component: ProgrammeResultIndicatorSubmissionPageComponent,
    canActivate: [AuthenticationGuard],
  },
  {
    path: 'resultIndicator/:indicatorId',
    component: ProgrammeResultIndicatorSubmissionPageComponent,
    canActivate: [AuthenticationGuard],
  },
  {
    path: 'strategies',
    component: ProgrammeStrategiesPageComponent,
    canActivate: [AuthenticationGuard],
  },
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
