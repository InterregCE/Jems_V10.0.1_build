import {Injectable} from '@angular/core';
import {ProjectStore} from '../../project-application/containers/project-application-detail/services/project-store.service';
import {BehaviorSubject, combineLatest, Observable, of} from 'rxjs';
import {map, share, shareReplay, startWith, switchMap, tap, withLatestFrom} from 'rxjs/operators';
import {ProjectLumpSumService} from '@cat/api';
import {ProjectLumpSum} from '../../model/lump-sums/projectLumpSum';
import {PartnerContribution} from '../../model/lump-sums/partnerContribution';
import {ProjectPartnerStore} from '../../project-application/containers/project-application-form-page/services/project-partner-store.service';

@Injectable()
export class ProjectLumpSumsPageStore {

  projectCallLumpSums$ = this.projectStore.projectCall$.pipe(map(it => it.lumpSums));
  projectLumpSums$: Observable<ProjectLumpSum[]>;
  projectAcronym$ = this.projectStore.getAcronym();
  isProjectEditable$ = this.projectStore.projectEditable$;
  partners$ = this.projectPartnerStore.partners$;
  projectPeriodNumbers$ = this.projectStore.getProject().pipe(map(project => project.periods.map(period => period.number)));

  private updateProjectLumpSumsEvent$ = new BehaviorSubject(null);

  constructor(private projectStore: ProjectStore, private projectLumpSumService: ProjectLumpSumService, private projectPartnerStore: ProjectPartnerStore) {
    this.projectLumpSums$ = this.projectLumpSums();
  }

  updateProjectLumpSums(projectLumpSums: ProjectLumpSum[]): Observable<any> {
    return of(projectLumpSums).pipe(withLatestFrom(this.projectStore.getProject())).pipe(
      switchMap(([lumpSums, project]) => this.projectLumpSumService.updateProjectLumpSums(project.id, lumpSums)),
      tap(() => this.updateProjectLumpSumsEvent$.next(null)),
      share()
    );
  }

  private projectLumpSums(): Observable<ProjectLumpSum[]> {
    return combineLatest([this.projectStore.getProject(), this.updateProjectLumpSumsEvent$.pipe(startWith(null))]).pipe(
      switchMap(([project]) => this.projectLumpSumService.getProjectLumpSums(project.id)),
      map(projectLumpSums =>
        projectLumpSums.map(projectLumpSum =>
          new ProjectLumpSum(
            projectLumpSum.id,
            projectLumpSum.programmeLumpSumId,
            projectLumpSum.period,
            projectLumpSum.lumpSumContributions.map(contribution =>
              new PartnerContribution(
                contribution.partnerId,
                contribution.amount)
            )))),
      shareReplay(1)
    );
  }

}
