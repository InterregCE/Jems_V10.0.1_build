import {Injectable} from '@angular/core';
import {BehaviorSubject, combineLatest, Observable, of, ReplaySubject, Subject} from 'rxjs';
import {
  PagePartnerReportControlFileDTO,
  PageProjectReportFileDTO,
  PluginInfoDTO,
  ProjectPartnerControlReportFileAPIService,
} from '@cat/api';
import {PluginStore} from '@common/services/plugin-store.service';
import {DownloadService} from '@common/services/download.service';
import {catchError, distinctUntilChanged, filter, map, startWith, switchMap, take, tap} from 'rxjs/operators';
import {CategoryInfo, CategoryNode} from '@project/common/components/category-tree/categoryModels';
import {APIError} from '@common/models/APIError';
import {Tables} from '@common/utils/tables';
import {MatSort} from '@angular/material/sort';
import {FileListTableConstants} from '@common/components/file-list/file-list-table/file-list-table-constants';
import {PartnerControlReportStore} from '@project/project-application/report/partner-control-report/partner-control-report-store.service';
import TypeEnum = PluginInfoDTO.TypeEnum;

@Injectable({providedIn: 'root'})
export class PartnerControlReportGenerateControlReportAndCertificateExportStore {
  certificateExportPlugins$: Observable<PluginInfoDTO[]>;

  certificateFileList$: Observable<PagePartnerReportControlFileDTO>;
  fileCategories$: Observable<CategoryNode>;
  selectedCategory$ = new ReplaySubject<CategoryInfo | undefined>(1);

  error$ = new Subject<APIError | null>();

  newPageSize$ = new BehaviorSubject<number>(Tables.DEFAULT_INITIAL_PAGE_SIZE);
  newPageIndex$ = new BehaviorSubject<number>(0);
  newSort$ = new BehaviorSubject<Partial<MatSort>>(FileListTableConstants.DEFAULT_SORT);
  certificateFilesChanged$ = new Subject<void>();

  private exportTriggeredEvent$ = new BehaviorSubject<void>(undefined);

  constructor(
    private pluginStore: PluginStore,
    private controlReportExportService: ProjectPartnerControlReportFileAPIService,
    private partnerControlReportStore: PartnerControlReportStore,
    private downloadService: DownloadService) {
    this.certificateExportPlugins$ = this.pluginStore.getPluginListByType(TypeEnum.PARTNERCONTROLCERTIFICATEEXPORT);
    this.certificateFileList$ = this.certificateFileList();
  }

  downloadFile(fileId: number): Observable<any> {
    return combineLatest([
      this.partnerControlReportStore.partnerId$,
      this.partnerControlReportStore.reportId$,
    ])
      .pipe(
        take(1),
        filter(([partnerId, reportId]) => !!partnerId && !!reportId),
        switchMap(([partnerId, reportId]) => {
          this.downloadService.download(`/api/project/report/partner/control/file/byPartnerId/${partnerId}/byReportId/${reportId}/download/${fileId}`, 'control-report-certificate');
          return of(null);
        })
      );
  }

  private certificateFileList(): Observable<PagePartnerReportControlFileDTO> {
    return combineLatest([
      this.partnerControlReportStore.partnerId$,
      this.partnerControlReportStore.reportId$,
      this.newPageIndex$,
      this.newPageSize$,
      this.newSort$.pipe(
        map(sort => sort?.direction ? sort : FileListTableConstants.DEFAULT_SORT),
        map(sort => `generatedFile.${sort.active},${sort.direction}`),
        distinctUntilChanged(),
      ),
      this.certificateFilesChanged$.pipe(startWith(null)),
      this.exportTriggeredEvent$.pipe(startWith(null))
    ])
      .pipe(
        filter(([partnerId, reportId, pageIndex, pageSize, sort]: any) => !!partnerId && !!reportId),
        switchMap(([partnerId, reportId, pageIndex, pageSize, sort]) =>
          this.controlReportExportService.listFiles(
            partnerId,
            reportId,
            pageIndex,
            pageSize,
            sort
          )
        ),
        tap((page: PagePartnerReportControlFileDTO) => {
          if (page.totalPages > 0 && page.number >= page.totalPages) {
            this.newPageIndex$.next(page.totalPages - 1);
          }
        }),
        catchError(error => {
          this.error$.next(error.error);
          return of({} as PagePartnerReportControlFileDTO);
        })
      );
  }

  exportData(partnerId: number, reportId: number): Observable<any> {
    return this.controlReportExportService.generateControlReportCertificate(partnerId, reportId).pipe(
      tap(() => this.exportTriggeredEvent$.next())
    );
  }
}
