import {Injectable} from '@angular/core';
import {combineLatest, Observable} from 'rxjs';
import {ProjectBudgetService, ProjectUnitCostDTO} from '@cat/api';
import {shareReplay, switchMap} from 'rxjs/operators';
import {ProjectStore} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {ProjectVersionStore} from '@project/common/services/project-version-store.service';

@Injectable()
export class ProjectUnitCostsStore {

  projectUnitCosts$: Observable<ProjectUnitCostDTO[]>;

  constructor(private projectBudgetService: ProjectBudgetService,
              private projectStore: ProjectStore,
              private projectVersionStore: ProjectVersionStore) {
    this.projectUnitCosts$ = this.projectUnitCosts();
  }

  private projectUnitCosts(): Observable<ProjectUnitCostDTO[]> {
    return combineLatest([
      this.projectStore.project$,
      this.projectVersionStore.currentRouteVersion$,
    ])
      .pipe(
        switchMap(([project, version]) =>
          this.projectBudgetService.getProjectUnitCosts(project.id, version)
        )
      );
  }
}
