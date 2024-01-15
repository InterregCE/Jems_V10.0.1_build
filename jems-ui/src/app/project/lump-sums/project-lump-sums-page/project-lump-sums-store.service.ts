import {Injectable} from '@angular/core';
import {
  ProjectStore
} from '../../project-application/containers/project-application-detail/services/project-store.service';
import {BehaviorSubject, combineLatest, Observable, of} from 'rxjs';
import {map, share, shareReplay, startWith, switchMap, tap, withLatestFrom} from 'rxjs/operators';
import {ProjectLumpSumDTO, ProjectLumpSumService, ProjectPartnerLumpSumDTO} from '@cat/api';
import {ProjectLumpSum} from '../../model/lump-sums/projectLumpSum';
import {PartnerContribution} from '../../model/lump-sums/partnerContribution';
import {
  ProjectPartnerStore
} from '../../project-application/containers/project-application-form-page/services/project-partner-store.service';
import {ProjectVersionStore} from '../../common/services/project-version-store.service';

@Injectable()
export class ProjectLumpSumsStore {

  projectCallLumpSums$ = this.projectStore.projectCallSettings$.pipe(map(it => it.lumpSums));
  projectLumpSums$: Observable<ProjectLumpSum[]>;
  projectTitle$ = this.projectStore.projectTitle$;
  isProjectEditable$ = this.projectStore.projectEditable$;
  partners$ = this.projectPartnerStore.partners$;
  projectPeriods$ = this.projectStore.projectForm$.pipe(map(project => project.periods));

  private updateProjectLumpSumsEvent$ = new BehaviorSubject(null);

  constructor(private projectStore: ProjectStore,
              private projectLumpSumService: ProjectLumpSumService,
              private projectPartnerStore: ProjectPartnerStore,
              private projectVersionStore: ProjectVersionStore) {
    this.projectLumpSums$ = this.projectLumpSums();
  }

  updateProjectLumpSums(projectLumpSums: ProjectLumpSum[]): Observable<any> {
    return of(projectLumpSums).pipe(withLatestFrom(this.projectStore.project$)).pipe(
      switchMap(([lumpSums, project]) => this.projectLumpSumService.updateProjectLumpSums(project.id, lumpSums)),
      tap(() => this.updateProjectLumpSumsEvent$.next(null)),
      share()
    );
  }

  private projectLumpSums(): Observable<ProjectLumpSum[]> {
    return combineLatest([
      this.projectStore.project$,
      this.projectVersionStore.selectedVersionParam$,
      this.updateProjectLumpSumsEvent$.pipe(startWith(null))
    ]).pipe(
      switchMap(([project, version]) =>
        this.projectLumpSumService.getProjectLumpSums(project.id, version)
      ),
      map((projectLumpSums: ProjectLumpSumDTO[]) =>
        projectLumpSums.map(projectLumpSum =>
          new ProjectLumpSum(
            projectLumpSum.orderNr,
            projectLumpSum.programmeLumpSumId,
            projectLumpSum.period,
            projectLumpSum.lumpSumContributions.map((contribution: ProjectPartnerLumpSumDTO) =>
              new PartnerContribution(
                contribution.partnerId,
                contribution.amount)
            ),
            projectLumpSum.comment,
            projectLumpSum.readyForPayment,
            projectLumpSum.fastTrack,
            projectLumpSum.paymentEnabledDate,
            projectLumpSum.lastApprovedVersionBeforeReadyForPayment,
            projectLumpSum.installmentsAlreadyCreated))),
      shareReplay(1)
    );
  }

}
