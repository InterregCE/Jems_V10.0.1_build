import {Injectable} from '@angular/core';
import {ContractingPartnerSummaryDTO, ProjectContractingPartnersService} from '@cat/api';
import {BehaviorSubject, combineLatest, Observable} from 'rxjs';
import {switchMap} from 'rxjs/operators';
import {
  ProjectStore
} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {UntilDestroy} from '@ngneat/until-destroy';

@Injectable({
    providedIn: 'root'
})
@UntilDestroy()
export class ContractingStore {

  partnerSummaries$: Observable<ContractingPartnerSummaryDTO[]>;

  private partnerUpdateEvent$ = new BehaviorSubject(null);

  constructor(
    private projectContractingPartnersService: ProjectContractingPartnersService,
    private projectStore: ProjectStore,
  ) {
    this.partnerSummaries$ = this.partnerSummariesForContracting();
  }

  private partnerSummariesForContracting(): Observable<ContractingPartnerSummaryDTO[]> {
    return combineLatest([
      this.projectStore.projectId$,
      this.partnerUpdateEvent$,
    ]).pipe(
      switchMap(([projectId]) => this.projectContractingPartnersService.getProjectPartnersForContracting(projectId, ['sortNumber'])),
    );
  }

  refreshPartners() {
    this.partnerUpdateEvent$.next(null);
  }

}
