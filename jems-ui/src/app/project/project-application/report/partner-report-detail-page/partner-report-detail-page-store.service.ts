import {Injectable} from '@angular/core';
import {
  PreConditionCheckResultDTO,
  ProjectPartnerReportDTO,
  ProjectPartnerReportIdentificationDTO,
  ProjectPartnerReportIdentificationService,
  ProjectPartnerReportPeriodDTO,
  ProjectPartnerReportService,
  ProjectPartnerReportSummaryDTO,
  ProjectPartnerSummaryDTO,
  UpdateProjectPartnerReportIdentificationDTO, UserRoleCreateDTO
} from '@cat/api';
import {BehaviorSubject, combineLatest, merge, Observable, of, Subject} from 'rxjs';
import {catchError, map, shareReplay, startWith, switchMap, tap} from 'rxjs/operators';
import {RoutingService} from '@common/services/routing.service';
import {Log} from '@common/utils/log';
import {ProjectPaths} from '@project/common/project-util';
import {
  ProjectStore
} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {PartnerReportPageStore} from '@project/project-application/report/partner-report-page-store.service';
import {ReportUtil} from '@project/common/report-util';
import PermissionsEnum = UserRoleCreateDTO.PermissionsEnum;
import {PermissionService} from '../../../../security/permissions/permission.service';

@Injectable({providedIn: 'root'})
export class PartnerReportDetailPageStore {
  public static REPORT_DETAIL_PATH = '/reports/';

  partnerSummary$: Observable<ProjectPartnerSummaryDTO>;
  partnerReport$: Observable<ProjectPartnerReportDTO>;
  partnerId$: Observable<string | number | null>;
  partnerReportId$: Observable<number>;
  partnerReportLevel$: Observable<string>;
  partnerIdentification$: Observable<ProjectPartnerReportIdentificationDTO>;
  refreshIdentification$ = new BehaviorSubject<any>(null);
  availablePeriods$: Observable<ProjectPartnerReportPeriodDTO[]>;
  reportStatus$: Observable<ProjectPartnerReportSummaryDTO.StatusEnum>;
  reportEditable$: Observable<boolean>;
  canUserAccessCall$: Observable<boolean>;

  newPageSize$ = new Subject<number>();
  newPageIndex$ = new Subject<number>();
  updatedReportStatus$ = new Subject<ProjectPartnerReportSummaryDTO.StatusEnum>();

  private updatedReport$ = new Subject<ProjectPartnerReportDTO>();
  private updatedIdentification$ = new Subject<ProjectPartnerReportIdentificationDTO>();

  constructor(private routingService: RoutingService,
              public partnerReportPageStore: PartnerReportPageStore,
              private projectPartnerReportService: ProjectPartnerReportService,
              private projectStore: ProjectStore,
              private reportIdentificationService: ProjectPartnerReportIdentificationService,
              private permissionService: PermissionService) {
    this.partnerId$ = this.partnerReportPageStore.partnerId$;
    this.partnerReportLevel$ = this.partnerReportPageStore.partnerReportLevel$;
    this.partnerReportId$ = this.partnerReportId();
    this.partnerSummary$ = this.partnerReportPageStore.partnerSummary$;
    this.partnerReport$ = this.partnerReport();
    this.partnerIdentification$ = this.reportIdentification();
    this.availablePeriods$ = this.availablePeriods();
    this.reportStatus$ = this.reportStatus();
    this.reportEditable$ = this.reportEditable();
    this.canUserAccessCall$ = this.canUserAccessCall();
  }

  private canUserAccessCall(): Observable<boolean> {
    return this.permissionService.hasPermission([PermissionsEnum.CallRetrieve, PermissionsEnum.ProjectCreate]);
  }

  private partnerReportId(): Observable<number> {
    return this.routingService.routeParameterChanges(PartnerReportDetailPageStore.REPORT_DETAIL_PATH, 'reportId')
      .pipe(map(id => Number(id)));
  }

  private partnerReport(): Observable<ProjectPartnerReportDTO> {
    const initialReport$ = combineLatest([
      this.partnerId$,
      this.partnerReportId$,
      this.projectStore.projectId$,
      this.updatedReportStatus$.pipe(startWith(null))
    ]).pipe(
      switchMap(([partnerId, reportId, projectId]) => !!partnerId && !!projectId && !!reportId
        ? this.projectPartnerReportService.getProjectPartnerReport(Number(partnerId), Number(reportId))
          .pipe(
            catchError(() => {
              this.routingService.navigate([ProjectPaths.PROJECT_DETAIL_PATH, projectId]);
              return of({} as ProjectPartnerReportDTO);
            })
          )
        : of({} as ProjectPartnerReportDTO)
      ),
      tap(report => Log.info('Fetched the partner report:', this, report)),
    );

    return merge(initialReport$, this.updatedReport$)
      .pipe(
        shareReplay(1)
      );
  }

  runPreCheck(partnerId: number, reportId: number): Observable<PreConditionCheckResultDTO> {
    return this.projectPartnerReportService.runPreCheck(partnerId, reportId)
      .pipe(
        tap(status => Log.info('Called pre-submission check on report', reportId, status))
      );
  }

  submitReport(partnerId: number, reportId: number): Observable<ProjectPartnerReportSummaryDTO.StatusEnum> {
    return this.projectPartnerReportService.submitProjectPartnerReport(partnerId, reportId)
      .pipe(
        map(status => status as ProjectPartnerReportSummaryDTO.StatusEnum),
        tap(status => this.updatedReportStatus$.next(status)),
        tap(status => Log.info('Changed status for report', reportId, status))
      );
  }

  reopenReport(partnerId: number, reportId: number): Observable<ProjectPartnerReportSummaryDTO.StatusEnum> {
    return this.projectPartnerReportService.reOpenProjectPartnerReport(partnerId, reportId)
      .pipe(
        map(status => status as ProjectPartnerReportSummaryDTO.StatusEnum),
        tap(status => this.updatedReportStatus$.next(status)),
        tap(status => Log.info('Changed status for report', reportId, status))
      );
  }

  startControlOnPartnerReport(partnerId: number, reportId: number): Observable<ProjectPartnerReportSummaryDTO.StatusEnum> {
    return this.projectPartnerReportService.startControlOnPartnerReport(partnerId, reportId)
      .pipe(
        map(status => status as ProjectPartnerReportSummaryDTO.StatusEnum),
        tap(status => this.updatedReportStatus$.next(status)),
        tap(status => Log.info('Changed status for report', reportId, status))
      );
  }

  runPreCheckOnControlReport(partnerId: number, reportId: number): Observable<PreConditionCheckResultDTO> {
    return this.projectPartnerReportService.runPreCheckOnControlReport(partnerId, reportId)
      .pipe(
        tap(status => Log.info('Called pre-submission check on control report', reportId, status))
      );
  }

  finalizeReport(partnerId: number, reportId: number): Observable<ProjectPartnerReportSummaryDTO.StatusEnum> {
    return this.projectPartnerReportService.finalizeControlOnPartnerReport(partnerId, reportId)
      .pipe(
        map(status => status as ProjectPartnerReportSummaryDTO.StatusEnum),
        tap(status => this.updatedReportStatus$.next(status)),
        tap(status => Log.info('Changed status for report', reportId, status))
      );
  }

  reportIdentification(): Observable<ProjectPartnerReportIdentificationDTO> {
    const initialIdentification$ = combineLatest([
      this.partnerId$,
      this.routingService.routeParameterChanges(PartnerReportDetailPageStore.REPORT_DETAIL_PATH, 'reportId'),
      this.projectStore.projectId$,
      this.refreshIdentification$,
    ]).pipe(
      switchMap(([partnerId, reportId, projectId]) => !!partnerId && !!projectId && !!reportId
        ? this.reportIdentificationService.getIdentification(Number(partnerId), Number(reportId))
          .pipe(
            catchError(() => {
              this.routingService.navigate([ProjectPaths.PROJECT_DETAIL_PATH, projectId, 'reporting']);
              return of({} as ProjectPartnerReportIdentificationDTO);
            })
          )
        : of({} as ProjectPartnerReportIdentificationDTO)
      ),
      tap(report => Log.info('Fetched the partner report identification:', this, report)),
    );

    return merge(initialIdentification$, this.updatedIdentification$)
      .pipe(
        shareReplay(1)
      );
  }

  availablePeriods(): Observable<ProjectPartnerReportPeriodDTO[]> {
    return combineLatest([
      this.partnerId$,
      this.routingService.routeParameterChanges(PartnerReportDetailPageStore.REPORT_DETAIL_PATH, 'reportId'),
      this.projectStore.projectId$,
    ]).pipe(
      switchMap(([partnerId, reportId, projectId]) => !!partnerId && !!projectId && !!reportId
        ? this.reportIdentificationService.getAvailablePeriods(Number(partnerId), Number(reportId))
          .pipe(
            catchError(() => {
              this.routingService.navigate([ProjectPaths.PROJECT_DETAIL_PATH, projectId, 'reporting']);
              return of([] as ProjectPartnerReportPeriodDTO[]);
            })
          )
        : of([] as ProjectPartnerReportPeriodDTO[])
      ),
      tap(periods => Log.info('Fetched the partner report available periods:', this, periods)),
      shareReplay(1),
    );
  }

  public saveIdentification(identification: UpdateProjectPartnerReportIdentificationDTO): Observable<ProjectPartnerReportIdentificationDTO> {
    return combineLatest([
      this.partnerId$,
      this.routingService.routeParameterChanges(PartnerReportDetailPageStore.REPORT_DETAIL_PATH, 'reportId'),
    ]).pipe(
      switchMap(([partnerId, reportId]) =>
        this.reportIdentificationService.updateIdentification(Number(partnerId), Number(reportId), identification)),
      tap(data => Log.info('Updated identification for report', this, data)),
      tap(data => this.updatedIdentification$.next(data)),
    );
  }

  private reportStatus(): Observable<ProjectPartnerReportSummaryDTO.StatusEnum> {
    return merge(
      this.partnerReport$.pipe(map(report => report.status)),
      this.updatedReportStatus$,
    );
  }

  private reportEditable(): Observable<boolean> {
    return combineLatest([
      this.partnerReportPageStore.userCanEditReport$,
      this.reportStatus$
    ])
      .pipe(
        map(([canEdit, status]) => canEdit && ReportUtil.isPartnerReportSubmittable(status))
      );
  }
}
