import {Injectable} from '@angular/core';
import {combineLatest, Observable, of, ReplaySubject, Subject} from 'rxjs';
import {
  PageProjectReportFileDTO,
  ProjectPartnerReportDTO,
  ProjectPartnerReportService,
  ProjectPartnerReportSummaryDTO,
  ProjectReportFileMetadataDTO,
  ProjectReportFileSearchRequestDTO,
  SettingsService
} from '@cat/api';
import {catchError, filter, map, startWith, switchMap, take, tap, withLatestFrom} from 'rxjs/operators';
import {MatSort} from '@angular/material/sort';
import {Tables} from '@common/utils/tables';
import {CategoryInfo, CategoryNode} from '@project/common/components/category-tree/categoryModels';
import {APIError} from '@common/models/APIError';
import {I18nMessage} from '@common/models/I18nMessage';
import {DownloadService} from '@common/services/download.service';
import {
  PartnerReportDetailPageStore
} from '@project/project-application/report/partner-report-detail-page/partner-report-detail-page-store.service';
import {
  ReportFileCategoryTypeEnum
} from '@project/project-application/report/partner-report-detail-page/partner-report-annexes-tab/report-file-category-type';
import {FileManagementStore} from "@project/common/components/file-management/file-management-store";

@Injectable({
  providedIn: 'root'
})
export class ReportFileManagementStore {

  reportFileList$: Observable<PageProjectReportFileDTO>;
  fileCategories$: Observable<CategoryNode>;
  selectedCategory$ = new ReplaySubject<CategoryInfo | undefined>(1);
  selectedCategoryPath$: Observable<I18nMessage[]>;

  reportStatus$: Observable<ProjectPartnerReportSummaryDTO.StatusEnum>;

  canUpload$: Observable<boolean>;
  canReadFiles$: Observable<boolean>;

  deleteSuccess$ = new Subject<boolean>();
  error$ = new Subject<APIError | null>();

  newPageSize$ = new Subject<number>();
  newPageIndex$ = new Subject<number>();
  newSort$ = new Subject<Partial<MatSort>>();
  reportFilesChanged$ = new Subject<void>();

  constructor(private settingsService: SettingsService,
              private downloadService: DownloadService,
              private partnerReportDetailPageStore: PartnerReportDetailPageStore,
              private projectPartnerReportService: ProjectPartnerReportService,
              private fileManagementStore: FileManagementStore
  ) {
    this.reportStatus$ = this.partnerReportDetailPageStore.reportStatus$;
    this.canUpload$ = this.canUpload();
    this.selectedCategoryPath$ = this.selectedCategoryPath();
    this.reportFileList$ = this.reportFileList();
  }

  setSection(section: CategoryInfo): void {
    this.selectedCategory$.next(section);
    this.fileCategories$ = this.fileCategories(section);
  }

  uploadFile(file: File): Observable<ProjectReportFileMetadataDTO> {
    return this.selectedCategory$
      .pipe(
        take(1),
        withLatestFrom(this.partnerReportDetailPageStore.partnerReportId$, this.partnerReportDetailPageStore.partnerId$),
        filter(([category, reportId, partnerId]) => !!partnerId && !!reportId),
        switchMap(([category, reportId, partnerId]) => this.projectPartnerReportService.uploadAttachmentForm(file, Number(partnerId), reportId)),
        tap(() => this.reportFilesChanged$.next()),
        tap(() => this.error$.next(null)),
        catchError(error => {
          this.error$.next(error.error);
          return of({} as ProjectReportFileMetadataDTO);
        })
      );
  }

  deleteFile(fileId: number): Observable<void> {
    return this.partnerReportDetailPageStore.partnerId$
      .pipe(
        take(1),
        filter(partnerId => !!partnerId),
        switchMap(partnerId => this.projectPartnerReportService.deleteAttachment(fileId, Number(partnerId))),
        tap(() => this.reportFilesChanged$.next()),
        tap(() => this.deleteSuccess$.next(true)),
        tap(() => setTimeout(() => this.deleteSuccess$.next(false), 3000)),
        catchError(error => {
          this.error$.next(error.error);
          return of({} as ProjectReportFileMetadataDTO);
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
        return selectedCategory?.type === ReportFileCategoryTypeEnum.REPORT && reportStatus === ProjectPartnerReportSummaryDTO.StatusEnum.Draft;
      })
    );
  }

  private reportFileList(): Observable<PageProjectReportFileDTO> {
    return combineLatest([
      this.selectedCategory$,
      this.partnerReportDetailPageStore.partnerId$,
      this.partnerReportDetailPageStore.partnerReportId$,
      this.newPageIndex$.pipe(startWith(Tables.DEFAULT_INITIAL_PAGE_INDEX)),
      this.newPageSize$.pipe(startWith(Tables.DEFAULT_INITIAL_PAGE_SIZE)),
      this.newSort$.pipe(
        startWith(Tables.DEFAULT_INITIAL_SORT),
        map(sort => sort?.direction ? sort : Tables.DEFAULT_INITIAL_SORT),
        map(sort => `${sort.active},${sort.direction}`)
      ),
      this.reportFilesChanged$.pipe(startWith(null))
    ])
      .pipe(
        filter(([selectedCategory, partnerId, partnerReportId, pageIndex, pageSize, sort]: any) => !!partnerId && !!partnerReportId),
        switchMap(([selectedCategory, partnerId, partnerReportId, pageIndex, pageSize, sort]) =>
          this.projectPartnerReportService.listAttachments(
            Number(partnerId),
            {
              reportId : Number(partnerReportId),
              treeNode: this.getTreeNodeFromCategory(selectedCategory),
              filterSubtypes: this.getSubFiltersFromCategory(selectedCategory),
            } as ProjectReportFileSearchRequestDTO,
            pageIndex,
            pageSize,
            sort
          )
        ),
        catchError(error => {
          this.error$.next(error.error);
          return of({} as PageProjectReportFileDTO);
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

  private selectedCategoryPath(): Observable<I18nMessage[]> {
    return combineLatest([this.selectedCategory$, this.fileCategories$])
      .pipe(
        map(([selectedCategory, fileCategories]) =>
          ([{i18nKey: 'file.tree.type.all'}, ...this.fileManagementStore.getPath(selectedCategory as any, fileCategories)])
        )
      );
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

  private getSubFiltersFromCategory(category: CategoryInfo): ProjectReportFileSearchRequestDTO.FilterSubtypesEnum[] {
    switch (category.type) {
      case ReportFileCategoryTypeEnum.ALL:
        return [];
      case ReportFileCategoryTypeEnum.REPORT:
        return [];
      case ReportFileCategoryTypeEnum.WORKPLAN:
        return [
          ProjectReportFileSearchRequestDTO.TreeNodeEnum.WorkPackage,
          ProjectReportFileSearchRequestDTO.TreeNodeEnum.Output,
          ProjectReportFileSearchRequestDTO.TreeNodeEnum.Activity,
          ProjectReportFileSearchRequestDTO.TreeNodeEnum.Deliverable];
      case ReportFileCategoryTypeEnum.EXPENDITURE:
        return [ProjectReportFileSearchRequestDTO.TreeNodeEnum.Expenditure];
      case ReportFileCategoryTypeEnum.PROCUREMENT:
        return [ProjectReportFileSearchRequestDTO.TreeNodeEnum.Procurement];
      case ReportFileCategoryTypeEnum.CONTRIBUTION:
        return [ProjectReportFileSearchRequestDTO.TreeNodeEnum.Contribution];
      default:
        return [];
    }
  }
}
