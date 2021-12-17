import {Injectable} from '@angular/core';
import {ProjectStore} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {ProjectPartnerStore} from '@project/project-application/containers/project-application-form-page/services/project-partner-store.service';
import {ProjectLumpSumService} from '@cat/api';
import {ProjectVersionStore} from '@project/common/services/project-version-store.service';
import {ProjectPartnerBudgetStore} from '@project/budget/services/project-partner-budget.store';
import {filter, shareReplay, startWith, switchMap} from 'rxjs/operators';
import {combineLatest, Observable} from 'rxjs';

@Injectable()
export class ProjectLumpSumStore {
  partnerTotalLumpSum$: Observable<number>;

  constructor(private projectStore: ProjectStore,
              private partnerStore: ProjectPartnerStore,
              private projectLumpSumService: ProjectLumpSumService,
              private projectVersionStore: ProjectVersionStore,
              private projectPartnerBudgetStore: ProjectPartnerBudgetStore) {
    this.partnerTotalLumpSum$ = this.partnerTotalLumpSum();
  }

  private partnerTotalLumpSum(): Observable<number> {
    return combineLatest([
      this.partnerStore.partner$,
      this.projectVersionStore.selectedVersionParam$,
      this.projectStore.projectId$,
      this.projectPartnerBudgetStore.updateBudgetOptionsEvent$.pipe(startWith(null))
    ])
      .pipe(
        filter(([partner]) => !!partner.id),
        switchMap(([partner, version, projectId]) => this.projectLumpSumService.getProjectLumpSumsTotalForPartner(partner.id, projectId, version)),
        shareReplay(1)
      );
  }
}
