import {Component} from '@angular/core';
import {ProjectNotificationConfigurationDTO} from '@cat/api';
import {FormArray, FormBuilder, Validators} from '@angular/forms';
import {FormService} from '@common/components/section/form/form.service';
import {CallNotificationSettingsStore} from '../call-notification-settings-store.service';
import {catchError, map, take, tap} from 'rxjs/operators';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {combineLatest, Observable, of} from 'rxjs';
import {NotificationVariable} from '../notification-variable.enum';
import {Alert} from '@common/components/forms/alert';

@UntilDestroy()
@Component({
  selector: 'jems-partner-report-notifications-settings-tab',
  templateUrl: './partner-report-notifications-settings-tab.component.html',
  styleUrls: ['./partner-report-notifications-settings-tab.component.scss']
})
export class PartnerReportNotificationsSettingsTabComponent {
  Alert = Alert;

  partnerReportNotificationsForm = this.formBuilder.group({
    partnerReportNotificationConfigurations: this.formBuilder.array([]),
  });

  data$: Observable<{
    partnerReportNotificationConfigurations: ProjectNotificationConfigurationDTO[];
    canEditCall: boolean;
  }>;

  partnerReportNotificationVariables = [
    NotificationVariable.PROGRAMME_NAME,
    NotificationVariable.CALL_ID,
    NotificationVariable.CALL_NAME,
    NotificationVariable.PROJECT_ID,
    NotificationVariable.PROJECT_IDENTIFIER,
    NotificationVariable.PROJECT_ACRONYM,
    NotificationVariable.PARTNER_ID,
    NotificationVariable.PARTNER_ROLE,
    NotificationVariable.PARTNER_NUMBER,
    NotificationVariable.PARTNER_ABBREVIATION,
    NotificationVariable.PARTNER_REPORT_ID,
    NotificationVariable.PARTNER_REPORT_NUMBER,
  ];
  constructor(
    private formBuilder: FormBuilder,
    private formService: FormService,
    private callNotificationSettingsStore: CallNotificationSettingsStore
  ) {
    this.data$ = combineLatest(
      this.callNotificationSettingsStore.partnerReportNotificationConfigurations$,
      this.callNotificationSettingsStore.canEditCall$,
    ).pipe(
      map(([partnerReportNotificationConfigurations, canEditCall]) => ({
        partnerReportNotificationConfigurations,
        canEditCall,
      })),
      tap(data => this.resetForm(data.partnerReportNotificationConfigurations, data.canEditCall)),
      untilDestroyed(this)
    );
  }

  get partnerReportNotificationConfigurationsArray(): FormArray {
    return this.partnerReportNotificationsForm.get('partnerReportNotificationConfigurations') as FormArray;
  }

  resetForm(partnerReportNotificationConfigurations: ProjectNotificationConfigurationDTO[], canEditCall: boolean): void {
    this.partnerReportNotificationConfigurationsArray.clear();
    partnerReportNotificationConfigurations.forEach(notificationConfig => {
      this.partnerReportNotificationConfigurationsArray.push(this.formBuilder.group(
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
    this.formService.init(this.partnerReportNotificationsForm, of(canEditCall));
  }

  save() {
    const notificationTemplates = this.partnerReportNotificationsForm.getRawValue().partnerReportNotificationConfigurations;
    this.callNotificationSettingsStore.updatePartnerReportNotifications(notificationTemplates).pipe(
      take(1),
      tap(() => this.formService.setSuccess('call.detail.notifications.config.tab.partner.report.form.save.success')),
      catchError(err => this.formService.setError(err)),
    ).subscribe();
  }

}

