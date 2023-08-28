import {Injectable} from '@angular/core';
import {
  ControllerInstitutionsApiService,
  PageProjectPartnerReportSummaryDTO, PaymentDetailDTO,
  ProjectPartnerReportService,
  ProjectPartnerReportSummaryDTO,
  ProjectPartnerSummaryDTO,
  ProjectPartnerUserCollaboratorService,
  UserRoleCreateDTO
} from '@cat/api';
import {BehaviorSubject, combineLatest, Observable, of, Subject} from 'rxjs';
import {catchError, filter, map, shareReplay, startWith, switchMap, take, tap} from 'rxjs/operators';
import {RoutingService} from '@common/services/routing.service';
import {ProjectPartnerStore} from '@project/project-application/containers/project-application-form-page/services/project-partner-store.service';
import {Log} from '@common/utils/log';
import {Tables} from '@common/utils/tables';
import {PermissionService} from 'src/app/security/permissions/permission.service';
import {ProgrammeEditableStateStore} from '../../../programme/programme-page/services/programme-editable-state-store.service';
import {PrivilegesPageStore} from '@project/project-application/privileges-page/privileges-page-store.service';
import PermissionsEnum = UserRoleCreateDTO.PermissionsEnum;
import {MatSort} from '@angular/material/sort';

@Injectable({providedIn: 'root'})
export class PartnerReportPageStore {
  public static PARTNER_REPORT_DETAIL_PATH = '/reporting/';

  partnerReports$: Observable<PageProjectPartnerReportSummaryDTO>;
  partnerSummary$: Observable<ProjectPartnerSummaryDTO>;
  partnerReportLevel$: Observable<string>;
  partnerId$: Observable<string | number | null>;
  userCanViewReport$: Observable<boolean>;
  userCanEditReport$: Observable<boolean>;
  institutionUserCanViewControlReports$: Observable<boolean>;
  institutionUserCanEditControlReports$: Observable<boolean>;
  userCanViewGdpr$: Observable<boolean>;
  canCreateReport$: Observable<boolean>;

  newPageSize$ = new BehaviorSubject<number>(Tables.DEFAULT_INITIAL_PAGE_SIZE);
  newPageIndex$ = new BehaviorSubject<number>(Tables.DEFAULT_INITIAL_PAGE_INDEX);
  newSort$ = new Subject<Partial<MatSort>>();

  private refreshReports$ = new Subject<void>();

  constructor(private routingService: RoutingService,
              private partnerProjectStore: ProjectPartnerStore,
              private projectPartnerReportService: ProjectPartnerReportService,
              private projectPartnerUserCollaboratorService: ProjectPartnerUserCollaboratorService,
              private permissionService: PermissionService,
              private programmeEditableStateStore: ProgrammeEditableStateStore,
              private controllerInstitutionService: ControllerInstitutionsApiService,
              private privilegesPageStore: PrivilegesPageStore) {
    this.partnerId$ = this.partnerId();
    this.partnerReports$ = this.partnerReports();
    this.partnerSummary$ = this.partnerSummary();
    this.partnerReportLevel$ = this.partnerReportLevel();
    this.userCanViewReport$ = this.userCanViewReports();
    this.userCanEditReport$ = this.userCanEditReports();
    this.institutionUserCanViewControlReports$ = this.institutionUserCanViewControlReports();
    this.institutionUserCanEditControlReports$ = this.institutionUserCanEditControlReports();
    this.userCanViewGdpr$ = this.userCanViewGdpr();
    this.canCreateReport$ = this.canCreateReport();
  }

  createPartnerReport(): Observable<ProjectPartnerReportSummaryDTO> {
    return this.partnerId$
      .pipe(
        take(1),
        switchMap((partnerId) => this.projectPartnerReportService.createProjectPartnerReport(partnerId as any)),
        tap(() => this.refreshReports$.next()),
        tap(created => Log.info('Created partnerReport:', this, created)),
      );
  }

  deletePartnerReport(reportId: number) {
    return this.partnerId$
      .pipe(
        take(1),
        switchMap((partnerId) => this.projectPartnerReportService.deleteProjectPartnerReport(partnerId as number, reportId)),
        tap(() => {
          Log.info('Partner report deleted');
          this.refreshReports$.next();
        }),
      );
  }

  private partnerReports(): Observable<PageProjectPartnerReportSummaryDTO> {
    return combineLatest([
      this.partnerId$,
      this.newPageIndex$,
      this.newPageSize$,
      this.newSort$.pipe(
          startWith(({ active: undefined, direction: undefined }) as Partial<MatSort>),
          map((sort: Partial<MatSort>) => sort?.direction ? `${sort.active},${sort.direction}` : 'id,desc'),
      ),
      this.refreshReports$.pipe(startWith(null)),
    ])
      .pipe(
        filter(([partnerId]) => !!partnerId),
        switchMap(([partnerId, pageIndex, pageSize, sort]) =>
          this.projectPartnerReportService.getProjectPartnerReports(Number(partnerId), pageIndex, pageSize, sort)),
        tap((data: PageProjectPartnerReportSummaryDTO) => Log.info('Fetched partner reports for partner:', this, data))
      );
  }

  private partnerSummary(): Observable<ProjectPartnerSummaryDTO> {
    return combineLatest([
      this.partnerId$,
      this.partnerProjectStore.partnerReportSummaries$
    ]).pipe(
      filter(([partnerId]) => !!partnerId),
      map(([partnerId, partnerSummaries]) =>
        partnerSummaries.find(value => value.id === Number(partnerId)) || {} as any
      ));
  }

  private partnerReportLevel(): Observable<string> {
    return this.partnerId$
      .pipe(
        filter((partnerId) => !!partnerId),
        switchMap((partnerId) => this.projectPartnerUserCollaboratorService.checkMyPartnerLevel(Number(partnerId))),
        map((level: string) => level),
        shareReplay(1)
      );
  }

  private institutionUserControlReportLevel(): Observable<string> {
    return this.partnerId$
      .pipe(
        filter((partnerId) => !!partnerId),
        switchMap((partnerId) => this.controllerInstitutionService.getControllerUserAccessLevelForPartner(Number(partnerId))),
        map((level: string) => level),
        shareReplay(1)
      );
  }

  private partnerId(): Observable<number | string | null> {
    return this.routingService.routeParameterChanges(PartnerReportPageStore.PARTNER_REPORT_DETAIL_PATH, 'partnerId');
  }

  private institutionUserCanViewControlReports(): Observable<boolean> {
    return this.institutionUserControlReportLevel()
      .pipe(
        map((level) => level === 'View' || level === 'Edit')
      );
  }

  private institutionUserCanEditControlReports(): Observable<boolean> {
    return this.institutionUserControlReportLevel()
      .pipe(
        map((level) => level === 'Edit')
      );
  }

  private userCanEditReports(): Observable<boolean> {
    return combineLatest([
      this.partnerReportLevel(),
      this.permissionService.hasPermission(PermissionsEnum.ProjectReportingEdit)
    ])
      .pipe(
        map(([level, canEdit]) => level === 'EDIT' || canEdit)
      );
  }

  private userCanViewReports(): Observable<boolean> {
    return combineLatest([
      this.partnerReportLevel(),
      this.permissionService.hasPermission(PermissionsEnum.ProjectReportingEdit),
      this.permissionService.hasPermission(PermissionsEnum.ProjectReportingView)
    ])
      .pipe(
        map(([level, canEdit, canView]) => level === 'VIEW' || level === 'EDIT' || canEdit || canView)
      );
  }

  userCanViewGdpr(): Observable<boolean> {
    return combineLatest([
      this.privilegesPageStore.currentUserPartnerCollaborations$,
      this.partnerId$.pipe(map(id => Number(id))),
      this.userCanEditReport$,
    ]).pipe(
      take(1),
      map(([currentUserPartnerCollaborations, partnerId, userCanEditReport]) =>
        userCanEditReport && currentUserPartnerCollaborations.find(el => el.partnerId === partnerId)?.gdpr === true
      ),
    );
  }

  canCreateReport(): Observable<boolean> {
    return combineLatest([
      this.partnerId$,
      this.partnerReportLevel$,
    ]).pipe(
      switchMap(([partnerId, partnerCollaboratorLevel]) => {
        if (partnerCollaboratorLevel === 'EDIT') {
          return this.projectPartnerReportService.canReportBeCreated(partnerId as number);
        } else {
          return of(false);
        }
      })
    );
  }

}
