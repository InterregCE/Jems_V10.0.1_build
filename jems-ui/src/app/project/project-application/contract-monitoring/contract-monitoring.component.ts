import {ChangeDetectionStrategy, Component} from '@angular/core';
import {BehaviorSubject, combineLatest, Observable, of, Subject} from 'rxjs';
import {ProjectDetailDTO, ProjectStatusDTO} from '@cat/api';
import {ProjectStore} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {catchError, filter, finalize, map, switchMap, take, tap} from 'rxjs/operators';
import {ContractMonitoringStore} from '@project/project-application/contract-monitoring/contract-monitoring-store';
import {APIError} from '@common/models/APIError';
import { Alert } from '@common/components/forms/alert';
import {Forms} from '@common/utils/forms';
import {MatDialog} from '@angular/material/dialog';

@Component({
  selector: 'jems-contract-monitoring',
  templateUrl: './contract-monitoring.component.html',
  styleUrls: ['./contract-monitoring.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ContractMonitoringComponent {

  STATUS = ProjectStatusDTO.StatusEnum;
  Alert = Alert;

  data$: Observable<{
    currentVersionOfProject: ProjectDetailDTO;
    currentVersionOfProjectTitle: string;
    canSetToContracted: boolean;
  }>;

  showSuccessMessage$ = new Subject<null | string>();
  error$ = new BehaviorSubject<APIError | null>(null);
  actionPending = false;

  constructor(public projectStore: ProjectStore, private contractMonitoringStore: ContractMonitoringStore, private dialog: MatDialog) {
    this.data$ = combineLatest([
      this.projectStore.currentVersionOfProject$,
      this.projectStore.currentVersionOfProjectTitle$,
      this.contractMonitoringStore.canSetToContracted$,
    ]).pipe(
      map(([currentVersionOfProject, currentVersionOfProjectTitle, canSetToContracted]) => ({
        currentVersionOfProject,
        currentVersionOfProjectTitle,
        canSetToContracted,
      }))
    );
  }

  setToContracted(projectId: number, projectTitle: string): void {
    Forms.confirm(
      this.dialog,
      {
        title: 'project.application.set.to.contracted.warning.header',
        message: {
          i18nKey: 'project.application.set.to.contracted.warning',
          i18nArguments: {projectName: projectTitle}
        }
      }).pipe(
      take(1),
      filter(confirmed => confirmed),
      switchMap(() => {
        this.actionPending = true;
        return this.contractMonitoringStore.setToContracted(projectId).pipe(
          tap(() => {
            this.showSuccessMessage$.next(projectTitle);
            setTimeout(() => this.showSuccessMessage$.next(null), 4000);
          }),
          catchError((error) => this.showErrorMessage(error.error)),
          finalize(() => this.actionPending = false)
        );
      }),
    ).subscribe();
  }

  private showErrorMessage(error: APIError): Observable<null> {
    this.error$.next(error);
    setTimeout(() => {
      this.error$.next(null);
    },         4000);
    return of(null);
  }
}
