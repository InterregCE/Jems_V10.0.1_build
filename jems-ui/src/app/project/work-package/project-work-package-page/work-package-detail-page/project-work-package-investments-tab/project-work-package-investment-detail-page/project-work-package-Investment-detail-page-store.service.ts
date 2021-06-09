import {Injectable} from '@angular/core';
import {WorkPackageInvestmentDTO, WorkPackageInvestmentService} from '@cat/api';
import {merge, Observable, of, Subject} from 'rxjs';
import {catchError, shareReplay, switchMap, tap, withLatestFrom} from 'rxjs/operators';
import {ProjectWorkPackagePageStore} from '../../project-work-package-page-store.service';
import {ProjectStore} from '../../../../../project-application/containers/project-application-detail/services/project-store.service';
import {Log} from '../../../../../../common/utils/log';
import {ProjectVersionStore} from '../../../../../services/project-version-store.service';
import {RoutingService} from '../../../../../../common/services/routing.service';

@Injectable()
export class ProjectWorkPackageInvestmentDetailPageStore {

  private investmentId: number;
  private workPackageId: number;
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
    return this.workPackageInvestmentService.addWorkPackageInvestment(this.workPackageId, workPackageInvestment)
      .pipe(
        tap(created => Log.info('Created work package investment:', this, created)),
        tap(() => this.workPackagePageStore.investmentChangeEvent$.next()),
      );
  }

  updateWorkPackageInvestment(workPackageInvestment: WorkPackageInvestmentDTO): Observable<number> {
    return this.workPackageInvestmentService.updateWorkPackageInvestment(this.workPackageId, workPackageInvestment)
      .pipe(
        tap(updated => Log.info('Updated work package investment:', this, updated)),
        tap(() => this.savedInvestment$.next(workPackageInvestment)),
      );
  }

  workPackageInvestment(investmentId: number | string | null, workPackageId: number): Observable<WorkPackageInvestmentDTO | any> {
    this.investmentId = Number(investmentId);
    this.workPackageId = workPackageId;

    const initialInvestment$ = this.projectVersionStore.currentRouteVersion$
      .pipe(
        switchMap(version => this.investmentId
          ? this.workPackageInvestmentService.getWorkPackageInvestment(this.investmentId, version)
            .pipe(
              catchError(() => this.projectStore.projectId$.pipe(
                tap(projectId => this.routingService.navigate([ProjectStore.PROJECT_DETAIL_PATH, projectId]))
                )
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
