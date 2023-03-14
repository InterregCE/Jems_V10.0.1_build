import {Injectable} from '@angular/core';
import {CallNotificationConfigurationService, ProjectNotificationConfigurationDTO} from '@cat/api';
import {RoutingService} from '@common/services/routing.service';
import {switchMap, tap} from 'rxjs/operators';
import {UntilDestroy} from '@ngneat/until-destroy';
import {merge, Observable, Subject} from 'rxjs';

@UntilDestroy()
@Injectable({
    providedIn: 'root'
})
export class CallNotificationSettingsStore {
    public static CALL_DETAIL_PATH = '/app/call/detail';
    private callId: number;
    projectNotificationConfigurations$: Observable<ProjectNotificationConfigurationDTO[]>;
    private projectNotificationConfigurationsSaved$ = new Subject< ProjectNotificationConfigurationDTO[]>();

    constructor(
        private callNotificationService: CallNotificationConfigurationService,
        private router: RoutingService
    ) {

      this.projectNotificationConfigurations$ = this.projectNotificationConfigurations();
    }


    projectNotificationConfigurations(): Observable<ProjectNotificationConfigurationDTO[]> {
        const initialConfigurations$ = this.router.routeParameterChanges(CallNotificationSettingsStore.CALL_DETAIL_PATH, 'callId').pipe(
            tap(callId => this.callId = Number(callId)),
            switchMap( callId => this.callNotificationService.getProjectNotificationsByCallId(Number(callId))),
        );

        return merge(initialConfigurations$, this.projectNotificationConfigurationsSaved$);
    }

    updateProjectNotifications(projectNotificationTemplates: ProjectNotificationConfigurationDTO[]): Observable<ProjectNotificationConfigurationDTO[]> {
        return this.callNotificationService.updateProjectNotifications(this.callId, projectNotificationTemplates).pipe(
            tap(configurations => this.projectNotificationConfigurationsSaved$.next(configurations))
        );
    }

}
