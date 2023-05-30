import {Component} from '@angular/core';
import {combineLatest, Observable, of} from 'rxjs';
import {ProjectNotificationConfigurationDTO} from '@cat/api';
import {NotificationVariable} from '../notification-variable.enum';
import {catchError, map, take, tap} from 'rxjs/operators';
import {FormArray, FormBuilder, Validators} from '@angular/forms';
import {FormService} from '@common/components/section/form/form.service';
import {CallNotificationSettingsStore} from '../call-notification-settings-store.service';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import { Alert } from '@common/components/forms/alert';

@UntilDestroy()
@Component({
  selector: 'jems-project-report-notifications-settings-tab',
  templateUrl: './project-report-notifications-settings-tab.component.html',
  styleUrls: ['./project-report-notifications-settings-tab.component.scss']
})
export class ProjectReportNotificationsSettingsTabComponent {
  Alert = Alert;

  projectReportNotificationsForm = this.formBuilder.group({
    projectReportNotificationConfigurations: this.formBuilder.array([]),
  });

  data$: Observable<{
    projectReportNotificationConfigurations: ProjectNotificationConfigurationDTO[];
    canEditCall: boolean;
  }>;

  projectReportNotificationVariables = [
    NotificationVariable.PROGRAMME_NAME,
    NotificationVariable.CALL_ID,
    NotificationVariable.CALL_NAME,
    NotificationVariable.PROJECT_ID,
    NotificationVariable.PROJECT_IDENTIFIER,
    NotificationVariable.PROJECT_ACRONYM,
    NotificationVariable.PROJECT_REPORT_ID,
    NotificationVariable.PROJECT_REPORT_NUMBER,
  ];
  constructor(
    private formBuilder: FormBuilder,
    private formService: FormService,
    private callNotificationSettingsStore: CallNotificationSettingsStore
  ) {
    this.data$ = combineLatest(
      this.callNotificationSettingsStore.projectReportNotificationConfigurations$,
      this.callNotificationSettingsStore.canEditCall$,
    ).pipe(
      map(([projectReportNotificationConfigurations, canEditCall]) => ({
        projectReportNotificationConfigurations,
        canEditCall,
      })),
      tap(data => this.resetForm(data.projectReportNotificationConfigurations, data.canEditCall)),
      untilDestroyed(this)
    );
  }

  get projectReportNotificationConfigurationsArray(): FormArray {
    return this.projectReportNotificationsForm.get('projectReportNotificationConfigurations') as FormArray;
  }

  resetForm(projectReportNotificationConfigurations: ProjectNotificationConfigurationDTO[], canEditCall: boolean): void {
    this.projectReportNotificationConfigurationsArray.clear();
    projectReportNotificationConfigurations.forEach(notificationConfig => {
      this.projectReportNotificationConfigurationsArray.push(this.formBuilder.group(
        {
          id: notificationConfig.id,
          active: notificationConfig.active,
          emailSubject: [notificationConfig.emailSubject, Validators.maxLength(255)],
          emailBody: [notificationConfig.emailBody, Validators.maxLength(10000)],
          sendToManager: notificationConfig.sendToManager,
          sendToLeadPartner: notificationConfig.sendToLeadPartner,
          sendToProjectPartners: notificationConfig.sendToProjectPartners,
          sendToProjectAssigned: notificationConfig.sendToProjectAssigned,
          sendToControllers: notificationConfig.sendToControllers
        }
      ));
    });
    this.formService.init(this.projectReportNotificationsForm, of(canEditCall));
  }

  save() {
    const notificationTemplates = this.projectReportNotificationsForm.getRawValue().projectReportNotificationConfigurations;
    this.callNotificationSettingsStore.updateProjectReportNotifications(notificationTemplates).pipe(
      take(1),
      tap(() => this.formService.setSuccess('call.detail.notifications.config.tab.project.report.form.save.success')),
      catchError(err => this.formService.setError(err)),
    ).subscribe();
  }

}

