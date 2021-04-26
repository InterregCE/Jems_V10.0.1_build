import {ChangeDetectionStrategy, Component} from '@angular/core';
import {ProjectDetailPageStore} from './project-detail-page-store.service';
import {OutputProjectFile} from '@cat/api';
import {ProjectApplicationFormSidenavService} from '../project-application/containers/project-application-form-page/services/project-application-form-sidenav.service';
import {ActivatedRoute} from '@angular/router';
import {Permission} from '../../security/permissions/permission';
import {combineLatest} from 'rxjs';
import {map} from 'rxjs/operators';
import {SecurityService} from '../../security/security.service';

@Component({
  selector: 'app-project-detail-page',
  templateUrl: './project-detail-page.component.html',
  styleUrls: ['./project-detail-page.component.scss'],
  providers: [ProjectDetailPageStore],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectDetailPageComponent {
  fileType = OutputProjectFile.TypeEnum;
  Permission = Permission;

  projectId = this.activatedRoute.snapshot.params.projectId;

  details$ = combineLatest([
    this.projectDetailStore.project$,
    this.securityService.currentUser,
  ]).pipe(
    map(([project, user]) => ({
      project,
      user,
    })),
  );

  constructor(public projectDetailStore: ProjectDetailPageStore,
              private activatedRoute: ActivatedRoute,
              private sidenavService: ProjectApplicationFormSidenavService,
              private securityService: SecurityService) {
  }

}
