import {Injectable} from '@angular/core';
import {combineLatest, merge, Observable, Subject} from 'rxjs';
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
  ProjectPartnerSummaryDTO,
} from '@cat/api';
import {Log} from '@common/utils/log';

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

  constructor(private partnerStore: ProjectPartnerStore,
              private projectStore: ProjectStore,
              private routingService: RoutingService,
              private beneficialOwnerService: ProjectContractingPartnerBeneficialOwnerService,) {
    this.partnerId$ = this.partnerId();
    this.projectId$ = this.projectStore.projectId$;
    this.partnerSummary$ = this.partnerInfo();
    this.beneficialOwners$ = this.beneficialOwners();
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
}
