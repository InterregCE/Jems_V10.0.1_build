import {Injectable} from '@angular/core';
import {PagePartnerReportCertificateDTO, ProjectReportCertificateService, ProjectReportService,} from '@cat/api';
import {combineLatest, Observable, Subject} from 'rxjs';
import {startWith, switchMap, tap} from 'rxjs/operators';
import {RoutingService} from '@common/services/routing.service';
import {Log} from '@common/utils/log';
import {ProjectStore} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {ProjectReportDetailPageStore} from '@project/project-application/report/project-report/project-report-detail-page/project-report-detail-page-store.service';
import {Tables} from '@common/utils/tables';

@Injectable({providedIn: 'root'})
export class ProjectReportCertificateTabStore {

  projectReportCertificates$: Observable<PagePartnerReportCertificateDTO>;

  newPageSize$ = new Subject<number>();
  newPageIndex$ = new Subject<number>();
  refresh$ = new Subject<void>();

  constructor(private routingService: RoutingService,
              private projectStore: ProjectStore,
              private projectReportDetailPageStore: ProjectReportDetailPageStore,
              private projectReportService: ProjectReportService,
              private projectReportCertificateService: ProjectReportCertificateService
  ) {
    this.projectReportCertificates$ = this.projectReportCertificates();
  }

  private projectReportCertificates(): Observable<PagePartnerReportCertificateDTO> {
    return combineLatest([
      this.projectStore.projectId$,
      this.projectReportDetailPageStore.projectReportId$,
      this.newPageIndex$.pipe(startWith(Tables.DEFAULT_INITIAL_PAGE_INDEX)),
      this.newPageSize$.pipe(startWith(Tables.DEFAULT_INITIAL_PAGE_SIZE)),
      this.refresh$.pipe(startWith(1))
    ])
      .pipe(
        switchMap(([projectId, reportId, page, size, refresh]) =>
          this.projectReportCertificateService.getProjectReportListOfCertificate(projectId, reportId, page, size)),
        tap((data: PagePartnerReportCertificateDTO) => Log.info('Fetched project report certificates for project:', this, data))
      );
  }

  public selectCertificate(projectId: number, projectReportId: number, partnerReportId: number): Observable<any> {
    return this.projectReportCertificateService.selectCertificate(partnerReportId, projectId, projectReportId)
      .pipe(
        tap(() => this.refresh$.next())
      );
  }

  public deselectCertificate(projectId: number, projectReportId: number, partnerReportId: number): Observable<any> {
    return this.projectReportCertificateService.deselectCertificate(partnerReportId, projectId, projectReportId)
      .pipe(
        tap(() => this.refresh$.next())
      );
  }
}

