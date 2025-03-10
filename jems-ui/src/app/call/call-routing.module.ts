import {RouterModule, Routes} from '@angular/router';
import {CallPageComponent} from './containers/call-page/call-page.component';
import {RouteData} from '@common/utils/route-data';
import {PermissionGuard} from '../security/permission.guard';
import {CallNameResolver} from './services/call-name.resolver';
import {CallBudgetSettingsPageComponent} from './call-budget-settings-page/call-budget-settings-page.component';
import {CallDetailPageComponent} from './call-detail-page/call-detail-page.component';
import {UserRoleDTO} from '@cat/api';
import {
  ApplicationFormConfigurationPageComponent
} from './application-form-configuration-page/application-form-configuration-page.component';
import {
  PreSubmissionCheckSettingsPageComponent
} from './pre-submission-check-settings-page/pre-submission-check-settings-page.component';
import {NotificationsSettingsComponent} from './notifications-settings/notifications-settings.component';
import {
  ProjectNotificationsSettingsTabComponent
} from './notifications-settings/project-notifications-settings-tab/project-notifications-settings-tab.component';
import {
  PartnerReportNotificationsSettingsTabComponent
} from './notifications-settings/partner-report-notifications-settings-tab/partner-report-notifications-settings-tab.component';
import {CallTranslationsConfigurationComponent} from './translations/call-translations-configuration.component';
import {
  ProjectReportNotificationsSettingsTabComponent
} from './notifications-settings/project-report-notifications-settings-tab/project-report-notifications-settings-tab.component';
import {NgModule} from '@angular/core';
import {ConfirmLeaveGuard} from '../security/confirm-leave.guard';
import { ChecklistsPageComponent } from './checklists-page/checklists-page.component';

const callRoutes: Routes = [
  {
    path: '',
    data: {
      breadcrumb: 'call.breadcrumb.list.of.calls'
    },
    children: [
      {
        path: '',
        component: CallPageComponent,
        canActivate: [PermissionGuard],
        data: {
          breadcrumb: 'call.list.header',
          permissionsOnly: [UserRoleDTO.PermissionsEnum.CallRetrieve],
        }
      },
      {
        path: 'create',
        component: CallDetailPageComponent,
        data: new RouteData({
          breadcrumb: 'call.breadcrumb.create',
          permissionsOnly: [UserRoleDTO.PermissionsEnum.CallUpdate],
        }),
        canActivate: [PermissionGuard],
      },
      {
        path: 'detail/:callId',
        data: {
          dynamicBreadcrumb: true,
          permissionsOnly: [
            UserRoleDTO.PermissionsEnum.CallRetrieve,
            UserRoleDTO.PermissionsEnum.CallPublishedRetrieve,
            UserRoleDTO.PermissionsEnum.ProjectCreate
          ],
        },
        resolve: {breadcrumb$: CallNameResolver},
        canActivate: [PermissionGuard],
        children: [
          {
            path: '',
            component: CallDetailPageComponent,
          },
          {
            path: 'budgetSettings',
            data: {
              breadcrumb: 'call.detail.budget.settings',
              permissionsOnly: [UserRoleDTO.PermissionsEnum.CallRetrieve],
            },
            canActivate: [PermissionGuard],
            component: CallBudgetSettingsPageComponent,
          },
          {
            path: 'applicationFormConfiguration',
            data: {
              breadcrumb: 'call.detail.application.form.config.title',
              permissionsOnly: [UserRoleDTO.PermissionsEnum.CallRetrieve],
            },
            canActivate: [PermissionGuard],
            component: ApplicationFormConfigurationPageComponent,
          },
          {
            path: 'checklists',
            data: {
              breadcrumb: 'call.detail.checklists.title',
              permissionsOnly: [UserRoleDTO.PermissionsEnum.CallRetrieve],
            },
            canActivate: [PermissionGuard],
            component: ChecklistsPageComponent,
          },
          {
            path: 'preSubmissionCheckSettings',
            data: {
              breadcrumb: 'call.detail.pre.submission.check.config.title',
              permissionsOnly: [UserRoleDTO.PermissionsEnum.CallRetrieve],
            },
            canActivate: [PermissionGuard],
            component: PreSubmissionCheckSettingsPageComponent,
          },
          {
            path: 'notificationSettings',
            component: NotificationsSettingsComponent,
            data: {
              breadcrumb: 'call.detail.notifications.config.title',
              permissionsOnly: [UserRoleDTO.PermissionsEnum.CallRetrieve],
            },
            children: [
              {
                path: '',
                redirectTo: 'project',
              },
              {
                path: 'project',
                component: ProjectNotificationsSettingsTabComponent,
                data: {breadcrumb: 'call.detail.notifications.config.tab.project'}
              },
              {
                path: 'projectReport',
                component: ProjectReportNotificationsSettingsTabComponent,
                data: {breadcrumb: 'call.detail.notifications.config.tab.project.report'}
              },
              {
                path: 'partnerReport',
                component: PartnerReportNotificationsSettingsTabComponent,
                data: {breadcrumb: 'call.detail.notifications.config.tab.partner.report'}
              }
            ]
          },
          {
            path: 'translationSettings',
            data: {
              breadcrumb: 'call.detail.translations.title',
              permissionsOnly: [UserRoleDTO.PermissionsEnum.CallRetrieve],
            },
            canActivate: [PermissionGuard],
            component: CallTranslationsConfigurationComponent,
          },
        ],
      },
      {
        path: 'apply/:callId',
        pathMatch: 'full',
        redirectTo: '/app/project/applyTo/:callId',
      },
    ]
  },
];

@NgModule({
  imports: [RouterModule.forChild(callRoutes)],
  exports: [RouterModule]
})
export class CallRoutingModule {
  constructor(private confirmLeaveGuard: ConfirmLeaveGuard) {
    this.confirmLeaveGuard.applyGuardToLeafRoutes(callRoutes);
  }
}
