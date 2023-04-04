import {Injectable} from '@angular/core';
import {BehaviorSubject, combineLatest, Observable, of, ReplaySubject, Subject} from 'rxjs';
import {
    PageJemsFileDTO,
    ProjectReportAnnexesService,
    JemsFileMetadataDTO,
    ProjectReportFileSearchRequestDTO,
    ProjectReportSummaryDTO,
    SettingsService
} from '@cat/api';
import {MatSort} from '@angular/material/sort';
import {FileListTableConstants} from '@common/components/file-list/file-list-table/file-list-table-constants';
import {APIError} from '@common/models/APIError';
import {DownloadService} from '@common/services/download.service';
import {RoutingService} from '@common/services/routing.service';
import {catchError, distinctUntilChanged, filter, finalize, map, startWith, switchMap, take, tap} from 'rxjs/operators';
import {v4 as uuid} from 'uuid';
import {
    ProjectStore
} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {CategoryInfo, CategoryNode} from '@project/common/components/category-tree/categoryModels';
import {Tables} from '@common/utils/tables';
import {
    ProjectReportCategoryTypeEnum
} from '@project/project-application/report/project-report/project-report-category-type';
import {FileManagementStore} from '@project/common/components/file-management/file-management-store';
import {
    ProjectReportPageStore
} from '@project/project-application/report/project-report/project-report-page-store.service';
import {
    ProjectReportDetailPageStore
} from '@project/project-application/report/project-report/project-report-detail-page/project-report-detail-page-store.service';
import {ActivatedRoute} from '@angular/router';
import {
    ProjectReportIdentificationExtensionStore
} from '@project/project-application/report/project-report/project-report-detail-page/project-report-identification-tab/project-report-identification-extension/project-report-identification-extension-store.service';

@Injectable({providedIn: 'root'})
export class ProjectReportAnnexesFileManagementStore {

    projectId$: Observable<number>;
    reportId$: Observable<number>;
    fileList$: Observable<PageJemsFileDTO>;
    fileCategories$: Observable<CategoryNode>;
    isEditable$: Observable<boolean>;
    isInDraft$: Observable<boolean>;
    currentProjectReport$: Observable<ProjectReportSummaryDTO>;

    selectedCategory$ = new ReplaySubject<CategoryInfo | undefined>(1);
    newPageSize$ = new BehaviorSubject<number>(Tables.DEFAULT_INITIAL_PAGE_SIZE);
    newPageIndex$ = new BehaviorSubject<number>(0);
    filesChanged$ = new Subject<void>();
    newSort$ = new BehaviorSubject<Partial<MatSort>>(FileListTableConstants.DEFAULT_SORT);
    error$ = new Subject<APIError | null>();

    constructor(
        private settingsService: SettingsService,
        private downloadService: DownloadService,
        private projectReportAnnexesService: ProjectReportAnnexesService,
        private fileManagementStore: FileManagementStore,
        private projectReportPageStore: ProjectReportPageStore,
        private routingService: RoutingService,
        private activateRoute: ActivatedRoute,
        private projectStore: ProjectStore
    ) {
        this.projectId$ = this.projectStore.projectId$;
        this.reportId$ = this.projectReportId();
        this.currentProjectReport$ = this.getCurrentProjectReport();
        this.fileList$ = this.fileList();
        this.isEditable$ = this.isEditable();
        this.isInDraft$ = this.isInDraft();
    }

    private projectReportId(): Observable<any> {
        return this.routingService.routeParameterChanges(ProjectReportIdentificationExtensionStore.REPORT_DETAIL_PATH, 'reportId');
    }

    getMaximumAllowedFileSize(): Observable<number> {
        return this.settingsService.getMaximumAllowedFileSize();
    }

    changeFilter(section: CategoryInfo): void {
        this.selectedCategory$.next(section);
        this.newPageIndex$.next(0);
    }

    private getCurrentProjectReport(): Observable<ProjectReportSummaryDTO> {
        return combineLatest([
            this.projectReportPageStore.projectReports$.pipe(map(reports => reports.content)),
            this.reportId$,
        ]).pipe(
            map(([reports, reportId]) => {
                const currentReport = reports.find(report => report.id == reportId);
                if (currentReport === undefined) {
                    throw new Error('Could not fetch the current project report!');
                }

                return currentReport;
            })
        );
    }

    private isInDraft(): Observable<boolean> {
        return this.currentProjectReport$.pipe(
            map((projectReport: ProjectReportSummaryDTO) => {
                return projectReport.status === ProjectReportSummaryDTO.StatusEnum.Draft;
            })
        );
    }

    private isEditable(): Observable<boolean> {
        return combineLatest([
            this.selectedCategory$,
            this.isInDraft()
        ]).pipe(
            map(([selectedCategory, isInDraft]) => {
                return selectedCategory?.type === ProjectReportCategoryTypeEnum.PROJECT_REPORT && isInDraft;
            })
        );
    }

    setSectionInit(section: CategoryInfo): void {
        this.selectedCategory$.next(section);
        this.fileCategories$ = this.fileCategories(section);
    }

    private fileCategories(section: CategoryInfo): Observable<CategoryNode> {
        return this.currentProjectReport$.pipe(
            map((projectReport: ProjectReportSummaryDTO) =>
                this.getCategories(section, projectReport.reportNumber)
            ),
            tap(filters => this.setParent(filters)),
        );
    }

    private setParent(node: CategoryNode): void {
        node?.children?.forEach(child => {
            child.parent = node;
            this.setParent(child);
        });
    }

    private getCategories(section: CategoryInfo,
                          reportNumber: number,
    ): CategoryNode {
        const reportFiles: CategoryNode = {
            name: {
                i18nKey: 'project.application.project.report.annexes.title.number',
                i18nArguments: {reportNumber: 'PR.' + reportNumber.toString()}
            },
            info: {type: ProjectReportCategoryTypeEnum.PROJECT_REPORT},
            children: []
        };

        reportFiles.children?.push(
            {
                name: {i18nKey: 'project.application.project.report.annexes.file.tree.work.plan.category'},
                info: {type: ProjectReportCategoryTypeEnum.WORKPLAN},
                children: []
            },
            {
                name: {i18nKey: 'project.application.project.report.annexes.file.tree.project.results.horizontal.principles.category'},
                info: {type: ProjectReportCategoryTypeEnum.PROJECT_RESULTS},
                children: []
            }
        );

        return this.fileManagementStore.findRootForSection(reportFiles, section) || {};
    }

    private fileList(): Observable<PageJemsFileDTO> {
        return combineLatest([
            this.selectedCategory$,
            this.projectId$.pipe(map(id => Number(id))),
            this.routingService.routeParameterChanges(ProjectReportDetailPageStore.REPORT_DETAIL_PATH, 'reportId')
                .pipe(map(id => Number(id))),
            this.newPageIndex$,
            this.newPageSize$,
            this.newSort$.pipe(
                map(sort => sort?.direction ? sort : FileListTableConstants.DEFAULT_SORT),
                map(sort => `${sort.active},${sort.direction}`),
                distinctUntilChanged(),
            ),
            this.filesChanged$.pipe(startWith(null)),
        ])
            .pipe(
                filter(([projectId, reportId]: any) => !!projectId && !!reportId),
                switchMap(([selectedCategory, projectId, reportId, pageIndex, pageSize, sort]) =>
                    this.projectReportAnnexesService.getProjectReportAnnexes(
                        projectId,
                        Number(reportId),
                        {
                            reportId: Number(reportId),
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

    uploadFile(file: File): Observable<JemsFileMetadataDTO> {
        const serviceId = uuid();
        this.routingService.confirmLeaveMap.set(serviceId, true);
        return combineLatest([
            this.projectId$.pipe(map(id => Number(id))),
            this.reportId$.pipe(map(id => Number(id))),
        ]).pipe(
            take(1),
            switchMap(([projectId, reportId]) =>
                this.projectReportAnnexesService.uploadProjectReportAnnexesFileForm(file, Number(projectId), reportId)
            ),
            tap(() => this.filesChanged$.next()),
            tap(() => this.error$.next(null)),
            catchError(error => {
                this.error$.next(error.error);
                return of({} as JemsFileMetadataDTO);
            }),
            finalize(() => this.routingService.confirmLeaveMap.delete(serviceId))
        );
    }

    downloadFile(fileId: number): Observable<any> {
        return combineLatest([
            this.projectId$.pipe(map(id => Number(id))),
            this.reportId$.pipe(map(id => Number(id))),
        ]).pipe(
            take(1),
            switchMap(([projectId, reportId]) => {
                this.downloadService.download(`/api/project/report/byProjectId/${projectId}/byReportId/${reportId}/byFileId/${fileId}/download`, 'project-report');
                return of(null);
            }),
        );
    }

    private getTreeNodeFromCategory(category: CategoryInfo): ProjectReportFileSearchRequestDTO.TreeNodeEnum {
        switch (category.type) {
            case ProjectReportCategoryTypeEnum.PROJECT_REPORT:
                return ProjectReportFileSearchRequestDTO.TreeNodeEnum.ProjectReport;
            case ProjectReportCategoryTypeEnum.WORKPLAN:
                return ProjectReportFileSearchRequestDTO.TreeNodeEnum.WorkPlanProjectReport;
            case ProjectReportCategoryTypeEnum.PROJECT_RESULTS:
                return ProjectReportFileSearchRequestDTO.TreeNodeEnum.ProjectResult;
            default:
                return ProjectReportFileSearchRequestDTO.TreeNodeEnum.ProjectReport;
        }
    }
}
