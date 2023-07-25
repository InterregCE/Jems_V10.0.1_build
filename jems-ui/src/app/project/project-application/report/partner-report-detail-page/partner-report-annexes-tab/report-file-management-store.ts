import {Injectable} from '@angular/core';
import {BehaviorSubject, combineLatest, Observable, of, ReplaySubject, Subject} from 'rxjs';
import {
  JemsFileMetadataDTO,
  PageJemsFileDTO,
  ProjectPartnerReportDTO,
  ProjectPartnerReportService,
  ProjectPartnerReportSummaryDTO,
  ProjectReportFileSearchRequestDTO,
  SettingsService
} from '@cat/api';
import {catchError, distinctUntilChanged, filter, finalize, map, startWith, switchMap, take, tap, withLatestFrom} from 'rxjs/operators';
import {MatSort} from '@angular/material/sort';
import {Tables} from '@common/utils/tables';
import {CategoryInfo, CategoryNode} from '@project/common/components/category-tree/categoryModels';
import {APIError} from '@common/models/APIError';
import {DownloadService} from '@common/services/download.service';
import {PartnerReportDetailPageStore} from '@project/project-application/report/partner-report-detail-page/partner-report-detail-page-store.service';
import {ReportFileCategoryTypeEnum} from '@project/project-application/report/partner-report-detail-page/partner-report-annexes-tab/report-file-category-type';
import {FileManagementStore} from '@project/common/components/file-management/file-management-store';
import {RoutingService} from '@common/services/routing.service';
import {v4 as uuid} from 'uuid';
import {FileListTableConstants} from '@common/components/file-list/file-list-table/file-list-table-constants';
import {ReportUtil} from '@project/common/report-util';

@Injectable({
  providedIn: 'root'
})
export class ReportFileManagementStore {

  reportFileList$: Observable<PageJemsFileDTO>;
  fileCategories$: Observable<CategoryNode>;
  selectedCategory$ = new ReplaySubject<CategoryInfo | undefined>(1);

  reportStatus$: Observable<ProjectPartnerReportSummaryDTO.StatusEnum>;

  canUpload$: Observable<boolean>;
  canReadFiles$: Observable<boolean>;

  deleteSuccess$ = new Subject<boolean>();
  error$ = new Subject<APIError | null>();

  newPageSize$ = new BehaviorSubject<number>(Tables.DEFAULT_INITIAL_PAGE_SIZE);
  newPageIndex$ = new BehaviorSubject<number>(0);
  newSort$ = new BehaviorSubject<Partial<MatSort>>(FileListTableConstants.DEFAULT_SORT);
  reportFilesChanged$ = new Subject<void>();

  constructor(
    private settingsService: SettingsService,
    private downloadService: DownloadService,
    private partnerReportDetailPageStore: PartnerReportDetailPageStore,
    private projectPartnerReportService: ProjectPartnerReportService,
    private fileManagementStore: FileManagementStore,
    private routingService: RoutingService
  ) {
    this.reportStatus$ = this.partnerReportDetailPageStore.reportStatus$;
    this.canUpload$ = this.canUpload();
    this.reportFileList$ = this.reportFileList();
  }

  setSectionInit(section: CategoryInfo): void {
    this.selectedCategory$.next(section);
    this.fileCategories$ = this.fileCategories(section);
  }

  changeFilter(section: CategoryInfo): void {
    this.selectedCategory$.next(section);
    this.newPageIndex$.next(0);
  }

  uploadFile(file: File): Observable<JemsFileMetadataDTO> {
    const serviceId = uuid();
    this.routingService.confirmLeaveMap.set(serviceId, true);
    return this.selectedCategory$
      .pipe(
        take(1),
        withLatestFrom(this.partnerReportDetailPageStore.partnerReportId$, this.partnerReportDetailPageStore.partnerId$),
        filter(([category, reportId, partnerId]) => !!partnerId && !!reportId),
        switchMap(([category, reportId, partnerId]) => this.projectPartnerReportService.uploadReportFileForm(file, Number(partnerId), reportId)),
        tap(() => this.reportFilesChanged$.next()),
        tap(() => this.error$.next(null)),
        finalize(() => this.routingService.confirmLeaveMap.delete(serviceId)),
        catchError(error => {
          this.error$.next(error.error);
          return of({} as JemsFileMetadataDTO);
        }),
      );
  }

  deleteFile(fileId: number): Observable<void> {
    return combineLatest([
      this.partnerReportDetailPageStore.partnerId$,
      this.partnerReportDetailPageStore.partnerReportId$,
    ])
      .pipe(
        take(1),
        filter(([partnerId, reportId]) => !!partnerId),
        switchMap(([partnerId, reportId]) =>
          this.projectPartnerReportService.deleteReportFile(fileId, Number(partnerId), reportId)
        ),
        tap(() => this.reportFilesChanged$.next()),
        tap(() => this.deleteSuccess$.next(true)),
        tap(() => setTimeout(() => this.deleteSuccess$.next(false), 3000)),
        catchError(error => {
          this.error$.next(error.error);
          return of({} as JemsFileMetadataDTO);
        })
      );
  }

  downloadFile(fileId: number): Observable<any> {
    return this.partnerReportDetailPageStore.partnerId$
      .pipe(
        take(1),
        filter(partnerId => !!partnerId),
        switchMap(partnerId => {
          this.downloadService.download(`/api/project/report/partner/byPartnerId/${partnerId}/${fileId}`, 'partner-report');
          return of(null);
        })
      );
  }

  private setParent(node: CategoryNode): void {
    node?.children?.forEach(child => {
      child.parent = node;
      this.setParent(child);
    });
  }

  private canUpload(): Observable<boolean> {
    return combineLatest([
      this.selectedCategory$,
      this.reportStatus$,
    ]).pipe(
      map(([selectedCategory, reportStatus]) => {
        return selectedCategory?.type === ReportFileCategoryTypeEnum.REPORT && ReportUtil.isPartnerReportSubmittable(reportStatus);
      })
    );
  }

  private reportFileList(): Observable<PageJemsFileDTO> {
    return combineLatest([
      this.selectedCategory$,
      this.partnerReportDetailPageStore.partnerId$,
      this.partnerReportDetailPageStore.partnerReportId$,
      this.newPageIndex$,
      this.newPageSize$,
      this.newSort$.pipe(
        map(sort => sort?.direction ? sort : FileListTableConstants.DEFAULT_SORT),
        map(sort => `${sort.active},${sort.direction}`),
        distinctUntilChanged(),
      ),
      this.reportFilesChanged$.pipe(startWith(null))
    ])
      .pipe(
        filter(([selectedCategory, partnerId, partnerReportId, pageIndex, pageSize, sort]: any) => !!partnerId && !!partnerReportId),
        switchMap(([selectedCategory, partnerId, partnerReportId, pageIndex, pageSize, sort]) =>
          this.projectPartnerReportService.listReportFiles(
            Number(partnerId),
            {
              reportId : Number(partnerReportId),
              treeNode: this.getTreeNodeFromCategory(selectedCategory),
              filterSubtypes: [ /* can be used in future for filtering */],
            } as ProjectReportFileSearchRequestDTO,
            pageIndex,
            pageSize,
            sort
          )
        ),
        tap(page => {
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

  private fileCategories(section: CategoryInfo): Observable<CategoryNode> {
    return this.partnerReportDetailPageStore.partnerReport$.pipe(
      map((report: ProjectPartnerReportDTO) =>
        this.getCategories(section, report.reportNumber)
      ),
      tap(filters => this.setParent(filters)),
    );
  }

  private getCategories(section: CategoryInfo,
                        reportNumber: number,
                        ): CategoryNode {
    const reportFiles: CategoryNode = {
      name: {i18nKey: 'project.application.partner.report.file.tree.type.report.files', i18nArguments: {reportNumber: reportNumber.toString()}},
      info: {type: ReportFileCategoryTypeEnum.REPORT},
      children: []
    };

    reportFiles.children?.push(
      {
        name: {i18nKey: 'project.application.partner.report.file.tree.type.report.workplan'},
        info: {type: ReportFileCategoryTypeEnum.WORKPLAN},
        children: []
      },
      {
        name: {i18nKey: 'project.application.partner.report.file.tree.type.report.expenditure'},
        info: {type: ReportFileCategoryTypeEnum.EXPENDITURE},
        children: []
      },
      {
        name: {i18nKey: 'project.application.partner.report.file.tree.type.report.procurement'},
        info: {type: ReportFileCategoryTypeEnum.PROCUREMENT},
        children: []
      },
      {
        name: {i18nKey: 'project.application.partner.report.file.tree.type.report.contribution'},
        info: {type: ReportFileCategoryTypeEnum.CONTRIBUTION},
        children: []
      }
    );

    return this.fileManagementStore.findRootForSection(reportFiles, section) || {};
  }

  getMaximumAllowedFileSize(): Observable<number> {
    return this.settingsService.getMaximumAllowedFileSize();
  }

  private getTreeNodeFromCategory(category: CategoryInfo): ProjectReportFileSearchRequestDTO.TreeNodeEnum {
    switch (category.type) {
      case ReportFileCategoryTypeEnum.ALL:
        return ProjectReportFileSearchRequestDTO.TreeNodeEnum.PartnerReport;
      case ReportFileCategoryTypeEnum.REPORT:
        return ProjectReportFileSearchRequestDTO.TreeNodeEnum.PartnerReport;
      case ReportFileCategoryTypeEnum.OUTPUT:
        return ProjectReportFileSearchRequestDTO.TreeNodeEnum.Output;
      case ReportFileCategoryTypeEnum.ACTIVITY:
        return ProjectReportFileSearchRequestDTO.TreeNodeEnum.Activity;
      case ReportFileCategoryTypeEnum.DELIVERABLE:
        return ProjectReportFileSearchRequestDTO.TreeNodeEnum.Deliverable;
      case ReportFileCategoryTypeEnum.WORKPLAN:
        return ProjectReportFileSearchRequestDTO.TreeNodeEnum.WorkPlan;
      case ReportFileCategoryTypeEnum.EXPENDITURE:
        return ProjectReportFileSearchRequestDTO.TreeNodeEnum.Expenditure;
      case ReportFileCategoryTypeEnum.PROCUREMENT:
        return ProjectReportFileSearchRequestDTO.TreeNodeEnum.Procurement;
      case ReportFileCategoryTypeEnum.CONTRIBUTION:
        return ProjectReportFileSearchRequestDTO.TreeNodeEnum.Contribution;
      default:
        return ProjectReportFileSearchRequestDTO.TreeNodeEnum.PartnerReport;
    }
  }

}
