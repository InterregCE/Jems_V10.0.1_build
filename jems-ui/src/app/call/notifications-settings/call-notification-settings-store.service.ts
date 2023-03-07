import {Injectable} from '@angular/core';
import {CallDetailDTO, CallNotificationConfigurationService, ProjectNotificationConfigurationDTO} from '@cat/api';
import {RoutingService} from '@common/services/routing.service';
import {switchMap, tap} from 'rxjs/operators';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {Observable} from 'rxjs';

@UntilDestroy()
@Injectable({
    providedIn: 'root'
})
export class CallNotificationSettingsStore {
    public static CALL_DETAIL_PATH = '/app/call/detail';
    private callId: number;
    projectNotificationConfigurations$: Observable<ProjectNotificationConfigurationDTO[]>;

    constructor(
        private callNotificationService: CallNotificationConfigurationService,
        private router: RoutingService
    ) {

      this.projectNotificationConfigurations$ = this.router.routeParameterChanges(CallNotificationSettingsStore.CALL_DETAIL_PATH, 'callId').pipe(
            tap(callId => this.callId = Number(callId)),
            switchMap( callId => this.callNotificationService.getProjectNotificationsByCallId(Number(callId))),
            untilDestroyed(this)
        );
    }

    updateProjectNotifications(projectNotificationTemplates: ProjectNotificationConfigurationDTO[]): Observable<ProjectNotificationConfigurationDTO[]> {
        return this.callNotificationService.updateProjectNotifications(this.callId, projectNotificationTemplates);
    }

}
