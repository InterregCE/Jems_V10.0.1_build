import {Component} from '@angular/core';
import {ProjectNotificationConfigurationDTO} from '@cat/api';
import {FormArray, FormBuilder, Validators} from '@angular/forms';
import {FormService} from '@common/components/section/form/form.service';
import {CallNotificationSettingsStore} from '../call-notification-settings-store.service';
import {catchError, map, take, tap} from 'rxjs/operators';
import {UntilDestroy} from '@ngneat/until-destroy';
import {combineLatest, Observable, of} from 'rxjs';
import {NotificationVariable} from '../notification-variable.enum';
import {Alert} from '@common/components/forms/alert';

@UntilDestroy()
@Component({
  selector: 'jems-project-notifications-settings-tab',
  templateUrl: './project-notifications-settings-tab.component.html',
  styleUrls: ['./project-notifications-settings-tab.component.scss']
})
export class ProjectNotificationsSettingsTabComponent {
  Alert = Alert;

  projectNotificationsForm = this.formBuilder.group({
    projectNotificationConfigurations: this.formBuilder.array([]),
  });

  data$: Observable<{
    projectNotificationConfigurations: ProjectNotificationConfigurationDTO[];
    canEditCall: boolean;
  }>;

  projectNotificationVariables = [
    NotificationVariable.PROGRAMME_NAME,
    NotificationVariable.CALL_ID,
    NotificationVariable.CALL_NAME,
    NotificationVariable.PROJECT_ID,
    NotificationVariable.PROJECT_IDENTIFIER,
    NotificationVariable.PROJECT_ACRONYM,
  ];

  constructor(
    private formBuilder: FormBuilder,
    private formService: FormService,
    private callNotificationSettingsStore: CallNotificationSettingsStore
  ) {
    this.data$ = combineLatest(
      this.callNotificationSettingsStore.projectNotificationConfigurations$,
      this.callNotificationSettingsStore.canEditCall$,
    ).pipe(
      map(([projectNotificationConfigurations, canEditCall]) => ({
        projectNotificationConfigurations,
        canEditCall,
      })),
      tap(data => this.resetForm(data.projectNotificationConfigurations, data.canEditCall)),
    );
  }

  get projectNotificationConfigurationsArray(): FormArray {
    return this.projectNotificationsForm.get('projectNotificationConfigurations') as FormArray;
  }

  resetForm(projectNotificationConfigurations: ProjectNotificationConfigurationDTO[], canEditCall: boolean): void {
    this.projectNotificationConfigurationsArray.clear();
    projectNotificationConfigurations.forEach(notificationConfig => {
      this.projectNotificationConfigurationsArray.push(this.formBuilder.group(
        {
          id: notificationConfig.id,
          active: notificationConfig.active,
          emailSubject: [notificationConfig.emailSubject, Validators.maxLength(255)],
          emailBody: [notificationConfig.emailBody, Validators.maxLength(10000)],
          sendToManager: notificationConfig.sendToManager,
          sendToLeadPartner: notificationConfig.sendToLeadPartner,
          sendToProjectPartners: notificationConfig.sendToProjectPartners,
          sendToProjectAssigned: notificationConfig.sendToProjectAssigned,
        }
      ));
    });
    this.formService.init(this.projectNotificationsForm, of(canEditCall));
  }

  save() {
    const notificationTemplates = this.projectNotificationsForm.getRawValue().projectNotificationConfigurations;
    this.callNotificationSettingsStore.updateProjectNotifications(notificationTemplates).pipe(
      take(1),
      tap(() => this.formService.setSuccess('call.detail.notifications.config.tab.project.form.save.success')),
      catchError(err => this.formService.setError(err)),
    ).subscribe();
  }

  isPartnerSelectionDisabled(id: ProjectNotificationConfigurationDTO.IdEnum): boolean {
    return [
      ProjectNotificationConfigurationDTO.IdEnum.ProjectSubmittedStep1,
      ProjectNotificationConfigurationDTO.IdEnum.ProjectSubmitted,
      ProjectNotificationConfigurationDTO.IdEnum.ProjectApprovedStep1,
      ProjectNotificationConfigurationDTO.IdEnum.ProjectApprovedWithConditionsStep1,
      ProjectNotificationConfigurationDTO.IdEnum.ProjectIneligibleStep1,
      ProjectNotificationConfigurationDTO.IdEnum.ProjectNotApprovedStep1,
      ProjectNotificationConfigurationDTO.IdEnum.ProjectApproved,
      ProjectNotificationConfigurationDTO.IdEnum.ProjectApprovedWithConditions,
      ProjectNotificationConfigurationDTO.IdEnum.ProjectIneligible,
      ProjectNotificationConfigurationDTO.IdEnum.ProjectNotApproved,
      ProjectNotificationConfigurationDTO.IdEnum.ProjectReturnedToApplicant,
      ProjectNotificationConfigurationDTO.IdEnum.ProjectResubmitted,
      ProjectNotificationConfigurationDTO.IdEnum.ProjectReturnedForConditions,
      ProjectNotificationConfigurationDTO.IdEnum.ProjectConditionsSubmitted
    ].includes(id);
  }
}

