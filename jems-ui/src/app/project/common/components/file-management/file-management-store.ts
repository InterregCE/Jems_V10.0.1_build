import {Injectable} from '@angular/core';
import {BehaviorSubject, combineLatest, Observable, of, ReplaySubject, Subject} from 'rxjs';
import {
  PageProjectFileMetadataDTO,
  ProjectCallSettingsDTO,
  ProjectFileMetadataDTO,
  ProjectFileService,
  ProjectPartnerSummaryDTO,
  ProjectStatusDTO,
  ProjectVersionDTO,
  SettingsService,
  UserRoleDTO
} from '@cat/api';
import {ProjectStore} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {catchError, finalize, map, shareReplay, startWith, switchMap, take, tap, withLatestFrom} from 'rxjs/operators';
import {MatSort} from '@angular/material/sort';
import {Tables} from '@common/utils/tables';
import {CategoryInfo, CategoryNode} from '@project/common/components/category-tree/categoryModels';
import {FileCategoryTypeEnum} from '@project/common/components/file-management/file-category-type';
import {APIError} from '@common/models/APIError';
import {InvestmentSummary} from '@project/work-package/project-work-package-page/work-package-detail-page/workPackageInvestment';
import {PermissionService} from '../../../../security/permissions/permission.service';
import {I18nMessage} from '@common/models/I18nMessage';
import {ProjectPartnerStore} from '@project/project-application/containers/project-application-form-page/services/project-partner-store.service';
import {FormVisibilityStatusService} from '@project/common/services/form-visibility-status.service';
import {APPLICATION_FORM} from '@project/common/application-form-model';
import {DownloadService} from '@common/services/download.service';
import {RoutingService} from '@common/services/routing.service';
import {v4 as uuid} from 'uuid';
import {ProjectUtil} from '@project/common/project-util';
import {ProjectVersionStore} from '@project/common/services/project-version-store.service';
import PermissionsEnum = UserRoleDTO.PermissionsEnum;
import CallTypeEnum = ProjectCallSettingsDTO.CallTypeEnum;
import { Page } from '@cat/api';
import {Log} from '@common/utils/log';

@Injectable({
  providedIn: 'root'
})
export class FileManagementStore {

  fileList$: Observable<PageProjectFileMetadataDTO>;
  fileCategories$: Observable<CategoryNode>;
  selectedCategory$ = new ReplaySubject<CategoryInfo | undefined>(1);
  selectedCategoryPath$: Observable<I18nMessage[]>;
  maxFileSize$ = new ReplaySubject<number>(1);

  userIsProjectOwnerOrEditCollaborator$: Observable<boolean>;

  canUpload$: Observable<boolean>;
  currentVersion$: Observable<ProjectVersionDTO | undefined>;
  currentProjectStatus$: Observable<ProjectStatusDTO | undefined>;
  canChangeAssessmentFile$: Observable<boolean>;
  canChangeApplicationFile$: Observable<boolean>;
  canChangeModificationFile$: Observable<boolean>;
  canReadAssessmentFile$: Observable<boolean>;
  canReadApplicationFile$: Observable<boolean>;
  canReadModificationFile$: Observable<boolean>;
  canReadFiles$: Observable<boolean>;

  deleteSuccess$ = new Subject<boolean>();
  error$ = new Subject<APIError | null>();

  newPageSize$ = new BehaviorSubject<number>(Tables.DEFAULT_INITIAL_PAGE_SIZE);
  newPageIndex$ = new BehaviorSubject<number>(0);
  newSort$ = new Subject<Partial<MatSort>>();
  filesChanged$ = new Subject<void>();

  constructor(private projectFileService: ProjectFileService,
              private settingsService: SettingsService,
              private projectStore: ProjectStore,
              private projectPartnerStore: ProjectPartnerStore,
              private permissionService: PermissionService,
              private visibilityStatusService: FormVisibilityStatusService,
              private downloadService: DownloadService,
              private routingService: RoutingService,
              private projectVersionStore: ProjectVersionStore
  ) {
    this.currentVersion$ = this.projectVersionStore.currentVersion$;
    this.currentProjectStatus$ = this.projectStore.currentVersionOfProjectStatus$;
    this.userIsProjectOwnerOrEditCollaborator$ = this.projectStore.userIsProjectOwnerOrEditCollaborator$;
    this.canChangeAssessmentFile$ = this.permissionService.hasPermission(PermissionsEnum.ProjectFileAssessmentUpdate);
    this.canChangeApplicationFile$ = this.canUpdateApplicationFile();
    this.canChangeModificationFile$ = this.permissionService.hasPermission(PermissionsEnum.ProjectModificationFileAssessmentUpdate);
    this.canReadApplicationFile$ = this.canReadApplicationFile();
    this.canReadAssessmentFile$ = this.permissionService.hasPermission(PermissionsEnum.ProjectFileAssessmentRetrieve);
    this.canReadModificationFile$ = this.permissionService.hasPermission(PermissionsEnum.ProjectModificationFileAssessmentRetrieve);
    this.canUpload$ = this.canUpload();
    this.canReadFiles$ = this.canReadFiles();
    this.selectedCategoryPath$ = this.selectedCategoryPath();
    this.fileList$ = this.fileList();
    this.getMaximumAllowedFileSize();
  }

  setSection(section: CategoryInfo): void {
    this.selectedCategory$.next(section);
    this.fileCategories$ = this.fileCategories(section);
  }

  uploadFile(file: File): Observable<ProjectFileMetadataDTO> {
    const serviceId = uuid();
    this.routingService.confirmLeaveSet.add(serviceId);
    return this.selectedCategory$
      .pipe(
        take(1),
        withLatestFrom(this.projectStore.projectId$),
        switchMap(([category, projectId]) => this.projectFileService.uploadFileForm(file, (category as any)?.type, projectId, (category as any)?.id)),
        tap(() => this.filesChanged$.next()),
        tap(() => this.error$.next(null)),
        finalize(() => this.routingService.confirmLeaveSet.delete(serviceId)),
        catchError(error => {
          this.error$.next(error.error);
          return of({} as ProjectFileMetadataDTO);
        }),
      );
  }

  downloadFile(fileId: number): Observable<any> {
    return this.projectStore.projectId$
      .pipe(
        take(1),
        switchMap(projectId => {
          this.downloadService.download(`/api/project/${projectId}/file/download/${fileId}`, 'translation.properties');
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
      this.canChangeAssessmentFile$,
      this.canChangeApplicationFile$,
      this.canChangeModificationFile$,
      this.userIsProjectOwnerOrEditCollaborator$,
      this.currentVersion$
    ]).pipe(
      map(([selectedCategory, canUploadAssessmentFile, canUploadApplicationFile, canUploadModificationFile, userIsProjectOwnerOrEditCollaborator, currentVersion]) => {
        if (selectedCategory?.type === FileCategoryTypeEnum.ASSESSMENT) {
          return canUploadAssessmentFile;
        }
        if (selectedCategory?.type === FileCategoryTypeEnum.MODIFICATION) {
          return canUploadModificationFile;
        }
        if ((selectedCategory?.type === FileCategoryTypeEnum.APPLICATION || selectedCategory?.id) && this.isInModifiableStatus(currentVersion ? currentVersion.status : '')) {
          return canUploadApplicationFile || userIsProjectOwnerOrEditCollaborator;
        }

        return false;
      })
    );
  }

  isInModifiableStatus(status: ProjectStatusDTO | string): boolean {
    return ProjectUtil.isOpenForModifications(status) ||
        ProjectUtil.isInModifiableStatusBeforeApproved(status) ||
        ProjectUtil.isInModifiableStatusAfterApproved(status);
  }

  private canReadFiles(): Observable<boolean> {
    return combineLatest([
      this.permissionService.hasPermission(PermissionsEnum.ProjectFileAssessmentRetrieve),
      this.canReadApplicationFile$,
      this.canReadModificationFile$
    ]).pipe(
      map(([canReadAssessment, canReadApplication, canReadModification]) => canReadAssessment || canReadApplication || canReadModification)
    );
  }

  private fileList(): Observable<PageProjectFileMetadataDTO> {
    return combineLatest([
      this.selectedCategory$,
      this.projectStore.projectId$,
      this.newPageIndex$,
      this.newPageSize$,
      this.newSort$.pipe(
        startWith(Tables.DEFAULT_INITIAL_SORT),
        map(sort => sort?.direction ? sort : Tables.DEFAULT_INITIAL_SORT),
        map(sort => `${sort.active},${sort.direction}`)
      ),
      this.filesChanged$.pipe(startWith(null))
    ])
      .pipe(
        switchMap(([category, projectId, pageIndex, pageSize, sort]) =>
          this.projectFileService.listProjectFiles(projectId, (category as any)?.type, (category as any)?.id, pageIndex, pageSize, sort)
        ),
        tap((page: Page) => {
          if (page.totalPages > 0 && page.number >= page.totalPages) {
            this.newPageIndex$.next(page.totalPages - 1);
          }
        }),
        catchError(error => {
          this.error$.next(error.error);
          return of({} as PageProjectFileMetadataDTO);
        })
      );
  }

  private fileCategories(section: CategoryInfo): Observable<CategoryNode> {
    return combineLatest([
      this.projectStore.projectTitle$,
      this.projectStore.projectCallType$,
      this.canReadApplicationFile$.pipe(switchMap(canReadApplicationFile => this.shouldFetchApplicationCategories(section, canReadApplicationFile) ? this.projectPartnerStore.latestPartnerSummaries$ : of([]))),
      this.canReadApplicationFile$.pipe(switchMap(canReadApplicationFile => this.shouldFetchApplicationCategories(section, canReadApplicationFile) ? this.projectStore.investmentSummariesForFiles$ : of([]))),
      this.canReadApplicationFile$,
      this.canReadAssessmentFile$,
      this.canReadModificationFile$
    ]).pipe(
      map(([projectTitle, callType, partners, investments, canReadApplicationFiles, canReadAssessmentFiles, canReadModificationFiles]) => {
          return this.getCategories(
            section,
            projectTitle as string,
            partners as ProjectPartnerSummaryDTO[],
            investments as InvestmentSummary[],
            canReadApplicationFiles as boolean,
            canReadAssessmentFiles as boolean,
            canReadModificationFiles as boolean,
            callType as CallTypeEnum);
        }
      ),
      tap(filters => this.setParent(filters)),
    );
  }

  private getCategories(section: CategoryInfo,
                        projectTitle: string,
                        partners: ProjectPartnerSummaryDTO[],
                        investments: InvestmentSummary[],
                        canReadApplicationFiles: boolean,
                        canReadAssessmentFiles: boolean,
                        canReadModificationFiles: boolean,
                        callType: CallTypeEnum): CategoryNode {
    const fullTree: CategoryNode = {
      name: {i18nKey: projectTitle},
      info: {type: FileCategoryTypeEnum.ALL},
      children: []
    };
    if (canReadApplicationFiles) {
      const applicationFiles: CategoryNode = {
        name: {i18nKey: 'file.tree.type.attachments'},
        info: {type: FileCategoryTypeEnum.APPLICATION},
        children: []
      };
      applicationFiles.children?.push(
        {
          name: {i18nKey: 'file.tree.type.partner'},
          info: {type: FileCategoryTypeEnum.PARTNER},
          children: partners.map(partner => ({
            name: {
              i18nKey: ProjectPartnerStore.getPartnerTranslationKey(partner.role, callType),
              i18nArguments: {partner: `${partner.sortNumber || ''} ${partner.abbreviation}`}
            },
            info: {type: FileCategoryTypeEnum.PARTNER, id: partner.id}
          }))
        });
      if (this.visibilityStatusService.isVisible(APPLICATION_FORM.SECTION_C.PROJECT_WORK_PLAN.INVESTMENTS)) {
        applicationFiles.children?.push(
          {
            name: {i18nKey: 'file.tree.type.investment'},
            info: {type: FileCategoryTypeEnum.INVESTMENT},
            children: investments.map(investment => ({
              name: {i18nKey: investment.toString()},
              info: {type: FileCategoryTypeEnum.INVESTMENT, id: investment.id}
            }))
          }
        );
      }
      fullTree.children?.push(applicationFiles);
    }
    if (canReadAssessmentFiles) {
      fullTree.children?.push({
        name: {i18nKey: 'file.tree.type.assessment'},
        info: {type: FileCategoryTypeEnum.ASSESSMENT},
        children: []
      });
    }

    if(canReadModificationFiles) {
      fullTree.children?.push({
        name: {i18nKey: 'file.tree.type.modification'},
        info: {type: FileCategoryTypeEnum.MODIFICATION},
        children: []
      });
    }

    return this.findRootForSection(fullTree, section) || {};
  }

  public findRootForSection(root: CategoryNode, section: CategoryInfo): CategoryNode | null {
    if (root.info?.type === section.type && root.info?.id === section.id) {
      return root;
    }
    if (root?.children) {
      for (const child of root.children) {
        const potentialRoot = this.findRootForSection(child, section);
        if (potentialRoot != null) {
          return potentialRoot;
        }
      }
    }

    return null;
  }

  private canReadApplicationFile(): Observable<boolean> {
    return combineLatest([
      this.permissionService.hasPermission(PermissionsEnum.ProjectFileApplicationRetrieve),
      this.projectStore.userIsProjectOwner$
    ])
      .pipe(
        map(([canReadApplicationFile, userIsProjectOwner]) => canReadApplicationFile || userIsProjectOwner)
      );
  }

  private canUpdateApplicationFile(): Observable<boolean> {
    return combineLatest([
      this.permissionService.hasPermission(PermissionsEnum.ProjectFileApplicationUpdate),
      this.projectStore.userIsEditOrManageCollaborator$
    ])
      .pipe(
        map(([canUpdateApplicationFile, userIsCollaboratorWithEditOrManage]) => canUpdateApplicationFile || userIsCollaboratorWithEditOrManage)
      );
  }

  private selectedCategoryPath(): Observable<I18nMessage[]> {
    return combineLatest([this.selectedCategory$, this.fileCategories$])
      .pipe(
        map(([selectedCategory, fileCategories]) =>
          ([{i18nKey: 'file.tree.type.all'}, ...this.getPath(selectedCategory as any, fileCategories)])
        )
      );
  }

  public getPath(selectedCategory: CategoryInfo, node: CategoryNode): I18nMessage[] {
    if (node.info?.type === selectedCategory?.type && (!selectedCategory?.id || selectedCategory.id === node.info?.id)) {
      return [node.name as any];
    }

    if (node.children && node.children.length > 0) {
      let potentialPath: I18nMessage[] = [];
      for (const child of node.children) {
        potentialPath = this.getPath(selectedCategory, child);
        if (potentialPath.filter(it => it.i18nKey === 'INVALID_PATH').length === 0) {
          break;
        }
      }

      return [node.name as any, ...potentialPath];
    }

    return [{i18nKey: 'INVALID_PATH'}];
  }

  private shouldFetchApplicationCategories(section: CategoryInfo, canReadApplicationFile: boolean): boolean {
    return section.type !== FileCategoryTypeEnum.ASSESSMENT && canReadApplicationFile;
  }

  getMaximumAllowedFileSize(): void {
    this.settingsService.getMaximumAllowedFileSize().pipe(
      take(1),
      tap(value => this.maxFileSize$.next(value)),
      tap(value => Log.info('Fetched max file size:', this, value))
    ).subscribe();
  }

  changeFilter(section: CategoryInfo): void {
    this.selectedCategory$.next(section);
    this.newPageIndex$.next(0);
  }
}
