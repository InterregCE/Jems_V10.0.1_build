import {Injectable} from '@angular/core';
import {combineLatest, merge, Observable, of, Subject} from 'rxjs';
import {
  ProjectReportIdentificationDTO,
  ProjectReportIdentificationService,
  ProjectReportResultIndicatorOverviewDTO,
  ProjectReportSummaryDTO,
  UpdateProjectReportIdentificationDTO
} from '@cat/api';
import {RoutingService} from '@common/services/routing.service';
import {ProjectStore} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {filter, startWith, switchMap, tap} from 'rxjs/operators';
import {Log} from '@common/utils/log';

@Injectable({providedIn: 'root'})
export class ProjectReportIdentificationExtensionStore {
  public static REPORT_DETAIL_PATH = '/projectReports/';

  projectReportId$: Observable<number>;
  updatedReportStatus$ = new Subject<ProjectReportSummaryDTO.StatusEnum>();
  projectReportIdentification$: Observable<ProjectReportIdentificationDTO>;
  resultIndicatorOverview$: Observable<ProjectReportResultIndicatorOverviewDTO[]>;

  private updatedReportIdentification$ = new Subject<ProjectReportIdentificationDTO>();

  constructor(private routingService: RoutingService,
              private projectReportIdentificationService: ProjectReportIdentificationService,
              private projectStore: ProjectStore) {
    this.projectReportId$ = this.projectReportId();
    this.projectReportIdentification$ = this.projectReportIdentification();
    this.resultIndicatorOverview$ = this.resultIndicatorOverview();
  }

  private projectReportId(): Observable<any> {
    return this.routingService.routeParameterChanges(ProjectReportIdentificationExtensionStore.REPORT_DETAIL_PATH, 'reportId');
  }

  private projectReportIdentification(): Observable<ProjectReportIdentificationDTO> {
    const initialIdentification$ = combineLatest([
      this.projectStore.projectId$,
      this.projectReportId$,
      this.updatedReportStatus$.pipe(startWith(null))
    ]).pipe(
      switchMap(([projectId, reportId]) => !!projectId && !!reportId
        ? this.projectReportIdentificationService.getProjectReportIdentification(Number(projectId), Number(reportId))
        : of({} as ProjectReportIdentificationDTO)
      ),
      tap(report => Log.info('Fetched project report identification:', this, report)),
    );

    return merge(initialIdentification$, this.updatedReportIdentification$);
  }

  public saveIdentificationExtension(identification: UpdateProjectReportIdentificationDTO): Observable<ProjectReportIdentificationDTO> {
    return combineLatest([
      this.projectStore.projectId$,
      this.routingService.routeParameterChanges(ProjectReportIdentificationExtensionStore.REPORT_DETAIL_PATH, 'reportId'),
    ]).pipe(
      switchMap(([partnerId, reportId]) =>
        this.projectReportIdentificationService.updateProjectReportIdentification(Number(partnerId), Number(reportId), identification)),
      tap(data => Log.info('Updated identification for project report', this, data)),
      tap(data => this.updatedReportIdentification$.next(data)),
    );
  }

  public resultIndicatorOverview(): Observable<ProjectReportResultIndicatorOverviewDTO[]> {
    return combineLatest([
      this.projectStore.projectId$,
      this.projectReportId$,
    ]).pipe(
      filter(([projectId, reportId]) => !!projectId && !!reportId),
      switchMap(([projectId, reportId]) => this.projectReportIdentificationService.getResultIndicatorOverview(Number(projectId), Number(reportId))),
      tap(data => Log.info('Loaded ResultIndicatorOverview', this, data)),
    );
  }

}
