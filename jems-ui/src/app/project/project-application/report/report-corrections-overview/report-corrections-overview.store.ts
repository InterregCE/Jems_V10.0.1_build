import {Injectable} from '@angular/core';
import {BehaviorSubject, combineLatest, Observable, Subject} from 'rxjs';
import {PageAuditControlDTO, ProjectAuditAndControlService, UserRoleDTO} from '@cat/api';
import {ProjectStore} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {map, shareReplay, startWith, switchMap, tap} from 'rxjs/operators';
import {Log} from '@common/utils/log';
import {PermissionService} from '../../../../security/permissions/permission.service';
import {Tables} from '@common/utils/tables';
import {MatSort} from '@angular/material/sort';
import PermissionsEnum = UserRoleDTO.PermissionsEnum;


@Injectable({
  providedIn: 'root'
})
export class ReportCorrectionsOverviewStore {

  auditControls$: Observable<PageAuditControlDTO>;
  refreshAudits$ = new Subject<void>();
  canEdit$: Observable<boolean>;

  newPageSize$ = new BehaviorSubject<number>(Tables.DEFAULT_INITIAL_PAGE_SIZE);
  newPageIndex$ = new BehaviorSubject<number>(Tables.DEFAULT_INITIAL_PAGE_INDEX);
  newSort$ = new Subject<Partial<MatSort>>();

  constructor(
    private projectStore: ProjectStore,
    private auditControlService: ProjectAuditAndControlService,
    private permissionService: PermissionService,
  ) {
    this.auditControls$ = this.auditControls();
    this.canEdit$ = this.canEdit();
  }

  private auditControls(): Observable<PageAuditControlDTO> {
    return combineLatest([
      this.projectStore.projectId$,
      this.newPageIndex$,
      this.newPageSize$,
      this.newSort$.pipe(
        startWith(({active: undefined, direction: undefined}) as Partial<MatSort>),
        map((sort: Partial<MatSort>)=> sort?.direction ? `${sort.active},${sort.direction}` : 'id,desc')
      ),
      this.refreshAudits$.pipe(startWith(null))
    ]).pipe(
      switchMap(([projectId, page, size, sort]) =>
        this.auditControlService.listAuditsForProject(projectId, page,size,sort)),
      tap((page: PageAuditControlDTO) => Log.info('Fetched auditControls', this, page.content)),
      shareReplay(1),
    );
  }

  private canEdit(): Observable<boolean> {
    return this.permissionService.hasPermission(PermissionsEnum.ProjectMonitorAuditAndControlEdit)
      .pipe(map(canEdit => canEdit));
  }

}
