import {Routes} from '@angular/router';
import {AuthenticationGuard} from '../security/authentication-guard.service';
import {ProgrammePageComponent} from './programme-page/containers/programme-page/programme-page.component';
import {ProgrammePrioritiesComponent} from './programme-page/containers/programme-priorities/programme-priorities.component';
import {ProgrammePriorityComponent} from './programme-page/containers/programme-priority/programme-priority.component';
import {ProgrammeOutputIndicatorSubmissionPageComponent} from './programme-page/containers/programme-output-indicator-submission-page/programme-output-indicator-submission-page.component';
import {ProgrammeResultIndicatorSubmissionPageComponent} from './programme-page/containers/programme-result-indicator-submission-page/programme-result-indicator-submission-page.component';
import {ProgrammeAreaComponent} from './programme-page/containers/programme-area/programme-area.component';
import {ProgrammeIndicatorsOverviewPageComponent} from './programme-page/containers/programme-indicators-overview-page/programme-indicators-overview-page.component';
import {ProgrammeStrategiesPageComponent} from './programme-page/containers/programme-strategies-page/programme-strategies-page.component';
import {Permission} from '../security/permissions/permission';
import {RouteData} from '../common/utils/route-data';
import {PermissionGuard} from '../security/permission.guard';

export const routes: Routes = [
  {
    path: '',
    data: {
      breadcrumb: 'programme.breadcrumb.setup',
    },
    children: [
      {
        path: '',
        component: ProgrammePageComponent,
        canActivate: [AuthenticationGuard, PermissionGuard],
      },
      {
        path: 'priorities',
        data: {
          breadcrumb: 'programme.breadcrumb.priorities',
        },
        children: [
          {
            path: '',
            component: ProgrammePrioritiesComponent,
            canActivate: [AuthenticationGuard, PermissionGuard],
          },
          {
            path: 'priority',
            component: ProgrammePriorityComponent,
            data: {
              breadcrumb: 'programme.breadcrumb.priority',
            },
            canActivate: [AuthenticationGuard, PermissionGuard],
          },
        ],
      },
      {
        path: 'areas',
        component: ProgrammeAreaComponent,
        data: {
          breadcrumb: 'programme.breadcrumb.areas',
        },
        canActivate: [AuthenticationGuard, PermissionGuard],
      },
      {
        path: 'indicators',
        data: {
          breadcrumb: 'programme.breadcrumb.indicators',
        },
        children: [
          {
            path: '',
            component: ProgrammeIndicatorsOverviewPageComponent,
            canActivate: [AuthenticationGuard, PermissionGuard]
          },
          {
            path: 'outputIndicator',
            children: [
              {
                path: '',
                component: ProgrammeIndicatorsOverviewPageComponent,
                canActivate: [AuthenticationGuard, PermissionGuard]
              },
              {
                path: 'create',
                component: ProgrammeOutputIndicatorSubmissionPageComponent,
                data: new RouteData({
                  breadcrumb: 'programme.breadcrumb.outputIndicator.create',
                  permissionsOnly: [Permission.ADMINISTRATOR, Permission.PROGRAMME_USER],
                }),
                canActivate: [AuthenticationGuard, PermissionGuard],
              },
              {
                path: 'detail/:indicatorId',
                data: new RouteData({
                  breadcrumb: 'programme.breadcrumb.outputIndicator.name',
                  permissionsOnly: [Permission.ADMINISTRATOR, Permission.PROGRAMME_USER],
                }),
                component: ProgrammeOutputIndicatorSubmissionPageComponent,
                canActivate: [AuthenticationGuard, PermissionGuard],
              },
            ]
          },
          {
            path: 'resultIndicator',
            children: [
              {
                path: '',
                component: ProgrammeIndicatorsOverviewPageComponent,
                canActivate: [AuthenticationGuard, PermissionGuard]
              },
              {
                path: 'create',
                component: ProgrammeResultIndicatorSubmissionPageComponent,
                data: {
                  breadcrumb: 'programme.breadcrumb.resultIndicator.create',
                },
                canActivate: [AuthenticationGuard, PermissionGuard],
              },
              {
                path: 'detail/:indicatorId',
                component: ProgrammeResultIndicatorSubmissionPageComponent,
                data: new RouteData({
                  breadcrumb: 'programme.breadcrumb.resultIndicator.name',
                  permissionsOnly: [Permission.ADMINISTRATOR, Permission.PROGRAMME_USER],
                }),
                canActivate: [AuthenticationGuard, PermissionGuard],
              },
            ]
          },
        ],
      },
      {
        path: 'strategies',
        component: ProgrammeStrategiesPageComponent,
        data: new RouteData({
          breadcrumb: 'programme.breadcrumb.strategies',
          permissionsOnly: [Permission.ADMINISTRATOR, Permission.PROGRAMME_USER],
        }),
        canActivate: [AuthenticationGuard, PermissionGuard],
      },
    ]
  }
];
