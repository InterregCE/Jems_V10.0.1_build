import {Routes} from '@angular/router';
import {ProgrammePageComponent} from './programme-page/containers/programme-page/programme-page.component';
import {ProgrammeOutputIndicatorSubmissionPageComponent} from './programme-page/containers/programme-output-indicator-submission-page/programme-output-indicator-submission-page.component';
import {ProgrammeResultIndicatorSubmissionPageComponent} from './programme-page/containers/programme-result-indicator-submission-page/programme-result-indicator-submission-page.component';
import {ProgrammeAreaComponent} from './programme-page/containers/programme-area/programme-area.component';
import {ProgrammeIndicatorsOverviewPageComponent} from './programme-page/containers/programme-indicators-overview-page/programme-indicators-overview-page.component';
import {ProgrammeStrategiesPageComponent} from './programme-page/containers/programme-strategies-page/programme-strategies-page.component';
import {ProgrammeLanguagesPageComponent} from './programme-page/containers/programme-languages-page/programme-languages-page.component';
import {ProgrammeLegalStatusComponent} from './programme-page/containers/programme-legal-status/programme-legal-status.component';
import {ProgrammeSimplifiedCostOptionsComponent} from './programme-page/containers/programme-simplified-cost-options/programme-simplified-cost-options.component';
import {ProgrammeLumpSumsSubmissionPageComponent} from './programme-page/containers/programme-lump-sums-submission-page/programme-lump-sums-submission-page.component';
import {ProgrammeUnitCostsSubmissionPageComponent} from './programme-page/containers/programme-unit-costs-submission-page/programme-unit-costs-submission-page.component';
import {ProgrammePriorityDetailPageComponent} from './priorities/programme-priority-list-page/programme-priority-detail-page/programme-priority-detail-page.component';
import {ProgrammePriorityListPageComponent} from './priorities/programme-priority-list-page/programme-priority-list-page.component';

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
      },
      {
        path: 'priorities',
        data: {breadcrumb: 'programme.breadcrumb.priorities'},
        children: [
          {
            path: '',
            component: ProgrammePriorityListPageComponent,
          },
          {
            path: 'create',
            component: ProgrammePriorityDetailPageComponent,
            data: {breadcrumb: 'programme.breadcrumb.priority'},
          },
          {
            path: ':priorityId',
            component: ProgrammePriorityDetailPageComponent,
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
      {
        path: 'legalStatus',
        component: ProgrammeLegalStatusComponent,
        data: {breadcrumb: 'programme.breadcrumb.legal.status'},
      },
      {
        path: 'costs',
        data: {breadcrumb: 'programme.breadcrumb.costs'},
        children: [
          {
            path: '',
            component: ProgrammeSimplifiedCostOptionsComponent,
          },
          {
            path: 'lumpSum',
            children: [
              {
                path: '',
                component: ProgrammeSimplifiedCostOptionsComponent,
              },
              {
                path: 'create',
                component: ProgrammeLumpSumsSubmissionPageComponent,
                data: {breadcrumb: 'programme.breadcrumb.lumpSum.create'},
              },
              {
                path: 'detail/:lumpSumId',
                data: {breadcrumb: 'programme.breadcrumb.lumpSum.name'},
                component: ProgrammeLumpSumsSubmissionPageComponent,
              },
            ]
          },
          {
            path: 'unitCost',
            children: [
              {
                path: '',
                component: ProgrammeSimplifiedCostOptionsComponent,
              },
              {
                path: 'create',
                component: ProgrammeUnitCostsSubmissionPageComponent,
                data: {breadcrumb: 'programme.breadcrumb.unitCost.create'},
              },
              {
                path: 'detail/:unitCostId',
                data: {breadcrumb: 'programme.breadcrumb.unitCost.name'},
                component: ProgrammeUnitCostsSubmissionPageComponent,
              },
            ]
          },
        ],
      },
    ]
  }
];
