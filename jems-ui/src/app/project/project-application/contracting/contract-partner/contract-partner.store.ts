import {Injectable} from '@angular/core';
import {combineLatest, merge, Observable, Subject} from 'rxjs';
import {ProjectStore} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {ProjectPartnerStore} from '@project/project-application/containers/project-application-form-page/services/project-partner-store.service';
import {filter, map, shareReplay, switchMap, tap} from 'rxjs/operators';
import {RoutingService} from '@common/services/routing.service';
import {
  ContractingPartnerBeneficialOwnerDTO,
  ContractingPartnerDocumentsLocationDTO,
  ContractingPartnerStateAidDeMinimisDTO,
  ContractingPartnerStateAidDeMinimisSectionDTO,
  ContractingPartnerStateAidGberDTO,
  ContractingPartnerStateAidGberSectionDTO,
  ProjectContractingPartnerBeneficialOwnerService,
  ProjectContractingPartnerLocationOfDocumentsService,
  ProjectContractingPartnerStateAidService,
  ProjectPartnerSummaryDTO,
  ProjectPartnerUserCollaboratorService,
  ProjectStatusDTO,
  ProjectUserCollaboratorDTO,
  UserRoleCreateDTO,
} from '@cat/api';
import {Log} from '@common/utils/log';
import {PermissionService} from '../../../../security/permissions/permission.service';
import {ProjectUtil} from '@project/common/project-util';
import {UntilDestroy} from '@ngneat/until-destroy';
import {ContractingStore} from '@project/project-application/contracting/contracting.store';
import PermissionsEnum = UserRoleCreateDTO.PermissionsEnum;
import LevelEnum = ProjectUserCollaboratorDTO.LevelEnum;

@Injectable({
  providedIn: 'root'
})
@UntilDestroy()
export class ContractPartnerStore {
  public static PARTNER_PATH = '/contractPartner/';
  projectId$: Observable<number>;
  partnerId$: Observable<number>;
  partnerSummary$: Observable<ProjectPartnerSummaryDTO>;
  beneficialOwners$: Observable<ContractingPartnerBeneficialOwnerDTO[]>;
  documentsLocation$: Observable<ContractingPartnerDocumentsLocationDTO>;
  savedBeneficialOwners$ = new Subject<ContractingPartnerBeneficialOwnerDTO[]>();
  savedDeMinimis$ = new Subject<ContractingPartnerStateAidDeMinimisSectionDTO>();
  savedGber$ = new Subject<ContractingPartnerStateAidGberSectionDTO>();
  savedDocumentsLocation$ = new Subject<ContractingPartnerDocumentsLocationDTO>();
  userCanEditContractPartner$: Observable<boolean>;
  userCanViewContractPartner$: Observable<boolean>;
  isPartnerLocked$: Observable<boolean>;
  deMinimis$: Observable<ContractingPartnerStateAidDeMinimisSectionDTO>;
  GBER$: Observable<ContractingPartnerStateAidGberSectionDTO>;

  constructor(
    private projectPartnerStore: ProjectPartnerStore,
    private projectStore: ProjectStore,
    private routingService: RoutingService,
    private beneficialOwnerService: ProjectContractingPartnerBeneficialOwnerService,
    private permissionService: PermissionService,
    private partnerUserCollaboratorService: ProjectPartnerUserCollaboratorService,
    private documentsLocationService: ProjectContractingPartnerLocationOfDocumentsService,
    private contractingStore: ContractingStore,
    private projectContractingPartnerStateAidService: ProjectContractingPartnerStateAidService,
  ) {
    this.projectId$ = this.projectStore.projectId$;
    this.partnerId$ = this.partnerId();
    this.isPartnerLocked$ = this.isPartnerLocked();
    this.partnerSummary$ = this.partnerInfo();
    this.beneficialOwners$ = this.beneficialOwners();
    this.userCanEditContractPartner$ = this.userCanEditContractPartner();
    this.userCanViewContractPartner$ = this.userCanViewContractPartner();
    this.documentsLocation$ = this.documentsLocation();
    this.deMinimis$ = this.deMinimis();
    this.GBER$ = this.GBER();
  }

  updateBeneficialOwners(beneficialOwners: ContractingPartnerBeneficialOwnerDTO[]) {
    return combineLatest([this.partnerId$, this.projectId$])
      .pipe(
        switchMap(([partnerId, projectId]) => this.beneficialOwnerService.updateBeneficialOwners(partnerId, projectId, beneficialOwners)),
        tap(saved => Log.info('Saved contract partner beneficial owners', saved)),
        tap(data => this.savedBeneficialOwners$.next(data))
      );
  }

  private partnerInfo(): Observable<ProjectPartnerSummaryDTO> {
    return combineLatest([this.partnerId$, this.projectPartnerStore.partnerSummariesOfLastApprovedVersion$])
      .pipe(
        map(([partnerId, partnerSummaries]) =>
          partnerSummaries.find(value => value.id === Number(partnerId)) || {} as any
        ));
  }

  private partnerId(): Observable<number> {
    return this.routingService.routeParameterChanges(ContractPartnerStore.PARTNER_PATH, 'partnerId')
      .pipe(
        filter(Boolean),
        map(Number),
      );
  }

  private beneficialOwners(): Observable<ContractingPartnerBeneficialOwnerDTO[]> {
    const initialData$ = combineLatest([this.partnerId$, this.projectId$])
      .pipe(
        switchMap(([partnerId, projectId]) => this.beneficialOwnerService.getBeneficialOwners(partnerId as number, projectId)),
      );

    return merge(initialData$, this.savedBeneficialOwners$)
      .pipe(
        shareReplay(1)
      );
  }

  private deMinimis(): Observable<ContractingPartnerStateAidDeMinimisSectionDTO> {
    const initialData$ = this.partnerId$.pipe(
      switchMap((partnerId) => this.projectContractingPartnerStateAidService.getDeMinimisSection(partnerId)),
    );

    return merge(initialData$, this.savedDeMinimis$)
      .pipe(
        shareReplay(1)
      );
  }

  private GBER(): Observable<ContractingPartnerStateAidGberSectionDTO> {
    const initialData$ = this.partnerId$.pipe(
      switchMap((partnerId) => this.projectContractingPartnerStateAidService.getGberSection(partnerId)),
    );

    return merge(initialData$, this.savedGber$)
      .pipe(
        shareReplay(1)
      );
  }

  updateDeMinimis(deMinimis: ContractingPartnerStateAidDeMinimisDTO) {
    return this.partnerId$
      .pipe(
        switchMap((partnerId) => this.projectContractingPartnerStateAidService.updateDeMinimisSection(partnerId, deMinimis)),
        tap(saved => Log.info('Saved deMinimis', saved)),
        tap(data => this.savedDeMinimis$.next(data))
      );
  }

  updateGber(gber: ContractingPartnerStateAidGberDTO) {
    return this.partnerId$
      .pipe(
        switchMap((partnerId) => this.projectContractingPartnerStateAidService.updateGberSection(partnerId, gber)),
        tap(saved => Log.info('Saved gber', saved)),
        tap(data => this.savedGber$.next(data))
      );
  }

  private userCanEditContractPartner(): Observable<boolean> {
    return combineLatest([
      this.partnerId$.pipe(switchMap(partnerId => this.partnerUserCollaboratorService.checkMyPartnerLevel(partnerId))),
      this.projectStore.userIsPartnerCollaborator$,
      this.permissionService.hasPermission(PermissionsEnum.ProjectContractingPartnerEdit),
      this.projectStore.userIsProjectOwnerOrEditCollaborator$,
      this.projectStore.collaboratorLevel$
    ]).pipe(
      map(([partnerLevel, userIsPartnerCollaborator, hasContractingPartnerEdit, isProjectOwnerOrEditCollaborator, collaboratorLevel]) =>
        hasContractingPartnerEdit || (userIsPartnerCollaborator && partnerLevel === LevelEnum.EDIT) || (isProjectOwnerOrEditCollaborator && (collaboratorLevel === LevelEnum.EDIT || collaboratorLevel === LevelEnum.MANAGE))),
      shareReplay(1)
    );
  }

  private userCanViewContractPartner(): Observable<boolean> {
    return combineLatest([
      this.partnerId$,
      this.partnerId$.pipe(switchMap(partnerId => this.partnerUserCollaboratorService.checkMyPartnerLevel(partnerId))),
      this.projectStore.userIsPartnerCollaborator$,
      this.permissionService.hasPermission(PermissionsEnum.ProjectContractingPartnerView),
      this.projectStore.userIsProjectOwner$,
      this.projectPartnerStore.partnerSummaries$,
      this.projectStore.projectStatus$
    ]).pipe(
      map(([partnerId, partnerLevel, userIsPartnerCollaborator, hasContractingPartnerView, userIsProjectOwner, partners, projectStatus]:
             [number, string, boolean, boolean, boolean, ProjectPartnerSummaryDTO[], ProjectStatusDTO]) =>
        (partners.map(partner => partner.id).find(id => id === partnerId) !== undefined) && ProjectUtil.isInApprovedOrAnyStatusAfterApproved(projectStatus) &&
        (userIsProjectOwner || hasContractingPartnerView || (userIsPartnerCollaborator && (partnerLevel === LevelEnum.EDIT || partnerLevel === LevelEnum.VIEW)))),
      shareReplay(1)
    );
  }

  updateDocumentsLocation(documentsLocation: ContractingPartnerDocumentsLocationDTO): Observable<ContractingPartnerDocumentsLocationDTO> {
    return combineLatest([this.partnerId$, this.projectId$])
      .pipe(
        switchMap(([partnerId, projectId]) => this.documentsLocationService.updateDocumentsLocation(partnerId as number, projectId, documentsLocation)),
        tap(saved => Log.info('Saved contract partner documents location', saved)),
        tap(data => this.savedDocumentsLocation$.next(data))
      );
  }

  private documentsLocation(): Observable<ContractingPartnerDocumentsLocationDTO> {
    const initialData$ = combineLatest([this.partnerId$, this.projectId$])
      .pipe(
        switchMap(([partnerId, projectId]) => this.documentsLocationService.getDocumentsLocation(partnerId, projectId)),
      );
    return merge(initialData$, this.savedDocumentsLocation$)
      .pipe(
        shareReplay(1)
      );
  }

  private isPartnerLocked(): Observable<boolean> {
    return combineLatest([
      this.partnerId$,
      this.contractingStore.partnerSummaries$,
    ]).pipe(
      map(([partnerId, partners]) =>
        partners.find(partner => partner.id === Number(partnerId))?.locked || false)
    );
  }

}
