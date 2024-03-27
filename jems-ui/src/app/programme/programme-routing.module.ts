import {RouterModule, Routes} from '@angular/router';
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
import {TranslationManagementPageComponent} from './translation-management-page/translation-management-page.component';
import {ProgrammeStateAidComponent} from './programme-page/containers/programme-state-aid/programme-state-aid.component';
import {ProgrammeBasicDataComponent} from './programme-basic-data/programme-basic-data.component';
import {ProgrammeFundsComponent} from './programme-funds/programme-funds.component';
import {ProgrammeDataExportComponent} from './programme-data-export/programme-data-export.component';
import {PermissionGuard} from '../security/permission.guard';
import {UserRoleDTO} from '@cat/api';
import PermissionsEnum = UserRoleDTO.PermissionsEnum;
import { ProgrammeChecklistListPageComponent } from './programme-checklist-list-page/programme-checklist-list-page.component';
import {ProgrammeChecklistDetailPageComponent} from './programme-checklist-list-page/programme-checklist-detail-page/programme-checklist-detail-page.component';
import { ProgrammeConversionRatesComponent } from './programme-conversion-rates/programme-conversion-rates.component';
import {
  ProgrammeTypologyErrorsComponent
} from './programme-page/containers/programme-typology-errors/programme-typology-errors.component';
import {NgModule} from '@angular/core';
import {ConfirmLeaveGuard} from '../security/confirm-leave.guard';

const programmeRoutes: Routes = [
  {
    path: '',
    data: {
      breadcrumb: 'programme.breadcrumb.setup',
    },
    children: [
      {
        path: '',
        canActivate: [PermissionGuard],
        data: {
          skipBreadcrumb: true,
          permissionsOnly: [PermissionsEnum.ProgrammeSetupRetrieve, PermissionsEnum.ProgrammeSetupUpdate, PermissionsEnum.ProgrammeDataExportRetrieve],
        },
        children:[
          {
            path: '',
            data: {
              breadcrumb: 'programme.breadcrumb.basic.data',
            },
            component: ProgrammeBasicDataComponent,
            canActivate: [PermissionGuard],
          },
          {
            path: 'funds',
            canActivate: [PermissionGuard],
            data: {
              breadcrumb: 'programme.breadcrumb.funds',
              permissionsOnly: [PermissionsEnum.ProgrammeSetupRetrieve, PermissionsEnum.ProgrammeSetupUpdate]
            },
            component: ProgrammeFundsComponent,
          },
          {
            path: 'priorities',
            canActivate: [PermissionGuard],
            data: {
              breadcrumb: 'programme.breadcrumb.priorities',
              permissionsOnly: [PermissionsEnum.ProgrammeSetupRetrieve, PermissionsEnum.ProgrammeSetupUpdate]
            },
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
            canActivate: [PermissionGuard],
            data: {
              breadcrumb: 'programme.breadcrumb.areas',
              permissionsOnly: [PermissionsEnum.ProgrammeSetupRetrieve, PermissionsEnum.ProgrammeSetupUpdate]
            },
          },
          {
            path: 'conversionRates',
            component: ProgrammeConversionRatesComponent,
            canActivate: [PermissionGuard],
            data: {
              breadcrumb: 'programme.breadcrumb.conversion.rates',
              permissionsOnly: [PermissionsEnum.ProgrammeSetupRetrieve, PermissionsEnum.ProgrammeSetupUpdate]
            },
          },
          {
            path: 'languages',
            component: ProgrammeLanguagesPageComponent,
            canActivate: [PermissionGuard],
            data: {
              breadcrumb: 'programme.breadcrumb.languages',
              permissionsOnly: [PermissionsEnum.ProgrammeSetupRetrieve, PermissionsEnum.ProgrammeSetupUpdate]
            },
          },
          {
            path: 'translationManagement',
            component: TranslationManagementPageComponent,
            canActivate: [PermissionGuard],
            data: {
              breadcrumb: 'programme.breadcrumb.translation.management',
              permissionsOnly: [PermissionsEnum.ProgrammeSetupRetrieve, PermissionsEnum.ProgrammeSetupUpdate]
            },
          },
          {
            path: 'indicators',
            canActivate: [PermissionGuard],
            data: {
              breadcrumb: 'programme.breadcrumb.indicators',
              permissionsOnly: [PermissionsEnum.ProgrammeSetupRetrieve, PermissionsEnum.ProgrammeSetupUpdate]
            },
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
            canActivate: [PermissionGuard],
            data: {
              breadcrumb: 'programme.breadcrumb.strategies',
              permissionsOnly: [PermissionsEnum.ProgrammeSetupRetrieve, PermissionsEnum.ProgrammeSetupUpdate]
            },
          },
          {
            path: 'legalStatus',
            component: ProgrammeLegalStatusComponent,
            canActivate: [PermissionGuard],
            data: {
              breadcrumb: 'programme.breadcrumb.legal.status',
              permissionsOnly: [PermissionsEnum.ProgrammeSetupRetrieve, PermissionsEnum.ProgrammeSetupUpdate]
            },
          },
          {
            path: 'typologyErrors',
            component: ProgrammeTypologyErrorsComponent,
            canActivate: [PermissionGuard],
            data: {
              breadcrumb: 'programme.breadcrumb.typology.errors',
              permissionsOnly: [PermissionsEnum.ProgrammeSetupRetrieve, PermissionsEnum.ProgrammeSetupUpdate]
            },
          },
          {
            path: 'costs',
            canActivate: [PermissionGuard],
            data: {
              breadcrumb: 'programme.breadcrumb.costs',
              permissionsOnly: [PermissionsEnum.ProgrammeSetupRetrieve, PermissionsEnum.ProgrammeSetupUpdate]
            },
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
          {
            path: 'stateAid',
            component: ProgrammeStateAidComponent,
            canActivate: [PermissionGuard],
            data: {
              breadcrumb: 'programme.breadcrumb.state.aid',
              permissionsOnly: [PermissionsEnum.ProgrammeSetupRetrieve, PermissionsEnum.ProgrammeSetupUpdate]
            },
          },
        ]
      },
      {
        path: 'export',
        component: ProgrammeDataExportComponent,
        canActivate: [PermissionGuard],
        data: {
          breadcrumb: 'programme.breadcrumb.data.export',
          permissionsOnly: [PermissionsEnum.ProgrammeDataExportRetrieve],
        },
      },
      {
        path: 'checklists',
        canActivate: [PermissionGuard],
        data: {
          breadcrumb: 'programme.checklists.title',
          permissionsOnly: [PermissionsEnum.ProgrammeSetupRetrieve, PermissionsEnum.ProgrammeSetupUpdate],
        },
        children: [
          {
            path: '',
            component: ProgrammeChecklistListPageComponent,
          },
          {
            path: 'create',
            component: ProgrammeChecklistDetailPageComponent,
            data: {breadcrumb: 'programme.checklists.detail.title'},
          },
          {
            path: ':checklistId',
            data: {breadcrumb: 'programme.checklists.detail.title'},
            component: ProgrammeChecklistDetailPageComponent,
          }
        ]
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(programmeRoutes)],
  exports: [RouterModule]
})
export class ProgrammeRoutingModule {
  constructor(private confirmLeaveGuard: ConfirmLeaveGuard) {
    this.confirmLeaveGuard.applyGuardToLeafRoutes(programmeRoutes);
  }
}
