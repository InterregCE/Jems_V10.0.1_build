import {Injectable} from '@angular/core';
import {combineLatest, merge, Observable, of, Subject} from 'rxjs';
import {AuditControlDTO, ProjectAuditAndControlService, ProjectAuditControlUpdateDTO} from '@cat/api';
import {RoutingService} from '@common/services/routing.service';
import {catchError, shareReplay, switchMap, tap} from 'rxjs/operators';
import {ProjectStore} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {Log} from '@common/utils/log';
import {ReportCorrectionsOverviewStore} from '@project/project-application/report/report-corrections-overview/report-corrections-overview.store';
import {UntilDestroy} from '@ngneat/until-destroy';
import {ProjectPaths} from '@project/common/project-util';

@UntilDestroy()
@Injectable({
  providedIn: 'root'
})
export class ReportCorrectionsAuditControlDetailPageStore {

  AUDIT_CONTROL_PATH = 'corrections/auditControl/';

  projectId$: Observable<number>;
  auditControlId$: Observable<string | number | null>;
  auditControl$: Observable<AuditControlDTO>;
  canEdit$: Observable<boolean>;
  updatedAuditControl$ = new Subject<AuditControlDTO>();

  constructor(
    private routingService: RoutingService,
    private projectStore: ProjectStore,
    private auditControlService: ProjectAuditAndControlService,
    private reportCorrectionsOverviewStore: ReportCorrectionsOverviewStore,
  ) {
    this.projectId$ = this.projectStore.projectId$;
    this.auditControlId$ = this.auditControlId();
    this.auditControl$ = this.auditControl();
    this.canEdit$ = reportCorrectionsOverviewStore.canEdit$;
  }

  private auditControlId(): Observable<string | number | null> {
    return this.routingService.routeParameterChanges(this.AUDIT_CONTROL_PATH, 'auditControlId');
  }

  private auditControl(): Observable<AuditControlDTO> {
    const initialAuditControl = combineLatest([
      this.projectStore.projectId$,
      this.auditControlId$
    ]).pipe(
      switchMap(([projectId, auditControlId]) =>
        auditControlId
          ? this.auditControlService.getAuditDetail(Number(auditControlId), projectId).pipe(
            catchError(() => {
              this.routingService.navigate([ProjectPaths.PROJECT_DETAIL_PATH, projectId]);
              return of({} as AuditControlDTO);
            })
          )
          : of({} as AuditControlDTO)
      ),
      tap((auditControl) => Log.info('Fetched auditControl', this, auditControl)),
      shareReplay(1),
    );

    return merge(initialAuditControl, this.updatedAuditControl$);
  }

  saveAuditControl(id: number | undefined, auditControlData: ProjectAuditControlUpdateDTO): Observable<AuditControlDTO> {
    return this.projectStore.projectId$.pipe(
      switchMap(projectId => id
        ? this.auditControlService.updateProjectAudit(id, projectId, auditControlData)
        : this.auditControlService.createProjectAudit(projectId, auditControlData)
      ),
      tap(() => this.reportCorrectionsOverviewStore.refreshAudits$.next()),
      tap(auditControl => this.updatedAuditControl$.next(auditControl)),
    );
  }
}
