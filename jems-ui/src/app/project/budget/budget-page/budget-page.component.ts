import {ChangeDetectionStrategy, Component} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {ProjectPartnerBudgetDTO, ProjectService} from '@cat/api';
import {switchMap} from 'rxjs/operators';
import {Observable} from 'rxjs';
import {ProjectVersionStore} from '@project/common/services/project-version-store.service';
import {ProjectStore} from '@project/project-application/containers/project-application-detail/services/project-store.service';

@Component({
  selector: 'app-budget-page',
  templateUrl: './budget-page.component.html',
  styleUrls: ['./budget-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class BudgetPageComponent {
  projectId = this.activatedRoute?.snapshot?.params?.projectId;

  dataSource$: Observable<ProjectPartnerBudgetDTO[]> =
    this.projectVersionStore.currentRouteVersion$
      .pipe(
        switchMap(version => this.projectService.getProjectBudget(this.projectId, version))
      );

  constructor(public projectStore: ProjectStore,
              private activatedRoute: ActivatedRoute,
              private projectService: ProjectService,
              private projectVersionStore: ProjectVersionStore) {
  }

}
