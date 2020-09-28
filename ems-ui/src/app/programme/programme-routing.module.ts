import {Routes} from '@angular/router';
import {ProgrammePageComponent} from './programme-page/containers/programme-page/programme-page.component';
import {ProgrammePrioritiesComponent} from './programme-page/containers/programme-priorities/programme-priorities.component';
import {ProgrammePriorityComponent} from './programme-page/containers/programme-priority/programme-priority.component';
import {ProgrammeOutputIndicatorSubmissionPageComponent} from './programme-page/containers/programme-output-indicator-submission-page/programme-output-indicator-submission-page.component';
import {ProgrammeResultIndicatorSubmissionPageComponent} from './programme-page/containers/programme-result-indicator-submission-page/programme-result-indicator-submission-page.component';
import {ProgrammeAreaComponent} from './programme-page/containers/programme-area/programme-area.component';
import {ProgrammeIndicatorsOverviewPageComponent} from './programme-page/containers/programme-indicators-overview-page/programme-indicators-overview-page.component';
import {ProgrammeStrategiesPageComponent} from './programme-page/containers/programme-strategies-page/programme-strategies-page.component';
import {ProgrammeLanguagesPageComponent} from './programme-page/containers/programme-languages-page/programme-languages-page.component';

export const routes: Routes = [
  {
    path: '',
    data: {breadcrumb: 'programme.breadcrumb.setup'},
    children: [
      {
        path: '',
        component: ProgrammePageComponent,
      },
      {
        path: 'priorities',
        data: {breadcrumb: 'programme.breadcrumb.priorities'},
        children: [
          {
            path: '',
            component: ProgrammePrioritiesComponent,
          },
          {
            path: 'priority',
            component: ProgrammePriorityComponent,
            data: {breadcrumb: 'programme.breadcrumb.priority'},
          },
        ],
      },
      {
        path: 'areas',
        component: ProgrammeAreaComponent,
        data: {breadcrumb: 'programme.breadcrumb.areas'},
      },
      {
        path: 'languages',
        component: ProgrammeLanguagesPageComponent,
        data: {breadcrumb: 'programme.breadcrumb.languages'},
      },
      {
        path: 'indicators',
        data: {breadcrumb: 'programme.breadcrumb.indicators'},
        children: [
          {
            path: '',
            component: ProgrammeIndicatorsOverviewPageComponent,
          },
          {
            path: 'outputIndicator',
            children: [
              {
                path: '',
                component: ProgrammeIndicatorsOverviewPageComponent,
              },
              {
                path: 'create',
                component: ProgrammeOutputIndicatorSubmissionPageComponent,
                data: {breadcrumb: 'programme.breadcrumb.outputIndicator.create'},
              },
              {
                path: 'detail/:indicatorId',
                data: {breadcrumb: 'programme.breadcrumb.outputIndicator.name'},
                component: ProgrammeOutputIndicatorSubmissionPageComponent,
              },
            ]
          },
          {
            path: 'resultIndicator',
            children: [
              {
                path: '',
                component: ProgrammeIndicatorsOverviewPageComponent,
              },
              {
                path: 'create',
                component: ProgrammeResultIndicatorSubmissionPageComponent,
                data: {breadcrumb: 'programme.breadcrumb.resultIndicator.create'},
              },
              {
                path: 'detail/:indicatorId',
                component: ProgrammeResultIndicatorSubmissionPageComponent,
                data: {breadcrumb: 'programme.breadcrumb.resultIndicator.name'},
              },
            ]
          },
        ],
      },
      {
        path: 'strategies',
        component: ProgrammeStrategiesPageComponent,
        data: {breadcrumb: 'programme.breadcrumb.strategies'},
      },
    ]
  }
];
