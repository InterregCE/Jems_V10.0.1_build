import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {ProjectStore} from '../services/project-store.service';
import {ActivatedRoute, Router} from '@angular/router';
import {Permission} from '../../../../../security/permissions/permission';

@Component({
  selector: 'app-project-application-eligibility-decision-page',
  templateUrl: './project-application-eligibility-decision-page.component.html',
  styleUrls: ['./project-application-eligibility-decision-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationEligibilityDecisionPageComponent {

  Permission = Permission;

  projectId = this.activatedRoute.snapshot.params.projectId;
  project$ = this.projectStore.getProject();

  constructor(public projectStore: ProjectStore,
              private router: Router,
              private activatedRoute: ActivatedRoute) {
  }

  redirectToProject(): void {
    this.router.navigate(['app', 'project', 'detail', this.projectId]);
  }

}
