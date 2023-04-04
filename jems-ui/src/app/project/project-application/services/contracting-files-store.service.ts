import {Injectable} from '@angular/core';
import {BehaviorSubject, combineLatest, Observable, of, ReplaySubject, Subject} from 'rxjs';
import {
  PageJemsFileDTO,
  ProjectContractingFileManagementService,
  ProjectContractingFileSearchRequestDTO,
  JemsFileDTO,
  JemsFileMetadataDTO,
  SettingsService,
  UserRoleDTO
} from '@cat/api';
import {catchError, map, startWith, switchMap, take, tap, withLatestFrom} from 'rxjs/operators';
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
import {
  ProjectStore
} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {PermissionService} from '../../../security/permissions/permission.service';
import {ProjectUtil} from '@project/common/project-util';
import {RoutingService} from '@common/services/routing.service';
import FileTypeEnum = JemsFileDTO.TypeEnum;
import PermissionsEnum = UserRoleDTO.PermissionsEnum;
import {FileListItem} from '@common/components/file-list/file-list-item';

@Injectable({
  providedIn: 'root'
})
export class ContractingFilesStoreService {

  fileList$: Observable<PageJemsFileDTO>;
  fileCategories$: Observable<CategoryNode>;
  selectedCategory$ = new ReplaySubject<CategoryInfo | undefined>(1);
  selectedCategoryPath$: Observable<I18nMessage[]>;

  canUpload$: Observable<boolean>;
  canDelete: boolean;

  deleteSuccess$ = new Subject<boolean>();
  error$ = new Subject<APIError | null>();

  newPageSize$ = new BehaviorSubject<number>(Tables.DEFAULT_INITIAL_PAGE_SIZE);
  newPageIndex$ = new BehaviorSubject<number>(0);
  newSort$ = new Subject<Partial<MatSort>>();
  filesChanged$ = new Subject<void>();

  constructor(private settingsService: SettingsService,
              private downloadService: DownloadService,
              private partnerReportDetailPageStore: PartnerReportDetailPageStore,
              private partnerStore: ProjectPartnerStore,
              private projectStore: ProjectStore,
              private contractingFileService: ProjectContractingFileManagementService,
              private fileManagementStore: FileManagementStore,
              private permissionService: PermissionService,
              private routingService: RoutingService
  ) {
    this.canUpload$ = this.canUpload();
    this.selectedCategoryPath$ = this.selectedCategoryPath();
    this.fileList$ = this.fileList();
  }

  setSection(section: CategoryInfo): void {
    this.selectedCategory$.next(section);
  }

  setFileCategories(fileCategories: Observable<CategoryNode>): void {
    this.fileCategories$ = fileCategories;
  }

  uploadFile(file: File): Observable<JemsFileMetadataDTO> {
    return this.selectedCategory$
      .pipe(
        take(1),
        withLatestFrom(this.projectStore.projectId$, this.routingService.routeParameterChanges('/', 'partnerId')),
        switchMap(([category, projectId, partnerIdInRoute]) => {
          switch (category?.type) {
            case FileTypeEnum.Contract:
              return this.contractingFileService.uploadContractFileForm(file, projectId);
            case FileTypeEnum.ContractDoc:
              return this.contractingFileService.uploadContractDocumentFileForm(file, projectId);
            case FileTypeEnum.ContractPartnerDoc:
            case FileTypeEnum.ContractPartner:
              return this.contractingFileService.uploadContractFileForPartnerForm(file, Number(partnerIdInRoute), projectId);
            case FileTypeEnum.ContractInternal:
            default:
              return this.contractingFileService.uploadContractInternalFileForm(file, projectId);
          }
        }),
        tap(() => this.filesChanged$.next()),
        tap(() => this.error$.next(null)),
        catchError(error => {
          this.error$.next(error.error);
          return of({} as JemsFileMetadataDTO);
        })
      );
  }

  setFileDescription(fileId: number, fileDescription: string): Observable<any> {
    return this.selectedCategory$.pipe(
      take(1),
      withLatestFrom(this.projectStore.projectId$, this.routingService.routeParameterChanges('/', 'partnerId')),
      switchMap(([category, projectId, partnerId]) => {
        switch (category?.type) {
          case FileTypeEnum.Contract:
          case FileTypeEnum.ContractDoc:
          case FileTypeEnum.ContractSupport:
            return this.contractingFileService.updateContractFileDescription(fileId, projectId, fileDescription);
          case FileTypeEnum.ContractPartnerDoc:
          case FileTypeEnum.ContractPartner:
            return this.contractingFileService.updatePartnerFileDescription(fileId, Number(partnerId), projectId, fileDescription);
          case FileTypeEnum.ContractInternal:
          default:
            return this.contractingFileService.updateInternalFileDescription(fileId, projectId, fileDescription);
        }
      })
    );
  }

  deleteFile(fileId: number): Observable<void> {
    return this.selectedCategory$.pipe(
      withLatestFrom(this.projectStore.projectId$, this.routingService.routeParameterChanges('/', 'partnerId')),
      switchMap(([category, projectId, partnerIdInRoute]) => {
        switch (category?.type) {
          case FileTypeEnum.Contract:
          case FileTypeEnum.ContractDoc:
          case FileTypeEnum.ContractSupport:
            return this.contractingFileService.deleteContractFile(fileId, projectId);
          case FileTypeEnum.ContractPartner:
          case FileTypeEnum.ContractPartnerDoc:
            return this.contractingFileService.deletePartnerFile(fileId, Number(partnerIdInRoute), projectId);
          case FileTypeEnum.ContractInternal:
          default:
            return this.contractingFileService.deleteInternalFile(fileId, projectId);
        }
      }),
      tap(() => this.deleteSuccess$.next(true)),
      tap(() => setTimeout(() => this.deleteSuccess$.next(false), 3000)),
      catchError(error => {
        this.error$.next(error.error);
        return of({} as JemsFileMetadataDTO);
      })
    );
  }

  downloadFile(file: FileListItem): Observable<any> {
    return this.projectStore.projectId$.pipe(
      switchMap((projectId) => {
        switch (file.type) {
          case FileTypeEnum.Contract:
          case FileTypeEnum.ContractDoc:
          case FileTypeEnum.ContractSupport:
            return this.downloadService.download(`/api/project/${projectId}/contracting/file/contract/download/${file.id}`, 'contracting-file');
          case FileTypeEnum.ContractPartner:
          case FileTypeEnum.ContractPartnerDoc:
            return this.downloadService.download(`/api/project/${projectId}/contracting/file/partnerDocument/download/${file.id}`, 'contracting-file');
          case FileTypeEnum.ContractInternal:
          default:
            return this.downloadService.download(`/api/project/${projectId}/contracting/file/internal/download/${file.id}`, 'contracting-file');
        }
      }),
      catchError(error => {
        this.error$.next(error.error);
        return of({} as JemsFileMetadataDTO);
      })
    );
  }

  setParent(node: CategoryNode): void {
    node?.children?.forEach(child => {
      child.parent = node;
      this.setParent(child);
    });
  }

  private canUpload(): Observable<boolean> {
    return combineLatest([
      this.selectedCategory$,
      this.permissionService.hasPermission(PermissionsEnum.ProjectSetToContracted)
    ]).pipe(
      map(([selectedCategory, hasUserEditPermission]) => hasUserEditPermission && FileTypeEnum.ContractInternal === selectedCategory?.type),
    );
  }

  private fileList(): Observable<PageJemsFileDTO> {
    return combineLatest([
      this.selectedCategory$,
      this.projectStore.projectId$,
      this.routingService.routeParameterChanges('/', 'partnerId'),
      this.newPageIndex$,
      this.newPageSize$,
      this.newSort$.pipe(
        startWith(Tables.DEFAULT_INITIAL_SORT),
        map(sort => sort?.direction ? sort : Tables.DEFAULT_INITIAL_SORT),
        map(sort => `${sort.active},${sort.direction}`)
      ),
      this.filesChanged$.pipe(startWith(null)),
    ])
      .pipe(
        tap(([selectedCategory, projectId, partnerId, pageIndex, pageSize, sort, filesChanged]: any) => this.canDelete = filesChanged),
        switchMap(([selectedCategory, projectId, partnerIdInRoute, pageIndex, pageSize, sort]) =>
          partnerIdInRoute ? this.contractingFileService.listPartnerFiles(
              Number(partnerIdInRoute),
              Number(projectId),
              {
                treeNode: selectedCategory.type,
                filterSubtypes: [],
              } as ProjectContractingFileSearchRequestDTO,
              pageIndex,
              pageSize,
              sort
            ) :
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
        tap(page => {
          if (page.totalPages > 0 && page.number >= page.totalPages) {
            this.newPageIndex$.next(page.totalPages - 1);
          }
        }),
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

  changeFilter(section: CategoryInfo): void {
    this.selectedCategory$.next(section);
    this.newPageIndex$.next(0);
  }

  readonly canSeeProjectContracts$: Observable<boolean> = combineLatest([
    this.permissionService.hasPermission(PermissionsEnum.ProjectContractsView),
    this.permissionService.hasPermission(PermissionsEnum.ProjectContractsEdit),
    this.projectStore.userIsProjectOwner$,
    this.projectStore.userIsPartnerCollaborator$,
    this.projectStore.currentVersionOfProjectStatus$,
  ]).pipe(
    map(([hasProjectManagementViewPermission, hasProjectManagementEditPermission, isOwner, isPartnerCollaborator, projectStatus]) =>
      (hasProjectManagementViewPermission || hasProjectManagementEditPermission || isOwner || isPartnerCollaborator) &&
      ProjectUtil.isInApprovedOrAnyStatusAfterApproved(projectStatus)
    )
  );
}
