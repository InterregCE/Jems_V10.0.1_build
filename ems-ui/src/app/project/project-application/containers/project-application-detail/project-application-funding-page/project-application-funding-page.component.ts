import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {ProjectStore} from '../services/project-store.service';
import {ActivatedRoute, Router} from '@angular/router';
import {Permission} from '../../../../../security/permissions/permission';

@Component({
  selector: 'app-project-application-funding-page',
  templateUrl: './project-application-funding-page.component.html',
  styleUrls: ['./project-application-funding-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFundingPageComponent implements OnInit {
  Permission = Permission;

  projectId = this.activatedRoute.snapshot.params.projectId;
  project$ = this.projectStore.getProject();

  constructor(public projectStore: ProjectStore,
              private router: Router,
              private activatedRoute: ActivatedRoute) {
  }

  ngOnInit(): void {
    this.projectStore.init(this.projectId);
  }

  redirectToProject(): void {
    this.router.navigate(['project', this.projectId]);
  }
}
