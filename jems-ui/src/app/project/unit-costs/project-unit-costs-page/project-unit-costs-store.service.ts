import {Injectable} from '@angular/core';
import {combineLatest, merge, Observable, of, Subject} from 'rxjs';
import {
  ProgrammeUnitCostDTO, ProgrammeUnitCostListDTO,
  ProjectBudgetService,
  ProjectCostOptionService,
  ProjectUnitCostDTO
} from '@cat/api';
import {catchError, filter, shareReplay, startWith, switchMap, tap} from 'rxjs/operators';
import {ProjectStore} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {ProjectVersionStore} from '@project/common/services/project-version-store.service';
import {Log} from '@common/utils/log';
import {ProjectPaths} from '@project/common/project-util';
import {RoutingService} from '@common/services/routing.service';

@Injectable()
export class ProjectUnitCostsStore {
  public static UNIT_COST_DETAIL_PATH = '/projectProposed/';
  projectUnitCosts$: Observable<ProjectUnitCostDTO[]>;
  projectProposedUnitCosts$: Observable<ProgrammeUnitCostListDTO[]>;
  unitCost$: Observable<ProgrammeUnitCostDTO>;

  private unitCostId: number;
  private projectId: number;

  private savedUnitCost$ = new Subject<ProgrammeUnitCostDTO>();
  private refreshUnitCosts$ = new Subject<void>();

  constructor(private projectBudgetService: ProjectBudgetService,
              private projectStore: ProjectStore,
              private projectVersionStore: ProjectVersionStore,
              private projectCostOptionService: ProjectCostOptionService,
              private routingService: RoutingService) {
    this.projectUnitCosts$ = this.projectUnitCosts();
    this.projectProposedUnitCosts$ = this.projectProposedUnitCosts();
    this.unitCost$ = this.unitCost();
  }

  private projectUnitCosts(): Observable<ProjectUnitCostDTO[]> {
    return combineLatest([
      this.projectStore.project$,
      this.projectVersionStore.selectedVersionParam$,
    ])
      .pipe(
        switchMap(([project, version]) =>
          this.projectBudgetService.getProjectUnitCosts(project.id, version)
        )
      );
  }

  private projectProposedUnitCosts(): Observable<ProgrammeUnitCostListDTO[]> {
    return combineLatest([
      this.projectStore.project$,
      this.projectVersionStore.selectedVersion$,
      this.refreshUnitCosts$.pipe(startWith(null))
    ])
      .pipe(
        tap(([project, version]) => {
          this.projectId = project.id;
        }),
        switchMap(([project, version]) =>
          this.projectCostOptionService.getProjectUnitCostList(project.id, version?.version),
        ),
        tap(list => Log.info('Fetched the project proposed Unit Costs:', this, list)),
      );
  }

  private unitCost(): Observable<ProgrammeUnitCostDTO> {
    const initialUnitCost$ = combineLatest([
      this.routingService.routeParameterChanges(ProjectUnitCostsStore.UNIT_COST_DETAIL_PATH, 'unitCostId'),
      this.projectStore.projectId$,
      this.projectVersionStore.selectedVersion$,
    ]).pipe(
      tap(([unitCostId, projectId]) => {
        this.unitCostId = Number(unitCostId);
        this.projectId = projectId;
      }),
      filter(([projectId]) => !!projectId),
      switchMap(([unitCostId, projectId, version]) => unitCostId
        ? this.projectCostOptionService.getProjectUnitCost(projectId, Number(unitCostId), version?.version)
          .pipe(
            catchError(() => {
              this.routingService.navigate([ProjectPaths.PROJECT_DETAIL_PATH, this.projectId]);
              return of({} as ProgrammeUnitCostDTO);
            })
          )
        : of({} as ProgrammeUnitCostDTO)
      ),
      tap(unitCostId => Log.info('Fetched the project proposed unit cost:', this, unitCostId))
    );

    return merge(initialUnitCost$, this.savedUnitCost$)
      .pipe(
        shareReplay(1)
      );
  }

  saveUnitCost(unitCost: ProgrammeUnitCostDTO, projectId: number): Observable<ProgrammeUnitCostDTO> {
    return this.projectCostOptionService.createProjectUnitCost(projectId, unitCost)
      .pipe(
        tap(saved => this.savedUnitCost$.next(saved)),
        tap(saved => Log.info('Created unit cost:', this, saved))
      );
  }

  updateUnitCost(unitCost: ProgrammeUnitCostDTO): Observable<ProgrammeUnitCostDTO> {
    return this.projectCostOptionService.updateProjectUnitCost(this.projectId, unitCost)
      .pipe(
        tap(saved => this.savedUnitCost$.next(saved)),
        tap(saved => Log.info('Updated unit cost:', this, saved))
      );
  }

  deleteUnitCost(unitCostId: number): Observable<ProgrammeUnitCostDTO> {
    return this.projectCostOptionService.deleteProjectUnitCost(this.projectId, unitCostId)
      .pipe(
        tap(() => this.refreshUnitCosts$.next()),
        tap(() => Log.info('Deleted unit cost with Id:', this, unitCostId))
      );
  }
}
