import {Injectable} from '@angular/core';
import {combineLatest, merge, Observable, of, Subject} from 'rxjs';
import {AuditControlDTO, ProjectAuditAndControlService, ProjectAuditControlUpdateDTO, UserRoleDTO} from '@cat/api';
import {RoutingService} from '@common/services/routing.service';
import {catchError, map, shareReplay, startWith, switchMap, tap} from 'rxjs/operators';
import {ProjectStore} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {Log} from '@common/utils/log';
import {ReportCorrectionsOverviewStore} from '@project/project-application/report/report-corrections-overview/report-corrections-overview.store';
import {UntilDestroy} from '@ngneat/until-destroy';
import {ProjectPaths} from '@project/common/project-util';
import {PermissionService} from '../../../../../security/permissions/permission.service';
import PermissionsEnum = UserRoleDTO.PermissionsEnum;

@UntilDestroy()
@Injectable({
  providedIn: 'root'
})
export class ReportCorrectionsAuditControlDetailPageStore {

  AUDIT_CONTROL_PATH = 'corrections/auditControl/';

  projectId$: Observable<number>;
  auditControlId$: Observable<string | number | null>;
  auditControl$: Observable<AuditControlDTO>;
  auditControlStatus$: Observable<AuditControlDTO.StatusEnum>;
  canEdit$: Observable<boolean>;
  canClose$: Observable<boolean>;
  updatedAuditControl$ = new Subject<AuditControlDTO>();
  updatedAuditControlStatus$ = new Subject<AuditControlDTO.StatusEnum>();

  constructor(
    private routingService: RoutingService,
    private projectStore: ProjectStore,
    private auditControlService: ProjectAuditAndControlService,
    private reportCorrectionsOverviewStore: ReportCorrectionsOverviewStore,
    private permissionService: PermissionService,
  ) {
    this.projectId$ = this.projectStore.projectId$;
    this.auditControlId$ = this.auditControlId();
    this.auditControl$ = this.auditControl();
    this.auditControlStatus$ = this.auditControlStatus();
    this.canEdit$ = this.canEdit();
    this.canClose$ = this.canClose();
  }

  private auditControlId(): Observable<string | number | null> {
    return this.routingService.routeParameterChanges(this.AUDIT_CONTROL_PATH, 'auditControlId');
  }

  private auditControl(): Observable<AuditControlDTO> {
    const initialAuditControl = combineLatest([
      this.projectStore.projectId$,
      this.auditControlId$,
      this.updatedAuditControlStatus$.pipe(startWith(null)),
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

  private auditControlStatus(): Observable<AuditControlDTO.StatusEnum> {
    return merge(
      this.auditControl$.pipe(map(auditControl => auditControl.status)),
      this.updatedAuditControlStatus$
    );
  }

  private canEdit(): Observable<boolean> {
    return combineLatest([
      this.permissionService.hasPermission(PermissionsEnum.ProjectMonitorAuditAndControlEdit),
      this.auditControlStatus$
    ]).pipe(
      map(([canEdit, status]) => canEdit && status !== AuditControlDTO.StatusEnum.Closed)
    );
  }

  private canClose(): Observable<boolean> {
    return combineLatest([
      this.permissionService.hasPermission(PermissionsEnum.ProjectMonitorAuditAndControlEdit),
      this.permissionService.hasPermission(PermissionsEnum.ProjectMonitorCloseAuditControl),
      this.auditControlStatus$
    ]).pipe(
      map(([canEdit, canClose, status]) => canEdit && canClose && status === AuditControlDTO.StatusEnum.Ongoing)
    );
  }

  saveAuditControl(id: number | undefined, auditControlData: ProjectAuditControlUpdateDTO): Observable<AuditControlDTO> {
    return this.projectStore.projectId$.pipe(
      switchMap(projectId => id
        ? this.auditControlService.updateProjectAudit(id, projectId, auditControlData)
        : this.auditControlService.createProjectAudit(projectId, auditControlData)
      ),
      tap(() => this.reportCorrectionsOverviewStore.refreshAudits$.next()),
      tap(auditControl => this.updatedAuditControl$.next(auditControl)),
      tap(auditControl => Log.info(id ? 'Updated audit' : 'Created audit', this, auditControl))
    );
  }

  closeAuditControl(projectId: number, auditControlId: number): Observable<AuditControlDTO.StatusEnum> {
    return this.auditControlService.closeAuditControl(auditControlId, projectId).pipe(
      map(status => status as AuditControlDTO.StatusEnum),
      tap(() => this.reportCorrectionsOverviewStore.refreshAudits$.next()),
      tap(status => this.updatedAuditControlStatus$.next(status)),
      tap(status => Log.info('Changed status for audit', this, auditControlId, status)),
    );
  }
}
