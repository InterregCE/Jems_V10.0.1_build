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
  ContractingPartnerBeneficialOwnerDTO,
  ProjectContractingPartnerBeneficialOwnerService,
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
  savedBeneficialOwners$ = new Subject<ContractingPartnerBeneficialOwnerDTO[]>();
  userCanEditContractPartner$: Observable<boolean>;
  userCanViewContractPartner$: Observable<boolean>;

  constructor(private partnerStore: ProjectPartnerStore,
              private projectStore: ProjectStore,
              private routingService: RoutingService,
              private beneficialOwnerService: ProjectContractingPartnerBeneficialOwnerService,
              private permissionService: PermissionService,
              private partnerUserCollaboratorService: ProjectPartnerUserCollaboratorService) {
    this.partnerId$ = this.partnerId();
    this.projectId$ = this.projectStore.projectId$;
    this.partnerSummary$ = this.partnerInfo();
    this.beneficialOwners$ = this.beneficialOwners();
    this.userCanEditContractPartner$ = this.userCanEditContractPartner();
    this.userCanViewContractPartner$ = this.userCanViewContractPartner();
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
      this.permissionService.hasPermission(PermissionsEnum.ProjectContractingPartnerEdit),
    ]).pipe(
      switchMap(([partnerId, hasContractingPartnerEdit]) =>
        combineLatest([
          this.partnerUserCollaboratorService.checkMyPartnerLevel(partnerId as number),
          of(hasContractingPartnerEdit),
        ])
      ),
      map(([partnerLevel, hasContractingPartnerEdit]) => hasContractingPartnerEdit || partnerLevel === LevelEnum.EDIT));
  }

  private userCanViewContractPartner(): Observable<boolean> {
    return combineLatest([
      this.partnerId$,
      this.permissionService.hasPermission(PermissionsEnum.ProjectContractingPartnerView),
    ]).pipe(
      switchMap(([partnerId, hasContractingPartnerView]) =>
        combineLatest([
          this.partnerUserCollaboratorService.checkMyPartnerLevel(partnerId as number),
          of(hasContractingPartnerView),
        ])
      ),
      map(([partnerLevel, hasContractingPartnerView]) => hasContractingPartnerView || partnerLevel === LevelEnum.EDIT || partnerLevel === LevelEnum.VIEW));
  }
}
