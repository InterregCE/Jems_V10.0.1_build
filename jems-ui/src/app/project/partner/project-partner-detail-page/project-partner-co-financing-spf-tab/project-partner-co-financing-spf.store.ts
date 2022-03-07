import {Injectable} from '@angular/core';
import {ProjectPartnerStore} from '@project/project-application/containers/project-application-form-page/services/project-partner-store.service';
import {
  ProjectPartnerBudgetService, ProjectPartnerCoFinancingAndContributionInputDTO,
  ProjectPartnerCoFinancingAndContributionOutputDTO,
} from '@cat/api';
import {ProjectVersionStore} from '@project/common/services/project-version-store.service';
import {filter, share, shareReplay, startWith, switchMap, tap, withLatestFrom} from 'rxjs/operators';
import {combineLatest, Observable, of, Subject} from 'rxjs';
import {Log} from '@common/utils/log';

@Injectable()
export class ProjectPartnerCoFinancingSpfStore {
  financingAndContribution$: Observable<ProjectPartnerCoFinancingAndContributionOutputDTO>;

  private updateFinancingAndContributionEvent = new Subject();

  constructor(private partnerStore: ProjectPartnerStore,
              private projectPartnerBudgetService: ProjectPartnerBudgetService,
              private projectVersionStore: ProjectVersionStore) {
    this.financingAndContribution$ = this.financingAndContribution();
  }

  updateCoFinancingAndContributions(model: ProjectPartnerCoFinancingAndContributionInputDTO): Observable<any> {
    return of(model).pipe(
      withLatestFrom(this.partnerStore.partner$),
      switchMap(([finances, partner]) =>
        this.projectPartnerBudgetService.updateProjectPartnerSpfCoFinancing(partner.id, finances)
      ),
      tap(() => this.updateFinancingAndContributionEvent.next(true)),
      share()
    );
  }

  private financingAndContribution(): Observable<ProjectPartnerCoFinancingAndContributionOutputDTO> {
    return combineLatest([
      this.partnerStore.partner$,
      this.projectVersionStore.selectedVersionParam$,
      this.updateFinancingAndContributionEvent.pipe(startWith(null))
    ])
      .pipe(
        filter(([partner, version]) => !!partner.id),
        switchMap(([partner, version]) =>
          this.projectPartnerBudgetService.getProjectPartnerSpfCoFinancing(partner.id, version)
        ),
        tap(financing => Log.info('Fetched partner financing and contribution SPF', this, financing)),
        shareReplay(1)
      );
  }
}
