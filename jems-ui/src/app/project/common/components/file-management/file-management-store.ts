import {Injectable} from '@angular/core';
import {combineLatest, Observable, of, ReplaySubject, Subject} from 'rxjs';
import {ProjectPartner} from '@project/model/ProjectPartner';
import {
  PageProjectFileMetadataDTO,
  ProjectFileMetadataDTO,
  ProjectFileService,
  ProjectPartnerService,
  ProjectService,
  ProjectStatusDTO,
  UserRoleDTO
} from '@cat/api';
import {ProjectStore} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {catchError, map, startWith, switchMap, take, tap, withLatestFrom} from 'rxjs/operators';
import {MatSort} from '@angular/material/sort';
import {Tables} from '@common/utils/tables';
import {
  FileCategoryEnum,
  FileCategoryInfo,
  FileCategoryNode
} from '@project/common/components/file-management/file-category';
import {APIError} from '@common/models/APIError';
import {InvestmentSummary} from '@project/work-package/project-work-package-page/work-package-detail-page/workPackageInvestment';
import {ProjectPartnerRoleEnumUtil} from '@project/model/ProjectPartnerRoleEnum';
import {PermissionService} from '../../../../security/permissions/permission.service';
import {ProjectUtil} from '@project/common/project-util';
import PermissionsEnum = UserRoleDTO.PermissionsEnum;

@Injectable()
export class FileManagementStore {

  fileList$: Observable<PageProjectFileMetadataDTO>;
  fileCategories$: Observable<FileCategoryNode>;
  selectedCategory$ = new ReplaySubject<FileCategoryInfo | undefined>(1);

  projectStatus$: Observable<ProjectStatusDTO>;
  userIsProjectOwner$: Observable<boolean>;

  canUpload$: Observable<boolean>;
  canChangeAssessmentFile$: Observable<boolean>;
  canChangeApplicationFile$: Observable<boolean>;
  canReadApplicationFile$: Observable<boolean>;
  canReadFiles$: Observable<boolean>;

  deleteSuccess$ = new Subject<boolean>();
  error$ = new Subject<APIError | null>();

  newPageSize$ = new Subject<number>();
  newPageIndex$ = new Subject<number>();
  newSort$ = new Subject<Partial<MatSort>>();
  filesChanged$ = new Subject<void>();

  // TODO: temporary observables without considering selected version => remove them and reuse the store ones
  private partners$: Observable<ProjectPartner[]>;
  private investments$: Observable<InvestmentSummary[]>;

  constructor(private projectFileService: ProjectFileService,
              private projectStore: ProjectStore,
              private permissionService: PermissionService,
              // temporary services for fetching partners/investments without considering selected version
              private projectService: ProjectService,
              private partnerService: ProjectPartnerService) {
    this.projectStatus$ = this.projectStore.projectStatus$;
    this.userIsProjectOwner$ = this.projectStore.userIsProjectOwner$;

    this.canChangeAssessmentFile$ = this.permissionService.hasPermission(PermissionsEnum.ProjectFileAssessmentUpdate);
    this.canChangeApplicationFile$ = this.permissionService.hasPermission(PermissionsEnum.ProjectFileApplicationUpdate);
    this.canReadApplicationFile$ = this.canReadApplicationFile();
    this.canUpload$ = this.canUpload();
    this.canReadFiles$ = this.canReadFiles();

    this.investments$ = this.investments();
    this.partners$ = this.partners();
    this.fileCategories$ = this.fileCategories();
    this.fileList$ = this.fileList();
  }

  uploadFile(file: File): Observable<ProjectFileMetadataDTO> {
    return this.selectedCategory$
      .pipe(
        take(1),
        withLatestFrom(this.projectStore.projectId$),
        switchMap(([category, projectId]) => this.projectFileService.uploadFileForm(file, projectId, (category as any)?.id, (category as any)?.type)),
        tap(() => this.filesChanged$.next()),
        tap(() => this.error$.next(null)),
        catchError(error => {
          this.error$.next(error.error);
          return of({} as ProjectFileMetadataDTO);
        })
      );
  }

  deleteFile(fileId: number): Observable<void> {
    return this.projectStore.projectId$
      .pipe(
        take(1),
        switchMap(projectId => this.projectFileService.deleteProjectFile(fileId, projectId)),
        tap(() => this.filesChanged$.next()),
        tap(() => this.deleteSuccess$.next(true)),
        tap(() => setTimeout(() => this.deleteSuccess$.next(false), 3000)),
        catchError(error => {
          this.error$.next(error.error);
          return of({} as ProjectFileMetadataDTO);
        })
      );
  }

  setFileDescription(fileId: number, description: string): Observable<ProjectFileMetadataDTO> {
    return this.projectStore.projectId$
      .pipe(
        take(1),
        switchMap(projectId => this.projectFileService.setProjectFileDescription(fileId, projectId, description)),
        catchError(error => {
          this.error$.next(error.error);
          return of({} as ProjectFileMetadataDTO);
        })
      );
  }

  downloadFile(fileId: number): Observable<any> {
    return this.projectStore.projectId$
      .pipe(
        take(1),
        switchMap(projectId => {
          window.open(
            `/api/project/${projectId}/file/download/${fileId}`,
            '_blank',
          );
          return of(null);
        })
      );
  }

  private setParent(node: FileCategoryNode): void {
    node?.children?.forEach(child => {
      child.parent = node;
      this.setParent(child);
    });
  }

  private investments(): Observable<InvestmentSummary[]> {
    return this.projectStore.projectId$
      .pipe(
        switchMap(projectId => this.projectService.getProjectInvestmentSummaries(projectId)),
        map(investmentSummeryDTOs => investmentSummeryDTOs.map(it => new InvestmentSummary(it.id, it.investmentNumber, it.workPackageNumber))),
      );
  }

  private partners(): Observable<ProjectPartner[]> {
    return this.projectStore.projectId$
      .pipe(
        switchMap(projectId => this.partnerService.getProjectPartnersForDropdown(projectId, undefined)),
        map(projectPartners => projectPartners.map((projectPartner, index) =>
          new ProjectPartner(projectPartner.id, index, projectPartner.abbreviation, ProjectPartnerRoleEnumUtil.toProjectPartnerRoleEnum(projectPartner.role), projectPartner.sortNumber, projectPartner.country))),
      );
  }

  private canUpload(): Observable<boolean> {
    return combineLatest([
      this.selectedCategory$,
      this.projectStore.currentVersionIsLatest$,
      this.projectStatus$,
      this.canChangeAssessmentFile$,
      this.canChangeApplicationFile$,
      this.userIsProjectOwner$
    ]).pipe(
      map(([selectedCategory, currentVersionIsLatest, projectStatus, canUploadAssessmentFile, canUploadApplicationFile, userIsProjectOwner]) => {
        if (!currentVersionIsLatest) {
          return false;
        }
        if (selectedCategory?.type === FileCategoryEnum.ASSESSMENT) {
          return canUploadAssessmentFile;
        }
        if (!ProjectUtil.isOpenForModifications(projectStatus)) {
          return false;
        }
        if (selectedCategory?.type === FileCategoryEnum.APPLICATION) {
          return canUploadApplicationFile || userIsProjectOwner;
        }
        // if no assessment/application are selected the category must be either a partner or investment
        return !!selectedCategory?.id;
      })
    );
  }

  private canReadFiles(): Observable<boolean> {
    return combineLatest([
      this.permissionService.hasPermission(PermissionsEnum.ProjectFileAssessmentRetrieve),
      this.canReadApplicationFile$
    ]).pipe(
      map(([canReadAssessment, canReadApplication]) => canReadAssessment || canReadApplication)
    );
  }

  private fileList(): Observable<PageProjectFileMetadataDTO> {
    return combineLatest([
      this.selectedCategory$,
      this.projectStore.projectId$,
      this.newPageIndex$.pipe(startWith(Tables.DEFAULT_INITIAL_PAGE_INDEX)),
      this.newPageSize$.pipe(startWith(Tables.DEFAULT_INITIAL_PAGE_SIZE)),
      this.newSort$.pipe(
        startWith(Tables.DEFAULT_INITIAL_SORT),
        map(sort => sort?.direction ? sort : Tables.DEFAULT_INITIAL_SORT),
        map(sort => `${sort.active},${sort.direction}`)
      ),
      this.filesChanged$.pipe(startWith(null))
    ])
      .pipe(
        switchMap(([category, projectId, pageIndex, pageSize, sort]) =>
          this.projectFileService.listProjectFiles(projectId, (category as any)?.id, pageIndex, pageSize, sort, (category as any)?.type)
        ),
        catchError(error => {
          this.error$.next(error.error);
          return of({} as PageProjectFileMetadataDTO);
        })
      );
  }

  private fileCategories(): Observable<FileCategoryNode> {
    return combineLatest([
      this.projectStore.projectTitle$,
      this.partners$,
      this.investments$,
      this.permissionService.hasPermission(PermissionsEnum.ProjectFileAssessmentRetrieve),
      this.canReadApplicationFile$
    ]).pipe(
      map(([projectTitle, partners, investments, canReadAssessment, canReadApplication]) =>
        this.getCategories(projectTitle, partners, investments, canReadApplication, canReadAssessment)
      ),
      tap(filters => this.setParent(filters))
    );
  }

  private getCategories(projectTitle: string,
                        partners: ProjectPartner[],
                        investments: InvestmentSummary[],
                        canReadApplication: boolean,
                        canReadAssessment: boolean): FileCategoryNode {
    const root: FileCategoryNode = {
      name: {i18nKey: projectTitle},
      info: {type: FileCategoryEnum.ALL},
      children: []
    };
    if (canReadApplication) {
      root.children?.push({
        name: {i18nKey: 'file.tree.type.attachments'},
        info: {type: FileCategoryEnum.APPLICATION},
        children: [
          {
            name: {i18nKey: 'file.tree.type.partner'},
            info: {type: FileCategoryEnum.PARTNER},
            children: partners.map(partner => ({
              name: {
                i18nKey: 'common.label.project.partner.role.shortcut.' + partner.role,
                i18nArguments: {partner: `${partner.sortNumber || ''} ${partner.abbreviation}`}
              },
              info: {type: FileCategoryEnum.PARTNER, id: partner.id}
            }))
          },
          {
            name: {i18nKey: 'file.tree.type.investment'},
            info: {type: FileCategoryEnum.INVESTMENT},
            children: investments.map(investment => ({
              name: {i18nKey: investment.toString()},
              info: {type: FileCategoryEnum.INVESTMENT, id: investment.id}
            }))
          }
        ]
      });
    }

    if (canReadAssessment) {
      root.children?.push({
        name: {i18nKey: 'file.tree.type.assessment'},
        info: {type: FileCategoryEnum.ASSESSMENT}
      });
    }
    return root;
  }

  private canReadApplicationFile(): Observable<boolean> {
    return combineLatest([
      this.permissionService.hasPermission(PermissionsEnum.ProjectFileApplicationRetrieve),
      this.userIsProjectOwner$
    ])
      .pipe(
        map(([canReadApplicationFile, userIsProjectOwner]) => canReadApplicationFile || userIsProjectOwner)
      );
  }
}
