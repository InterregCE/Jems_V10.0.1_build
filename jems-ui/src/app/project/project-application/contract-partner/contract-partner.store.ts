import {Injectable} from '@angular/core';
import {combineLatest, merge, Observable, of, Subject} from 'rxjs';
import {
  ProjectStore
} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {
  ProjectPartnerStore
} from '@project/project-application/containers/project-application-form-page/services/project-partner-store.service';
import {filter, map, shareReplay, switchMap, tap} from 'rxjs/operators';
import {RoutingService} from '@common/services/routing.service';
import {
  ContractingPartnerBeneficialOwnerDTO, ContractingPartnerDocumentsLocationDTO,
  ProjectContractingPartnerBeneficialOwnerService, ProjectContractingPartnerLocationOfDocumentsService,
  ProjectPartnerSummaryDTO, ProjectPartnerUserCollaboratorService, ProjectUserCollaboratorDTO, UserRoleCreateDTO,
} from '@cat/api';
import {Log} from '@common/utils/log';
import PermissionsEnum = UserRoleCreateDTO.PermissionsEnum;
import LevelEnum = ProjectUserCollaboratorDTO.LevelEnum;
import {PermissionService} from '../../../security/permissions/permission.service';

@Injectable({
  providedIn: 'root'
})
export class ContractPartnerStore {
  public static PARTNER_PATH = '/contractPartner/';
  projectId$: Observable<number>;
  partnerSummary$: Observable<ProjectPartnerSummaryDTO>;
  partnerId$: Observable<string | number | null>;
  beneficialOwners$: Observable<ContractingPartnerBeneficialOwnerDTO[]>;
  documentsLocation$: Observable<ContractingPartnerDocumentsLocationDTO>;
  savedBeneficialOwners$ = new Subject<ContractingPartnerBeneficialOwnerDTO[]>();
  savedDocumentsLocation$ = new Subject<ContractingPartnerDocumentsLocationDTO>();
  userCanEditContractPartner$: Observable<boolean>;
  userCanViewContractPartner$: Observable<boolean>;

  constructor(private partnerStore: ProjectPartnerStore,
              private projectStore: ProjectStore,
              private routingService: RoutingService,
              private beneficialOwnerService: ProjectContractingPartnerBeneficialOwnerService,
              private permissionService: PermissionService,
              private partnerUserCollaboratorService: ProjectPartnerUserCollaboratorService,
              private documentsLocationService: ProjectContractingPartnerLocationOfDocumentsService) {
    this.partnerId$ = this.partnerId();
    this.projectId$ = this.projectStore.projectId$;
    this.partnerSummary$ = this.partnerInfo();
    this.beneficialOwners$ = this.beneficialOwners();
    this.userCanEditContractPartner$ = this.userCanEditContractPartner();
    this.userCanViewContractPartner$ = this.userCanViewContractPartner();
    this.documentsLocation$ = this.documentsLocation();
  }

  updateBeneficialOwners(beneficialOwners: ContractingPartnerBeneficialOwnerDTO[]) {
    return combineLatest([this.partnerId$, this.projectId$])
      .pipe(
        switchMap(([partnerId, projectId]) => this.beneficialOwnerService.updateBeneficialOwners(partnerId as number,projectId,  beneficialOwners)),
        tap(saved => Log.info('Saved contract partner beneficial owners', saved)),
        tap(data => this.savedBeneficialOwners$.next(data))
      );
  }

  private partnerInfo(): Observable<ProjectPartnerSummaryDTO> {
    return combineLatest([this.partnerId$, this.partnerStore.partnerSummaries$])
      .pipe(
        filter(([partnerId, partnerSummaries]) => !!partnerId),
        map(([partnerId, partnerSummaries]) =>
          partnerSummaries.find(value => value.id === Number(partnerId)) || {} as any
        ));
  }

  private partnerId(): Observable<number | string | null> {
    return this.routingService.routeParameterChanges(ContractPartnerStore.PARTNER_PATH, 'partnerId');
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

  private userCanEditContractPartner(): Observable<boolean> {
    return combineLatest([
      this.partnerId$,
      this.projectStore.userIsPartnerCollaborator$,
      this.permissionService.hasPermission(PermissionsEnum.ProjectContractingPartnerEdit),
      this.projectStore.userIsProjectOwnerOrEditCollaborator$,
      this.projectStore.collaboratorLevel$
    ]).pipe(
      switchMap(([partnerId, userIsPartnerCollaborator, hasContractingPartnerEdit, isProjectOwnerOrEditCollaborator, collaboratorLevel  ]) =>
        combineLatest([
          this.partnerUserCollaboratorService.checkMyPartnerLevel(partnerId as number),
          of(userIsPartnerCollaborator),
          of(hasContractingPartnerEdit),
          of(isProjectOwnerOrEditCollaborator),
          of(collaboratorLevel),
        ])
      ),
      map(([partnerLevel, userIsPartnerCollaborator, hasContractingPartnerEdit, isProjectOwnerOrEditCollaborator, collaboratorLevel]) => hasContractingPartnerEdit || (userIsPartnerCollaborator && partnerLevel === LevelEnum.EDIT) || (isProjectOwnerOrEditCollaborator && (collaboratorLevel === LevelEnum.EDIT || collaboratorLevel === LevelEnum.MANAGE))));
  }

  private userCanViewContractPartner(): Observable<boolean> {
    return combineLatest([
      this.partnerId$,
      this.projectStore.userIsPartnerCollaborator$,
      this.permissionService.hasPermission(PermissionsEnum.ProjectContractingPartnerView),
      this.projectStore.userIsProjectOwner$,
    ]).pipe(
      switchMap(([partnerId, userIsPartnerCollaborator, hasContractingPartnerView, userIsProjectOwner]) =>
        combineLatest([
          this.partnerUserCollaboratorService.checkMyPartnerLevel(partnerId as number),
          of(userIsPartnerCollaborator),
          of(hasContractingPartnerView),
          of(userIsProjectOwner),
        ])
      ),
      map(([partnerLevel, userIsPartnerCollaborator, hasContractingPartnerView, userIsProjectOwner]) => userIsProjectOwner || hasContractingPartnerView || (userIsPartnerCollaborator && (partnerLevel === LevelEnum.EDIT || partnerLevel === LevelEnum.VIEW))));
  }

  updateDocumentsLocation(documentsLocation: ContractingPartnerDocumentsLocationDTO): Observable<ContractingPartnerDocumentsLocationDTO> {
    return combineLatest([this.partnerId$, this.projectId$])
      .pipe(
        switchMap(([partnerId, projectId]) => this.documentsLocationService.updateDocumentsLocation(partnerId as number, projectId,  documentsLocation)),
        tap(saved => Log.info('Saved contract partner documents location', saved)),
        tap(data => this.savedDocumentsLocation$.next(data))
      );
  }

  private documentsLocation(): Observable<ContractingPartnerDocumentsLocationDTO> {
    const initialData$ = combineLatest([this.partnerId$, this.projectId$])
      .pipe(
        switchMap(([partnerId, projectId]) => this.documentsLocationService.getDocumentsLocation(partnerId as number, projectId)),
      );
    return merge(initialData$, this.savedDocumentsLocation$)
      .pipe(
        shareReplay(1)
      );
  }
}
