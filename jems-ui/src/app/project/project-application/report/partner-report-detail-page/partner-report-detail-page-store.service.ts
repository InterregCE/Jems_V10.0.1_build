import {Injectable} from '@angular/core';
import {
  ProjectPartnerReportDTO, ProjectPartnerReportIdentificationDTO, ProjectPartnerReportIdentificationService,
  ProjectPartnerReportService, ProjectPartnerReportSummaryDTO,
  ProjectPartnerSummaryDTO, UpdateProjectPartnerReportIdentificationDTO, UpdateProjectPartnerReportWorkPackageDTO
} from '@cat/api';
import {combineLatest, merge, Observable, of, ReplaySubject, Subject} from 'rxjs';
import {catchError, shareReplay, startWith, switchMap, tap} from 'rxjs/operators';
import {RoutingService} from '@common/services/routing.service';
import {Log} from '@common/utils/log';
import {ProjectPaths} from '@project/common/project-util';
import {
  ProjectStore
} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {PartnerReportPageStore} from '@project/project-application/report/partner-report-page-store.service';

@Injectable({providedIn: 'root'})
export class PartnerReportDetailPageStore {
  public static REPORT_DETAIL_PATH = '/reports/';

  partnerSummary$: Observable<ProjectPartnerSummaryDTO>;
  partnerReport$: Observable<ProjectPartnerReportDTO>;
  partnerId$: Observable<string | number | null>;
  partnerIdentification$: Observable<ProjectPartnerReportIdentificationDTO>;

  newPageSize$ = new Subject<number>();
  newPageIndex$ = new Subject<number>();

  private updatedReport$ = new Subject<ProjectPartnerReportDTO>();
  private updatedIdentification$ = new Subject<ProjectPartnerReportIdentificationDTO>();
  private updatedReportStatus$ = new Subject<any>();
  private isReportEditable$ = new ReplaySubject<boolean>(1);

  constructor(private routingService: RoutingService,
              private partnerReportPageStore: PartnerReportPageStore,
              private projectPartnerReportService: ProjectPartnerReportService,
              private projectStore: ProjectStore,
              private reportIdentificationService: ProjectPartnerReportIdentificationService) {
    this.partnerId$ = this.partnerReportPageStore.partnerId$;
    this.partnerSummary$ = this.partnerReportPageStore.partnerSummary$;
    this.partnerReport$ = this.partnerReport();
    this.partnerIdentification$ = this.reportIdentification();
  }

  isReportEditable(): Observable<boolean> {
    return this.isReportEditable$.asObservable();
  }

  private partnerReport(): Observable<ProjectPartnerReportDTO> {
    const initialReport$ = combineLatest([
      this.partnerId$,
      this.routingService.routeParameterChanges(PartnerReportDetailPageStore.REPORT_DETAIL_PATH, 'reportId'),
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
      tap(report => this.isReportEditable$.next(report.status === ProjectPartnerReportSummaryDTO.StatusEnum.Draft)),
      tap(report => Log.info('Fetched the partner report:', this, report)),
    );

    return merge(initialReport$, this.updatedReport$)
      .pipe(
        shareReplay(1)
      );
  }

  submitReport(partnerId: number, reportId: number): Observable<ProjectPartnerReportSummaryDTO> {
    return this.projectPartnerReportService.submitProjectPartnerReport(partnerId, reportId)
      .pipe(
        tap(status => this.updatedReportStatus$.next()),
        tap(status => this.isReportEditable$.next(status.status === ProjectPartnerReportSummaryDTO.StatusEnum.Draft)),
        tap(status => Log.info('Changed status for report', reportId, status))
      );
  }

  reportIdentification(): Observable<ProjectPartnerReportIdentificationDTO> {
    const initialIdentification$ = combineLatest([
      this.partnerId$,
      this.routingService.routeParameterChanges(PartnerReportDetailPageStore.REPORT_DETAIL_PATH, 'reportId'),
      this.projectStore.projectId$,
      this.updatedReportStatus$.pipe(startWith(null))
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
}
