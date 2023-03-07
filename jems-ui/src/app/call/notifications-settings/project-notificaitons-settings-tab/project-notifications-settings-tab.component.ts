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
        projectNotificationTemplates: ProjectNotificationConfigurationDTO[];
    }>;

    constructor(
        private formBuilder: FormBuilder,
        private formService: FormService,
        private callNotificationSettingsStore: CallNotificationSettingsStore
    ) {
        this.data$ = this.callNotificationSettingsStore.projectNotificationConfigurations$.pipe(
            tap(templates => this.resetForm(templates)),
            map(templates => ({
                projectNotificationTemplates: templates
            })),
            untilDestroyed(this)
        );
    }

    get projectNotificationConfigurationsArray(): FormArray {
        return this.projectNotificationsForm.get('projectNotificationConfigurations') as FormArray;
    }

    resetForm(projectNotificationTemplates: ProjectNotificationConfigurationDTO[]): void {
        this.projectNotificationConfigurationsArray.clear();
        projectNotificationTemplates.forEach(notificationTemplate => {
            this.projectNotificationConfigurationsArray.push(this.formBuilder.group(
                {
                    id: notificationTemplate.id,
                    active: notificationTemplate.active,
                    emailSubject: notificationTemplate.emailSubject,
                    emailBody: [notificationTemplate.emailBody, Validators.maxLength(1000)],
                    sendToManager: notificationTemplate.sendToManager,
                    sendToLeadPartner: notificationTemplate.sendToLeadPartner,
                    sendToProjectPartners: notificationTemplate.sendToProjectPartners,
                    sendToProjectAssigned: notificationTemplate.sendToProjectAssigned
                }
            ));
        });
        this.formService.init(this.projectNotificationsForm);
    }

    save() {
        const notificationTemplates = this.projectNotificationsForm.getRawValue().projectNotificationConfigurations;
        this.callNotificationSettingsStore.updateProjectNotifications(notificationTemplates).pipe(
            take(1),
            tap(projectNotificationConfigurations => this.resetForm(projectNotificationConfigurations)),
            tap(() => this.formService.setSuccess('call.detail.notifications.config.tab.project.form.save.success')),
            catchError(err => this.formService.setError(err)),
        ).subscribe();
    }
}
