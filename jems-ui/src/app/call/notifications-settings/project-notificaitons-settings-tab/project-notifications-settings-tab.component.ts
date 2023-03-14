import {Component} from '@angular/core';
import {ProjectNotificationConfigurationDTO} from '@cat/api';
import {FormArray, FormBuilder, Validators} from '@angular/forms';
import {FormService} from '@common/components/section/form/form.service';
import {CallNotificationSettingsStore} from '../call-notification-settings-store.service';
import {catchError, map, take, tap} from 'rxjs/operators';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {Observable} from 'rxjs';

@UntilDestroy()
@Component({
    selector: 'jems-project-notifications-settings-tab',
    templateUrl: './project-notifications-settings-tab.component.html',
    styleUrls: ['./project-notifications-settings-tab.component.scss']
})
export class ProjectNotificationsSettingsTabComponent {



    projectNotificationsForm = this.formBuilder.group({
        projectNotificationConfigurations: this.formBuilder.array([]),
    });

    data$: Observable<{
        projectNotificationConfigurations: ProjectNotificationConfigurationDTO[];
    }>;

    constructor(
        private formBuilder: FormBuilder,
        private formService: FormService,
        private callNotificationSettingsStore: CallNotificationSettingsStore
    ) {
        this.data$ = this.callNotificationSettingsStore.projectNotificationConfigurations$.pipe(
            tap(notificationConfigurations => this.resetForm(notificationConfigurations)),
            map(notificationConfigurations => ({
                projectNotificationConfigurations: notificationConfigurations
            })),
            untilDestroyed(this)
        );
    }

    get projectNotificationConfigurationsArray(): FormArray {
        return this.projectNotificationsForm.get('projectNotificationConfigurations') as FormArray;
    }

    resetForm(projectNotificationConfigurations: ProjectNotificationConfigurationDTO[]): void {
        this.projectNotificationConfigurationsArray.clear();
        projectNotificationConfigurations.forEach(notificationConfig => {
            this.projectNotificationConfigurationsArray.push(this.formBuilder.group(
                {
                    id: notificationConfig.id,
                    active: notificationConfig.active,
                    emailSubject: notificationConfig.emailSubject,
                    emailBody: [notificationConfig.emailBody, Validators.maxLength(10000)],
                    sendToManager: notificationConfig.sendToManager,
                    sendToLeadPartner: notificationConfig.sendToLeadPartner,
                    sendToProjectPartners: notificationConfig.sendToProjectPartners,
                    sendToProjectAssigned: notificationConfig.sendToProjectAssigned
                }
            ));
        });
        this.formService.init(this.projectNotificationsForm);
    }

    save() {
        const notificationTemplates = this.projectNotificationsForm.getRawValue().projectNotificationConfigurations;
        this.callNotificationSettingsStore.updateProjectNotifications(notificationTemplates).pipe(
            take(1),
            tap(() => this.formService.setSuccess('call.detail.notifications.config.tab.project.form.save.success')),
            catchError(err => this.formService.setError(err)),
        ).subscribe();
    }
}

