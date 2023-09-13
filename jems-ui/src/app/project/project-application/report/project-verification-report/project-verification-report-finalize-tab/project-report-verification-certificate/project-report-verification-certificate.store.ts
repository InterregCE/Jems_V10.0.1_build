import {Injectable} from '@angular/core';
import {PageJemsFileDTO, PluginInfoDTO, ProjectReportVerificationCertificateService} from '@cat/api';
import {ProjectStore} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {
  ProjectReportDetailPageStore
} from '@project/project-application/report/project-report/project-report-detail-page/project-report-detail-page-store.service';
import {PluginStore} from '@common/services/plugin-store.service';
import {BehaviorSubject, combineLatest, Observable, of, Subject} from 'rxjs';
import {catchError, distinctUntilChanged, filter, map, startWith, switchMap, take, tap} from 'rxjs/operators';
import {Log} from '@common/utils/log';
import {DownloadService} from '@common/services/download.service';
import {Tables} from '@common/utils/tables';
import {MatSort} from '@angular/material/sort';
import {FileListTableConstants} from '@common/components/file-list/file-list-table/file-list-table-constants';
import {APIError} from '@common/models/APIError';

@Injectable({providedIn: 'root'})
export class ProjectReportVerificationCertificateStore {

  projectId$: Observable<number>;
  reportId$: Observable<number>;
  plugins$: Observable<PluginInfoDTO[]>;
  certificates$: Observable<PageJemsFileDTO>;

  error$ = new Subject<APIError | null>();
  newPageSize$ = new BehaviorSubject<number>(Tables.DEFAULT_INITIAL_PAGE_SIZE);
  newPageIndex$ = new BehaviorSubject<number>(0);
  newSort$ = new BehaviorSubject<Partial<MatSort>>(FileListTableConstants.DEFAULT_SORT);
  certificatesChanged$ = new Subject();

  constructor(
    private projectReportVerificationCertificateService: ProjectReportVerificationCertificateService,
    private projectStore: ProjectStore,
    private projectReportDetailStore: ProjectReportDetailPageStore,
    private pluginStore: PluginStore,
    private downloadService: DownloadService,
  ) {
    this.projectId$ = projectStore.projectId$.pipe(filter(Boolean), map(Number));
    this.reportId$ = projectReportDetailStore.projectReportId$.pipe(filter(Boolean), map(Number));
    this.plugins$ = this.plugins();
    this.certificates$ = this.certificates();
  }


  private plugins(): Observable<PluginInfoDTO[]> {
    return this.pluginStore.fetchPluginList(PluginInfoDTO.TypeEnum.REPORTPROJECTVERIFICATIONCERTIFICATE);
  }

  private certificates(): Observable<PageJemsFileDTO> {
    return combineLatest([
      this.projectId$,
      this.reportId$,
      this.newPageIndex$,
      this.newPageSize$,
      this.newSort$.pipe(
        map(sort => sort?.direction ? sort : FileListTableConstants.DEFAULT_SORT),
        map(sort => `${sort.active},${sort.direction}`),
        distinctUntilChanged(),
      ),
      this.certificatesChanged$.pipe(startWith(null)),
    ]).pipe(
      switchMap(([projectId, reportId, pageIndex, pageSize, sort]) =>
        this.projectReportVerificationCertificateService.list(projectId, reportId, pageIndex, pageSize, sort)
      ),
      tap((page: PageJemsFileDTO) => {
        if (page.totalPages > 0 && page.number >= page.totalPages) {
          this.newPageIndex$.next(page.totalPages - 1);
        }
      }),
      catchError(error => {
        this.error$.next(error.error);
        return of({} as PageJemsFileDTO);
      })
    );
  }

  generateVerificationCertificate(pluginKey: string) {
    return combineLatest([
      this.projectId$,
      this.reportId$,
    ]).pipe(
      switchMap(([projectId, reportId]) =>
        this.projectReportVerificationCertificateService.generate(pluginKey, projectId, reportId)),
      tap(() => Log.info('Generate VerificationCertificate done')),
      tap(() => this.certificatesChanged$.next()),
    );
  }

  downloadFile(fileId: number): Observable<any> {
    return combineLatest([
      this.projectId$,
      this.reportId$,
    ]).pipe(
      take(1),
      switchMap(([projectId, reportId]) => {
        this.downloadService.download(
          `/api/project/report/byProjectId/${projectId}/byReportId/${reportId}/verification/certificate/byFileId/${fileId}/`,
          'project-report-verification-certificate'
        );
        return of(null);
      }),
    );
  }

  updateDescription(fileId: number, description: string): Observable<any> {
    return combineLatest([
      this.projectId$,
      this.reportId$
    ]).pipe(
      take(1),
      switchMap(([projectId, reportId]) =>
        this.projectReportVerificationCertificateService.updateDescription(fileId, projectId, reportId, description)
      ),
    );
  }

}
