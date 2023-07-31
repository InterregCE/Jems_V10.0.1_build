import {Injectable} from '@angular/core';
import {BehaviorSubject, combineLatest, Observable, of, Subject} from 'rxjs';
import {JemsFileDTO, JemsFileMetadataDTO, ProjectPartnerReportDTO, ProjectPartnerReportService, SettingsService} from '@cat/api';
import {catchError, distinctUntilChanged, finalize, map, startWith, switchMap, take, tap} from 'rxjs/operators';
import {DownloadService} from '@common/services/download.service';
import {PartnerReportDetailPageStore} from '@project/project-application/report/partner-report-detail-page/partner-report-detail-page-store.service';
import {FileManagementStore} from '@project/common/components/file-management/file-management-store';
import {RoutingService} from '@common/services/routing.service';
import {Log} from '@common/utils/log';
import {MatSort} from '@angular/material/sort';
import {FileListTableConstants} from '@common/components/file-list/file-list-table/file-list-table-constants';
import {APIError} from '@common/models/APIError';
import {v4 as uuid} from 'uuid';
import {PartnerControlReportStore} from '@project/project-application/report/partner-control-report/partner-control-report-store.service';

@Injectable({ providedIn: 'root' })
export class PartnerControlReportFileManagementStore {

  partnerId$: Observable<number>;
  reportId$: Observable<number>;
  fileList$: Observable<JemsFileDTO[]>;
  report$: Observable<ProjectPartnerReportDTO>;

  filesChanged$ = new Subject<void>();
  newSort$ = new BehaviorSubject<Partial<MatSort>>(FileListTableConstants.DEFAULT_SORT);
  error$ = new Subject<APIError | null>();

  constructor(
    private settingsService: SettingsService,
    private downloadService: DownloadService,
    private partnerReportDetailPageStore: PartnerReportDetailPageStore,
    private projectPartnerReportService: ProjectPartnerReportService,
    private fileManagementStore: FileManagementStore,
    private routingService: RoutingService,
    public controlReportFileStore: PartnerControlReportStore
  ) {
    this.partnerId$ = controlReportFileStore.partnerId$;
    this.reportId$ = controlReportFileStore.reportId$;
    this.fileList$ = this.fileList();
    this.report$ = this.partnerReportDetailPageStore.partnerReport$;
  }

  getMaximumAllowedFileSize(): Observable<number> {
    return this.settingsService.getMaximumAllowedFileSize();
  }

  private fileList(): Observable<JemsFileDTO[]> {
    return combineLatest([
      this.partnerId$.pipe(map(id => Number(id))),
      this.routingService.routeParameterChanges(PartnerReportDetailPageStore.REPORT_DETAIL_PATH, 'reportId')
        .pipe(map(id => Number(id))),
      this.newSort$.pipe(
        map(sort => sort?.direction ? sort : FileListTableConstants.DEFAULT_SORT),
        map(sort => `${sort.active},${sort.direction}`),
        distinctUntilChanged(),
      ),
      this.filesChanged$.pipe(startWith(null)),
    ]).pipe(
      switchMap(([partnerId, reportId, sort]) =>
        this.projectPartnerReportService.listControlReportFiles(partnerId, reportId, 0, 40, sort)
      ),
      map(pageData => pageData.content),
      tap(data => Log.info('Fetched project procurement attachments by id', this, data))
    );
  }

  uploadFile(file: File): Observable<JemsFileMetadataDTO> {
    const serviceId = uuid();
    this.routingService.confirmLeaveMap.set(serviceId, true);
    return combineLatest([
      this.partnerId$.pipe(map(id => Number(id))),
      this.routingService.routeParameterChanges(PartnerReportDetailPageStore.REPORT_DETAIL_PATH, 'reportId')
        .pipe(map(id => Number(id))),
    ]).pipe(
      take(1),
      switchMap(([partnerId, reportId]) =>
        this.projectPartnerReportService.uploadControlReportFileForm(file, Number(partnerId), reportId)
      ),
      tap(() => this.filesChanged$.next()),
      tap(() => this.error$.next(null)),
      finalize(() => this.routingService.confirmLeaveMap.delete(serviceId)),
      catchError(error => {
        this.error$.next(error.error);
        return of({} as JemsFileMetadataDTO);
      }),
    );
  }

  downloadFile(fileId: number): Observable<any> {
    return combineLatest([
      this.partnerId$.pipe(map(id => Number(id))),
      this.routingService.routeParameterChanges(PartnerReportDetailPageStore.REPORT_DETAIL_PATH, 'reportId')
        .pipe(map(id => Number(id))),
    ]).pipe(
      take(1),
      switchMap(([partnerId, reportId]) => {
        this.downloadService.download(`/api/project/report/partner/control/byPartnerId/${partnerId}/byReportId/${reportId}/byFileId/${fileId}`, 'partner-control-report');
        return of(null);
      }),
    );
  }

}
