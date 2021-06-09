import {Injectable} from '@angular/core';
import {WorkPackageInvestmentDTO, WorkPackageInvestmentService} from '@cat/api';
import {combineLatest, merge, Observable, of, Subject} from 'rxjs';
import {catchError, shareReplay, switchMap, tap} from 'rxjs/operators';
import {ProjectWorkPackagePageStore} from '../../project-work-package-page-store.service';
import {ProjectStore} from '../../../../../project-application/containers/project-application-detail/services/project-store.service';
import {Log} from '../../../../../../common/utils/log';
import {ProjectVersionStore} from '../../../../../services/project-version-store.service';
import {RoutingService} from '../../../../../../common/services/routing.service';

@Injectable()
export class ProjectWorkPackageInvestmentDetailPageStore {

  private projectId: number;
  private workPackageId: number;
  private investmentId: number;
  private savedInvestment$ = new Subject<WorkPackageInvestmentDTO>();

  isProjectEditable$: Observable<boolean>;

  constructor(private workPackagePageStore: ProjectWorkPackagePageStore,
              private projectStore: ProjectStore,
              private projectVersionStore: ProjectVersionStore,
              private workPackageInvestmentService: WorkPackageInvestmentService,
              private routingService: RoutingService) {
    this.isProjectEditable$ = this.projectStore.projectEditable$;
  }

  createWorkPackageInvestment(workPackageInvestment: WorkPackageInvestmentDTO): Observable<number> {
    return this.workPackageInvestmentService.addWorkPackageInvestment(this.projectId, this.workPackageId, workPackageInvestment)
      .pipe(
        tap(created => Log.info('Created work package investment:', this, created)),
        tap(() => this.workPackagePageStore.investmentChangeEvent$.next()),
      );
  }

  updateWorkPackageInvestment(workPackageInvestment: WorkPackageInvestmentDTO): Observable<number> {
    return this.workPackageInvestmentService.updateWorkPackageInvestment(this.projectId, this.workPackageId, workPackageInvestment)
      .pipe(
        tap(updated => Log.info('Updated work package investment:', this, updated)),
        tap(() => this.savedInvestment$.next(workPackageInvestment)),
      );
  }

  workPackageInvestment(projectId: number, workPackageId: number, investmentId: number | string | null): Observable<WorkPackageInvestmentDTO | any> {
    this.projectId = projectId;
    this.workPackageId = workPackageId;
    this.investmentId = Number(investmentId);

    const initialInvestment$ = combineLatest([
      this.projectStore.projectId$,
      this.projectVersionStore.currentRouteVersion$
    ]).pipe(
        switchMap(([projectId, version]) => this.investmentId
          ? this.workPackageInvestmentService.getWorkPackageInvestment(this.investmentId, this.projectId, this.workPackageId, version)
            .pipe(
              catchError(() => {
                this.routingService.navigate([ProjectStore.PROJECT_DETAIL_PATH, projectId]);
                return of({});
                }
              ),
              tap(investment => Log.info('Fetched work package investment', this, investment))
            )
          : of({}))
      );

    return merge(this.savedInvestment$, initialInvestment$)
      .pipe(
        tap(investment => Log.info('Fetched work package investment', this, investment)),
        shareReplay(1)
      );
  }
}
