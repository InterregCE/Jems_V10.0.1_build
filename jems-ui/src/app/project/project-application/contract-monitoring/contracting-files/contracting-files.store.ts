import {Injectable} from '@angular/core';
import {combineLatest, Observable, of, ReplaySubject, Subject} from 'rxjs';
import {
  ProjectContractingFileManagementService,
  ProjectContractingFileSearchRequestDTO,
  ProjectReportFileDTO,
  ProjectReportFileMetadataDTO,
  SettingsService, UserRoleDTO
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
import {FileManagementStore} from '@project/common/components/file-management/file-management-store';
import {
  ProjectPartnerStore
} from '@project/project-application/containers/project-application-form-page/services/project-partner-store.service';
import FileTypeEnum = ProjectReportFileDTO.TypeEnum;
import {
  ProjectStore
} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {PageFileList} from '@common/components/file-list/page-file-list';
import {FileListItem} from '@common/components/file-list/file-list-item';
import {PermissionService} from '../../../../security/permissions/permission.service';
import PermissionsEnum = UserRoleDTO.PermissionsEnum;

@Injectable({
  providedIn: 'root'
})
export class ContractingFilesStore {

  fileList$: Observable<PageFileList>;
  fileCategories$: Observable<CategoryNode>;
  selectedCategory$ = new ReplaySubject<CategoryInfo | undefined>(1);
  selectedCategoryPath$: Observable<I18nMessage[]>;

  canUpload$: Observable<boolean>;
  canDelete: boolean;

  deleteSuccess$ = new Subject<boolean>();
  error$ = new Subject<APIError | null>();

  newPageSize$ = new Subject<number>();
  newPageIndex$ = new Subject<number>();
  newSort$ = new Subject<Partial<MatSort>>();
  filesChanged$ = new Subject<void>();

  constructor(private settingsService: SettingsService,
              private downloadService: DownloadService,
              private partnerReportDetailPageStore: PartnerReportDetailPageStore,
              private partnerStore: ProjectPartnerStore,
              private projectStore: ProjectStore,
              private contractingFileService: ProjectContractingFileManagementService,
              private fileManagementStore: FileManagementStore,
              private permissionService: PermissionService
  ) {
    this.canUpload$ = this.canUpload();
    this.selectedCategoryPath$ = this.selectedCategoryPath();
    this.fileList$ = this.fileList();
  }

  setSection(section: CategoryInfo): void {
    this.selectedCategory$.next(section);
    this.fileCategories$ = this.fileCategories();
  }

  uploadFile(file: File): Observable<ProjectReportFileMetadataDTO> {
    return this.selectedCategory$
      .pipe(
        take(1),
        withLatestFrom(this.projectStore.projectId$),
        switchMap(([category, projectId]) => {
          switch (category?.type) {
            case FileTypeEnum.Contract:
              return this.contractingFileService.uploadContractFileForm(file, projectId);
            case FileTypeEnum.ContractDoc:
              return this.contractingFileService.uploadContractDocumentFileForm(file, projectId);
            case FileTypeEnum.ContractPartnerDoc:
              return this.contractingFileService.uploadContractFileForPartnerForm(file, Number(category?.id), projectId);
            case FileTypeEnum.ContractInternal:
            default:
              return this.contractingFileService.uploadContractInternalFileForm(file, projectId);
          }
        }),
        tap(() => this.filesChanged$.next()),
        tap(() => this.error$.next(null)),
        catchError(error => {
          this.error$.next(error.error);
          return of({} as ProjectReportFileMetadataDTO);
        })
      );
  }

  deleteFile(fileId: number): Observable<void> {
    return this.projectStore.projectId$
      .pipe(
        take(1),
        filter(projectId => !!projectId),
        switchMap(projectId => this.contractingFileService.deleteFile(fileId, Number(projectId))),
        tap(() => this.filesChanged$.next()),
        tap(() => this.deleteSuccess$.next(true)),
        tap(() => setTimeout(() => this.deleteSuccess$.next(false), 3000)),
        catchError(error => {
          this.error$.next(error.error);
          return of({} as ProjectReportFileMetadataDTO);
        })
      );
  }

  downloadFile(fileId: number): Observable<any> {
    return this.projectStore.projectId$
      .pipe(
        take(1),
        filter(projectId => !!projectId),
        switchMap(projectId => {
          this.downloadService.download(`/api/project/${projectId}/contracting/file/download/${fileId}`, 'contracting-file');
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

  private hasDeletionPrivilege(): Observable<boolean> {
    return this.permissionService.hasPermission(PermissionsEnum.ProjectSetToContracted);
  }

  private canUpload(): Observable<boolean> {
    return combineLatest([
      this.selectedCategory$,
      this.permissionService.hasPermission(PermissionsEnum.ProjectSetToContracted)
    ]).pipe(
      map(([selectedCategory, hasUserEditPermission]) => hasUserEditPermission && FileTypeEnum.ContractInternal === selectedCategory?.type),
    );
  }

  private fileList(): Observable<PageFileList> {
    return combineLatest([
      this.selectedCategory$,
      this.projectStore.projectId$,
      of(1),
      this.newPageIndex$.pipe(startWith(Tables.DEFAULT_INITIAL_PAGE_INDEX)),
      this.newPageSize$.pipe(startWith(Tables.DEFAULT_INITIAL_PAGE_SIZE)),
      this.newSort$.pipe(
        startWith(Tables.DEFAULT_INITIAL_SORT),
        map(sort => sort?.direction ? sort : Tables.DEFAULT_INITIAL_SORT),
        map(sort => `${sort.active},${sort.direction}`)
      ),
      this.hasDeletionPrivilege(),
      this.filesChanged$.pipe(startWith(null)),
    ])
      .pipe(
        filter(([selectedCategory, projectId, partnerId, pageIndex, pageSize, sort, hasDeletionPrivilege]: any) => !!partnerId),
        tap(data => this.canDelete = data[6]),
        switchMap(([selectedCategory, projectId, partnerId, pageIndex, pageSize, sort]) =>
          this.contractingFileService.listFiles(
            selectedCategory?.id || 0,
            Number(projectId),
            {
              treeNode: selectedCategory.type,
              filterSubtypes: [],
            } as ProjectContractingFileSearchRequestDTO,
            pageIndex,
            pageSize,
            sort
          )
        ),
        map(pageFiles => ({
          ...pageFiles,
          content: this.transform(pageFiles.content),
        } as PageFileList)),
        catchError(error => {
          this.error$.next(error.error);
          return of({} as PageFileList);
        })
      );
  }

  private transform(content: ProjectReportFileDTO[]): FileListItem[] {
    return content.map(file => ({
      ...file,
      deletable: this.canDelete,
      editable: false,
      tooltipIfNotDeletable: '',
      iconIfNotDeletable: 'delete',
    }));
  }

  private fileCategories(): Observable<CategoryNode> {
    return this.partnerStore.partnerReportSummaries$.pipe(
      map(partners => ({
        info: { type: FileTypeEnum.Contracting },
        name: { i18nKey: 'project.application.contracting.title' },
        parent: undefined,
        children: [
          {
            info: { type: FileTypeEnum.ContractSupport },
            name: { i18nKey: 'project.application.contract.and.supporting' },
            children: [
              {
                info: { type: FileTypeEnum.Contract },
                name: { i18nKey: 'project.application.contract.and.supporting.contracts' },
              },
              {
                info: { type: FileTypeEnum.ContractDoc },
                name: { i18nKey: 'project.application.contract.and.supporting.project' },
              },
            ],
          },
          {
            info: { type: FileTypeEnum.ContractPartner },
            name: { i18nKey: 'project.application.contract.partner' },
            children: partners.map(partner => ({
              info: { type: FileTypeEnum.ContractPartnerDoc, id: partner.id },
              name: {
                i18nKey: `common.label.project.partner.role.shortcut.${partner.role}`,
                i18nArguments: {partner: `${partner.sortNumber || ''} ${partner.abbreviation}`}
              },
            })),
          },
          {
            info: { type: FileTypeEnum.ContractInternal },
            name: { i18nKey: 'project.application.contract.internal' },
          },
        ],
      })),
      tap(filters => this.setParent(filters)),
    );
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

}
