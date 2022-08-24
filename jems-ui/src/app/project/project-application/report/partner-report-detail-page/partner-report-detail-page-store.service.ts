import {Injectable} from '@angular/core';
import {
  ProjectPartnerReportDTO,
  ProjectPartnerReportIdentificationDTO,
  ProjectPartnerReportIdentificationService,
  ProjectPartnerReportPeriodDTO,
  ProjectPartnerReportService,
  ProjectPartnerReportSummaryDTO,
  ProjectPartnerSummaryDTO,
  UpdateProjectPartnerReportIdentificationDTO
} from '@cat/api';
import {BehaviorSubject, combineLatest, merge, Observable, of, Subject} from 'rxjs';
import {catchError, map, shareReplay, startWith, switchMap, tap} from 'rxjs/operators';
import {RoutingService} from '@common/services/routing.service';
import {Log} from '@common/utils/log';
import {ProjectPaths} from '@project/common/project-util';
import {ProjectStore} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {PartnerReportPageStore} from '@project/project-application/report/partner-report-page-store.service';

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

  newPageSize$ = new Subject<number>();
  newPageIndex$ = new Subject<number>();
  updatedReportStatus$ = new Subject<ProjectPartnerReportSummaryDTO.StatusEnum>();

  private updatedReport$ = new Subject<ProjectPartnerReportDTO>();
  private updatedIdentification$ = new Subject<ProjectPartnerReportIdentificationDTO>();

  constructor(private routingService: RoutingService,
              public partnerReportPageStore: PartnerReportPageStore,
              private projectPartnerReportService: ProjectPartnerReportService,
              private projectStore: ProjectStore,
              private reportIdentificationService: ProjectPartnerReportIdentificationService) {
    this.partnerId$ = this.partnerReportPageStore.partnerId$;
    this.partnerReportLevel$ = this.partnerReportPageStore.partnerReportLevel$;
    this.partnerReportId$ = this.partnerReportId();
    this.partnerSummary$ = this.partnerReportPageStore.partnerSummary$;
    this.partnerReport$ = this.partnerReport();
    this.partnerIdentification$ = this.reportIdentification();
    this.availablePeriods$ = this.availablePeriods();
    this.reportStatus$ = this.reportStatus();
    this.reportEditable$ = this.reportEditable();
  }

  private partnerReportId(): Observable<any> {
    return this.routingService.routeParameterChanges(PartnerReportDetailPageStore.REPORT_DETAIL_PATH, 'reportId');
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
              this.routingService.navigate([ProjectPaths.PROJECT_DETAIL_PATH, projectId, 'reporting']);
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

  submitReport(partnerId: number, reportId: number): Observable<ProjectPartnerReportSummaryDTO.StatusEnum> {
    return this.projectPartnerReportService.submitProjectPartnerReport(partnerId, reportId)
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
      this.partnerReportPageStore.userCanEditReports$,
      this.reportStatus$
    ])
      .pipe(
        map(([canEdit, status]) => canEdit && status === ProjectPartnerReportSummaryDTO.StatusEnum.Draft)
      );
  }
}
