import {Injectable} from '@angular/core';
import {CallNotificationConfigurationService, ProjectNotificationConfigurationDTO} from '@cat/api';
import {switchMap, tap} from 'rxjs/operators';
import {UntilDestroy} from '@ngneat/until-destroy';
import {merge, Observable, Subject} from 'rxjs';
import {CallStore} from '../services/call-store.service';

@UntilDestroy()
@Injectable({
  providedIn: 'root'
})
export class CallNotificationSettingsStore {
  callId$: Observable<number>;
  canEditCall$: Observable<boolean>;
  projectNotificationConfigurations$: Observable<ProjectNotificationConfigurationDTO[]>;
  partnerReportNotificationConfigurations$: Observable<ProjectNotificationConfigurationDTO[]>;
  private projectNotificationConfigurationsSaved$ = new Subject<ProjectNotificationConfigurationDTO[]>();
  private partnerReportNotificationConfigurationsSaved$ = new Subject<ProjectNotificationConfigurationDTO[]>();

  constructor(
    private readonly callStore: CallStore,
    private callNotificationService: CallNotificationConfigurationService,
  ) {
    this.callId$ = callStore.callId$;
    this.canEditCall$ = callStore.callIsEditable$;
    this.projectNotificationConfigurations$ = this.projectNotificationConfigurations();
    this.partnerReportNotificationConfigurations$ = this.partnerReportNotificationConfigurations();
  }


  private projectNotificationConfigurations(): Observable<ProjectNotificationConfigurationDTO[]> {
    const initialConfigurations$ = this.callId$.pipe(
      switchMap(callId => this.callNotificationService.getProjectNotificationsByCallId(callId)),
    );

    return merge(initialConfigurations$, this.projectNotificationConfigurationsSaved$);
  }

  private partnerReportNotificationConfigurations(): Observable<ProjectNotificationConfigurationDTO[]> {
    const initialConfigurations$ = this.callId$.pipe(
      switchMap(callId => this.callNotificationService.getPartnerReportNotificationsByCallId(callId)),
    );

    return merge(initialConfigurations$, this.partnerReportNotificationConfigurationsSaved$);
  }

  updateProjectNotifications(projectNotificationTemplates: ProjectNotificationConfigurationDTO[]): Observable<ProjectNotificationConfigurationDTO[]> {
    return this.callId$.pipe(
      switchMap(callId => this.callNotificationService.updateProjectNotifications(callId, projectNotificationTemplates)),
      tap(configurations => this.projectNotificationConfigurationsSaved$.next(configurations))
    );
  }

  updatePartnerReportNotifications(partnerReportNotificationTemplates: ProjectNotificationConfigurationDTO[]): Observable<ProjectNotificationConfigurationDTO[]> {
    return this.callId$.pipe(
      switchMap(callId => this.callNotificationService.updatePartnerReportNotifications(callId, partnerReportNotificationTemplates)),
      tap(configurations => this.partnerReportNotificationConfigurationsSaved$.next(configurations))
    );
  }

}
