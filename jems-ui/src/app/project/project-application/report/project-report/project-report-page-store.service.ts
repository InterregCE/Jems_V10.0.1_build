import {Injectable} from '@angular/core';
import {BehaviorSubject, combineLatest, Observable, Subject} from 'rxjs';
import {
  PageProjectReportSummaryDTO,
  ProjectReportDTO,
  ProjectReportService,
  ProjectReportUpdateDTO,
  ProjectUserCollaboratorService,
  UserRoleDTO
} from '@cat/api';
import {Tables} from '@common/utils/tables';
import {RoutingService} from '@common/services/routing.service';
import {PermissionService} from '../../../../security/permissions/permission.service';
import {filter, map, shareReplay, startWith, switchMap, tap} from 'rxjs/operators';
import {Log} from '@common/utils/log';
import {MatSort} from '@angular/material/sort';
import {ProjectStore} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import PermissionsEnum = UserRoleDTO.PermissionsEnum;

@Injectable({providedIn: 'root'})
export class ProjectReportPageStore {

  projectReports$: Observable<PageProjectReportSummaryDTO>;
  userCanCreateReport$: Observable<boolean>;
  userCanViewReport$: Observable<boolean>;
  userCanEditReport$: Observable<boolean>;
  userCanViewVerification$: Observable<boolean>;
  userCanEditVerification$: Observable<boolean>;
  userCanFinalizeVerification$: Observable<boolean>;

  newPageSize$ = new BehaviorSubject<number>(Tables.DEFAULT_INITIAL_PAGE_SIZE);
  newPageIndex$ = new BehaviorSubject<number>(Tables.DEFAULT_INITIAL_PAGE_INDEX);
  newSort$ = new Subject<Partial<MatSort>>();
  private refreshReports$ = new Subject<void>();

  constructor(private routingService: RoutingService,
              private projectReportService: ProjectReportService,
              private projectStore: ProjectStore,
              private projectUserCollaboratorService: ProjectUserCollaboratorService,
              private permissionService: PermissionService) {
    this.projectReports$ = this.projectReports();
    this.userCanCreateReport$ = this.userCanCreateReports();
    this.userCanViewReport$ = this.userCanViewReports();
    this.userCanEditReport$ = this.userCanEditReports();
    this.userCanViewVerification$ = this.userCanViewVerification();
    this.userCanEditVerification$ = this.userCanEditVerification();
    this.userCanFinalizeVerification$ = this.userCanFinalizeVerification();
  }

  createProjectReport(identification: ProjectReportUpdateDTO): Observable<ProjectReportDTO> {
    return this.projectStore.projectId$
      .pipe(
        switchMap((projectId) => this.projectReportService.createProjectReport(Number(projectId), identification)),
        tap(() => this.refreshReports$.next()),
        tap(created => Log.info('Created projectReport:', this, created)),
      );
  }

  deleteProjectReport(reportId: number) {
    return this.projectStore.projectId$
      .pipe(
        switchMap((projectId) => this.projectReportService.deleteProjectReport(projectId , reportId)),
        tap(() => {
          Log.info('Project report deleted');
          this.refreshReports$.next();
        }),
      );
  }

  private projectReports(): Observable<PageProjectReportSummaryDTO> {
    return combineLatest([
      this.projectStore.projectId$,
      this.newPageIndex$,
      this.newPageSize$,
      this.newSort$.pipe(
        startWith(({ active: undefined, direction: undefined }) as Partial<MatSort>),
        map((sort: Partial<MatSort>) => sort?.direction ? `${sort.active},${sort.direction}` : 'id,desc'),
      ),
      this.refreshReports$.pipe(startWith(null)),
    ])
      .pipe(
        filter(([projectId]) => !!projectId),
        switchMap(([projectId, page, size, sort]) =>
          this.projectReportService.getProjectReportList(projectId, page, size, sort)),
        tap((data: PageProjectReportSummaryDTO) => Log.info('Fetched project reports for project:', this, data))
      );
  }

  private projectReportLevel(): Observable<string> {
    return this.projectStore.projectId$
      .pipe(
        filter((projectId) => !!projectId),
        switchMap((projectId) => this.projectUserCollaboratorService.checkMyProjectLevel(Number(projectId))),
        map((level: string) => level),
        shareReplay(1)
      );
  }

  private userCanCreateReports(): Observable<boolean> {
    return combineLatest([
      this.projectReportLevel(),
      this.permissionService.hasPermission(PermissionsEnum.ProjectCreatorReportingProjectCreate),
      this.permissionService.hasPermission(PermissionsEnum.ProjectReportingProjectEdit),
    ])
        .pipe(
            map(([level, creatorCanCreateReport, monitorCanEdit]) => {
              const creatorCanEdit = level === 'EDIT' || level === 'MANAGE';
              return (creatorCanEdit && creatorCanCreateReport) || monitorCanEdit;
            })
        );
  }

  private userCanEditReports(): Observable<boolean> {
    return combineLatest([
      this.projectReportLevel(),
      this.permissionService.hasPermission(PermissionsEnum.ProjectReportingProjectEdit)
    ])
      .pipe(
        map(([level, canEdit]) => level === 'EDIT' || level === 'MANAGE' || canEdit)
      );
  }

  private userCanViewReports(): Observable<boolean> {
    return combineLatest([
      this.projectReportLevel(),
      this.permissionService.hasPermission(PermissionsEnum.ProjectReportingProjectEdit),
      this.permissionService.hasPermission(PermissionsEnum.ProjectReportingProjectView)
    ])
      .pipe(
        map(([level, canEdit, canView]) => level === 'VIEW' || level === 'EDIT' || level === 'MANAGE' || canEdit || canView)
      );
  }

  private userCanViewVerification(): Observable<boolean> {
    return combineLatest([
      this.projectReportLevel(),
      this.permissionService.hasPermission(PermissionsEnum.ProjectReportingVerificationProjectView),
      this.permissionService.hasPermission(PermissionsEnum.ProjectReportingVerificationProjectEdit),
    ]).pipe(
      map(([level, canView, canEdit]) => level === 'VIEW' || level === 'EDIT' || level === 'MANAGE' || canView || canEdit)
    );
  }

  private userCanEditVerification(): Observable<boolean> {
    return combineLatest([
      this.projectReportLevel(),
      this.permissionService.hasPermission(PermissionsEnum.ProjectReportingVerificationProjectEdit),
    ]).pipe(
      map(([level, canEdit]) => level === 'EDIT' || level === 'MANAGE' || canEdit)
    );
  }

  private userCanFinalizeVerification(): Observable<boolean> {
    return combineLatest([
      this.permissionService.hasPermission(PermissionsEnum.ProjectReportingVerificationFinalize),
    ]).pipe(
      map(([canFinalize]) => canFinalize)
    );
  }
}
