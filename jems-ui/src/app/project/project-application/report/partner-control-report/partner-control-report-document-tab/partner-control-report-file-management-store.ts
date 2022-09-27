import {Injectable} from '@angular/core';
import {BehaviorSubject, combineLatest, Observable, of, Subject} from 'rxjs';
import {
  ProjectPartnerReportDTO,
  ProjectPartnerReportService,
  ProjectReportFileDTO,
  ProjectReportFileMetadataDTO,
  SettingsService
} from '@cat/api';
import {catchError, distinctUntilChanged, filter, map, startWith, switchMap, take, tap} from 'rxjs/operators';
import {DownloadService} from '@common/services/download.service';
import {
  PartnerReportDetailPageStore
} from '@project/project-application/report/partner-report-detail-page/partner-report-detail-page-store.service';
import {FileManagementStore} from '@project/common/components/file-management/file-management-store';
import {RoutingService} from '@common/services/routing.service';
import {Log} from '@common/utils/log';
import {PartnerReportPageStore} from '@project/project-application/report/partner-report-page-store.service';
import {FileListItem} from '@common/components/file-list/file-list-item';
import {MatSort} from '@angular/material/sort';
import {FileListTableConstants} from "@common/components/file-list/file-list-table/file-list-table-constants";
import {APIError} from '@common/models/APIError';
import {v4 as uuid} from 'uuid';

@Injectable({ providedIn: 'root' })
export class PartnerControlReportFileManagementStore {

  partnerId$: Observable<number>;
  reportId$: Observable<number>;
  fileList$: Observable<ProjectReportFileDTO[]>;
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
    private routingService: RoutingService
  ) {
    this.partnerId$ = this.partnerId();
    this.reportId$ = this.reportId();
    this.fileList$ = this.fileList();
    this.report$ = this.partnerReportDetailPageStore.partnerReport$;
  }

  getMaximumAllowedFileSize(): Observable<number> {
    return this.settingsService.getMaximumAllowedFileSize();
  }

  private fileList(): Observable<ProjectReportFileDTO[]> {
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

  private partnerId(): Observable<number> {
    return this.routingService.routeParameterChanges(PartnerReportPageStore.PARTNER_REPORT_DETAIL_PATH, 'partnerId')
      .pipe(map(id => Number(id)));
  }

  private reportId(): Observable<number> {
    return this.routingService.routeParameterChanges(PartnerReportDetailPageStore.REPORT_DETAIL_PATH, 'reportId')
      .pipe(map(id => Number(id)));
  }

  deleteFile(file: FileListItem): Observable<void> {
    return combineLatest([this.partnerId$, this.reportId$])
      .pipe(
        switchMap(([partnerId, reportId]) =>
          this.projectPartnerReportService.deleteControlReportFile(file.id, partnerId, reportId)),
        tap(() => this.filesChanged$.next()),
      );
  }

  uploadFile(file: File): Observable<ProjectReportFileMetadataDTO> {
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
      tap(() => this.routingService.confirmLeaveMap.delete(serviceId)),
      catchError(error => {
        this.error$.next(error.error);
        return of({} as ProjectReportFileMetadataDTO);
      }),
    );
  }

  downloadFile(fileId: number): Observable<any> {
    return this.partnerId$
      .pipe(
        take(1),
        filter(partnerId => !!partnerId),
        switchMap(partnerId => {
          this.downloadService.download(`/api/project/report/partner/control/byPartnerId/${partnerId}/${fileId}`, 'partner-control-report');
          return of(null);
        })
      );
  }

}
