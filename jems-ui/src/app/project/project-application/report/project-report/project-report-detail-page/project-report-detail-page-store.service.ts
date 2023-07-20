import {Injectable} from '@angular/core';
import {
  PreConditionCheckResultDTO,
  ProjectPartnerReportSummaryDTO,
  ProjectReportDTO,
  ProjectReportService,
  ProjectReportSummaryDTO,
  ProjectReportUpdateDTO
} from '@cat/api';
import {combineLatest, merge, Observable, of, ReplaySubject, Subject} from 'rxjs';
import {catchError, map, startWith, switchMap, tap} from 'rxjs/operators';
import {RoutingService} from '@common/services/routing.service';
import {Log} from '@common/utils/log';
import {ProjectPaths} from '@project/common/project-util';
import {ProjectStore} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {ProjectReportPageStore} from '@project/project-application/report/project-report/project-report-page-store.service';

@Injectable({providedIn: 'root'})
export class ProjectReportDetailPageStore {
  public static REPORT_DETAIL_PATH = '/projectReports/';

  projectReport$: Observable<ProjectReportDTO>;
  projectReportId$: Observable<number>;
  reportStatus$: Observable<ProjectReportSummaryDTO.StatusEnum>;
  reportEditable$: Observable<boolean>;
  reportVersion$ = new ReplaySubject<string | undefined>(1);

  newPageSize$ = new Subject<number>();
  newPageIndex$ = new Subject<number>();
  updatedReportStatus$ = new Subject<ProjectReportSummaryDTO.StatusEnum>();

  private updatedReport$ = new Subject<ProjectReportDTO>();

  constructor(private routingService: RoutingService,
              public projectReportPageStore: ProjectReportPageStore,
              private projectReportService: ProjectReportService,
              public projectStore: ProjectStore) {
    this.projectReportId$ = this.projectReportId();
    this.projectReport$ = this.projectReport();
    this.reportStatus$ = this.reportStatus();
    this.reportEditable$ = this.reportEditable();
  }

  private projectReportId(): Observable<any> {
    return this.routingService.routeParameterChanges(ProjectReportDetailPageStore.REPORT_DETAIL_PATH, 'reportId');
  }

  private projectReport(): Observable<ProjectReportDTO> {
    const initialReport$ = combineLatest([
      this.projectStore.projectId$,
      this.projectReportId$,
      this.updatedReportStatus$.pipe(startWith(null))
    ]).pipe(
      switchMap(([projectId, reportId]) => !!projectId && !!reportId
        ? this.projectReportService.getProjectReport(Number(projectId), Number(reportId))
          .pipe(
            catchError(() => {
              this.routingService.navigate([ProjectPaths.PROJECT_DETAIL_PATH, projectId, 'reports']);
              return of({} as ProjectReportDTO);
            })
          )
        : of({} as ProjectReportDTO)
      ),
      tap(report => this.reportVersion$.next(report.linkedFormVersion)),
      tap(report => Log.info('Fetched the project report:', this, report)),
    );

    return merge(initialReport$, this.updatedReport$);
  }
  public saveIdentification(identification: ProjectReportUpdateDTO): Observable<ProjectReportDTO> {
    return combineLatest([
      this.projectStore.projectId$,
      this.routingService.routeParameterChanges(ProjectReportDetailPageStore.REPORT_DETAIL_PATH, 'reportId'),
    ]).pipe(
      switchMap(([projectId, reportId]) =>
        this.projectReportService.updateProjectReport(Number(projectId), Number(reportId), identification)),
      tap(data => Log.info('Updated identification for project report', this, data)),
      tap(data => this.updatedReport$.next(data)),
    );
  }

  private reportStatus(): Observable<ProjectReportSummaryDTO.StatusEnum> {
    return merge(
      this.projectReport$.pipe(map(report => report.status)),
      this.updatedReportStatus$,
    );
  }

  private reportEditable(): Observable<boolean> {
    return combineLatest([
      this.projectReportPageStore.userCanEditReport$,
      this.reportStatus$
    ])
      .pipe(
        map(([canEdit, status]) => canEdit && status === ProjectReportSummaryDTO.StatusEnum.Draft)
      );
  }

  runPreCheck(partnerId: number, reportId: number): Observable<PreConditionCheckResultDTO> {
    return this.projectReportService.runPreCheck(partnerId, reportId)
      .pipe(
        tap(status => Log.info('Called pre-submission check on report', reportId, status))
      );
  }

  submitReport(projectId: number, reportId: number): Observable<ProjectReportSummaryDTO.StatusEnum> {
    return this.projectReportService.submitProjectReport(projectId, reportId)
      .pipe(
        map(status => status as ProjectReportSummaryDTO.StatusEnum),
        tap(status => this.updatedReportStatus$.next(status)),
        tap(status => Log.info('Changed status for report', reportId, status))
      );
  }

  startVerificationWork(projectId: number, reportId: number) {
    return this.projectReportService.startVerificationOnProjectReport(projectId, reportId)
      .pipe(
        map(status => status as ProjectReportSummaryDTO.StatusEnum),
        tap(status => this.updatedReportStatus$.next(status)),
        tap(status => Log.info('Changed status for report', reportId, status))
      );
  }

  finalizeReport(projectId: number, reportId: number): Observable<ProjectReportSummaryDTO.StatusEnum> {
    return this.projectReportService.finalizeVerificationOnProjectReport(projectId, reportId)
      .pipe(
        map(status => status as ProjectReportSummaryDTO.StatusEnum),
        tap(status => this.updatedReportStatus$.next(status)),
        tap(status => Log.info('Changed status for report', reportId, status))
      );
  }
}

