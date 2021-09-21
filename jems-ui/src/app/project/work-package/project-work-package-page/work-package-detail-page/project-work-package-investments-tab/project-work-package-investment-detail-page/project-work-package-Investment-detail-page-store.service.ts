import {Injectable} from '@angular/core';
import {OutputNuts, WorkPackageInvestmentDTO, WorkPackageInvestmentService} from '@cat/api';
import {combineLatest, merge, Observable, of, Subject} from 'rxjs';
import {catchError, map, shareReplay, switchMap, tap} from 'rxjs/operators';
import {WorkPackagePageStore} from '../../work-package-page-store.service';
import {ProjectStore} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {Log} from '@common/utils/log';
import {ProjectVersionStore} from '@project/common/services/project-version-store.service';
import {RoutingService} from '@common/services/routing.service';
import {NutsStore} from '@common/services/nuts.store';
import {filter, take} from 'rxjs/internal/operators';
import {ProjectPaths} from '@project/common/project-util';

@Injectable()
export class ProjectWorkPackageInvestmentDetailPageStore {
  public static INVESTMENT_DETAIL_PATH = 'investments/';
  investment$: Observable<WorkPackageInvestmentDTO>;
  isProjectEditable$: Observable<boolean>;
  workPackageNumber$: Observable<number>;
  nuts$: Observable<OutputNuts[]>;

  private savedInvestment$ = new Subject<WorkPackageInvestmentDTO>();

  constructor(private workPackagePageStore: WorkPackagePageStore,
              private projectStore: ProjectStore,
              private projectVersionStore: ProjectVersionStore,
              private workPackageInvestmentService: WorkPackageInvestmentService,
              private routingService: RoutingService,
              private nutsStore: NutsStore) {
    this.isProjectEditable$ = this.projectStore.projectEditable$;
    this.investment$ = this.workPackageInvestment();
    this.nuts$ = this.nutsStore.getNuts();
    this.workPackageNumber$ = this.workPackageNumber();
  }

  createWorkPackageInvestment(workPackageInvestment: WorkPackageInvestmentDTO): Observable<number> {
    return combineLatest([
      this.projectStore.projectId$,
      this.workPackagePageStore.workPackage$
    ])
      .pipe(
        take(1),
        switchMap(([projectId, workPackage]) => this.workPackageInvestmentService.addWorkPackageInvestment(projectId, workPackage.id, workPackageInvestment)),
        tap(created => Log.info('Created work package investment:', this, created)),
        tap(() => this.projectStore.investmentChangeEvent$.next()),
      );
  }

  updateWorkPackageInvestment(workPackageInvestment: WorkPackageInvestmentDTO): Observable<number> {
    return combineLatest([
      this.projectStore.projectId$,
      this.workPackagePageStore.workPackage$
    ])
      .pipe(
        take(1),
        switchMap(([projectId, workPackage]) => this.workPackageInvestmentService.updateWorkPackageInvestment(projectId, workPackage.id, workPackageInvestment)),
        tap(updated => Log.info('Updated work package investment:', this, updated)),
        tap(() => this.savedInvestment$.next(workPackageInvestment)),
      );
  }

  private workPackageInvestment(): Observable<WorkPackageInvestmentDTO | any> {
    const initialInvestment$ = combineLatest([
      this.projectStore.projectId$,
      this.workPackagePageStore.workPackage$,
      this.routingService.routeParameterChanges(ProjectWorkPackageInvestmentDetailPageStore.INVESTMENT_DETAIL_PATH, 'workPackageInvestmentId'),
      this.projectVersionStore.currentRouteVersion$
    ]).pipe(
        switchMap(([projectId, workPackage, investmentId, version]) => investmentId
          ? this.workPackageInvestmentService.getWorkPackageInvestment(Number(investmentId), projectId, workPackage.id, version)
            .pipe(
              catchError(() => {
                this.routingService.navigate([ProjectPaths.PROJECT_DETAIL_PATH, projectId]);
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

  private workPackageNumber(): Observable<number> {
    return this.workPackagePageStore.workPackage$
      .pipe(
        filter(workPackage => !!workPackage.number),
        map(workPackage => workPackage.number)
      );
  }
}
